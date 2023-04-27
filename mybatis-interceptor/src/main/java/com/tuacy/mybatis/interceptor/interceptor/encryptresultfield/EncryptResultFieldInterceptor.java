package com.tuacy.mybatis.interceptor.interceptor.encryptresultfield;

import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.sql.Statement;
import java.util.*;

/**
 * 通过拦截器对返回结果中的某个字段进行加密处理
 * 注册拦截器的方法有三种：1.直接给拦截器添加一个@Component，拦截器即可生效   --不会调用setProperties()
 *                     2.在配置类（MybatisConfiguration.class）里添加拦截器，这种方法结果同上 --不会调用setProperties()
 *                     3.在yaml配置文件中指定mybatis的xml配置文件（老方法）,注意：config-location属性和configuration属性不能同时指定 --会调用setProperties()
 */
// @Component
@Intercepts({ // 标识该类是一个拦截器；
  @Signature( // 指明自定义拦截器需要拦截哪一个类型，哪一个方法；
      type = ResultSetHandler.class, // 对应四种类型中的一种； mybatis拦截器默认可拦截的类型只有四种，按照执行顺序：
                                     // Executor：拦截执行器的方法。
                                     // ParameterHandler：拦截参数的处理。
                                     // ResultHandler：拦截结果集的处理。
                                     // StatementHandler：拦截Sql语法构建的处理。
      method = "handleResultSets", // 对应接口中的哪个方法；
      args = {Statement.class} // 对应哪一个方法参数类型（因为可能存在重载方法），三个参数对应的就是ResultSetHandler中的handleResultSets(Statement stmt)方法；
      )
})
public class EncryptResultFieldInterceptor implements Interceptor {

    private static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
    private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
    private static final ReflectorFactory REFLECTOR_FACTORY = new DefaultReflectorFactory();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取到返回结果
        ResultSetHandler resultSetHandler = (ResultSetHandler) invocation.getTarget();
        MetaObject metaResultSetHandler = MetaObject.forObject(resultSetHandler, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, REFLECTOR_FACTORY);
        MappedStatement mappedStatement = (MappedStatement) metaResultSetHandler.getValue("mappedStatement");
        EncryptResultFieldAnnotation annotation = getEncryptResultFieldAnnotation(mappedStatement);
        Object returnValue = invocation.proceed();
        if (annotation != null && returnValue != null) {
            String[] fieldKeyList = annotation.fieldKey();
            Class<? extends IEncryptResultFieldStrategy>[] strategyClassList = annotation.encryptStrategy();
            if (strategyClassList.length != 0 && fieldKeyList.length == strategyClassList.length) {
                Map<String, Class<? extends IEncryptResultFieldStrategy>> strategyMap = null;
                for (int index = 0; index < fieldKeyList.length; index++) {
                    if (strategyMap == null) {
                        strategyMap = new HashMap<>();
                    }
                    strategyMap.put(fieldKeyList[index], strategyClassList[index]);
                }
                // 对结果进行处理
                try {
                    if (returnValue instanceof ArrayList<?>) {
                        List<?> list = (ArrayList<?>) returnValue;
                        for (int index = 0; index < list.size(); index++) {
                            Object returnItem = list.get(index);
                            if (returnItem instanceof String) {
                                List<String> stringList = (List<String>) list;
                                IEncryptResultFieldStrategy encryptStrategy = strategyMap.get(fieldKeyList[0]).newInstance();
                                stringList.set(index, encryptStrategy.encrypt((String) returnItem));
                            } else {
                                MetaObject metaReturnItem = MetaObject.forObject(returnItem, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, REFLECTOR_FACTORY);
                                for (Map.Entry<String, Class<? extends IEncryptResultFieldStrategy>> entry : strategyMap.entrySet()) {
                                    String fieldKey = entry.getKey();
                                    IEncryptResultFieldStrategy fieldEncryptStrategy = entry.getValue().newInstance();
                                    Object fieldValue = metaReturnItem.getValue(fieldKey);
                                    if (fieldValue instanceof String) {
                                        metaReturnItem.setValue(fieldKey, fieldEncryptStrategy.encrypt((String) fieldValue));
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    // ignore
                }

            }
        }
        return returnValue;

    }


    /**
     * Plugin的wrap方法，它根据当前的Interceptor上面的注解定义哪些接口需要拦截，然后判断当前目标对象是否有实现对应需要拦截的接口，
     * 如果没有则返回目标对象本身，如果有则返回一个代理对象。而这个代理对象的InvocationHandler正是一个Plugin。
     * 所以当目标对象在执行接口方法时，如果是通过代理对象执行的，则会调用对应InvocationHandler的invoke方法，也就是Plugin的invoke方法。
     * 所以接着我们来看一下该invoke方法的内容。这里invoke方法的逻辑是：如果当前执行的方法是定义好的需要拦截的方法，则把目标对象、要执行的方法以及方法参数封装成一个Invocation对象，
     * 再把封装好的Invocation作为参数传递给当前拦截器的intercept方法。如果不需要拦截，则直接调用当前的方法。Invocation中定义了定义了一个proceed方法，其逻辑就是调用当前方法，
     * 所以如果在intercept中需要继续调用当前方法的话可以调用invocation的procced方法。
     */
    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {

    }

    /**
     * 获取方法上的EncryptResultFieldAnnotation注解
     *
     * @param mappedStatement MappedStatement
     * @return EncryptResultFieldAnnotation注解
     */
    private EncryptResultFieldAnnotation getEncryptResultFieldAnnotation(MappedStatement mappedStatement) {
        EncryptResultFieldAnnotation encryptResultFieldAnnotation = null;
        try {
            String id = mappedStatement.getId();
            String className = id.substring(0, id.lastIndexOf("."));
            String methodName = id.substring(id.lastIndexOf(".") + 1);
            final Method[] method = Class.forName(className).getMethods();
            for (Method me : method) {
                if (me.getName().equals(methodName) && me.isAnnotationPresent(EncryptResultFieldAnnotation.class)) {
                    encryptResultFieldAnnotation = me.getAnnotation(EncryptResultFieldAnnotation.class);
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return encryptResultFieldAnnotation;
    }


}

package com.tuacy.mybatis.interceptor.aspect.dataResource;


import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import javax.annotation.Resource;
import javax.sql.DataSource;

import com.tuacy.mybatis.interceptor.entity.vo.PageVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;

/**
 * @description: 数据表操作权限切面，根据注解上的表名，去查权限-主表资源关联表，判断表所属的菜单权限是否可用，不可用则不执行切点方法
 **/
@Slf4j
@Aspect
public class DataResourceAspect {

    /**
     * @annotation用于拦截标注在方法上面的注解
     */
    @Pointcut("@annotation(com.tuacy.mybatis.interceptor.aspect.dataResource.DataResource)")
    public void DataResourceMethodPoint() {}

    /**
     * @within用于拦截标注在类上面的注解
     */
    @Pointcut("@within(com.tuacy.mybatis.interceptor.aspect.dataResource.DataResource)")
    public void DataResourceClassPoint() {}

    /**
     * execution用于拦截指定类的方法，可以具体指定，也可以对类的全部方法
     */
    @Pointcut("execution(* com.tuacy.mybatis.interceptor.service.impl.UserManageServiceImpl.*(..))")
    public void DataResourceFieldPoint(){}

    @Resource
    private DataSource dataSource;

    @Resource
    private DataResourceProperties dataResourceProperties;

    /**
     * 默认数据库名字
     */
    @Value("${default.datasource.name}")
    String defaultDatasourceName;

    /**
     * 环绕通知，可以在方法执行前后做切面处理，也可以提前终止原方法的执行
     * ProceedingJoinpoint 继承了 JoinPoint。是在JoinPoint的基础上暴露出 proceed 这个方法。
     * 环绕通知 = 前置 + 目标方法执行 + 后置通知，proceed方法就是用于启动目标方法的执行。暴露出这个方法，就能支持 aop:around 这种切面。
     * （Proceedingjoinpoint 仅支持环绕通知@Around，而其他的几种切面只需要用到JoinPoint，这也是环绕通知和前置、后置通知方法的一个最大区别。这跟切面类型有关）
     */
    @Around("DataResourceMethodPoint() || DataResourceClassPoint() || DataResourceFieldPoint()")
    public Object dataPermPerformance(ProceedingJoinPoint pjp) throws Throwable {
        //获取注解标注的方法
        MethodSignature methodSignature = (MethodSignature)pjp.getSignature();
        Method method = methodSignature.getMethod();

        //通过方法获取注解
        DataResource annotation = method.getAnnotation(DataResource.class);
        if (annotation == null) {
            //获取类上的注解
            annotation = pjp.getTarget().getClass().getAnnotation(DataResource.class);
        }

        // 获取绑定标识（即关联数据表名）
        String tableKey = null;
        if (annotation != null)
            tableKey = annotation.value();
        // 判断当前表关联菜单权限是否启用
        if (StringUtils.isNotBlank(tableKey) && !tableValid(tableKey)) {
            // 停用则不执行方法，获取方法返回类型，直接返回
            Class<?> returnType = methodSignature.getReturnType();
            // 判断方法返回类型，设置不同返回
            // isAssignableFrom()方法是从类继承的角度去判断，instanceof关键字是从实例继承的角度去判断
            // isAssignableFrom()方法是判断是否为某个类的父类，instanceof关键字是判断是否某个类的子类
            if (void.class.isAssignableFrom(returnType)) {
                return null;
            } else if (returnType.isPrimitive()){
                // 返回基本数据类型
                if (int.class.isAssignableFrom(returnType))
                    return 0;
                if (boolean.class.isAssignableFrom(returnType))
                    return false;
                if (long.class.isAssignableFrom(returnType))
                    return 0L;
                if (double.class.isAssignableFrom(returnType) || float.class.isAssignableFrom(returnType))
                    return 0.0;
            } else if (List.class.isAssignableFrom(returnType)) {
                // 返回空的 ArrayList 对象
                return new ArrayList<>();
            } else if (Set.class.isAssignableFrom(returnType)) {
                // 返回空的 HashSet 对象
                return new HashSet<>();
            } else if (Map.class.isAssignableFrom(returnType)) {
                // 返回空的 HashMap 对象
                return new HashMap<>();
            } else if (Optional.class.isAssignableFrom(returnType)) {
                // 返回空的 Optional 对象
                return Optional.empty();
            } else if (PageVO.class.isAssignableFrom(returnType)) {
                // 返回初始化分页对象
                return returnType.newInstance();
            } else {
                return null;
            }
        }
        // 启用则正常执行方法返回
        return pjp.proceed();
        // 环绕-->可以执行完目标方法后再做一些处理
        // Object result = pjp.proceed();
        // do someThing like do logs
        // return pjp.proceed();  或者  return "其他结果";
    }

    public boolean tableValid(String table) {
        if (StringUtils.isBlank(dataResourceProperties.getPermTable())
                || StringUtils.isBlank(dataResourceProperties.getPermFeatureTable())
                || StringUtils.isBlank(dataResourceProperties.getPermId())
                || StringUtils.isBlank(dataResourceProperties.getPermStatus())
                || Objects.isNull(dataResourceProperties.getPermStatusEnableValue())
                || StringUtils.isBlank(dataResourceProperties.getPermRelFeatureId())
                || StringUtils.isBlank(dataResourceProperties.getPermFeatureTableField()))
            return true;
        String selectSql = "select count(*) as count from ";
        String fromSql = dataResourceProperties.getPermTable() + " p, " +  dataResourceProperties.getPermFeatureTable() + " f ";
        String whereSql = "where p." + dataResourceProperties.getPermId() + "= f." + dataResourceProperties.getPermRelFeatureId()
                + " and p." + dataResourceProperties.getPermStatus() + "!= " + dataResourceProperties.getPermStatusEnableValue()
                + " and f." + dataResourceProperties.getPermFeatureTableField() + " like '%" + table + "%'";
        String sql = selectSql + fromSql + whereSql;

        try (Connection conn = dataSource.getConnection()) {
            // CataLog 为jdbc 连接的当前目录/数据库，注意这里是直接操作jdbc连接，不会被Mybatis拦截器拦截
            conn.setCatalog(defaultDatasourceName);
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int count = rs.getInt("count");
                return !(count > 0);
            }
            return true;
        } catch (Exception e) {
            log.error("【数据表操作权限切面】注解表验证失败，表名：{} 异常：{}", table, e);
            return true;
        }
    }


}

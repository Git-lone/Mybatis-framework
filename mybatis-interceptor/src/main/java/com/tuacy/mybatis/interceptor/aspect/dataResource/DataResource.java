package com.tuacy.mybatis.interceptor.aspect.dataResource;

import java.lang.annotation.*;

// 以下几个都是元注解
/**
 * @Retention 表示注解的生命周期，生命周期的长短取决于@Retention的属性RetentionPolicy指定的值
 *                取值             |                     描述                                      |         作用范围         |                          使用场景                           |
 *      1.RetentionPolicy.SOURCE   表示注解只保留在源文件，当java文件编译成class文件，就会消失                       源文件       只是做一些检查性的操作，，比如 @Override 和 @SuppressWarnings
 *      2.RetentionPolicy.SOURCE   注解被保留到class文件，但jvm加载class文件时候被遗弃，这是默认的生命周期       class文件（默认）    要在编译时进行一些预处理操作，比如生成一些辅助代码（如 ButterKnife）
 *      3.RetentionPolicy.RUNTIME  注解不仅被保存到class文件中，jvm加载class文件之后，仍然存在            注解不仅被保存到class文件中，jvm加载class文件之后，仍然存在     注解不仅被保存到class文件中，jvm加载class文件之后，仍然存在
 */
@Retention(RetentionPolicy.RUNTIME)
/**
 *  @Target 指示注释类型适用的上下文，也就指明注解可以修饰的范围，具体范围通过java.lang.annotation.ElementType的枚举进行声明
 */
@Target({ElementType.TYPE, ElementType.METHOD})
/**
 *  如果一个注解@B被@Documented标注，那么被@B修饰的类，生成文档时，会显示@B。如果@B没有被@Documented标准，最终生成的文档中就不会显示@B
 */
@Documented
public @interface DataResource {

    String value() default "";

}

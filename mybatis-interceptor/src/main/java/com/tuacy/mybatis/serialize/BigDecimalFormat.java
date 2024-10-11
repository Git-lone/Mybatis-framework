package com.tuacy.mybatis.serialize;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.RoundingMode;

/**
 * @author Zhang Junlong
 * @date 2024/10/11 9:25
 * @description
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = BigDecimalFormatSerialize.class)
public @interface BigDecimalFormat {

    int scale() default 2;
    RoundingMode roundingMode() default RoundingMode.DOWN;
    boolean stripTrailingZeros() default false;
}

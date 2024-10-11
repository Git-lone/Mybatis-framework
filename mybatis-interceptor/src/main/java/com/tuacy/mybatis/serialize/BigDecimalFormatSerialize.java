package com.tuacy.mybatis.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * @author Zhang Junlong
 * @date 2024/10/11 9:25
 * @description
 */
public class BigDecimalFormatSerialize extends JsonSerializer<BigDecimal> implements ContextualSerializer {

    private Integer scale;
    private RoundingMode roundingMode;
    private boolean stripTrailingZeros;

    @Override
    public void serialize(BigDecimal bigDecimal, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (stripTrailingZeros){
            bigDecimal = bigDecimal.stripTrailingZeros();
        }
        if (scale != null && roundingMode != null) {
            bigDecimal = bigDecimal.setScale(scale, roundingMode);
        }
        String plainString = bigDecimal.stripTrailingZeros().toPlainString();
        jsonGenerator.writeString(plainString);
    }

    /** ContextualSerializer是 Jackson 提供的另一个序列化相关的接口，它的作用是通过字段已知的上下文信息定制JsonSerializer，
     * 只会在第一次序列化字段时调用createContextual,可以获得字段的类型以及注解。
     * 取出注解中的value值，并创建定制的DoubleFormatSerialize，这样在serialize方法中便可以得到这个value值了。
     **/
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty != null) {
            // 非 BigDecimal 直接跳过
            if (Objects.equals(beanProperty.getType().getRawClass(), BigDecimal.class)) {
                // 获取注解信息
                BigDecimalFormat annotation = beanProperty.getAnnotation(BigDecimalFormat.class);
                if (annotation == null) {
                    annotation = beanProperty.getContextAnnotation(BigDecimalFormat.class);
                }
                if (annotation != null) {
                    // 获得注解上的值并赋值
                    this.scale = annotation.scale();
                    this.roundingMode = annotation.roundingMode();
                    this.stripTrailingZeros = annotation.stripTrailingZeros();
                    return this;
                }
            }
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return serializerProvider.findNullValueSerializer(null);
    }
}

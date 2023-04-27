package com.tuacy.mybatis.interceptor.aspect;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @description: 数据表操作权限配置参数
 **/
@Component
@Data
/**
 * 1.在@ConfigurationProperties的使用，把配置类的属性与yml配置文件绑定起来的时候，还需要加上@Component注解才能绑定并注入IOC容器中，若不加上@Component，则会无效。
 * 2.@EnableConfigurationProperties的作用：则是将让使用了 @ConfigurationProperties 注解的配置类生效,将该类注入到 IOC 容器中,交由 IOC 容器进行管理，此时则不用再配置类上加上@Component。
 * 3.@EnableConfigurationProperties注解用于启用@ConfigurationProperties注解的类的自动配置功能。如果在@EnableConfigurationProperties注解中不指定任何值，则默认情况下会启用所有使用@ConfigurationProperties注解的类的自动配置功能。（同包下没有路径限制）
 */
@ConfigurationProperties(prefix = "data-perm")
public class DataResourceProperties {

    @Value(value = "${data-perm.perm.table:sys_permission}")
    private String permTable;

    @Value("${data-perm.feature.table:sys_permission_features}")
    private String permFeatureTable;

    @Value("${data-perm.perm.key:id}")
    private String permId;

    @Value("${data-perm.perm.status-field:status}")
    private String permStatus;

    @Value("${data-perm.perm.status-field-enable:1}")
    private Integer permStatusEnableValue;

    @Value("${data-perm.feature.relation-key:permission_id}")
    private String permRelFeatureId;

    @Value("${data-perm.feature.table-field:primary_table}")
    private String permFeatureTableField;

}

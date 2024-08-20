package com.tuacy.mybatis.interceptor;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@SpringBootApplication
//@EnableDiscoveryClient
@EnableTransactionManagement
@MapperScan(basePackages = "com.tuacy.mybatis.interceptor.mapper")
//@EnableFeignClients
//@EnableHystrix
public class MyBatisInterceptorApplication {

    public static void main(String[] args) {
        Matcher matcher = Pattern.compile("(\\w+[-]?)([0-9]{4}[a|b|c]?)").matcher("gsk0808sk");
        boolean matches = matcher.matches();
        System.out.println(matches);
    }

}

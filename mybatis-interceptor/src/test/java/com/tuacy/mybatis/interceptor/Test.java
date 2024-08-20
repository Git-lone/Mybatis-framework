package com.tuacy.mybatis.interceptor;

import com.tuacy.mybatis.interceptor.entity.vo.UserInfoVo;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Zhang Junlong
 * @version 1.0.0
 * @date 2023/7/28 16:41
 * @description
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class Test {

    public static void main(String[] args) {
        // String originalFileName = "";
        // String tempPath = "/1231312312312312";
        // String s = replaceTempPath(tempPath);
        // System.out.println(s);

        UserInfoVo mock = Mockito.mock(UserInfoVo.class, "mock_1");
        System.out.println(mock);
    }

    @org.junit.Test
    public void filterWindowFileName(){
        UserInfoVo mock = Mockito.mock(UserInfoVo.class, "mock_1");
        System.out.println(mock);
    }

    /**
     * @description 过滤文件名非法字符 Windows
     * @param originalFileName 原始文件名
     * @return 将非法字符替换为空字符的文件名
     **/
    public static String filterWindowFileName(String originalFileName){
        // 使用正则表达式过滤非法字符
        // 在WINDOWS中常见的非法字符
        String WINDOWS_FILE_ILLGAL_CHAR = "[\\\\\\\\/:*?\\\"<>|]";
        String replaceMent = "";
        return originalFileName.replaceAll(WINDOWS_FILE_ILLGAL_CHAR, replaceMent);
    }

    public static String replaceTempPath(String tempPath){
        // 使用正则表达式过滤非法字符
        // 在WINDOWS中常见的非法字符
        String WINDOWS_FILE_ILLGAL_CHAR = "/temp";
        String replaceMent = "";
        return tempPath.replaceFirst(WINDOWS_FILE_ILLGAL_CHAR, replaceMent);
    }

}

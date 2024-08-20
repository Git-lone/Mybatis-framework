package com.tuacy.mybatis.interceptor.strategy;

import java.util.Comparator;

/**
 * @author Zhang Junlong
 * @date 2023/10/31 10:27
 * @description
 */
public class CustomComparator implements Comparator<User> {

    @Override
    public int compare(User user1, User user2) {
        boolean isOpeningFan1 = user1.getName().startsWith("开启");
        boolean isOpeningFan2 = user2.getName().startsWith("开启");

        if (isOpeningFan1 && isOpeningFan2) {
            // 提取开启扇的序号进行比较
            int num1 = Integer.parseInt(user1.getName().substring(2));
            int num2 = Integer.parseInt(user2.getName().substring(2));
            return Integer.compare(num1, num2);
        } else if (isOpeningFan1) {
            return -1;
        } else if (isOpeningFan2) {
            return 1;
        } else {
            // 提取固定扇的序号进行比较
            int num1 = Integer.parseInt(user1.getName().substring(2));
            int num2 = Integer.parseInt(user2.getName().substring(2));
            return Integer.compare(num1, num2);
        }
    }
}

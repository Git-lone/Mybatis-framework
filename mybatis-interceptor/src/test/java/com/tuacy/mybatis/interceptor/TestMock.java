package com.tuacy.mybatis.interceptor;

import cn.hutool.core.collection.CollUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.text.Collator;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Zhang Junlong
 * @date 2023/12/20 10:40
 * @description
 */
@RunWith(MockitoJUnitRunner.class)
public class TestMock {

    @Test
    public void testMock(){
        String modelIdentifyType = "1";
        String substring = modelIdentifyType.substring(modelIdentifyType.length() - 1);
        System.out.println(substring);
    };

    @Test
    public void testRegex(){
        String name = "TC0614aL";
        // Pattern pattern = Pattern.compile("(\\w+[-]?)([0-9]{4}[a|b|c]?)(L|R|LR)?");
        Pattern pattern = Pattern.compile("(\\w+[-]?)([0-9]{2}[`]?[0-9]{2}[`]?)(L|R|LR)?");
        Matcher matcher = pattern.matcher(name);
        matcher.matches();
    }

    @Test
    public void testMatch(){
        // String behindName = "TC06a12b2267c-sadfasfasfasfsa";
        // String behindRegex = "(\\w+[-]?)([0-9]{4}[a|b|c]?)?.*";
        // boolean matchBehind = behindName.matches(behindRegex);
        // System.out.println("matchBehind: " + matchBehind);
        // String validSizeRegex1 = ".*(\\d{4}[a|b|c]?)";
        // Pattern pattern1 = Pattern.compile(validSizeRegex1);
        // Matcher matcher1 = pattern1.matcher(behindName);
        // // 循环匹配，只保留最后一次匹配的结果，即从字符串的末尾开始获取
        // if (matcher1.find()){
        //     String validSize1 = matcher1.group(1);
        //     System.out.println("validSize: " + validSize1);
        //     int validSizeIndex1 = behindName.lastIndexOf(validSize1);
        //     // 有效尺寸前是前缀
        //     String stylePrefix1 = behindName.substring(0, validSizeIndex1);
        //     String standardName1 = stylePrefix1 + validSize1;
        //     System.out.println("stylePrefix1: " + stylePrefix1);
        //     System.out.println("standardName1: " + standardName1);
        // }

        // System.out.println();


        String betweenName = "TC0617";
        String betweenRegex = "(\\w+[-]?)([0-9]{2}[`]?[0-9]{2}[`]?)?.*";
        boolean matchBetween = betweenName.matches(betweenRegex);
        System.out.println("matchBetween: " + matchBetween);

        Matcher matcher = Pattern.compile(betweenRegex).matcher(betweenName);
        String s = matcher.find() ? matcher.group(1) : StringUtils.EMPTY;


        String validSizeRegex2 = ".*(\\d{2}['`]?\\d{2}['`]?)";
        Pattern pattern2 = Pattern.compile(validSizeRegex2);
        Matcher matcher2 = pattern2.matcher(betweenName);
        // 循环匹配，只保留最后一次匹配的结果，即从字符串的末尾开始获取
        if (matcher2.find()){
            String validSize2 = matcher2.group(1);
            System.out.println("validSize2: " + validSize2);
            int validSizeIndex2 = betweenName.lastIndexOf(validSize2);
            // 有效尺寸前是前缀
            String stylePrefix2 = betweenName.substring(0, validSizeIndex2);
            String standardName2 = stylePrefix2 + validSize2;
            System.out.println("stylePrefix2: " + stylePrefix2);
            System.out.println("standardName2: " + standardName2);

            String finalSize = "";
            int abcCount = 1;
            String validSizeRegex3 = "(\\d{2}['`]?)";
            Pattern pattern3 = Pattern.compile(validSizeRegex3);
            Matcher matcher3 = pattern3.matcher(validSize2);
            while (matcher3.find()){
                String size = matcher3.group(1);
                System.out.println("size: " + size);
                if (size.contains("`") || size.contains("‘")) {
                    char abc =(char) (abcCount + 'a');
                    finalSize =  Character.toString(abc);
                    abcCount++;
                }
            }
            System.out.println("finalSize: " + finalSize);
        }
    }

    @Test
    public void testMatchAll(){
        String input = "BY16’23’";

        // 尺寸段
        Pattern validSizePattern = Pattern.compile(".*(\\d{2}[`'‘]?\\d{2}[`'‘]?)");
        String sizeSegment = null;
        Matcher matcher = validSizePattern.matcher(input);
        // 循环匹配，只保留最后一次匹配的结果，即从字符串的末尾开始获取
        if (matcher.find()){
            sizeSegment = matcher.group(1);
        }
        System.out.println("sizeSegment: "+ sizeSegment);

    }

    @Test
    public void getFileFormat() {
        String filePath = "/library/component/detail/file/1719632145988952064.dwg";
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filePath.length() - 1) {
            // 通过截取字符串获取文件格式
            String fileType = filePath.substring(lastDotIndex).toLowerCase();
            System.out.println("fileType:" + fileType);
        } else {
            // 文件路径中没有点或者点在末尾，无法获取文件格式
            System.out.println("获取文件类型错误");
        }
    }

    @Test
    public void testLoop() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        for (Integer integer : list) {
            if (integer == 2) {
                list.remove(integer);
            }
        }
        System.out.println("list: " + list);
    }

    @Test
    public void testStringFormat() {
        Long num = 46L;
        String format = String.format("%03d", num);
        System.out.println("format: " + format);
    }

    @Test
    public void splitLayer() {
        String layer = "-1~7,9_10,13~15,17,17~20";
        List<String> retList = new LinkedList<>();
        // 获取 楼层
        String[] layerRange = layer.split(",");
        // 获取 标准层
        for (String range : layerRange){
            // 楼层连接符有~和_，统一转为~
            range = range.replace("_", "~");
            String[] layers = range.split("~");
            // // 对地下层的处理
            // for (int i = 0; i < layers.length; i++) {
            //     String each = layers[i];
            //     if (each.startsWith("B")) {
            //         layers[i] = each.replaceFirst("B", "-");
            //     }
            //     if (each.startsWith("b")) {
            //         layers[i] = each.replaceFirst("b", "-");
            //     }
            // }

            if (layers.length == 1){
                retList.add(layers[0]);
            }
            if (layers.length > 1){
                int start = Integer.parseInt(layers[0]);
                int end = Integer.parseInt(layers[1]);
                if (start > end){
                    int temp = start;
                    start = end;
                    end = temp;
                }
                // 获取完整楼层
                List<String> layerList = IntStream.rangeClosed(start, end)
                        .boxed()
                        .filter(each -> each != 0)
                        .map(String::valueOf)
                        .collect(Collectors.toList());
                retList.addAll(layerList);
            }
        }
        System.out.println(retList.stream().distinct().collect(Collectors.toList()));
    }
    @Test
    public void testStreamSorted(){
        List<String> elements = Arrays.asList("Z", "3abc", "2abc", "A30abc2", "5abc", "a", "b", "c", "1#", "3#", "2#", "11abc", "A#", "B1213", "30abc2");

        Collator COLLATOR = Collator.getInstance(Locale.TRADITIONAL_CHINESE);

        // 使用Stream进行升序排序
        List<String> result = elements.stream()
                .sorted((a, b) -> {
                    char firstCharA = a.charAt(0);
                    char firstCharB = b.charAt(0);
                    Character.UnicodeBlock ubA = Character.UnicodeBlock.of(firstCharA);
                    Character.UnicodeBlock ubB = Character.UnicodeBlock.of(firstCharB);

                    if (Character.isDigit(firstCharA) && Character.isDigit(firstCharB)) {
                        // 如果都是数字开头，则按数字升序排序
                        int numComparison = Integer.compare(Integer.parseInt(a.replaceAll("\\D", "")), Integer.parseInt(b.replaceAll("\\D", "")));
                        // 如果数字相同，则按原始字符串比较
                        return numComparison == 0 ? a.compareTo(b) : numComparison;
                    } else if ((ubA == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                            || ubA == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                            || ubA == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A)
                            && (ubB == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                            || ubB == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                            || ubB == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A)) {
                        // 如果都是中文开头，则按照中文拼音升序排序
                        return COLLATOR.compare(a, b);
                    } else if (Character.isLetter(firstCharA) && Character.isLetter(firstCharB)) {
                        // 如果都是字母开头，则按字母升序排序
                        if (Character.toLowerCase(firstCharA) == Character.toLowerCase(firstCharB)){
                            return a.compareTo(b);
                        }else {
                            return Character.compare(Character.toLowerCase(firstCharA), Character.toLowerCase(firstCharB));
                        }
                    } else {
                        // 否则，数字在前，字母在后
                        return Character.isDigit(firstCharA) ? -1 : 1;
                    }
                })
                .collect(Collectors.toList());

        System.out.println(result);

    }

    private static boolean isChineseStart(String input) {
        if (input != null && !input.isEmpty()) {
            char firstChar = input.charAt(0);
            Character.UnicodeBlock ub = Character.UnicodeBlock.of(firstChar);
            return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                    || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                    || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A;
        }
        return false;
    }

    class Person {
        private String name;
        private int age;

        private BigDecimal money;

        public Person(String name, int age, BigDecimal money) {
            this.name = name;
            this.age = age;
            this.money = money;
        }

        public Person(BigDecimal money) {
            this.money = money;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public BigDecimal getMoney() {
            return money;
        }
    }

    @Test
    public void groupByTwoFields() {
        Integer aLong = Integer.parseInt("180.00"); // fail
        Integer aLong1 = Integer.valueOf("180.00"); // fail
        System.out.println("along: " + aLong);
        System.out.println("along: " + aLong1);
    }

    @Test
    public void treeMap() {
        Map<String, String> treeMap = new TreeMap<>();
        treeMap.put("02", "B");
        treeMap.put("01", "A");
        treeMap.put("04", "D");
        treeMap.put("03", "C");
        Set<String> strings = treeMap.keySet();
        System.out.println("treeMap: " + strings);
    }

    @Test
    public void sortedBigDecimal() {
        List<Person> list = new ArrayList<>();

        list.add(new Person("1", 1, new BigDecimal("3.14")));
        list.add(new Person("2", 2, new BigDecimal("1.23")));
        list.add(new Person("3", 3, new BigDecimal("2.71")));

        List<Person> sortedList = list.stream().sorted(Comparator.comparing(Person::getMoney)).collect(Collectors.toList());
        System.out.println(sortedList);
    }

    @Test
    public void sortedString() {
        List<String> list = new ArrayList<>();

        list.add("02");
        list.add("01");
        list.add("04");
        list.add("03");

        List<String> sortedList = list.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        System.out.println(sortedList);
    }

    private String convertNum2Letter(Integer number){
        char letter = 0;
        if (number < 0){
            return null;
        }
        if (number <= 26) {
            int offset = (number - 1) % 26;
            letter = (char) (letter + offset);
        }else {
            letter = (char) ('A' + number - 1);
        }
        return String.valueOf(letter);
    }

    @Test
    public void rangeClosed() {
        List<List<Integer>> spanList = new ArrayList<>();
        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();
        list1.add(1);
        list1.add(2);
        list2.add(5);
        list2.add(8);
        List<Integer> gridSize = new ArrayList<>();
        gridSize.add(10);
        gridSize.add(10);
        gridSize.add(10);
        gridSize.add(10);
        spanList.add(list1);
        spanList.add(list2);
        List<Integer> collect = spanList.stream().flatMap(item -> {
            Integer min = CollUtil.min(item);
            Integer max = CollUtil.max(item);
            int[] array = IntStream.rangeClosed(min, max).toArray();
            List<Integer> collect1 = IntStream.rangeClosed(min, max).boxed().collect(Collectors.toList());
            return IntStream.rangeClosed(min, max).boxed();
        }).collect(Collectors.toList());
        System.out.println(collect);
    }

    @Test
    public void length() {
        String str = "/library/component/image/file/1806599229766385664/";
        System.out.println(str.length());
    }

    @Test
    public void throwException() {
        int i = 1;
        if (i==1){
            System.out.println("RuntimeException1");
            throw new RuntimeException("1");
        }
        System.out.println("RuntimeException2");
        throw new RuntimeException("2");
    }

    @Test
    public void throwBigDecimal() {
        String val1 = "0.10";
        BigDecimal decimal1 = new BigDecimal(val1);
        System.out.println("decimal1: " + decimal1);
        String val2 = "00.1";
        BigDecimal decimal2 = new BigDecimal(val2);
        System.out.println("decimal2: " + decimal2);
        String val3 = "1.";
        BigDecimal decimal3 = new BigDecimal(val3);
        System.out.println("decimal3: " + decimal3);
        String val4 = "1.09";
        BigDecimal decimal4 = new BigDecimal(val4);
        System.out.println("decimal4: " + decimal4);
    }

    @Test
    public void getByIndex() {
        String input = "sash-i-12312313";
        String substring = input.substring(5, 6);
        System.out.println(substring);
    }

    @Test
    public void testEqual() {
        BigDecimal bigDecimal = new BigDecimal("10");
        BigDecimal bigDecimal1 = BigDecimal.valueOf(10.0d);
        System.out.println(bigDecimal.equals(bigDecimal1));
    }

}

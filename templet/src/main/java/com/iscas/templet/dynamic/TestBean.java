package com.iscas.templet.dynamic;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/4/8 14:56
 * @since jdk1.8
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TestBean extends  DynamicBean{
    private String realName;
    private Integer age;

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException {
        TestBean testBean = new TestBean();
        testBean.setAge(18);
        testBean.setRealName("张三");
        System.out.println(testBean);
        // 设置类成员属性
        HashMap propertyMap = new LinkedHashMap();

        propertyMap.put("id", Class.forName("java.lang.Integer"));

        propertyMap.put("name", Class.forName("java.lang.String"));

        propertyMap.put("children", Class.forName("java.util.List"));

        propertyMap.put("aaa", Class.forName("java.util.Map"));
        testBean.dynamicAddProps(propertyMap);
        testBean.setValue("id", 111);
        testBean.setValue("name", "zhangsan");
        testBean.setValue("children", Arrays.asList("zhangsanson1", "zhangsanson2"));
        Map map = new HashMap<>();
        map.put("awegweg",1111);
        map.put("wgwegwe",1111);
        testBean.setValue("aaa", map);
        Object object = testBean.getObject();
        Object id = testBean.getValue("id");
        System.out.println("id:" + id);
        Object name = testBean.getValue("name");
        System.out.println("name:" + name);
        Object children = testBean.getValue("children");
        System.out.println("children:" + children);
        Object aaa = testBean.getValue("aaa");
        System.out.println("aaa:" + aaa);
        System.out.println(testBean.convertToMap());
    }
}

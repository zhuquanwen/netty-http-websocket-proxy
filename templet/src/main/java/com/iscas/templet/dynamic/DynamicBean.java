package com.iscas.templet.dynamic;

import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.beans.BeanMap;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/4/8 14:26
 * @since jdk1.8
 */
public class DynamicBean implements Serializable {

    /**
     * 实体Object
     */
    protected Object object = null;

    /**
     * 属性map
     */
    protected BeanMap beanMap = null;

    public DynamicBean() {
        super();
    }

    public DynamicBean(Map propertyMap) {
        this.object = generateBean(propertyMap);
        this.beanMap = BeanMap.create(this.object);
    }

    /**
     * 给bean属性赋值
     * @param property 属性名
     * @param value 值
     */
    public void setValue(String property, Object value) {
        beanMap.put(property, value);
    }

    /**
     * 通过属性名得到属性值
     * @param property 属性名
     * @return 值
     */
    public Object getValue(String property) {
        return beanMap.get(property);
    }

    /**
     * 得到该实体bean对象
     * @return
     */
    public Object getObject() {
        return this.object;
    }

    /**
     * @param propertyMap
     * @return
     */
    private Object generateBean(Map propertyMap) {
        BeanGenerator generator = new BeanGenerator();
        Set keySet = propertyMap.keySet();
        for (Iterator i = keySet.iterator(); i.hasNext();) {
            String key = (String) i.next();
            generator.addProperty(key, (Class) propertyMap.get(key));
        }
        return generator.create();
    }

    public void dynamicAddProps(Map propertyMap) throws IllegalAccessException {
        BeanGenerator generator = new BeanGenerator();
        Set keySet = propertyMap.keySet();
        for (Iterator i = keySet.iterator(); i.hasNext();) {
            String key = (String) i.next();
            generator.addProperty(key, (Class) propertyMap.get(key));
        }


        Class<? extends DynamicBean> aClass = this.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        if (declaredFields != null) {
            for (Field declaredField : declaredFields) {
                generator.addProperty(declaredField.getName(), declaredField.getType());
            }
        }
        this.object = generator.create();
        this.beanMap = BeanMap.create(this.object);
        if (declaredFields != null) {
            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                setValue(declaredField.getName(), declaredField.get(this));
            }
        }

    }

    public Map convertToMap() throws IllegalAccessException {
        Map map = new LinkedHashMap();
        Field[] declaredFields = this.getClass().getDeclaredFields();
        if (declaredFields != null) {
            for (Field declaredField : declaredFields) {
                //如果是这俩属性就不进行转化了，否则转化入Map
                if (!Objects.equals(declaredField.getName(), "object") &&
                        !Objects.equals(declaredField.getName(), "beanMap")) {
                    declaredField.setAccessible(true);
                    map.put(declaredField.getName(), declaredField.get(this));
                }
            }
        }
        if (beanMap != null) {
            for (Object key : beanMap.keySet()) {
                map.put(String.valueOf(key), this.getValue(String.valueOf(key)));
            }
        }
        return map;
    }

}

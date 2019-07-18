package com.iscas.common.tools.core.reflect;

import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 反射增强工具类
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2018/7/13
 * @since jdk1.8
 */
public class ReflectUtils {
    /**私有构造方法，防止被实例化使用*/
    private ReflectUtils(){}

    /**
     * 反射执行一个对象的某个方法,不带参数
     *
     * @version 1.0
     * @since jdk1.8
     * @date 2018/7/13
     * @param data {@link Object} 任意一个对象
     * @param methodName 函数名称
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @return java.lang.Object 方法返回的结果
     */
    public static Object doMethod(Object data, String methodName) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {

        assert StringUtils.isNotBlank(methodName);
        assert data != null;
        Method m1 = data.getClass().getDeclaredMethod(methodName);
        Object obj = m1.invoke(data);
        return obj;
    }
    /**
     * 反射执行一个对象的某个方法,携带参数<br/>
     * 这里比较难处理的是8中基本类型，<br/>
     * 传入参数的类型取出来的class 变为了包装类.<br/>
     * 处理方式是将8中基本数据类型与包装类分别作一个映射匹配，判断函数，然后执行。
     *
     * @version 1.0
     * @since jdk1.8
     * @date 2018/7/13
     * @param data {@link Object} 任意一个对象
     * @param methodName 函数名称
     * @param args 参数
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @return java.lang.Object 方法返回的结果
     */
    public static Object doMethodWithParam(Object data, String methodName, Object... args) throws InvocationTargetException, IllegalAccessException {
        assert StringUtils.isNotBlank(methodName);
        assert data != null;
        assert args != null;
        Class[] c=null;
        if(args != null){//存在
            int len = args.length;
            c = new Class[len];
            for(int i=0;i<len;++i){
                c[i] = args[i].getClass();
            }
        }
        Method[] methods = data.getClass().getDeclaredMethods();
        for (Method method: methods) {
            //判断方法名称匹配
            if(StringUtils.equals(methodName, method.getName())){
                //判断方法参数匹配
                Class<?>[] clazzs = method.getParameterTypes();
                if(clazzs != null && c != null){
                    boolean flag = true;
                    for (int i = 0; i< clazzs.length; i++){
                        Class clazz = clazzs[i];
                        Class claxx = c[i];
                        if(claxx != clazz){
                            if("int".equals(clazz.getName())){
                                if(!"Integer".equals(claxx.getSimpleName())){
                                    flag = false;
                                    break;
                                }
                            }else if("byte".equals(clazz.getName())){
                                if(!"Byte".equals(claxx.getSimpleName())){
                                    flag = false;
                                    break;
                                }
                            }else if("short".equals(clazz.getName())){
                                if(!"Short".equals(claxx.getSimpleName())){
                                    flag = false;
                                    break;
                                }
                            }else if("long".equals(clazz.getName())){
                                if(!"Long".equals(claxx.getSimpleName())){
                                    flag = false;
                                    break;
                                }
                            }else if("boolean".equals(clazz.getName())){
                                if(!"Boolean".equals(claxx.getSimpleName())){
                                    flag = false;
                                    break;
                                }
                            }else if("char".equals(clazz.getName())){
                                if(!"Character".equals(claxx.getSimpleName())){
                                    flag = false;
                                    break;
                                }
                            } else if("float".equals(clazz.getName())){
                                if(!"Float".equals(claxx.getSimpleName())){
                                    flag = false;
                                    break;
                                }
                            }else if("double".equals(clazz.getName())){
                                if(!"Double".equals(claxx.getSimpleName())){
                                    flag = false;
                                    break;
                                }
                            }
                        }

                    }
                    Object obj = method.invoke(data, args);
                    return obj;
                }
            }
        }
        return null;
    }
    /**
     * 判断一个Classs是否为基本数据类型
     * @version 1.0
     * @since jdk1.8
     * @date 2018/7/16
     * @param clz Class对象
     * @return boolean
     */
    public static boolean isWrapClass(Class clz)  {
        try {
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 对象是否为数组对象
     *
     * @param obj 对象
     * @return 是否为数组对象，如果为{@code null} 返回false
     */
    public static boolean isArray(Object obj) {
        if (null == obj) {
            return false;
        }
//        反射 获得类型
        return obj.getClass().isArray();
    }

    /**
     * 获取一个类和其父类的所有属性
     * @version 1.0
     * @since jdk1.8
     * @date 2018/7/16
     * @param clazz Class对象
     * @return java.util.List<java.lang.reflect.Field>
     */
    public static List<Field> findAllFieldsOfSelfAndSuperClass(Class clazz) {

        Field[] fields = null;
        List fieldList = new ArrayList();
        while (true) {
            if (clazz == null || clazz == Object.class) {
                break;
            } else {
                fields = clazz.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    fieldList.add(fields[i]);
                }
                clazz = clazz.getSuperclass();
            }
        }
        return fieldList;
    }
    /**
     * 把一个Bean对象转换成Map对象
     *
     * @param obj 对象
     * @param ignores 忽略的fields
     * @return Map
     * @throws Exception
     */
    public static Map convertBean2Map(Object obj, String[] ignores) throws Exception {
        Map map = new HashMap();
        Class clazz = obj.getClass();
        List<Field> fieldList = findAllFieldsOfSelfAndSuperClass(clazz);
        Field field = null;
        for (int i = 0; i < fieldList.size(); i++) {
            field = fieldList.get(i);
            // 定义fieldName是否在拷贝忽略的范畴内
            boolean flag = false;
            if (ignores != null && ignores.length != 0) {
                flag = isExistOfIgnores(field.getName(), ignores);
            }
            if (!flag) {
                Object value = getProperty(obj, field.getName());
                if (null != value
                        && !StringUtils.isEmpty(value.toString())) {
                    map.put(field.getName(),
                            getProperty(obj, field.getName()));
                }
            }
        }
        return map;
    }
    /**
     * 把一个Bean对象转换成Map对象</br>
     *
     * @param obj 对象
     * @return Map
     */
    public static Map convertBean2Map(Object obj)throws Exception {
        return convertBean2Map(obj, null);
    }
    /**
     * 把一个Map对象转换成Bean对象</br>
     *
     * @param map map对象
     * @param beanClass class对象
     * @return Map
     */
    public static Object convertMap2Bean(Map<String, Object> map, Class<?> beanClass) throws Exception {
        if (map == null) {
            return null;
        }
        Object obj = beanClass.newInstance();
        org.apache.commons.beanutils.BeanUtils.populate(obj, map);
        return obj;
    }

    /**
     * 判断fieldName是否是ignores中排除的
     *
     * @param fieldName
     * @param ignores
     * @return
     */
    private static boolean isExistOfIgnores(String fieldName,
                                            String[] ignores) {
        boolean flag = false;
        for (String str : ignores) {
            if (str.equals(fieldName)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     *  调用get方法获取某个属性的值,当前类没有尝试去父类拿
     * @version 1.0
     * @since jdk1.8
     * @date 2018/7/16
     * @param obj 对象
     * @param propertyName 属性名
     * @throws Exception
     * @return java.lang.Object
     */
    public static Object invokeGet(Object obj, String propertyName) throws Exception {
        assert obj != null;
        assert propertyName != null;
        Class clazz = obj.getClass();
        Object result = null;
        try{
            PropertyDescriptor pd = new PropertyDescriptor(propertyName,clazz);
            Method rM = pd.getReadMethod();
            result = rM.invoke(obj);
        }catch (Exception e){
            if(!(clazz instanceof Object)){
                clazz = clazz.getSuperclass();
                result = invokeGet(obj,propertyName);
            }else{
                throw new Exception("has no this getMethod");
            }
        }
        return result;
    }
    public static PropertyDescriptor getPropertyDescriptor(Class clazz,
                                                           String propertyName) throws Exception {
        StringBuffer sb = new StringBuffer();// 构建一个可变字符串用来构建方法名称
        Method setMethod = null;
        Method getMethod = null;
        PropertyDescriptor pd = null;
        boolean[] superC = new boolean[1];
        superC[0] = false;
        Arrays.stream(clazz.getDeclaredFields()).forEach(field -> {
            if(propertyName.equalsIgnoreCase(field.getName())){
                superC[0] = true;
            }
        });
        Field f = null;
        if(superC[0]){
            f = clazz.getDeclaredField(propertyName);// 根据字段名来获取字段
        }else{
            clazz = clazz.getSuperclass();
            f = clazz.getDeclaredField(propertyName); //父类
        }
        if (f != null) {
            // 构建方法的后缀
            String methodEnd = propertyName.substring(0, 1).toUpperCase()
                    + propertyName.substring(1);
            sb.append("set" + methodEnd);// 构建set方法
            setMethod = clazz.getDeclaredMethod(sb.toString(),
                    new Class[] {f.getType()});
            sb.delete(0, sb.length());// 清空整个可变字符串
            sb.append("get" + methodEnd);// 构建get方法
            // 构建get 方法
            getMethod =
                    clazz.getDeclaredMethod(sb.toString(), new Class[] {});
            // 构建一个属性描述器 把对应属性 propertyName 的 get 和 set 方法保存到属性描述器中
            pd = new PropertyDescriptor(propertyName, getMethod, setMethod);
        }
        return pd;
    }
    /**
     * 设置属性
     * @version 1.0
     * @since jdk1.8
     * @date 2018/7/16
     * @param obj 对象
     * @param propertyName 属性名
     * @param value 要设置的值
     * @throws Exception
     * @return void
     */
    public static void setProperty(Object obj, String propertyName,
                                   Object value) throws Exception {

        Class clazz = obj.getClass();// 获取对象的类型
        PropertyDescriptor pd = getPropertyDescriptor(clazz, propertyName);// 获取 clazz
        // 类型中的
        // propertyName
        // 的属性描述器
        Method setMethod = pd.getWriteMethod();// 从属性描述器中获取 set 方法
        try {
            setMethod.invoke(obj, new Object[] {value});// 调用 set 方法将传入的value值保存属性中去
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     *  调用get方法获取某个属性的值
     * @version 1.0
     * @since jdk1.8
     * @date 2018/7/16
     * @param obj 对象
     * @param propertyName 属性名
     * @throws Exception
     * @return java.lang.Object
     */
    public static Object getProperty(Object obj, String propertyName) throws Exception {
        Class clazz = obj.getClass();// 获取对象的类型
        PropertyDescriptor pd = null;
        pd = getPropertyDescriptor(clazz, propertyName);// 获取 clazz
        // 类型中的
        // propertyName
        // 的属性描述器
        Method getMethod = pd.getReadMethod();// 从属性描述器中获取 get 方法
        Object value = null;
        try {
            value = getMethod.invoke(obj, new Object[] {});// 调用方法获取方法的返回值
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;// 返回值
    }

    /**
     * 获取本地和父类的所有属性名称
     * @version 1.0
     * @since jdk1.8
     * @date 2018/7/25
     * @param clazz Class类型
     * @return java.util.List<java.lang.String>
     */
    public static List<String> getAllFieldNames(Class clazz){

        List<String> fields = new ArrayList<>();
        getFieldNames(fields, clazz);
        return fields;
    }
    private static void getFieldNames(List<String> fields, Class clazz){
        if(clazz != Object.class){
            Field[] declaredFields = clazz.getDeclaredFields();
            if(declaredFields != null){
                for (Field field : declaredFields) {
                    String name = field.getName();
                    fields.add(name);
                }
            }
            getFieldNames(fields, clazz.getSuperclass());
        }
    }


    /**
     * 获取子类和所有父类的所有Field
     * @version 1.0
     * @since jdk1.8
     * @date 2018/9/6
     * @param clazz Class类型
     * @throws
     * @return java.util.List<java.lang.reflect.Field>
     */
    public static List<Field> getAllFields(Class clazz){
        List<Field> fields = new ArrayList<>();
        getFields(fields, clazz);
        return fields;
    }

    private static void getFields(List<Field> fields, Class clazz){
        if(clazz != Object.class){
            Field[] declaredFields = clazz.getDeclaredFields();
            if(declaredFields != null){
                for (Field field : declaredFields) {
                    fields.add(field);
                }
            }
            getFields(fields, clazz.getSuperclass());
        }
    }

    /**
     * 将一个实体的数据按照某些字段名取出放入一个map
     * @version 1.0
     * @since jdk1.8
     * @date 2018/8/24
     * @param obj 待转换的对象
     * @param needFields 需要转换的对象
     * @throws IllegalAccessException 反射异常
     * @return java.util.Map
     */
    public static Map getNeedFields(Object obj, String... needFields) throws IllegalAccessException {


        Map map = new HashMap();
        if(obj == null){
            throw new RuntimeException("待转换对象不能为空");
        }
        if(needFields == null){
            throw new RuntimeException("需要的字段不能为空");
        }
        Class clazz = obj.getClass();
        getNeedFields(map,obj,clazz,needFields);
        return map;
    }



    private static void getNeedFields(Map map, Object obj, Class clazz, String... needFields) throws IllegalAccessException {
        Field[] fields = clazz.getDeclaredFields();
        if(fields != null){
            for (Field field: fields ) {
                for (int i = needFields.length - 1; i >= 0 ; i--) {
                    if(field.getName().equals(needFields[i])){
                        field.setAccessible(true);
                        Object o = field.get(obj);
                        map.put(needFields[i], o);
                    }
                }
            }
        }
        Class superclass = clazz.getSuperclass();
        if(superclass != Object.class){
            getNeedFields(map, obj, superclass,needFields);
        }
    }
}

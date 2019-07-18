package com.iscas.cs.server.bean;

import cn.hutool.core.lang.Assert;
import com.iscas.common.tools.core.classloader.LoadUtils;
import com.iscas.cs.server.unproxy.self.web.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/16 21:10
 * @since jdk1.8
 */
@Slf4j
public class BeanRegister {
    private static Map<String, Object> BEAN_MAP = new ConcurrentHashMap<>();

    public static void register(Class clazz, Object obj ) {
        Assert.notNull(clazz, "class不能为空");
        Assert.notNull(obj, "实体不能为空");
        BEAN_MAP.put(clazz.getName(), obj);
    }

    public static Object get(String beanName) {
        Assert.notEmpty(beanName, "beanName不能为空");
        Object obj = BEAN_MAP.get(beanName);
        Assert.notNull(obj, String.format("未获取到%s对应的实体", beanName));
        return obj;
    }

    public static <T> T  get(Class<T> clazz) {
        Assert.notNull(clazz, "class不能为空");
        return (T) get(clazz.getName());
    }

    public static synchronized void registerAll(String packageName) throws IOException, IllegalAccessException, InstantiationException {
        Set<Class<?>> set = getCsComponentAndRestControllerClasses(packageName);
        if (CollectionUtils.isNotEmpty(set)) {
            for (Class<?> aClass: set) {
                registNestFields(aClass);
            }
        }
    }

    public static Object registNestFields(Class<?> aClass) throws IllegalAccessException, InstantiationException {
        Object obj = BEAN_MAP.get(aClass.getName());
        if (obj == null) {
            obj = aClass.newInstance();
            Field[] fields = aClass.getDeclaredFields();
            if (ArrayUtils.isNotEmpty(fields)) {
                for (Field field : fields) {
                    Annotation autowried = field.getAnnotation(Autowired.class);
                    if (autowried != null) {
                        Object o = registNestFields(field.getType());
                        field.setAccessible(true);
                        field.set(obj, o);
                    }
                }
            }
            BEAN_MAP.put(aClass.getName(), obj);
        }
        return obj;
    }

    /**
     *
     * 获取RestController*
     * */
    public static Set<Class<?>> getCsComponentAndRestControllerClasses(String packageName) throws IOException {
        Assert.notEmpty(packageName, "packageName不能为空");
        //查找class上携带@CsComponent注解
        Set<Class<?>> classSet = LoadUtils.getClasses(packageName);
        Set<Class<?>> newSet = new HashSet<>();
        if (CollectionUtils.isNotEmpty(classSet)) {
            newSet = classSet.stream().filter(clazz -> {
                boolean flag = false;
                Component[] annotationsByType = clazz.getAnnotationsByType(Component.class);
                if (annotationsByType != null && annotationsByType.length > 0) {
                    flag = true;
                }
                RestController[] annotationsByType2 = clazz.getAnnotationsByType(RestController.class);
                if (annotationsByType2 != null && annotationsByType2.length > 0) {
                    flag = true;
                }
                return flag;
            }).collect(Collectors.toSet());

        }
        return newSet;
    }
}

package com.iscas.common.tools.core.classloader;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static cn.hutool.core.util.ClassUtil.getClassLoader;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/11 11:19
 * @since jdk1.8
 */

public class LoadUtils {
    private LoadUtils() {}

//    public static void  bindMethodAndPath(String packageName) throws Exception {
//        ProxyServiceSetting proxyServiceSetting = Constant.PROXY_SERVICE_SETTING;
//        String basePath = proxyServiceSetting.getBasePath();
//        String selfWebPath = proxyServiceSetting.getSelfWebPath();
//
//        Set<Class<?>> conrollerClasses = getRestControllerClasses(packageName);
//        if (conrollerClasses != null) {
//            for (Class<?> clazz: conrollerClasses) {
//                RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
//                if (requestMapping != null) {
//                    StringBuilder path = new StringBuilder();
//                    path.append(basePath).append(selfWebPath);
//                    String value = requestMapping.value();
//                    path.append(value);
//                    if (!value.endsWith("/")) {
//                        path.append("/");
//                    }
//                    //获取所有方法
//                    Method[] methods = clazz.getMethods();
//                    if (methods != null) {
//                        for (Method method: methods) {
//                            GetMapping getMapping = method.getAnnotation(GetMapping.class);
//                            if (getMapping != null) {
//                                String[] methodPaths = getMapping.value();
//                                bind(new StringBuilder().append(path), methodPaths, method, "GET", clazz);
//                            }
//                            PostMapping postMapping = method.getAnnotation(PostMapping.class);
//                            if (postMapping != null) {
//                                String[] methodPaths = postMapping.value();
//                                bind(new StringBuilder().append(path), methodPaths, method, "POST", clazz);
//                            }
//                            PutMapping putMapping = method.getAnnotation(PutMapping.class);
//                            if (putMapping != null) {
//                                String[] methodPaths = putMapping.value();
//                                bind(new StringBuilder().append(path), methodPaths, method, "PUT", clazz);
//                            }
//                            DeleteMapping deleteMapping = method.getAnnotation(DeleteMapping.class);
//                            if (deleteMapping != null) {
//                                String[] methodPaths = deleteMapping.value();
//                                bind(new StringBuilder().append(path), methodPaths, method, "DELETE", clazz);
//                            }
//                        }
//                    }
//                }
//
//            }
//            //校验有没有重复的PATH
//            List<WebInfo> webInfos = new ArrayList<>();
//            CollectionUtils.addAll(webInfos, WebBinding.WEB_INFOS);
//            for (int i = webInfos.size() - 1; i >= 0; i--) {
//                WebInfo webInfo = webInfos.remove(i);
//                if (webInfos.contains(webInfo)) {
//                    log.error("路由:{}在多处出现", webInfo.getPath());
//                    throw new Exception("不可绑定相同的路由");
//                }
//            }
//        }
//    }

//    private static void bind(StringBuilder path, String[] methodPaths, Method method,
//                             String requestMethod, Class<?> clazz) {
//        if (methodPaths != null) {
//            for (String methodPath: methodPaths) {
//                path.append(methodPath);
//                WebInfo webInfo = new WebInfo();
//                webInfo.setPath(path.toString());
//                webInfo.setMethod(method);
//                webInfo.setRequestMethod(requestMethod);
//                webInfo.setClazz(clazz);
//                WebBinding.WEB_INFOS.add(webInfo);
//            }
//        }
//    }


//    /**
//     * 获取RestController
//     * */
//    public static Set<Class<?>> getRestControllerClasses(String packageName) throws IOException {
//        Set<Class<?>> classSet = new HashSet<>();
//        Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(packageName.replace(".", "/"));
//        while (urls.hasMoreElements()) {
//            URL url = urls.nextElement();
//            if (url != null) {
//                String protocol = url.getProtocol();
//                if (protocol.equals("file")) {
//                    String packagePath = url.getPath().replaceAll("%20", " ");
//                    addClass(classSet, packagePath, packageName);
//                } else if (protocol.equals("jar")) {
//                    JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
//                    if (jarURLConnection != null) {
//                        JarFile jarFile = jarURLConnection.getJarFile();
//                        if (jarFile != null) {
//                            Enumeration<JarEntry> jarEntries = jarFile.entries();
//                            while (jarEntries.hasMoreElements()) {
//                                JarEntry jarEntry = jarEntries.nextElement();
//                                String jarEntryName = jarEntry.getName();
//                                if (jarEntryName.endsWith(".class")) {
//
//                                    String packageName2 = packageName.replace(".","/");
//                                    if (jarEntryName != null && jarEntryName.startsWith(packageName2)) {
//                                        log.debug(jarEntryName);
//                                        String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
//                                        doAddClass(classSet, className);
//                                    }
//
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        //查找class上携带@RestController注解的
//        Set<Class<?>> newSet = new HashSet<>();
//        if (CollectionUtils.isNotEmpty(classSet)) {
//            newSet = classSet.stream().filter(clazz -> {
//                boolean flag = false;
//                RestController[] annotationsByType = clazz.getAnnotationsByType(RestController.class);
//                if (annotationsByType != null && annotationsByType.length > 0) {
//                    flag = true;
//                }
//                return flag;
//            }).collect(Collectors.toSet());
//
//        }
//        return newSet;
//    }

    public static void addClass(Set<Class<?>> classSet, String packagePath, String packageName) {
        File[] files = new File(packagePath).listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory();
            }
        });
        for (File file : files) {
            String fileName = file.getName();
            if (file.isFile()) {
                String className = fileName.substring(0, fileName.lastIndexOf("."));
                if (StringUtils.isNotEmpty(packageName)) {
                    className = packageName + "." + className;
                }
                doAddClass(classSet, className);
            } else {
                String subPackagePath = fileName;
                if (StringUtils.isNotEmpty(packagePath)) {
                    subPackagePath = packagePath + "/" + subPackagePath;
                }
                String subPackageName = fileName;
                if (StringUtils.isNotEmpty(packageName)) {
                    subPackageName = packageName + "." + subPackageName;
                }
                addClass(classSet, subPackagePath, subPackageName);
            }
        }
    }

    /**
     * 加载类
     */
    public static Class<?> loadClass(String className, boolean isInitialized) {
        Class<?> cls;
        try {
            cls = Class.forName(className, isInitialized, getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return cls;
    }
    /**
     * 加载类（默认将初始化类）
     */
    public static Class<?> loadClass(String className) {
        return loadClass(className, true);
    }
    public static void doAddClass(Set<Class<?>> classSet, String className) {
        Class<?> cls = loadClass(className, false);
        classSet.add(cls);
    }


    /**
     *
     * 获取RestController*
     * */
    public static Set<Class<?>> getClasses(String packageName) throws IOException {
        Set<Class<?>> classSet = new HashSet<>();
        Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(packageName.replace(".", "/"));
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            if (url != null) {
                String protocol = url.getProtocol();
                if (protocol.equals("file")) {
                    String packagePath = url.getPath().replaceAll("%20", " ");
                    LoadUtils.addClass(classSet, packagePath, packageName);
                } else if (protocol.equals("jar")) {
                    JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                    if (jarURLConnection != null) {
                        JarFile jarFile = jarURLConnection.getJarFile();
                        if (jarFile != null) {
                            Enumeration<JarEntry> jarEntries = jarFile.entries();
                            while (jarEntries.hasMoreElements()) {
                                JarEntry jarEntry = jarEntries.nextElement();
                                String jarEntryName = jarEntry.getName();
                                if (jarEntryName.endsWith(".class")) {

                                    String packageName2 = packageName.replace(".","/");
                                    if (jarEntryName != null && jarEntryName.startsWith(packageName2)) {
                                        String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
                                        LoadUtils.doAddClass(classSet, className);
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
        return classSet;
    }


}

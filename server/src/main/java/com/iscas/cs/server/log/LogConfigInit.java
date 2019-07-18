package com.iscas.cs.server.log;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/3/1 21:14
 * @since jdk1.8
 */
public class LogConfigInit {
    private final static String DEFAULT_CONFIG_FILE_NAME = "log-config.xml";


    /**
     * 加载logback配置,默认方式，读取resources下的配置；
     * 默认文件名是log-config.xml
     *
     * @version 1.0
     * @since jdk1.8
     * @date 2019/3/1
     * @throws
     * @return void
     */
    public static void loadFromResource () throws IOException, JoranException {
        loadFromResource(DEFAULT_CONFIG_FILE_NAME);
    }

    /**
     * 加载logback配置,默认方式，读取resources下的配置；
     * 默认文件名是log-config.xml
     *
     * @version 1.0
     * @since jdk1.8
     * @date 2019/3/1
     * @param  configFileName resources下的配置文件名
     * @throws
     * @return void
     */
    public static void loadFromResource (String configFileName) throws IOException, JoranException {
        InputStream inputStream = LogConfigInit.class.getResourceAsStream("/" + configFileName);
        load(inputStream);
    }

    /**
     * 加载logback配置
     * @version 1.0
     * @since jdk1.8
     * @date 2019/3/1
     * @param inputSource logback配置InputSource
     * @throws
     * @return void
     */
    public static void load (InputSource inputSource) throws IOException, JoranException {
        if (inputSource == null) {
            throw new IOException("Logback配置输入为空");
        }
        load(inputSource);
    }

    /**
     * 加载logback配置
     * @version 1.0
     * @since jdk1.8
     * @date 2019/3/1
     * @param URL logback配置URL
     * @throws
     * @return void
     */
    public static void load (URL URL) throws IOException, JoranException {
        if (URL == null) {
            throw new IOException("Logback配置输入URL为空");
        }
        toLoad(URL);
    }

    /**
     * 加载logback配置
     * @version 1.0
     * @since jdk1.8
     * @date 2019/3/1
     * @param inputStream logback配置输入流
     * @throws
     * @return void
     */
    public static void load (InputStream inputStream) throws IOException, JoranException {
        if (inputStream == null) {
            throw new IOException("Logback配置输入流为空");
        }
        toLoad(inputStream);
    }

    /**
     *加载logback配置
     * @version 1.0
     * @since jdk1.8
     * @date 2019/3/1
     * @param externalFilePath 外部配置文件路径
     * @throws
     * @return void
     */
    public static void load (String externalFilePath) throws IOException, JoranException {
        if (externalFilePath == null || Objects.equals("", externalFilePath)) {
            throw new IOException("Logback 引用外部配置文件，但此文件路径为空");
        }
        load(new File(externalFilePath));
    }

    /**
     * 加载logback配置
     * @version 1.0
     * @since jdk1.8
     * @date 2019/3/1
     * @param externalConfigFile 外部配置文件
     * @throws
     * @return void
     */
    public static void load (File externalConfigFile) throws IOException, JoranException {
        if (externalConfigFile == null) {
            throw new IOException("文件为空");
        }
        if(!externalConfigFile.exists()){
            throw new IOException("Logback引用外部的配置文件，但此文件不存在");
        }
        if (!externalConfigFile.isFile()) {
            throw new IOException("Logback引用外部的配置文件，但'" + externalConfigFile.getAbsolutePath() + "'不是一个文件类型");
        }
        if (!externalConfigFile.canRead()) {
            throw new IOException("Logback引用外部的配置文件，但'" + externalConfigFile.getAbsolutePath() + "'此文件不可读取");
        }
        toLoad(externalConfigFile);
    }

    private static void toLoad (Object config) throws JoranException{
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        lc.reset();
        if (config instanceof File) {
            File configFile = (File) config;
            configurator.doConfigure(configFile);
        } else if (config instanceof InputStream) {
            InputStream inputStream = (InputStream) config;
            configurator.doConfigure(inputStream);
        } else if (config instanceof URL) {
            URL URL = (java.net.URL) config;
            configurator.doConfigure(URL);
        } else if (config instanceof InputSource) {
            InputSource inputSource = (InputSource) config;
            configurator.doConfigure(inputSource);
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(lc);

    }
}

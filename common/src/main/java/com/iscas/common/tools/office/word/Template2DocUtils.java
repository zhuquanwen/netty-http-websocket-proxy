package com.iscas.common.tools.office.word;

import freemarker.template.*;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;

/**
 *  <p>使用freemarker将模板文件转为Word</p>
 *  <p>模板文件构建方式参见</p>
 *  <p>https://www.cnblogs.com/duanrantao/p/9377818.html</p>
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/5/22 15:47
 * @since jdk1.8
 */
public class Template2DocUtils {
    private Template2DocUtils() {}
    private static final String ENCODING = "UTF-8";
    /**
     * 据数据及模板生成文件,从某个绝对路径下读取模板文件
     * @param data             Map的数据结果集
     * @param templateFileName ftl模版文件名,在resources下的路径 如 “/templates/word/aaa.ftl”
     * @param outFilePath      生成文件名称(可带路径)
     */
    public static File crateDocFromDir(Map<String, Object> data, String templateFileName, String outFilePath) throws IOException, TemplateException {
        return crateWord(data, templateFileName, outFilePath, "dir");
    }


    private static File crateWord(Map<String, Object> data, String templateFileName, String outFilePath, String type) throws IOException, TemplateException {
        templateFileName = templateFileName.replace("\\","/");
        Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        //设置模板所在文件夹
        String path = "/";
        String ftlFileName = templateFileName;
        if (templateFileName.contains("/")) {
            path = StringUtils.substringBeforeLast(templateFileName, "/") + "/";
//            path = templateFileName.substring(0, templateFileName.lastIndexOf("/") + 1);
//            ftlFileName = templateFileName.substring(templateFileName.lastIndexOf("/") + 1);
            ftlFileName = StringUtils.substringAfterLast(templateFileName, "/");
        }
        if ("resources".equals(type)) {
            cfg.setClassForTemplateLoading(Template2DocUtils.class, path);
        } else if ("dir".equals(type)) {
            cfg.setDirectoryForTemplateLoading(new File(path));
        }

        // setEncoding这个方法一定要设置国家及其编码，不然在ftl中的中文在生成html后会变成乱码
        cfg.setEncoding(Locale.getDefault(), ENCODING);
        // 设置对象的包装器
        cfg.setObjectWrapper(new DefaultObjectWrapper(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS));
        // 设置异常处理器,这样的话就可以${a.b.c.d}即使没有属性也不会出错
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);

        Writer out = null;
        File outFile = new File(outFilePath);
        try {
            // 获取模板,并设置编码方式，这个编码必须要与页面中的编码格式一致
//            Template template = getTemplate(templateFileName);
            Template template = cfg.getTemplate(ftlFileName, ENCODING);
            if (!outFile.getParentFile().exists()) {
                outFile.getParentFile().mkdirs();
            }
            out = new OutputStreamWriter(new FileOutputStream(outFile), ENCODING);
            // 处理模版
            template.process(data, out);
            out.flush();
            System.out.println("由模板文件" + templateFileName + "生成" + outFilePath + "成功.");
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                System.err.println("关闭Write对象出错");
                e.printStackTrace();
            }
        }
        return outFile;
    }



    /**
     * 据数据及模板生成文件,从resources下读取模板文件
     * @param data             Map的数据结果集
     * @param templateFileName ftl模版文件名,在resources下的路径 如 “/templates/word/aaa.ftl”
     * @param outFilePath      生成文件名称(可带路径)
     */
    public static File crateDocFromResources(Map<String, Object> data, String templateFileName, String outFilePath) throws TemplateException, IOException {
        return crateWord(data, templateFileName, outFilePath, "resources");
    }

    //获得图片的base64码
    public static String getImageBase(InputStream in) throws IOException {
        if (in == null) {
            return "";
        }
        byte[] data = null;
        try {
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(data);
    }

    //获得图片的base64码
    public static String getImageBase(String src) throws IOException {
        if (src == null || src == "") {
            return "";
        }
        File file = new File(src);
        if (!file.exists()) {
            return "";
        }
        InputStream in = new FileInputStream(file);
        return getImageBase(in);
    }

}

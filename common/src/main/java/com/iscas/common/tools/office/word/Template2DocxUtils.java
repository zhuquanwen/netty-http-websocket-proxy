package com.iscas.common.tools.office.word;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 *  <p>使用freemarker将模板文件转为Word</p>
 *  <p>模板文件构建方式参见</p>
 *  <p>https://blog.csdn.net/fenfenguai/article/details/78731331</p>
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/5/23 15:34
 * @since jdk1.8
 */
public class Template2DocxUtils {

    /**
     * @param dataMap               参数数据
     * @param docxTemplateFile      docx模版文件路径
     * @param xmlDocument           docx中document.xml模板文件  用来存在word文档的主要数据信息
     * @param xmlDocumentXmlRels    docx中document.xml.rels 模板文件  用来存在word文档的主要数据配置 包括图片的指向
     * @param xmlContentTypes       docx中 [Content_Types].xml 模板文件 用来配置 docx文档中所插入图片的类型 如 png、jpeg、jpg等
     * @param xmlHeader             docx中 header1.xml 模板文件 用来配置docx文档的页眉文件
     * @param outputFileName        所生成的docx文件名称  如  xxx.docx  或  xxx.doc
     * */
    public static void crateDocxFromDir(Map dataMap, String docxTemplateFile, String xmlDocument, String xmlDocumentXmlRels,
                                        String xmlContentTypes, String xmlHeader,
                                        String outputFileName) throws Exception {

//        URL basePath = Template2DocxUtils.class.getClassLoader().getResource("");
//        String realTemplatePath = /*basePath.getPath() +*/ templatePath;
        //临时文件产出的路径
        String outputPath =/* basePath.getPath() +*/ UUID.randomUUID().toString();
        File file = new File(outputPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        List<File> delFileList = new ArrayList<>();
        delFileList.add(file);
        try {


            //================================获取 document.xml.rels 输入流================================
            String xmlDocumentXmlRelsComment = getFreemarkerContent(dataMap, xmlDocumentXmlRels);
            ByteArrayInputStream documentXmlRelsInput =
                    new ByteArrayInputStream(xmlDocumentXmlRelsComment.getBytes());
            //================================获取 document.xml.rels 输入流================================

            //================================获取 header1.xml 输入流================================
            ByteArrayInputStream headerInput = null;
            if (xmlHeader != null) {
                headerInput = getFreemarkerContentInputStream(dataMap, xmlHeader);

            }

            //================================获取 header1.xml 输入流================================

            //================================获取 [Content_Types].xml 输入流================================
            ByteArrayInputStream contentTypesInput = getFreemarkerContentInputStream(dataMap, xmlContentTypes);
            //================================获取 [Content_Types].xml 输入流================================


            //读取 document.xml.rels  文件 并获取rId 与 图片的关系 (如果没有图片 此文件不用编辑直接读取就行了)
            Document document = DocumentHelper.parseText(xmlDocumentXmlRelsComment);
            Element rootElt = document.getRootElement(); // 获取根节点
            Iterator iter = rootElt.elementIterator();// 获取根节点下的子节点head
            List<Map<String, String>> picList = (List<Map<String, String>>) dataMap.get("images");

            // 遍历Relationships节点
            while (iter.hasNext()) {
                Element recordEle = (Element) iter.next();
                String id = recordEle.attribute("Id").getData().toString();
                String target = recordEle.attribute("Target").getData().toString();
                if (target.indexOf("media") == 0) {
//                        System.out.println("id>>>"+id+"   >>>"+target);
//                        id>>>rId18   >>>media/pic1
//
                    for (Map<String, String> picMap : picList) {
                        if (target.endsWith(picMap.get("name"))) {
                            picMap.put("rId", id);
                        }
                    }
                }
            }
            dataMap.put("images", picList);//覆盖原来的picList;

            //================================获取 document.xml 输入流================================
            ByteArrayInputStream documentInput = getFreemarkerContentInputStream(dataMap, xmlDocument);
            //================================获取 document.xml 输入流================================


            ZipFile zipFile = new ZipFile(docxTemplateFile);
            Enumeration<? extends ZipEntry> zipEntrys = zipFile.entries();
            ZipOutputStream zipout = new ZipOutputStream(new FileOutputStream(outputFileName));


            //------------------覆盖文档------------------
            int len = -1;
            byte[] buffer = new byte[1024];
            while (zipEntrys.hasMoreElements()) {
                ZipEntry next = zipEntrys.nextElement();
                InputStream is = zipFile.getInputStream(next);
                if (next.toString().indexOf("media") < 0) {
                    // 把输入流的文件传到输出流中 如果是word/document.xml由我们输入
                    zipout.putNextEntry(new ZipEntry(next.getName()));
//                    System.out.println("next.getName()>>>" + next.getName() + "  next.isDirectory()>>>" + next.isDirectory());
                    //写入图片配置类型
                    if (next.getName().equals("[Content_Types].xml")) {
                        if (contentTypesInput != null) {
                            while ((len = contentTypesInput.read(buffer)) != -1) {
                                zipout.write(buffer, 0, len);
                            }
                            contentTypesInput.close();
                        }

                    } else if (next.getName().indexOf("document.xml.rels") > 0) {
                        //写入填充数据后的主数据配置信息
                        if (documentXmlRelsInput != null) {
                            while ((len = documentXmlRelsInput.read(buffer)) != -1) {
                                zipout.write(buffer, 0, len);
                            }
                            documentXmlRelsInput.close();
                        }
                    } else if ("word/document.xml".equals(next.getName())) {
                        //写入填充数据后的主数据信息
                        if (documentInput != null) {
                            while ((len = documentInput.read(buffer)) != -1) {
                                zipout.write(buffer, 0, len);
                            }
                            documentInput.close();
                        }

                    } else if ("word/header1.xml".equals(next.getName())) {
                        //写入填充数据后的页眉信息
                        if (headerInput != null) {
                            while ((len = headerInput.read(buffer)) != -1) {
                                zipout.write(buffer, 0, len);
                            }
                            headerInput.close();
                        }

                    } else {
                        while ((len = is.read(buffer)) != -1) {
                            zipout.write(buffer, 0, len);
                        }
                        is.close();
                    }

                }

            }
            //------------------覆盖文档------------------

            //------------------写入新图片------------------
            len = -1;
            if (picList != null && !picList.isEmpty()) {
                for (Map<String, String> pic : picList) {
                    ZipEntry next = new ZipEntry("word" + "/" + "media" + "/" + pic.get("name"));
                    zipout.putNextEntry(new ZipEntry(next.toString()));
                    InputStream in = new FileInputStream(pic.get("path"));
                    while ((len = in.read(buffer)) != -1) {
                        zipout.write(buffer, 0, len);
                    }
                    in.close();
                }
            }


            //------------------写入新图片------------------
            zipout.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("生成docx文件失败！");
        } finally {
            if (delFileList != null) {
                for (File file1 : delFileList) {
                    if (file1.exists()) {
                        file1.delete();
                    }
                }
            }
        }

    }

    /**
     * 获取模板字符串
     * @param dataMap   参数
     * @param templateName  模板名称
     * @return
     */
    public static String getFreemarkerContent(Map dataMap, String templateName) {
        String result = "";
        try {
            //创建配置实例
            Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);

            //设置编码
            configuration.setDefaultEncoding("UTF-8");

            templateName = templateName.replace("\\","/");
            //设置模板所在文件夹
            String path = "/";
            String name = templateName;
            if (templateName.contains("/")) {
                path = StringUtils.substringBeforeLast(templateName, "/") + "/";
                name = StringUtils.substringAfterLast(templateName, "/");
            }
            //ftl模板文件统一放至 com.lun.template 包下面
            configuration.setDirectoryForTemplateLoading(new File(path));
            //获取模板
            Template template = configuration.getTemplate(name);

            StringWriter swriter = new StringWriter();
            //生成文件
            template.process(dataMap, swriter);
            result = swriter.toString();


        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 生成主数据模板xml
     *
     * @param dataMap      数据参数
     * @param templateName 模板名称 eg: xxx.xml
     * @param filePath     生成路径 eg: d:/ex/ee/xxx.xml
     */
    public static void createTemplateXml(Map dataMap, String templateName, String filePath) {
        try {
            //创建配置实例
            Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);

            //设置编码
            configuration.setDefaultEncoding("UTF-8");

            templateName = templateName.replace("\\","/");
            //设置模板所在文件夹
            String path = "/";
            String name = templateName;
            if (templateName.contains("/")) {
                path = StringUtils.substringBeforeLast(templateName, "/") + "/";
                name = StringUtils.substringAfterLast(templateName, "/");
            }
            configuration.setDirectoryForTemplateLoading(new File(path));
            //获取模板
            Template template = configuration.getTemplate(name);
//            System.out.println("filePath ==> " + filePath);
            //输出文件
            File outFile = new File(filePath);
//            System.out.println("outFile.getParentFile() ==> " + outFile.getParentFile());
            //如果输出目标文件夹不存在，则创建
            if (!outFile.getParentFile().exists()) {
                outFile.getParentFile().mkdirs();
            }

            //将模板和数据模型合并生成文件
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));


            //生成文件
            template.process(dataMap, out);

            //关闭流
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取模板字符串输入流
     * @param dataMap   参数
     * @param templateName  模板名称
     * @return
     */
    public static ByteArrayInputStream getFreemarkerContentInputStream(Map dataMap, String templateName) {
        ByteArrayInputStream in = null;

        try {
            //创建配置实例
            Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);

            //设置编码
            configuration.setDefaultEncoding("UTF-8");

            templateName = templateName.replace("\\","/");
            //设置模板所在文件夹
            String path = "/";
            String name = templateName;
            if (templateName.contains("/")) {
                path = StringUtils.substringBeforeLast(templateName, "/") + "/";
                name = StringUtils.substringAfterLast(templateName, "/");
            }
            configuration.setDirectoryForTemplateLoading(new File(path));
            //获取模板
            Template template = configuration.getTemplate(name);

            StringWriter swriter = new StringWriter();
            //生成文件
            template.process(dataMap, swriter);
            String result = swriter.toString();
            in = new ByteArrayInputStream(swriter.toString().getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return in;
    }

}

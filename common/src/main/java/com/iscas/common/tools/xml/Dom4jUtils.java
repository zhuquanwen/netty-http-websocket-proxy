package com.iscas.common.tools.xml;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * dom4j 工具类
 * @author zhuquanwen
 * @date: 2018/7/16
 **/
public class Dom4jUtils {
    private Dom4jUtils(){}

    /**
     * 通过文输入流获取xml的document对象
     *
     * @param inputStream	输入流
     * @return		返回文档对象
     */
    public static Document getXMLByInputStream(InputStream inputStream) {
        if (null == inputStream) {
            return null;
        }
        Document document = null;
        try {
            SAXReader reader = new SAXReader();
            document = reader.read(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }

    /**
     * 通过文件的路径获取xml的document对象
     *
     * @param path	文件的路径
     * @return		返回文档对象
     */
    public static Document getXMLByFilePath(String path) {
        if (null == path) {
            return null;
        }
        Document document = null;
        try {
            SAXReader reader = new SAXReader();
            document = reader.read(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return document;
    }

    /**
     * 通过xml字符获取document文档
     * @param xmlstr	要序列化的xml字符
     * @return			返回文档对象
     * @throws DocumentException
     */
    public static Document getXMLByString(String xmlstr) throws DocumentException {
        if(xmlstr==""||xmlstr==null){
            return null;
        }
        Document document = DocumentHelper.parseText(xmlstr);
        return document;
    }

    /**
     * 获取某个元素的所有的子节点
     * @param node	制定节点
     * @return		返回所有的子节点
     */
    public static List<Element> getChildElements(Element node) {
        if (null == node) {
            return null;
        }
        @SuppressWarnings("unchecked")
        List<Element> lists = node.elements();
        return lists;
    }

    /**
     * 获取指定节点的子节点
     * @param node			父节点
     * @param childnode		指定名称的子节点
     * @return				返回指定的子节点
     */
    public static Element getChildElement(Element node, String childnode){
        if(null==node||null == childnode||"".equals(childnode)){
            return null;
        }
        return node.element(childnode);
    }

    /**
     * 获取所有的属性值
     * @param node
     * @param arg
     * @return
     */
    public static Map<String, String> getAttributes(Element node, String...arg){
        if(node==null||arg.length==0){
            return null;
        }
        Map<String, String> attrMap = new HashMap<String, String>();
        for(String attr:arg){
            String attrValue = node.attributeValue(attr);
            attrMap.put(attr, attrValue);
        }
        return attrMap;
    }

    /**
     * 获取element的单个属性
     * @param node		需要获取属性的节点对象
     * @param attr		需要获取的属性值
     * @return			返回属性的值
     */
    public static String getAttribute(Element node, String attr){
        if(null == node||attr==null||"".equals(attr)){
            return "";
        }
        return node.attributeValue(attr);
    }

    /**
     * 添加孩子节点元素
     *
     * @param parent
     *            父节点
     * @param childName
     *            孩子节点名称
     * @param childValue
     *            孩子节点值
     * @return 新增节点
     */
    public static Element addChild(Element parent, String childName, String childValue) {
        Element child = parent.addElement(childName);// 添加节点元素
        child.setText(childValue == null ? "" : childValue); // 为元素设值
        return child;
    }

    /**
     * DOM4j的Document对象转为XML报文串
     *
     * @param document
     * @param charset
     * @return 经过解析后的xml字符串
     */
    public static String documentToString(Document document, String charset) {
        StringWriter stringWriter = new StringWriter();
        OutputFormat format = OutputFormat.createPrettyPrint();// 获得格式化输出流
        format.setEncoding(charset);// 设置字符集,默认为UTF-8
        XMLWriter xmlWriter = new XMLWriter(stringWriter, format);// 写文件流
        try {
            xmlWriter.write(document);
            xmlWriter.flush();
            xmlWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stringWriter.toString();
    }

    /**
     * 去掉声明头的(即<?xml...?>去掉)
     *
     * @param document
     * @param charset
     * @return
     */
    public static String documentToStringNoDeclaredHeader(Document document, String charset) {
        String xml = documentToString(document, charset);
        return xml.replaceFirst("\\s*<[^<>]+>\\s*", "");
    }

    /**
     * 解析XML为Document对象
     *
     * @param xml
     *            被解析的XMl
     * @return Document
     */
    public final static Element parseXml(String xml) {
        StringReader sr = new StringReader(xml);
        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(sr);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element rootElement = document != null ? document.getRootElement() : null;
        return rootElement;
    }

    /**
     * 获取element对象的text的值
     *
     * @param e
     *            节点的对象
     * @param tag
     *            节点的tag
     * @return
     */
    public final static String getText(Element e, String tag) {
        Element element = e.element(tag);
        if (element != null) {
            return element.getText();
        } else {
            return null;
        }
    }

    /**
     * 获取去除空格的字符串
     *
     * @param e
     * @param tag
     * @return
     */
    public final static String getTextTrim(Element e, String tag) {
        Element element = e.element(tag);
        if (element != null) {
            return element.getTextTrim();
        } else {
            return null;
        }
    }

    /**
     * 获取节点值.节点必须不能为空，否则抛错
     *
     * @param parent	父节点
     * @param tag		想要获取的子节点
     * @return			返回子节点
     */
    public final static String getTextTrimNotNull(Element parent, String tag) {
        Element e = parent.element(tag);
        if (e == null) {
            throw new NullPointerException("节点为空");
        } else {
            return e.getTextTrim();
        }
    }

    /**
     * 节点必须不能为空，否则抛错
     *
     * @param parent	父节点
     * @param tag		想要获取的子节点
     * @return			子节点
     */
    public final static Element elementNotNull(Element parent, String tag) {
        Element e = parent.element(tag);
        if (e == null) {
            throw new NullPointerException("节点为空");
        } else {
            return e;
        }
    }

    /**
     * 将文档对象写入对应的文件中
     * @param document		文档对象
     * @param path			写入文档的路径
     * @throws IOException
     */
    public final static void writeXMLToFile(Document document, String path) throws IOException {
        if(document==null||path==null){
            return;
        }
        XMLWriter writer = new XMLWriter(new FileWriter(path));
        writer.write(document);
        writer.close();
    }

}

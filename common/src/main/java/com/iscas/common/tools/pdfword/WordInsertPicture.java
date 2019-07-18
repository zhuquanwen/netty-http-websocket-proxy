package com.iscas.common.tools.pdfword;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class WordInsertPicture {
    public WordInsertPicture() {
    }

    // 声明一个静态的类实例化对象
    private static WordInsertPicture instance;
    // 声明word文档对象
    private Dispatch doc = null;
    // 声明word文档当前活动视窗对象
    private Dispatch activeWindow = null;
    // 声明word文档选定区域或插入点对象
    private Dispatch docSelection = null;
    // 声明所有word文档集合对象
    private Dispatch wrdDocs = null;
    // 声明word文档名称对象
    private String fileName;
    // 声明ActiveX组件对象：word.Application,Excel.Application,Powerpoint.Application等等
    private ActiveXComponent wrdCom;

    /**
     * 获取Word操作静态实例对象
     *
     * @return 报表汇总业务操作
     */
    public final static synchronized WordInsertPicture getInstance() {
        if (instance == null) {
            instance = new WordInsertPicture();
        }
        return instance;
    }

    /**
     * 初始化Word对象
     *
     * @return 是否初始化成功
     */
    public boolean initWordObj() {
        boolean retFlag = false;
        ComThread.InitSTA();// 初始化com的线程，非常重要！！使用结束后要调用 realease方法
        wrdCom = new ActiveXComponent("Word.Application");// 实例化ActiveX组件对象：对word进行操作
        try {
                /*
                 * 返回wrdCom.Documents的Dispatch
                 * 获取Dispatch的Documents对象，可以把每个Dispatch对象看成是对Activex控件的一个操作
                 * 这一步是获得该ActiveX控件的控制权。
                 */
            wrdDocs = wrdCom.getProperty("Documents").toDispatch();
            // 设置打开的word应用程序是否可见
            wrdCom.setProperty("Visible", new Variant(false));
            retFlag = true;
        } catch (Exception e) {
            retFlag = false;
            e.printStackTrace();
        }
        return retFlag;
    }

    /**
     * 创建一个新的word文档
     *
     */
    public void createNewDocument() {
        // 创建一个新的文档
        doc = Dispatch.call(wrdDocs, "Add").toDispatch();
        // 获得当前word文档文本
        docSelection = Dispatch.get(wrdCom, "Selection").toDispatch();
    }

    /**
     * 取得活动窗体对象
     *
     */
    public void getActiveWindow() {
        // 获得活动窗体对象
        activeWindow = wrdCom.getProperty("ActiveWindow").toDispatch();
    }

    /**
     * 打开一个已存在的文档
     *
     * @param docPath
     */
    public void openDocument(String docPath) {
        if (this.doc != null) {
            this.closeDocument();
        }
        this.doc = Dispatch.call(wrdDocs, "Open", docPath).toDispatch();
        this.docSelection = Dispatch.get(wrdCom, "Selection").toDispatch();
    }

    /**
     * 关闭当前word文档
     *
     */
    public void closeDocument() {
        if (this.doc != null) {
            Dispatch.call(this.doc, "Save");
            Dispatch.call(this.doc, "Close", new Variant(true));
            this.doc = null;
        }
    }

    /**
     * 文档设置图片水印
     *
     * @param waterMarkPath
     *            水印路径
     */
    public void setWaterMark(String waterMarkPath) {
        // 取得活动窗格对象
        Dispatch activePan = Dispatch.get(this.activeWindow, "ActivePane")
                .toDispatch();
        // 取得视窗对象
        Dispatch view = Dispatch.get(activePan, "View").toDispatch();
        // 打开页眉，值为9，页脚为10
        Dispatch.put(view, "SeekView", new Variant(9));
        // 获取页眉和页脚
        Dispatch headfooter = Dispatch.get(this.docSelection, "HeaderFooter")
                .toDispatch();
        // 获取水印图形对象
        Dispatch shapes = Dispatch.get(headfooter, "Shapes").toDispatch();
        // 给文档全部加上水印,设置了水印效果，内容，字体，大小，是否加粗，是否斜体，左边距，上边距。
        // 调用shapes对象的AddPicture方法将全路径为picname的图片插入当前文档
        Dispatch picture = Dispatch.call(shapes, "AddPicture", waterMarkPath)
                .toDispatch();
        // 选择当前word文档的水印
        Dispatch.call(picture, "Select");
        Dispatch.put(picture, "Left", new Variant(0));
        Dispatch.put(picture, "Top", new Variant(0));
//        Dispatch.put(picture, "Width", new Variant(200));
        Dispatch.put(picture, "Height", new Variant(780));

        // 关闭页眉
        Dispatch.put(view, "SeekView", new Variant(0));
    }

    /**
     * 关闭Word资源
     *
     *
     */
    public void closeWordObj() {
        // 关闭word文件
        wrdCom.invoke("Quit", new Variant[] {});
        // 释放com线程。根据jacob的帮助文档，com的线程回收不由java的垃圾回收器处理
        ComThread.Release();
    }

    /**
     * 得到文件名
     *
     * @return .
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 设置文件名
     *
     * @param fileName
     *            .
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 开始为word文档添加水印
     *
     * @param wordPath
     *            word文档的路径
     * @param waterMarkPath
     *            添加的水印图片路径
     * @return 是否成功添加
     */
    public boolean addWaterMark(String wordPath, String waterMarkPath) {
        try {
            if (initWordObj()) {
                openDocument(wordPath);
                getActiveWindow();
                setWaterMark(waterMarkPath);
                closeDocument();
                closeWordObj();
                return true;

            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            closeDocument();
            closeWordObj();
            return false;
        }
    }

    /**
     * 测试功能
     *
     */
    public static void main(String[] argv) {
        WordInsertPicture wordObj = WordInsertPicture.getInstance();
        boolean aa =  wordObj.addWaterMark("f:/file/word111.doc", "f:/file/111.png");
        System.out.println(aa);
    }
}

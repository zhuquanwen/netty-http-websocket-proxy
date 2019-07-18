package com.iscas.common.tools.pdfword;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/5/13 14:44
 * @since jdk1.8
 */
public class Word2Htm {

    public static boolean chageFormat(String docPath, String htmPath) {
        boolean flag = true;
        ActiveXComponent app = null;
    try {
        //创建Word对象，启动WINWORD.exe进程
        app = new ActiveXComponent("Word.Application");
        //设置用后台隐藏方式打开
        app.setProperty("Visible", new Variant(false));
        //获取操作word的document调用
        Dispatch documents = app.getProperty("Documents").toDispatch();
        //调用打开命令，同时传入word路径
        Dispatch doc = Dispatch.call(documents, "Open", docPath).toDispatch();
        //调用另外为命令，同时传入html的路径
        Dispatch.invoke(doc, "SaveAs", Dispatch.Method,
                new Object[]{htmPath, new Variant(8)}, new int[1]);
        //关闭document对象
        Dispatch.call(doc, "Close", new Variant(0));
    //清空对象
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        //关闭WINWORD.exe进程
        Dispatch.call(app, "Quit");
    }
        return flag;
    }

    public static void main(String[] args) {
        String file = "C:\\Users\\Administrator\\Desktop\\南海专项问题20190311.docx";
        chageFormat(file, "e:/wordhtm/a.htm");
    }
}

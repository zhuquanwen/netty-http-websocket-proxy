package com.iscas.templet.view.tree;

import lombok.Data;
import lombok.ToString;

import java.io.*;
import java.util.List;

/**
 * @Author: zhuquanwen
 * @Description:
 * @Date: 2017/12/25 17:21
 * @Modified:
 **/
@Data
@ToString(callSuper = true)
public class TreeResponseData<T> implements Serializable, Cloneable {
    /*显示名称*/
    protected String label;
    /*对应ID*/
    protected Object id;
    /*是否展开*/
    protected Boolean expanded = false;
    /*是否可选*/
    protected Boolean selectable = true;
    /*是否选中*/
    protected Boolean selected = false;
    /*子节点*/
    protected List<TreeResponseData> children;
    /**前台Path*/
    protected String path;

    /*当前节点对应的值(实体对象值)*/
    protected T data;


    @Override
    public TreeResponseData clone() {

        ByteArrayOutputStream byteOut = null;
        ObjectOutputStream objOut = null;
        ByteArrayInputStream byteIn = null;
        ObjectInputStream objIn = null;
        try {
            byteOut = new ByteArrayOutputStream();
            objOut = new ObjectOutputStream(byteOut);
            objOut.writeObject(this);
            byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            objIn = new ObjectInputStream(byteIn);
            return (TreeResponseData) objIn.readObject();
        } catch (IOException e) {
            throw new RuntimeException("Clone Object failed in IO.",e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found.",e);
        } finally{
            try{
                byteIn = null;
                byteOut = null;
                if(objOut != null) {
                    objOut.close();
                }
                if(objIn != null) {
                    objIn.close();
                }
            }catch(IOException e){
            }
        }
    }


//    public TreeResponseData clone() throws CloneNotSupportedException{
//        TreeResponseData o = (TreeResponseData) super.clone();//Object 中的clone()识别出你要复制的是哪一个对象。
//        return o;
//    }
}

package com.iscas.templet.view.table;

import lombok.Data;
import lombok.ToString;

import java.io.*;
import java.util.List;

/**
 * @Author: zhuquanwen
 * @Description:
 * @Date: 2017/12/25 17:04
 * @Modified:
 **/
@Data
@ToString(callSuper = true)
public class TableHeaderResponseData implements Serializable {
    /*表头列信息*/
    protected List<TableField> cols;
    /*表的一些设置信息*/
    protected TableSetting setting;

    public List<TableField> getCols() {
        return cols;
    }
    //remove by zqm 2018.09.12
//    public Map<String, TableField> getColMap(){
//        if(cols == null){
//            return new HashMap<>();
//        }
//        return cols.stream().collect(Collectors.toMap(TableField::getField, tableField -> tableField));
//    }


    @Override
    public TableHeaderResponseData clone() {

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
            return (TableHeaderResponseData) objIn.readObject();
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
}

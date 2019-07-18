package com.iscas.templet.view.chart.base;

import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zhuquanwen
 * @Description:
 * @Date: 2018/4/11 11:33
 * @Modified:
 **/
@Getter
@Setter
@Deprecated
public class ChartResponseData implements Serializable, Cloneable {
    protected ChartTitle title = new ChartTitle();
    protected ChartAxis xAxis = new ChartAxis();
    protected ChartAxis yAxis = new ChartAxis();
    protected List<ChartSeries> series = new ArrayList<>();

    protected Legend legend = new Legend();
    protected Object others;

    @Override
    public ChartResponseData clone() {
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
            return (ChartResponseData) objIn.readObject();
        } catch (IOException e) {
            throw new RuntimeException("Clone Object failed in IO.",e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found.",e);
        } finally {
            try {
                byteIn = null;
                byteOut = null;
                if (objOut != null) {
                    objOut.close();
                }
                if (objIn != null) {
                    objIn.close();
                }
            } catch (IOException e) {
            }
        }
    }

}

package com.iscas.cs.server.unproxy.self.web.utils;

import java.math.BigDecimal;

public class DataUtils {

    public static double formatDouble(double value){
        return formatDouble( value , 3);
    }
    public static double formatDouble(double value , int count){
        BigDecimal bigDecimal   =   new   BigDecimal(value);
        return  bigDecimal.setScale(3,   BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}

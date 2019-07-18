package com.iscas.cs.server.unproxy.self.web.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ReadLine {
    public static List<String> readLine(){
        List<String> moniterList = new ArrayList<>();
        URL path = ReadLine.class.getClassLoader().getResource("config/redisMonitor.txt");
        File file=new File(path.getPath());
        BufferedReader reader=null;
        String temp=null;
        int line=1;
        try{
            reader=new BufferedReader(new FileReader(file));
            while((temp=reader.readLine())!=null){
                moniterList.add(temp);
                line++;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            if(reader!=null){
                try{
                    reader.close();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        return moniterList;
    }
}

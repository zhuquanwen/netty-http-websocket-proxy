package com.iscas.cs.server.unproxy.self.web.utils;

import java.net.InetAddress;

/**
 * Created by ISCAS on 2017/1/12.
 */
public class SystemUtils {

	/**
	 * 检查是否为linux操作系统
	 * @return
	 */
	public static boolean isLinuxSystem(){
		if( System.getProperty("os.name").toLowerCase().contains("win")) {
			return false;
		}

		return true;
	}

	/**
	 * 获取本机IP
	 * @return
	 */
	public static String getHostIP() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			return addr.getHostAddress().toString();//获得本机IP
		}catch (Exception e){
			e.printStackTrace();
			return "";
		}
	}
}

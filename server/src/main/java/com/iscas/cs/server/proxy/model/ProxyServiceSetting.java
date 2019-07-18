package com.iscas.cs.server.proxy.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/6/10 15:39
 * @since jdk1.8
 */
@Data
public class ProxyServiceSetting {
	private int port = 8080;
	/**缓存服务前缀*/
	private String basePath;
	/**缓存页面服务的前缀*/
	private String selfWebPath;
	private List<ProxySetting> servletSettings = new ArrayList<>();

	
}

package com.iscas.cs.server.proxy.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/6/10 15:42
 * @since jdk1.8
 */
@Data
@Accessors(chain = true)
public class ProxySetting {

	/**代理key*/
	private String proxyUrl;

	/**目标服务URL*/
	private String targetUrl;

	/**健康监测心跳URL*/
	private String healthUrl;

	/**url前缀，包括协议、域名、端口，不用配置用程序获取*/
	private String urlPrefix;

	/**
	 * 应用名字
	 * */
	private String name;

	/**获取缓存数据策略*/
	private String getDataCache = "3";

	/**数据缓存策略*/
	private String dataCache = "1";

	/**数据回放策略*/
	private String dataCachePlayback = "1";
	@Override
	public String toString() {
		
		return String.format("proxyUrl:%s,targetUrl:%s,healthUrl:s%", proxyUrl,targetUrl, healthUrl);
	}
	
}

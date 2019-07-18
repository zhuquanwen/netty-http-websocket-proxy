package com.iscas.cs.server.proxy.util;


import com.iscas.common.tools.url.URLUtils;
import com.iscas.cs.server.proxy.base.Constant;
import com.iscas.cs.server.proxy.model.*;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * 目前先放在XML配置文件中
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/6/10 15:55
 * @since jdk1.8
 */
@Slf4j
public final class ConfigUtils {
	private ConfigUtils() {
		
	}
	private static final String ELEMENT_DEFAULT_SETTING = "defaultSettings";
	private static final String ELEMENT_SELFWEBPATH = "selfWebPath";
	private static final String ELEMENT_PORT = "port";
	private static final String ELEMENT_BASEPATH = "basePath";
	private static final String ELEMENT_PROXYSERVICES = "ProxyServices";
	private static final String ELEMENT_PROXYURL = "proxyUrl";
	private static final String ELEMENT_TARGETURL = "targetUrl";
	private static final String ELEMENT_HEALTHURL = "healthUrl";
	private static final String ELEMENT_APPNAME = "name";
	private static final String ELEMENT_TACTICS = "tactics";
	private static final String ELEMENT_GET_DATA_CACHE = "get-data-cache";
	private static final String ELEMENT_DATA_CACHE = "data-cache";
	private static final String ELEMENT_DATA_CACHE_PLAYBACK = "data-cache-playback";
	private static final String ELEMENT_DOWNFILE_PATH = "downfile-path";
	private static final String ELEMENT_UPLOAD_PATH = "uploadfile-path";
	private static final String ELEMENT_JS_COMPRESS = "js-compress-tactics";
	private static final String ELEMENT_CSS_COMPRESS = "css-compress-tactics";
	private static final String ELEMENT_HTML_COMPRESS = "html-compress-tactics";
	private static final String ELEMENT_PLAYBACK_THRESHOLD_SIZE = "playback-threshold-size";
	private static final String ELEMENT_PLAYBACK_PATH = "playback-path";
	private static final String DLL_PATH = "dllPath";
	private static final String ELEMENT_JETTY_MAX_THREADS = "maxThreads";
	private static final String ELEMENT_JETTY_MIN_THREADS = "minThreads";
	private static final String ELEMENT_JETTY_IDLETIMEOUT = "idleTimeout";

	private volatile static CacheTactics cacheTactics = null;

	private static InputStream getInOutConfigStream(String uri) throws FileNotFoundException {
		String filePath = System.getProperty("user.dir") + uri;
		File file = new File(filePath);
		if (file.exists()) {
			return new FileInputStream(file);
		} else {
			return ConfigUtils.class.getResourceAsStream(uri);
		}
	}

	public static ProxyServiceSetting readProxyServiceConfig() throws Exception {
		return readProxyServiceConfig("/proxySetting.xml");
	}


		public static ProxyServiceSetting readProxyServiceConfig(String uri) throws Exception {
		if (Constant.PROXY_SERVICE_SETTING == null) {
			synchronized (ProxyServiceSetting.class) {
				if (Constant.PROXY_SERVICE_SETTING == null) {
					Constant.PROXY_SERVICE_SETTING = new ProxyServiceSetting();
					@Cleanup InputStream inputStream = getInOutConfigStream(uri);
					SAXReader reader = new SAXReader();
					Document doc = reader.read(inputStream);
					Element root = doc.getRootElement();
					System.err.println(root.getName());
					Element defaultSetting = root.element(ELEMENT_DEFAULT_SETTING);
					int port = Integer.parseInt(defaultSetting.element(ELEMENT_PORT).getTextTrim());
					String basePath = defaultSetting.elementTextTrim(ELEMENT_BASEPATH);
					String selfWebPath = defaultSetting.elementTextTrim(ELEMENT_SELFWEBPATH);

					Element servlets = root.element(ELEMENT_PROXYSERVICES);
					Iterator<Element> servletIterator = servlets.elementIterator();
					List<ProxySetting> servletSettings = new ArrayList<>();
					while (servletIterator.hasNext()) {
						ProxySetting servletSetting = new ProxySetting();
						Element servlet = servletIterator.next();
						String proxyUrl = servlet.elementTextTrim(ELEMENT_PROXYURL);
						String targetUrl = servlet.elementTextTrim(ELEMENT_TARGETURL);
						String healthUrl = servlet.elementTextTrim(ELEMENT_HEALTHURL);
						String name = servlet.elementTextTrim(ELEMENT_APPNAME);
						Element tactics = servlet.element(ELEMENT_TACTICS);
						if (tactics != null) {
							Iterator<Element> iterator = tactics.elementIterator();
							while (iterator.hasNext()) {
								Element element = iterator.next();
								String getDataCache = element.elementTextTrim(ELEMENT_GET_DATA_CACHE);
								String dataCache = element.elementTextTrim(ELEMENT_DATA_CACHE);
								String dataCachePlayback = element.elementTextTrim(ELEMENT_DATA_CACHE_PLAYBACK);
								if (StringUtils.isNotEmpty(getDataCache)) {
									servletSetting.setGetDataCache(getDataCache);
								}
								if (StringUtils.isNotEmpty(dataCache)) {
									servletSetting.setDataCache(dataCache);
								}
								if (StringUtils.isNotEmpty(dataCachePlayback)) {
									servletSetting.setDataCachePlayback(dataCachePlayback);
								}
							}
						}
						servletSetting.setProxyUrl(proxyUrl)
								.setTargetUrl(targetUrl)
								.setHealthUrl(healthUrl)
								.setName(name);
						servletSettings.add(servletSetting);

					}
					Constant.PROXY_SERVICE_SETTING.setPort(port);
					Constant.PROXY_SERVICE_SETTING.setBasePath(basePath);
					Constant.PROXY_SERVICE_SETTING.setServletSettings(servletSettings);
					Constant.PROXY_SERVICE_SETTING.setSelfWebPath(selfWebPath);
					//将ServletSettings转为map
					if (CollectionUtils.isNotEmpty(servletSettings)) {
						Constant.PROXY_SERVLET_SETTING_MAP = servletSettings.stream()
								.map(ss -> {
									String targetUrl = ss.getTargetUrl();
									try {
										String prefix = URLUtils.prefixUrl(targetUrl);
										ss.setUrlPrefix(prefix);
									} catch (Exception e) {

									}
									return ss;
								}).collect(Collectors.toMap(ProxySetting::getProxyUrl, a -> a));
					}
				}
			}
		}

		return Constant.PROXY_SERVICE_SETTING;
	}

	public static CacheTactics realGetCacheTacticsProps() {
		cacheTactics = new CacheTactics();
		Properties props = new Properties();
		try {
			log.debug("读取redis缓存策略配置文件:cache-tactics.properties");
//			@Cleanup InputStream is = ConfigUtils.class.getResourceAsStream("/cache-tactics.properties");
			@Cleanup InputStream is = getInOutConfigStream("/cache-tactics.properties");
			props.load(is);
		} catch (IOException e) {
			log.error("获取缓存数据配置策略出错", e);
			throw new RuntimeException(e);
		}
//					String dataCacheTactics = props.getProperty("data-cache-tactics");
//					String playbackTactics = props.getProperty("data-cache-playback-tactics");
//					String getDataCacheTactics = props.getProperty("get-data-cache-tactics");
		String jsCompressTactics = props.getProperty(ELEMENT_JS_COMPRESS);
		String cssCompressTactics = props.getProperty(ELEMENT_CSS_COMPRESS);
		String htmlCompressTactics = props.getProperty(ELEMENT_HTML_COMPRESS);
		String downfilePath = props.getProperty(ELEMENT_DOWNFILE_PATH);
		String uploadfilePath = props.getProperty(ELEMENT_UPLOAD_PATH);
		String playbackThresholdSize = props.getProperty(ELEMENT_PLAYBACK_THRESHOLD_SIZE);
		String playbackPath = props.getProperty(ELEMENT_PLAYBACK_PATH);
		String dllPath = props.getProperty(DLL_PATH);
//					cacheTactics.setDataCacheTactics(dataCacheTactics);
//					cacheTactics.setPlaybackTactics(playbackTactics);
//					cacheTactics.setGetDataCacheTactics(getDataCacheTactics);
		cacheTactics.setJsCompressTactics(jsCompressTactics)
		        .setCssCompressTactics(cssCompressTactics)
		        .setHtmlCompressTactics(htmlCompressTactics)
		        .setDownFilePath(downfilePath)
		        .setUploadFilePath(uploadfilePath)
		        .setPlaybackThresholdSize(Integer.valueOf(playbackThresholdSize))
                .setPlaybackThresholdByteSize(Integer.valueOf(playbackThresholdSize) * 1024 * 1024)
		        .setPlaybackPath(playbackPath).setDllPath(dllPath);
		return cacheTactics;
	}

	/**
	 * 获取缓存策略
	 * */
	public static CacheTactics getCacheTacticsProps() {
		if (cacheTactics == null) {
			synchronized (ConfigUtils.class) {
				if (cacheTactics == null) {
					cacheTactics = realGetCacheTacticsProps();
				}
			}
		}
		return cacheTactics;
	}

	/**
	 * 获取HttpClient配置
	 * */
	public static HttpClientProps readHttpClientProps() {
		HttpClientProps httpClientProps = null;
		Properties props = new Properties();
		try {
			log.debug("读取httpclient配置文件:httpclient.properties");
//			@Cleanup InputStream is = ConfigUtils.class.getResourceAsStream("/httpclient.properties");
			@Cleanup InputStream is = getInOutConfigStream("/httpclient.properties");
			props.load(is);
		} catch (IOException e) {
			log.error("获取httpclient配置文件出错", e);
			throw new RuntimeException(e);
		}
		httpClientProps = new HttpClientProps();
		String doHandleRedirects = props.getProperty("doHandleRedirects");
		String cookieSpecs = props.getProperty("cookieSpecs");
		String connectTimeout = props.getProperty("connectTimeout");
		String readTimeout = props.getProperty("readTimeout");
		String connectionRequestTimeout = props.getProperty("connectionRequestTimeout");
		String maxTotal = props.getProperty("maxTotal");
		String maxPerRoute = props.getProperty("maxPerRoute");
		String tcpNoDelay = props.getProperty("tcpNoDelay");
		String soReuseAddress = props.getProperty("soReuseAddress");
		String soTimeout = props.getProperty("soTimeout");
		String soLinger = props.getProperty("soLinger");
		String retry = props.getProperty("retry");
		httpClientProps.setDoHandleRedirects(Boolean.valueOf(doHandleRedirects))
				.setCookieSpecs(cookieSpecs)
				.setConnectTimeout(Integer.valueOf(connectTimeout))
				.setReadTimeout(Integer.valueOf(readTimeout))
				.setConnectionRequestTimeout(Integer.valueOf(connectionRequestTimeout))
				.setMaxTotal(Integer.valueOf(maxTotal))
				.setMaxPerRoute(Integer.valueOf(maxPerRoute))
				.setTcpNoDelay(Boolean.valueOf(tcpNoDelay))
				.setSoReuseAddress(Boolean.valueOf(soReuseAddress))
				.setSoTimeout(Integer.valueOf(soTimeout))
				.setSoLinger(Integer.valueOf(soLinger))
				.setRetry(Integer.valueOf(retry));
		return httpClientProps;
	}

	/**
	 * 获取返回html格式模板
	 * */
	public static String getResponseHtml() throws IOException {
		if (Constant.CS_RES_HTML == null) {
			synchronized (ConfigUtils.class) {
				if (Constant.CS_RES_HTML == null) {
					Constant.CS_RES_HTML = getFileStr("/response/response.html");
				}
			}
		}
		return Constant.CS_RES_HTML;
	}

	/**
	 * 获取返回xml格式模板
	 * */
	public static String getResponseXml() throws IOException {
		if (Constant.CS_RES_XML == null) {
			synchronized (ConfigUtils.class) {
				if (Constant.CS_RES_XML == null) {
					Constant.CS_RES_XML = getFileStr("/response/response.xml");
				}
			}
		}
		return Constant.CS_RES_XML;
	}

	/**
	 * 获取返回json格式模板
	 * */
	public static String getResponseJson() throws IOException {
		if (Constant.CS_RES_JSON == null) {
			synchronized (ConfigUtils.class) {
				if (Constant.CS_RES_JSON == null) {
					Constant.CS_RES_JSON = getFileStr("/response/response.json");
				}
			}
		}
		return Constant.CS_RES_JSON;
	}

	private static String getFileStr(String filePath) throws IOException {
		StringBuilder result = new StringBuilder();
//		@Cleanup InputStream is = ConfigUtils.class.getResourceAsStream(filePath);
		@Cleanup InputStream is = getInOutConfigStream(filePath);
		@Cleanup InputStreamReader isr = new InputStreamReader(is);
		@Cleanup BufferedReader br = new BufferedReader(isr);
		String line = null;
		while ((line = br.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
	}

	/**
	 * 读取jetty配置信息
	 * */
	public static JettyProps readJettyProps() {
		if (Constant.JETTY_PROPS == null) {
			synchronized (JettyProps.class) {
				if (Constant.JETTY_PROPS == null) {
					Constant.JETTY_PROPS = new JettyProps();
					Properties props = new Properties();
					try {
						log.debug("读取jetty配置文件:jetty.properties");
						@Cleanup InputStream is = getInOutConfigStream("/jetty.properties");
						props.load(is);
					} catch (IOException e) {
						log.error("获取jetty配置文件出错", e);
						throw new RuntimeException(e);
					}
					Constant.JETTY_PROPS.setMaxThreads(Integer.valueOf(props.getProperty(ELEMENT_JETTY_MAX_THREADS)));
					Constant.JETTY_PROPS.setMinThreads(Integer.valueOf(props.getProperty(ELEMENT_JETTY_MIN_THREADS)));
					Constant.JETTY_PROPS.setIdleTimeout(Integer.valueOf(props.getProperty(ELEMENT_JETTY_IDLETIMEOUT)));
				}
			}
		}
		return Constant.JETTY_PROPS;
	}
}

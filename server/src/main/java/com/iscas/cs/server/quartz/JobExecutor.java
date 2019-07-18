package com.iscas.cs.server.quartz;

import com.iscas.cs.server.proxy.base.Constant;
import com.iscas.cs.server.proxy.model.ProxySetting;
import com.iscas.cs.server.proxy.model.ServerHealth;
import com.iscas.cs.server.proxy.netty.service.IPlaybackService;
import com.iscas.cs.server.proxy.netty.service.PlaybackService;
import com.iscas.cs.server.proxy.okhttp.OkHttpCustomClient;
import com.iscas.cs.server.proxy.util.ConfigUtils;
import com.iscas.cs.server.proxy.util.json.JsonUtils;
import com.iscas.cs.server.redis.RedisConn;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.io.SAXReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/5 8:55
 * @since jdk1.8
 */
@Slf4j
public class JobExecutor {
    private OkHttpCustomClient okHttpCustomClient;
    private IPlaybackService playbackService;
    public JobExecutor() {
        //TODO 暂时使用默认配置
        okHttpCustomClient = OkHttpCustomClient.getInstance();
        playbackService = PlaybackService.getInstance();
    }

    public static JobExecutor getInstance() {
        return JobExecutorHolder.jobExecutor;
    }

    public void execute() {
        synchronized (this) {
            //重写读取配置文件，路由，缓存策略等
            try {
                ConfigUtils.readProxyServiceConfig();
                ConfigUtils.getCacheTacticsProps();
                ConfigUtils.realGetCacheTacticsProps();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for(ProxySetting proxySetting: Constant.PROXY_SERVLET_SETTING_MAP.values()) {
            String proxyUrl = proxySetting.getProxyUrl();
            String healthUrl = proxySetting.getHealthUrl();
            if (!Constant.SERVER_HEALTH_MAP.containsKey(proxyUrl)) {
                ServerHealth serverHealth = new ServerHealth();
                Constant.SERVER_HEALTH_MAP.put(proxyUrl, serverHealth);
            }

            try {
                okHttpCustomClient.doGetAsyn(healthUrl, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        log.warn("获取{}健康状况失败", healthUrl);
                        Constant.SERVER_HEALTH_MAP.get(proxyUrl).setHealth("-1");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        analyzeHealthResult(response, proxyUrl);
                    }

                });
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (IOException e) {
//                e.printStackTrace();
                log.warn("定时获取服务健康状况出错");
                Constant.SERVER_HEALTH_MAP.get(proxyUrl).setHealth("-1");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void analyzeHealthResult(Response response, String proxyUrl) {
        try {
            MediaType mediaType = response.body().contentType();
            String media = mediaType.toString();
            String result = response.body().string();
            String health = null, version = null, cacheRefresh = null;
            if (media != null && (media.contains("application/json") || media.contains("text/plain"))) {
                log.debug("处理{}的json结果", proxyUrl);
                //处理JSON结果
                Map<String, Object> map = JsonUtils.fromJson(result, Map.class);
                health = String.valueOf(map.get("health"));
                version = (String) map.get("version");
                cacheRefresh = String.valueOf(map.get("cache_refresh"));
                if (health == null || version == null || cacheRefresh == null) {
                    log.warn("解析{}健康状况,未解析到需要的字段值", proxyUrl);
                    Constant.SERVER_HEALTH_MAP.get(proxyUrl).setHealth("-1");
                    return;
                }
            } else if (media != null && media.contains("text/html")) {
                log.debug("处理{}的html结果", proxyUrl);
                Document document = Jsoup.parse(result);
                Element body = document.body();
                Elements divElements = body.getElementsByTag("div");
                if (CollectionUtils.isNotEmpty(divElements) && divElements.size() >= 3) {
                    health = StringUtils.substringAfter(divElements.first().text(),"health:");
                    version = StringUtils.substringAfter(divElements.get(1).text(), "version:");
                    cacheRefresh = StringUtils.substringAfter(divElements.get(2).text(),"cache_refresh:");
                }
                if (health == null || version == null || cacheRefresh == null) {
                    log.warn("解析{}健康状况,未解析到需要的字段值", proxyUrl);
                    Constant.SERVER_HEALTH_MAP.get(proxyUrl).setHealth("-1");
                    return;
                }
            } else if (media != null && media.contains("text/xml")) {
                log.debug("处理{}的xml结果", proxyUrl);
                SAXReader reader=new SAXReader();
                org.dom4j.Document doc = reader.read(result);
                org.dom4j.Element rootElement = doc.getRootElement();
                org.dom4j.Element healthElement = rootElement.element("health");
                org.dom4j.Element versionElement = rootElement.element("version");
                org.dom4j.Element refreshElement = rootElement.element("cache_refresh");
                if (healthElement != null) {
                    health = healthElement.getTextTrim();
                }
                if (versionElement != null) {
                    version = versionElement.getTextTrim();
                }
                if (refreshElement != null) {
                    cacheRefresh = refreshElement.getTextTrim();
                }
                if (health == null || version == null || cacheRefresh == null) {
                    log.warn("解析{}健康状况,未解析到需要的字段值", proxyUrl);
                    Constant.SERVER_HEALTH_MAP.get(proxyUrl).setHealth("-1");
                    return;
                }
            } else {
                Constant.SERVER_HEALTH_MAP.get(proxyUrl).setHealth("-1");
                log.warn("解析{}时出现未实现的MediaType：{}", proxyUrl, media);
            }
            synchronized (proxyUrl.intern()) {

                String key = StringUtils.substringBeforeLast(proxyUrl, "*");
                ServerHealth serverHealth = Constant.SERVER_HEALTH_MAP.get(proxyUrl);
                if (!Objects.equals("1", health)) {
                    //如果当前服务不可用,把可能在回放的线程关掉，防止无谓的请求
                    ExecutorService executorService = Constant.PLAYBACK_THREADPOOL_MAP.get(key);
                    if (executorService != null && !executorService.isShutdown()) {
                        executorService.shutdown();
                        Constant.PLAYBACK_THREADPOOL_MAP.remove(key);
                    }
                }

                String oldHealth = serverHealth.getHealth();
                boolean playbackFlag = false;
                if ((oldHealth == null || Objects.equals("-1", oldHealth)) && Objects.equals("1", health)) {
                    playbackFlag = true;
                }
                if (playbackFlag) {
                    //服务由中断状态变为良好
                    //TODO 这样处理是否存在问题？每个服务相当于都开了一个single线程池，后面优化?
                    ExecutorService executorService = Constant.PLAYBACK_THREADPOOL_MAP.get(key);
                    if (executorService == null || executorService.isShutdown()) {
                        executorService = Executors.newSingleThreadExecutor();

                        Constant.PLAYBACK_THREADPOOL_MAP.put(key, executorService);
                    }
                    executorService.submit(() -> {
                        log.info("{}服务状态由中断切换为良好，开始回放请求", key);
                        playbackService.playback(key);
                    });
                }

                serverHealth.setHealth(health);
                String oldVs = serverHealth.getVersion();

                if (oldVs != null && !Objects.equals(oldVs, version) && Objects.equals("1", cacheRefresh)) {
                    //服务重启了，需要清除此服务对应的所有缓存
                    StringBuilder keyPrefix = new StringBuilder();
                    keyPrefix.append("res:").append(StringUtils.substringBeforeLast(proxyUrl, "/")).append(":").append("*");
                    RedisConn.getClient().deleteByPattern(keyPrefix.toString());
                    log.info("服务:{}对应的缓存已经清除", proxyUrl);
                }
                serverHealth.setProxyUrl(proxyUrl);
                serverHealth.setVersion(version);
                serverHealth.setCacheRefresh(cacheRefresh);
            }
        } catch (Exception e) {
            log.warn("解析{}健康状况出错", proxyUrl);
            Constant.SERVER_HEALTH_MAP.get(proxyUrl).setHealth("-1");
        }
    }


    static class JobExecutorHolder {
        public static final JobExecutor jobExecutor = new JobExecutor();
    }
}

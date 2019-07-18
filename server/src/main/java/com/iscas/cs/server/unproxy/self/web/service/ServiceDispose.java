package com.iscas.cs.server.unproxy.self.web.service;

import com.iscas.common.tools.url.URLUtils;
import com.iscas.cs.server.proxy.base.Constant;
import com.iscas.cs.server.proxy.model.ProxySetting;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceDispose {



    public boolean updateTactics() {

        List<ProxySetting> servletSettings = new ArrayList<>();

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

        return true;
    }

}

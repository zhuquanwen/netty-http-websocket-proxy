package com.iscas.cs.server;

import ch.qos.logback.core.joran.spi.JoranException;
import com.iscas.cs.server.bean.BeanRegister;
import com.iscas.cs.server.log.LogConfigInit;
import com.iscas.cs.server.proxy.netty.NettyServer;
import com.iscas.cs.server.quartz.CronTriggerHelper;
import com.iscas.cs.server.quartz.JobExecutor;
import com.iscas.cs.server.unproxy.self.web.common.WebBinding;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/16 20:57
 * @since jdk1.8
 */
@Slf4j
public class App {

    public static void main(String[] args) throws Exception {
        LogConfigInit.loadFromResource();

        //注册所有bean
        BeanRegister.registerAll("com.iscas.cs.server");
        log.info("所有bean都已注册");

        //绑定本服务页面路由（监控等等）
        WebBinding.bindMethodAndPath("com.iscas.cs.server.unproxy.self.web.controller");
        log.info("本地服务路由信息已注册");

        //定时任务启动
        JobExecutor.getInstance().execute();
        CronTriggerHelper.trigger();
        log.info("定时任务已启动...");

        NettyServer nettyServer = BeanRegister.get(NettyServer.class);
        nettyServer.start();
//        log.info("netty服务启动");
    }
}

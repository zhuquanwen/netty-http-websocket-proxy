package com.iscas.cs.server.proxy.netty;

import com.iscas.cs.server.bean.Autowired;
import com.iscas.cs.server.bean.Component;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/16 20:57
 * @since jdk1.8
 */
@Slf4j
@Component

public class NettyServer {
    @Autowired
    private SocketHandler socketHandler;

    public void start() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try{
            bootstrap.group(boss, worker);
            bootstrap.option(ChannelOption.SO_BACKLOG, 2048);
            bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel ch) throws Exception {

                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new HttpServerCodec());// Http消息编码解码
                    pipeline.addLast(new HttpObjectAggregator(1024*1024*1024));
                    pipeline.addLast(new ChunkedWriteHandler());
                    pipeline.addLast(socketHandler);//自定义处理类

                }
            });
            ChannelFuture future = bootstrap.bind(8180);
            log.debug("Netty服务已启动");
            future.channel().closeFuture().sync();

        }catch (Exception e){
            e.printStackTrace();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }


}

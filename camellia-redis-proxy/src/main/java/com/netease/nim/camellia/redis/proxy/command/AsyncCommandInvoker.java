package com.netease.nim.camellia.redis.proxy.command;

import com.netease.nim.camellia.redis.proxy.netty.GlobalRedisProxyEnv;
import com.netease.nim.camellia.redis.proxy.auth.AuthCommandProcessor;
import com.netease.nim.camellia.redis.proxy.conf.CamelliaServerProperties;
import com.netease.nim.camellia.redis.proxy.conf.CamelliaTranspondProperties;
import com.netease.nim.camellia.redis.proxy.monitor.*;
import com.netease.nim.camellia.redis.proxy.netty.ChannelInfo;
import com.netease.nim.camellia.redis.proxy.plugin.DefaultProxyPluginFactory;
import com.netease.nim.camellia.redis.proxy.upstream.AsyncCamelliaRedisTemplateChooser;
import com.netease.nim.camellia.redis.proxy.util.ConfigInitUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.FastThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 *
 * Created by caojiajun on 2019/12/12.
 */
public class AsyncCommandInvoker implements CommandInvoker {

    private static final Logger logger = LoggerFactory.getLogger(AsyncCommandInvoker.class);

    private final AsyncCamelliaRedisTemplateChooser chooser;
    private final CommandInvokeConfig commandInvokeConfig;

    public AsyncCommandInvoker(CamelliaServerProperties serverProperties, CamelliaTranspondProperties transpondProperties) {
        this.chooser = new AsyncCamelliaRedisTemplateChooser(transpondProperties, serverProperties.getProxyBeanFactory());
        GlobalRedisProxyEnv.chooser = chooser;

        MonitorCallback monitorCallback = ConfigInitUtil.initMonitorCallback(serverProperties);
        ProxyMonitorCollector.init(serverProperties, monitorCallback);

        AuthCommandProcessor authCommandProcessor = new AuthCommandProcessor(ConfigInitUtil.initClientAuthProvider(serverProperties));
        DefaultProxyPluginFactory proxyPluginFactory = new DefaultProxyPluginFactory(serverProperties.getPlugins(), serverProperties.getProxyBeanFactory());
        this.commandInvokeConfig = new CommandInvokeConfig(authCommandProcessor, proxyPluginFactory);
    }

    private static final FastThreadLocal<CommandsTransponder> threadLocal = new FastThreadLocal<>();

    @Override
    public void invoke(ChannelHandlerContext ctx, ChannelInfo channelInfo, List<Command> commands) {
        if (commands.isEmpty()) return;
        try {
            CommandsTransponder trandponder = threadLocal.get();
            if (trandponder == null) {
                trandponder = new CommandsTransponder(chooser, commandInvokeConfig);
                logger.info("CommandsTransponder init success");
                threadLocal.set(trandponder);
            }
            trandponder.transpond(channelInfo, commands);
        } catch (Exception e) {
            ctx.close();
            logger.error(e.getMessage(), e);
        }
    }
}

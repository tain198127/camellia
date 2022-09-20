package com.netease.nim.camellia.redis.proxy.plugin.bigkey;

import com.netease.nim.camellia.redis.proxy.command.Command;
import com.netease.nim.camellia.redis.proxy.conf.ProxyDynamicConf;
import com.netease.nim.camellia.redis.proxy.monitor.ProxyMonitorCollector;
import com.netease.nim.camellia.redis.proxy.plugin.*;
import com.netease.nim.camellia.redis.proxy.util.BeanInitUtils;

/**
 * Created by caojiajun on 2022/9/13
 */
public class BigKeyProxyPlugin implements ProxyPlugin {

    private BigKeyHunter bigKeyHunter;

    @Override
    public void init(ProxyBeanFactory factory) {
        String callbackClassName = ProxyDynamicConf.getString("big.key.monitor.callback.className", DummyBigKeyMonitorCallback.class.getName());
        Class<?> clazz = BeanInitUtils.parseClass(callbackClassName);
        BigKeyMonitorCallback callback = (BigKeyMonitorCallback) factory.getBean(clazz);
        bigKeyHunter = new BigKeyHunter(callback);
    }

    @Override
    public ProxyPluginOrder order() {
        return new ProxyPluginOrder() {
            @Override
            public int request() {
                return BuildInProxyPluginEnum.BIG_KEY_PLUGIN.getRequestOrder();
            }

            @Override
            public int reply() {
                return BuildInProxyPluginEnum.BIG_KEY_PLUGIN.getReplyOrder();
            }
        };
    }

    @Override
    public ProxyPluginResponse executeRequest(ProxyRequest request) {
        //属于监控类plugin，因此也受isMonitorEnable控制
        if (!ProxyMonitorCollector.isMonitorEnable()) return ProxyPluginResponse.SUCCESS;
        bigKeyHunter.checkRequest(request.getCommand());
        return ProxyPluginResponse.SUCCESS;
    }

    @Override
    public ProxyPluginResponse executeReply(ProxyReply reply) {
        //属于监控类plugin，因此也受isMonitorEnable控制
        if (!ProxyMonitorCollector.isMonitorEnable()) return ProxyPluginResponse.SUCCESS;
        Command command = reply.getCommand();
        if (command == null) return ProxyPluginResponse.SUCCESS;
        bigKeyHunter.checkReply(reply.getCommand(), reply.getReply());
        return ProxyPluginResponse.SUCCESS;
    }
}

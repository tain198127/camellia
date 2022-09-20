package com.netease.nim.camellia.redis.proxy.util;

import com.netease.nim.camellia.redis.proxy.ProxyDiscoveryFactory;
import com.netease.nim.camellia.redis.proxy.auth.ClientAuthByConfigProvider;
import com.netease.nim.camellia.redis.proxy.auth.ClientAuthProvider;
import com.netease.nim.camellia.redis.proxy.conf.CamelliaServerProperties;
import com.netease.nim.camellia.redis.proxy.conf.CamelliaTranspondProperties;
import com.netease.nim.camellia.redis.proxy.monitor.MonitorCallback;
import com.netease.nim.camellia.redis.proxy.plugin.ProxyBeanFactory;


/**
 * Created by caojiajun on 2020/10/22
 */
public class ConfigInitUtil {

    public static ClientAuthProvider initClientAuthProvider(CamelliaServerProperties serverProperties) {
        String className = serverProperties.getClientAuthProviderClassName();
        if (className == null || className.equals(ClientAuthByConfigProvider.class.getName())) {
            return new ClientAuthByConfigProvider(serverProperties.getPassword());
        }
        ProxyBeanFactory proxyBeanFactory = serverProperties.getProxyBeanFactory();
        return (ClientAuthProvider) proxyBeanFactory.getBean(BeanInitUtils.parseClass(className));
    }

    public static MonitorCallback initMonitorCallback(CamelliaServerProperties serverProperties) {
        String className = serverProperties.getMonitorCallbackClassName();
        if (className != null) {
            ProxyBeanFactory proxyBeanFactory = serverProperties.getProxyBeanFactory();
            return (MonitorCallback) proxyBeanFactory.getBean(BeanInitUtils.parseClass(className));
        }
        return null;
    }

    public static ProxyDiscoveryFactory initProxyDiscoveryFactory(CamelliaTranspondProperties.RedisConfProperties redisConfProperties, ProxyBeanFactory proxyBeanFactory) {
        String className = redisConfProperties.getProxyDiscoveryFactoryClassName();
        if (className != null) {
            return (ProxyDiscoveryFactory) proxyBeanFactory.getBean(BeanInitUtils.parseClass(className));
        }
        return null;
    }
}

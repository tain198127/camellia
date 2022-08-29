package com.netease.nim.camellia.redis.proxy.springboot.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "camellia-redis-proxy.cluster")
public class ProxyClusterProperties {
    private String resource;
}

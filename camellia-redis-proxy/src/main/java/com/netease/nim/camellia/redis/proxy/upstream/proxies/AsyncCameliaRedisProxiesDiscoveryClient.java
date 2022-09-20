package com.netease.nim.camellia.redis.proxy.upstream.proxies;

import com.netease.nim.camellia.core.discovery.CamelliaDiscovery;
import com.netease.nim.camellia.core.model.Resource;
import com.netease.nim.camellia.redis.exception.CamelliaRedisException;
import com.netease.nim.camellia.redis.proxy.conf.ProxyDynamicConf;
import com.netease.nim.camellia.redis.proxy.discovery.common.IProxyDiscovery;
import com.netease.nim.camellia.redis.proxy.discovery.common.Proxy;
import com.netease.nim.camellia.redis.proxy.netty.GlobalRedisProxyEnv;
import com.netease.nim.camellia.redis.proxy.upstream.client.RedisClient;
import com.netease.nim.camellia.redis.proxy.upstream.client.RedisClientAddr;
import com.netease.nim.camellia.redis.proxy.upstream.client.RedisClientHub;
import com.netease.nim.camellia.redis.proxy.upstream.standalone.AsyncCamelliaSimpleClient;
import com.netease.nim.camellia.redis.proxy.util.ExecutorUtils;
import com.netease.nim.camellia.redis.resource.RedisProxiesDiscoveryResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


/**
 * 基于服务发现的proxy client
 * 对于每个proxy，都会当做一个普通的redis去访问
 */
public class AsyncCameliaRedisProxiesDiscoveryClient extends AsyncCamelliaSimpleClient {

    private static final Logger logger = LoggerFactory.getLogger(AsyncCameliaRedisProxiesDiscoveryClient.class);

    private final RedisProxiesDiscoveryResource resource;
    private final IProxyDiscovery proxyDiscovery;
    private List<RedisClientAddr> proxyList = new ArrayList<>();
    private List<RedisClientAddr> dynamicList;
    private final Object lock = new Object();

    public AsyncCameliaRedisProxiesDiscoveryClient(RedisProxiesDiscoveryResource resource) {
        this.resource = resource;
        if (GlobalRedisProxyEnv.discoveryFactory == null) {
            throw new CamelliaRedisException("proxy discovery not init");
        }
        this.proxyDiscovery = GlobalRedisProxyEnv.discoveryFactory.getProxyDiscovery(resource.getProxyName());
        if (proxyDiscovery == null) {
            throw new CamelliaRedisException("proxy discovery init fail, resource = " + resource.getUrl());
        }
        List<Proxy> proxies = proxyDiscovery.findAll();
        init(proxies);
        if (proxyList.isEmpty()) {
            throw new CamelliaRedisException("no reachable proxy, resource = " + resource.getUrl());
        }
        proxyDiscovery.setCallback(new CamelliaDiscovery.Callback<Proxy>() {
            @Override
            public void add(Proxy proxy) {
                AsyncCameliaRedisProxiesDiscoveryClient.this.add(proxy);
            }
            @Override
            public void remove(Proxy proxy) {
                AsyncCameliaRedisProxiesDiscoveryClient.this.remove(proxy);
            }
        });
        int seconds = ProxyDynamicConf.getInt("redis.proxies.discovery.reload.interval.seconds", 60);
        ExecutorUtils.scheduleAtFixedRate(this::reload, seconds, seconds, TimeUnit.SECONDS);
    }

    @Override
    public RedisClientAddr getAddr() {
        try {
            if (proxyList.isEmpty()) return null;
            if (proxyList.size() == 1) {
                return proxyList.get(0);
            }
            int retry = proxyList.size();
            while (retry-- > 0) {
                if (dynamicList.isEmpty()) {
                    dynamicList = new ArrayList<>(proxyList);
                }
                int i = ThreadLocalRandom.current().nextInt(dynamicList.size());
                RedisClientAddr addr = dynamicList.get(i);
                if (check(addr)) {
                    return addr;
                } else {
                    dynamicList.remove(addr);
                }
            }
            int i = ThreadLocalRandom.current().nextInt(proxyList.size());
            return proxyList.get(i);
        } catch (Exception e) {
            try {
                if (proxyList.isEmpty()) return null;
                int i = ThreadLocalRandom.current().nextInt(proxyList.size());
                return proxyList.get(i);
            } catch (Exception ex) {
                try {
                    return proxyList.get(0);
                } catch (Exception exc) {
                    return null;
                }
            }
        }
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    private void reload() {
        try {
            List<Proxy> proxies = proxyDiscovery.findAll();
            init(proxies);
        } catch (Exception e) {
            logger.error("reload error, resource = {}", resource, e);
        }
    }

    private void init(List<Proxy> proxies) {
        if (proxies == null || proxies.isEmpty()) return;
        synchronized (lock) {
            List<RedisClientAddr> list = new ArrayList<>();
            for (Proxy proxy : proxies) {
                list.add(toAddr(proxy));
            }
            list.removeIf(addr -> !check(addr));
            if (!list.isEmpty()) {
                this.proxyList = list;
                this.dynamicList = new ArrayList<>(list);
            }
        }
    }

    private void add(Proxy proxy) {
        if (proxy == null) return;
        logger.info("proxy add, proxy = {}, resource = {}", proxy, resource);
        synchronized (lock) {
            RedisClientAddr addr = toAddr(proxy);
            if (!this.proxyList.contains(addr)) {
                if (check(addr)) {
                    this.proxyList.add(addr);
                    this.dynamicList = new ArrayList<>(proxyList);
                }
            }
        }
    }

    private void remove(Proxy proxy) {
        if (proxy == null) return;
        logger.info("proxy remove, proxy = {}, resource = {}", proxy, resource);
        synchronized (lock) {
            RedisClientAddr addr = toAddr(proxy);
            if (this.proxyList.contains(addr)) {
                if (this.proxyList.size() > 1) {
                    this.proxyList.remove(addr);
                    this.dynamicList = new ArrayList<>(proxyList);
                } else {
                    logger.warn("proxy list size = 1, skip remove, proxy = {}, resource = {}", proxy, resource);
                }
            }
        }
    }

    private RedisClientAddr toAddr(Proxy proxy) {
        return new RedisClientAddr(proxy.getHost(), proxy.getPort(), resource.getUserName(), resource.getPassword());
    }

    private boolean check(RedisClientAddr addr) {
        RedisClient redisClient = RedisClientHub.get(addr);
        return redisClient != null && redisClient.isValid();
    }
}

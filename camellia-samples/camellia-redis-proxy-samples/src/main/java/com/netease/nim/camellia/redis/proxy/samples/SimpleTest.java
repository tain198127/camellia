package com.netease.nim.camellia.redis.proxy.samples;

import com.netease.nim.camellia.redis.proxy.util.CamelliaRedisProxyStarter;

/**
 * 不使用spring-boot-starter，手工启动一个proxy的方法
 * Created by caojiajun on 2021/8/3
 */
public class SimpleTest {

    public static void main(String[] args) {
        //设置相关参数
        CamelliaRedisProxyStarter.updatePort(6380);//设置proxy的端口
        CamelliaRedisProxyStarter.updatePassword("pass123");//设置proxy的密码
        CamelliaRedisProxyStarter.updateRouteConf("redis://@127.0.0.1:6379");//可以设置单个地址，也可以设置一个json去配置双写/分片等
        //其他参数设置....

        //启动
        CamelliaRedisProxyStarter.start();
    }
}

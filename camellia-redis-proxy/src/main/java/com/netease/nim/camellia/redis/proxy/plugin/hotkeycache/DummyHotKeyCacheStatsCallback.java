package com.netease.nim.camellia.redis.proxy.plugin.hotkeycache;

import com.netease.nim.camellia.redis.proxy.auth.IdentityInfo;

/**
 *
 * Created by caojiajun on 2021/4/25
 */
public class DummyHotKeyCacheStatsCallback implements HotKeyCacheStatsCallback {

    @Override
    public void callback(IdentityInfo identityInfo, HotKeyCacheInfo hotKeyCacheStats, long checkMillis, long checkThreshold) {

    }
}

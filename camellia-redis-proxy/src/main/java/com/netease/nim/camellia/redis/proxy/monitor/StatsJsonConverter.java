package com.netease.nim.camellia.redis.proxy.monitor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.netease.nim.camellia.redis.proxy.conf.ProxyDynamicConf;
import com.netease.nim.camellia.redis.proxy.monitor.model.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 把统计数据转换成JSON格式（符合网易哨兵系统的采集格式）
 * Created by caojiajun on 2022/9/16
 */
public class StatsJsonConverter {

    private static final ThreadLocal<SimpleDateFormat> dataFormat = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    public static String converter(Stats stats) {
        if (stats == null) return new JSONObject().toJSONString();
        JSONObject monitorJson = new JSONObject();
        JSONArray connectJsonArray = new JSONArray();
        JSONObject connectJson = new JSONObject();
        connectJson.put("connect", stats.getClientConnectCount());
        connectJsonArray.add(connectJson);
        monitorJson.put("connectStats", connectJsonArray);

        JSONArray countJsonArray = new JSONArray();
        JSONObject countJson = new JSONObject();
        countJson.put("count", stats.getCount());
        countJson.put("totalReadCount", stats.getTotalReadCount());
        countJson.put("totalWriteCount", stats.getTotalWriteCount());
        countJsonArray.add(countJson);
        monitorJson.put("countStats", countJsonArray);

        JSONArray qpsJsonArray = new JSONArray();
        JSONObject qpsJson = new JSONObject();
        qpsJson.put("qps", stats.getCount() / (stats.getIntervalSeconds() * 1.0));
        qpsJson.put("readQps", stats.getTotalReadCount() / (stats.getIntervalSeconds() * 1.0));
        qpsJson.put("writeQps", stats.getTotalWriteCount() / (stats.getIntervalSeconds() * 1.0));
        qpsJsonArray.add(qpsJson);
        monitorJson.put("qpsStats", qpsJsonArray);

        JSONArray totalJsonArray = new JSONArray();
        for (TotalStats totalStats : stats.getTotalStatsList()) {
            JSONObject totalJson = new JSONObject();
            totalJson.put("command", totalStats.getCommand());
            totalJson.put("count", totalStats.getCount());
            totalJson.put("qps", totalStats.getCount() / (stats.getIntervalSeconds() * 1.0));
            totalJsonArray.add(totalJson);
        }
        monitorJson.put("total", totalJsonArray);

        JSONArray bigBgroupJsonArray = new JSONArray();
        for (BidBgroupStats bidBgroupStats : stats.getBidBgroupStatsList()) {
            JSONObject bidBgroupJson = new JSONObject();
            bidBgroupJson.put("bid", bidBgroupStats.getBid() == null ? "default" : String.valueOf(bidBgroupStats.getBid()));
            bidBgroupJson.put("bgroup", bidBgroupStats.getBgroup() == null ? "default" : bidBgroupStats.getBgroup());
            bidBgroupJson.put("count", bidBgroupStats.getCount());
            bidBgroupJson.put("qps", bidBgroupStats.getCount() / (stats.getIntervalSeconds() * 1.0));
            bigBgroupJsonArray.add(bidBgroupJson);
        }
        monitorJson.put("bidbgroup", bigBgroupJsonArray);

        JSONArray detailJsonArray = new JSONArray();
        for (DetailStats detailStats : stats.getDetailStatsList()) {
            JSONObject detailJson = new JSONObject();
            detailJson.put("bid", detailStats.getBid() == null ? "default" : String.valueOf(detailStats.getBid()));
            detailJson.put("bgroup", detailStats.getBgroup() == null ? "default" : detailStats.getBgroup());
            detailJson.put("command", detailStats.getCommand());
            detailJson.put("count", detailStats.getCount());
            detailJson.put("qps", detailStats.getCount() / (stats.getIntervalSeconds() * 1.0));
            detailJsonArray.add(detailJson);
        }
        monitorJson.put("detail", detailJsonArray);

        JSONArray failJsonArray = new JSONArray();
        for (Map.Entry<String, Long> entry : stats.getFailMap().entrySet()) {
            String failReason = entry.getKey();
            Long count = entry.getValue();
            JSONObject failJson = new JSONObject();
            failJson.put("reason", failReason);
            failJson.put("count", count);
            failJsonArray.add(failJson);
        }
        monitorJson.put("failStats", failJsonArray);

        JSONArray spendJsonArray = new JSONArray();
        for (SpendStats spendStats : stats.getSpendStatsList()) {
            JSONObject spendJson = new JSONObject();
            spendJson.put("command", spendStats.getCommand());
            spendJson.put("count", spendStats.getCount());
            spendJson.put("avgSpendMs", spendStats.getAvgSpendMs());
            spendJson.put("maxSpendMs", spendStats.getMaxSpendMs());
            spendJsonArray.add(spendJson);
        }
        monitorJson.put("spendStats", spendJsonArray);

        JSONArray bidBgroupSpendJsonArray = new JSONArray();
        for (BidBgroupSpendStats bidBgroupSpendStats : stats.getBidBgroupSpendStatsList()) {
            JSONObject spendJson = new JSONObject();
            spendJson.put("bid", bidBgroupSpendStats.getBid() == null ? "default" : bidBgroupSpendStats.getBid());
            spendJson.put("bgroup", bidBgroupSpendStats.getBgroup() == null ? "default" : bidBgroupSpendStats.getBgroup());
            spendJson.put("command", bidBgroupSpendStats.getCommand());
            spendJson.put("count", bidBgroupSpendStats.getCount());
            spendJson.put("avgSpendMs", bidBgroupSpendStats.getAvgSpendMs());
            spendJson.put("maxSpendMs", bidBgroupSpendStats.getMaxSpendMs());
            bidBgroupSpendJsonArray.add(spendJson);
        }
        monitorJson.put("bidBgroupSpendStats", bidBgroupSpendJsonArray);

        JSONArray resourceStatsJsonArray = new JSONArray();
        for (ResourceStats resourceStats : stats.getResourceStatsList()) {
            JSONObject json = new JSONObject();
            json.put("resource", resourceStats.getResource());
            json.put("count", resourceStats.getCount());
            json.put("qps", resourceStats.getCount() / (stats.getIntervalSeconds() * 1.0));
            resourceStatsJsonArray.add(json);
        }
        monitorJson.put("resourceStats", resourceStatsJsonArray);

        JSONArray resourceCommandStatsJsonArray = new JSONArray();
        for (ResourceCommandStats resourceCommandStats : stats.getResourceCommandStatsList()) {
            JSONObject json = new JSONObject();
            json.put("resource", resourceCommandStats.getResource());
            json.put("command", resourceCommandStats.getCommand());
            json.put("count", resourceCommandStats.getCount());
            json.put("qps", resourceCommandStats.getCount() / (stats.getIntervalSeconds() * 1.0));
            resourceCommandStatsJsonArray.add(json);
        }
        monitorJson.put("resourceCommandStats", resourceCommandStatsJsonArray);

        JSONArray resourceBidBgroupStatsJsonArray = new JSONArray();
        for (ResourceBidBgroupStats resourceBidBgroupStats : stats.getResourceBidBgroupStatsList()) {
            JSONObject json = new JSONObject();
            json.put("bid", resourceBidBgroupStats.getBid() == null ? "default" : resourceBidBgroupStats.getBid());
            json.put("bgroup", resourceBidBgroupStats.getBgroup() == null ? "default" : resourceBidBgroupStats.getBgroup());
            json.put("resource", resourceBidBgroupStats.getResource());
            json.put("count", resourceBidBgroupStats.getCount());
            json.put("qps", resourceBidBgroupStats.getCount() / (stats.getIntervalSeconds() * 1.0));
            resourceBidBgroupStatsJsonArray.add(json);
        }
        monitorJson.put("resourceBidBgroupStats", resourceBidBgroupStatsJsonArray);

        JSONArray resourceBidBgroupCommandStatsJsonArray = new JSONArray();
        for (ResourceBidBgroupCommandStats resourceBidBgroupCommandStats : stats.getResourceBidBgroupCommandStatsList()) {
            JSONObject json = new JSONObject();
            json.put("bid", resourceBidBgroupCommandStats.getBid() == null ? "default" : resourceBidBgroupCommandStats.getBid());
            json.put("bgroup", resourceBidBgroupCommandStats.getBgroup() == null ? "default" : resourceBidBgroupCommandStats.getBgroup());
            json.put("resource", resourceBidBgroupCommandStats.getResource());
            json.put("command", resourceBidBgroupCommandStats.getCommand());
            json.put("count", resourceBidBgroupCommandStats.getCount());
            json.put("qps", resourceBidBgroupCommandStats.getCount() / (stats.getIntervalSeconds() * 1.0));
            resourceBidBgroupCommandStatsJsonArray.add(json);
        }
        monitorJson.put("resourceBidBgroupCommandStats", resourceBidBgroupCommandStatsJsonArray);

        JSONArray routeConfJsonArray = new JSONArray();
        for (RouteConf routeConf : stats.getRouteConfList()) {
            JSONObject json = new JSONObject();
            json.put("bid", routeConf.getBid() == null ? "default" : routeConf.getBid());
            json.put("bgroup", routeConf.getBgroup() == null ? "default" : routeConf.getBgroup());
            json.put("resourceTable", routeConf.getResourceTable());
            json.put("updateTime", dataFormat.get().format(new Date(routeConf.getUpdateTime())));
            routeConfJsonArray.add(json);
        }
        monitorJson.put("routeConf", routeConfJsonArray);

        RedisConnectStats redisConnectStats = stats.getRedisConnectStats();
        JSONArray redisConnectTotalStatsJsonArray = new JSONArray();
        JSONObject redisConnectTotalStatsJson = new JSONObject();
        redisConnectTotalStatsJson.put("connect", redisConnectStats.getConnectCount());
        redisConnectTotalStatsJsonArray.add(redisConnectTotalStatsJson);
        monitorJson.put("redisConnectStats", redisConnectTotalStatsJsonArray);

        JSONArray redisConnectDetailStatsJsonArray = new JSONArray();
        for (RedisConnectStats.Detail detail : redisConnectStats.getDetailList()) {
            JSONObject json = new JSONObject();
            json.put("addr", detail.getAddr());
            json.put("connect", detail.getConnectCount());
            redisConnectDetailStatsJsonArray.add(json);
        }
        monitorJson.put("redisConnectDetailStats", redisConnectDetailStatsJsonArray);

        List<UpstreamRedisSpendStats> upstreamRedisSpendStatsList = stats.getUpstreamRedisSpendStatsList();
        JSONArray upstreamRedisSpendStatsJsonArray = new JSONArray();
        for (UpstreamRedisSpendStats spendStats : upstreamRedisSpendStatsList) {
            JSONObject json = new JSONObject();
            json.put("addr", spendStats.getAddr());
            json.put("count", spendStats.getCount());
            json.put("avgSpendMs", spendStats.getAvgSpendMs());
            json.put("maxSpendMs", spendStats.getMaxSpendMs());
            upstreamRedisSpendStatsJsonArray.add(json);
        }
        monitorJson.put("upstreamRedisSpendStats", upstreamRedisSpendStatsJsonArray);

        List<BigKeyStats> bigKeyStatsList = stats.getBigKeyStatsList();
        JSONArray bigKeyJsonArray = new JSONArray();
        int maxCount = ProxyDynamicConf.getInt("big.key.monitor.json.max.count", Integer.MAX_VALUE);
        for (BigKeyStats bigKeyStats : bigKeyStatsList) {
            JSONObject bigKeyJson = new JSONObject();
            bigKeyJson.put("bid", bigKeyStats.getBid());
            bigKeyJson.put("bgroup", bigKeyStats.getBgroup());
            bigKeyJson.put("commandType", bigKeyStats.getCommandType());
            bigKeyJson.put("command", bigKeyStats.getCommand());
            bigKeyJson.put("key", bigKeyStats.getKey());
            bigKeyJson.put("size", bigKeyStats.getSize());
            bigKeyJson.put("threshold", bigKeyStats.getThreshold());
            bigKeyJsonArray.add(bigKeyJson);
            if (bigKeyJsonArray.size() >= maxCount) {
                break;
            }
        }
        monitorJson.put("bigKeyStats", bigKeyJsonArray);

        List<HotKeyStats> hotKeyStatsList = stats.getHotKeyStatsList();
        JSONArray hotKeyJsonArray = new JSONArray();
        for (HotKeyStats hotKeyStats : hotKeyStatsList) {
            JSONObject hotKeyJson = new JSONObject();
            hotKeyJson.put("bid", hotKeyStats.getBid());
            hotKeyJson.put("bgroup", hotKeyStats.getBgroup());
            hotKeyJson.put("key", hotKeyStats.getKey());
            hotKeyJson.put("count", hotKeyStats.getCount());//total count of callback
            hotKeyJson.put("times", hotKeyStats.getTimes());//callback times
            hotKeyJson.put("avg", hotKeyStats.getAvg());//avg count of every callback
            hotKeyJson.put("max", hotKeyStats.getMax());//max count of every callback
            hotKeyJson.put("checkMillis", hotKeyStats.getCheckMillis());
            hotKeyJson.put("checkThreshold", hotKeyStats.getCheckThreshold());
            hotKeyJsonArray.add(hotKeyJson);
        }
        monitorJson.put("hotKeyStats", hotKeyJsonArray);

        List<SlowCommandStats> slowCommandStatsList = stats.getSlowCommandStatsList();
        JSONArray slowCommandJsonArray = new JSONArray();
        for (SlowCommandStats slowCommandStats : slowCommandStatsList) {
            JSONObject slowCommandJson = new JSONObject();
            slowCommandJson.put("bid", slowCommandStats.getBid());
            slowCommandJson.put("bgroup", slowCommandStats.getBgroup());
            slowCommandJson.put("command", slowCommandStats.getCommand());
            slowCommandJson.put("keys", slowCommandStats.getKeys());
            slowCommandJson.put("spendMillis", slowCommandStats.getSpendMillis());
            slowCommandJson.put("thresholdMillis", slowCommandStats.getThresholdMillis());
            slowCommandJsonArray.add(slowCommandJson);
        }
        monitorJson.put("slowCommandStats", slowCommandJsonArray);

        List<HotKeyCacheStats> hotKeyCacheStatsList = stats.getHotKeyCacheStatsList();
        JSONArray hotKeyCacheStatsJsonArray = new JSONArray();
        for (HotKeyCacheStats hotKeyCacheStats : hotKeyCacheStatsList) {
            JSONObject hotKeyCacheStatsJson = new JSONObject();
            hotKeyCacheStatsJson.put("bid", hotKeyCacheStats.getBid());
            hotKeyCacheStatsJson.put("bgroup", hotKeyCacheStats.getBgroup());
            hotKeyCacheStatsJson.put("key", hotKeyCacheStats.getKey());
            hotKeyCacheStatsJson.put("hitCount", hotKeyCacheStats.getHitCount());
            hotKeyCacheStatsJson.put("checkMillis", hotKeyCacheStats.getCheckMillis());
            hotKeyCacheStatsJson.put("checkThreshold", hotKeyCacheStats.getCheckThreshold());
            hotKeyCacheStatsJsonArray.add(hotKeyCacheStatsJson);
        }
        monitorJson.put("hotKeyCacheStats", hotKeyCacheStatsJsonArray);
        return monitorJson.toJSONString();
    }
}

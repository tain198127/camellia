package com.netease.nim.camellia.redis.proxy.monitor.model;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by caojiajun on 2019/11/28.
 */
public class Stats {

    private int intervalSeconds;
    private long clientConnectCount;
    private long count;
    private long totalReadCount;
    private long totalWriteCount;
    private List<TotalStats> totalStatsList = new ArrayList<>();
    private List<BidBgroupStats> bidBgroupStatsList = new ArrayList<>();
    private List<DetailStats> detailStatsList = new ArrayList<>();
    private Map<String, Long> failMap = new HashMap<>();
    private List<SpendStats> spendStatsList = new ArrayList<>();
    private List<BidBgroupSpendStats> bidBgroupSpendStatsList = new ArrayList<>();
    private List<ResourceStats> resourceStatsList = new ArrayList<>();
    private List<ResourceCommandStats> resourceCommandStatsList = new ArrayList<>();
    private List<ResourceBidBgroupStats> resourceBidBgroupStatsList = new ArrayList<>();
    private List<ResourceBidBgroupCommandStats> resourceBidBgroupCommandStatsList = new ArrayList<>();
    private List<RouteConf> routeConfList = new ArrayList<>();
    private RedisConnectStats redisConnectStats = new RedisConnectStats();
    private List<UpstreamRedisSpendStats> upstreamRedisSpendStatsList = new ArrayList<>();
    private List<BigKeyStats> bigKeyStatsList = new ArrayList<>();
    private List<HotKeyStats> hotKeyStatsList = new ArrayList<>();
    private List<HotKeyCacheStats> hotKeyCacheStatsList = new ArrayList<>();
    private List<SlowCommandStats> slowCommandStatsList = new ArrayList<>();

    public int getIntervalSeconds() {
        return intervalSeconds;
    }

    public void setIntervalSeconds(int intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
    }

    public long getClientConnectCount() {
        return clientConnectCount;
    }

    public void setClientConnectCount(long clientConnectCount) {
        this.clientConnectCount = clientConnectCount;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getTotalReadCount() {
        return totalReadCount;
    }

    public void setTotalReadCount(long totalReadCount) {
        this.totalReadCount = totalReadCount;
    }

    public long getTotalWriteCount() {
        return totalWriteCount;
    }

    public void setTotalWriteCount(long totalWriteCount) {
        this.totalWriteCount = totalWriteCount;
    }

    public List<TotalStats> getTotalStatsList() {
        return totalStatsList;
    }

    public void setTotalStatsList(List<TotalStats> totalStatsList) {
        this.totalStatsList = totalStatsList;
    }

    public List<BidBgroupStats> getBidBgroupStatsList() {
        return bidBgroupStatsList;
    }

    public void setBidBgroupStatsList(List<BidBgroupStats> bidBgroupStatsList) {
        this.bidBgroupStatsList = bidBgroupStatsList;
    }

    public List<DetailStats> getDetailStatsList() {
        return detailStatsList;
    }

    public void setDetailStatsList(List<DetailStats> detailStatsList) {
        this.detailStatsList = detailStatsList;
    }

    public Map<String, Long> getFailMap() {
        return failMap;
    }

    public void setFailMap(Map<String, Long> failMap) {
        this.failMap = failMap;
    }

    public List<SpendStats> getSpendStatsList() {
        return spendStatsList;
    }

    public void setSpendStatsList(List<SpendStats> spendStatsList) {
        this.spendStatsList = spendStatsList;
    }

    public List<BidBgroupSpendStats> getBidBgroupSpendStatsList() {
        return bidBgroupSpendStatsList;
    }

    public void setBidBgroupSpendStatsList(List<BidBgroupSpendStats> bidBgroupSpendStatsList) {
        this.bidBgroupSpendStatsList = bidBgroupSpendStatsList;
    }

    public List<ResourceCommandStats> getResourceCommandStatsList() {
        return resourceCommandStatsList;
    }

    public void setResourceCommandStatsList(List<ResourceCommandStats> resourceCommandStatsList) {
        this.resourceCommandStatsList = resourceCommandStatsList;
    }

    public List<ResourceBidBgroupCommandStats> getResourceBidBgroupCommandStatsList() {
        return resourceBidBgroupCommandStatsList;
    }

    public void setResourceBidBgroupCommandStatsList(List<ResourceBidBgroupCommandStats> resourceBidBgroupCommandStatsList) {
        this.resourceBidBgroupCommandStatsList = resourceBidBgroupCommandStatsList;
    }

    public List<ResourceStats> getResourceStatsList() {
        return resourceStatsList;
    }

    public void setResourceStatsList(List<ResourceStats> resourceStatsList) {
        this.resourceStatsList = resourceStatsList;
    }

    public List<ResourceBidBgroupStats> getResourceBidBgroupStatsList() {
        return resourceBidBgroupStatsList;
    }

    public void setResourceBidBgroupStatsList(List<ResourceBidBgroupStats> resourceBidBgroupStatsList) {
        this.resourceBidBgroupStatsList = resourceBidBgroupStatsList;
    }

    public List<RouteConf> getRouteConfList() {
        return routeConfList;
    }

    public void setRouteConfList(List<RouteConf> routeConfList) {
        this.routeConfList = routeConfList;
    }

    public RedisConnectStats getRedisConnectStats() {
        return redisConnectStats;
    }

    public void setRedisConnectStats(RedisConnectStats redisConnectStats) {
        this.redisConnectStats = redisConnectStats;
    }

    public List<UpstreamRedisSpendStats> getUpstreamRedisSpendStatsList() {
        return upstreamRedisSpendStatsList;
    }

    public void setUpstreamRedisSpendStatsList(List<UpstreamRedisSpendStats> upstreamRedisSpendStatsList) {
        this.upstreamRedisSpendStatsList = upstreamRedisSpendStatsList;
    }

    public List<BigKeyStats> getBigKeyStatsList() {
        return bigKeyStatsList;
    }

    public void setBigKeyStatsList(List<BigKeyStats> bigKeyStatsList) {
        this.bigKeyStatsList = bigKeyStatsList;
    }

    public List<HotKeyStats> getHotKeyStatsList() {
        return hotKeyStatsList;
    }

    public void setHotKeyStatsList(List<HotKeyStats> hotKeyStatsList) {
        this.hotKeyStatsList = hotKeyStatsList;
    }

    public List<SlowCommandStats> getSlowCommandStatsList() {
        return slowCommandStatsList;
    }

    public void setSlowCommandStatsList(List<SlowCommandStats> slowCommandStatsList) {
        this.slowCommandStatsList = slowCommandStatsList;
    }

    public List<HotKeyCacheStats> getHotKeyCacheStatsList() {
        return hotKeyCacheStatsList;
    }

    public void setHotKeyCacheStatsList(List<HotKeyCacheStats> hotKeyCacheStatsList) {
        this.hotKeyCacheStatsList = hotKeyCacheStatsList;
    }
}

package com.netease.nim.camellia.redis.proxy.command.async.cluster.stat;

public class ProxyStat {
    private ProxyClusterNodesInfo nodesInfo;
    private ProxyClusterStatInfo statInfo;
    private ProxyClusterSlotInfo slotInfo;
    
    public ProxyClusterNodesInfo getNodesInfo() {
        return nodesInfo;
    }
    
    public void setNodesInfo(final ProxyClusterNodesInfo nodesInfo) {
        this.nodesInfo = nodesInfo;
    }

    public ProxyClusterStatInfo getStatInfo() {
        return statInfo;
    }
    
    public void setStatInfo(final ProxyClusterStatInfo statInfo) {
        this.statInfo = statInfo;
    }
    
    public ProxyClusterSlotInfo getSlotInfo() {
        return slotInfo;
    }
    
    public void setSlotInfo(final ProxyClusterSlotInfo slotInfo) {
        this.slotInfo = slotInfo;
    }
}

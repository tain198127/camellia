package com.netease.nim.camellia.redis.proxy.command.async.cluster.stat;

import java.util.ArrayList;
import java.util.List;

/**
 * 根据redis cluster 的slot结构
 * 1) 1) (integer) 0
 *    2) (integer) 5460
 *    3) 1) "127.0.0.1"
 *       2) (integer) 6379
 *       3) "3e308a929050d0b361d0fbd28a7f56088a48f9a9"
 *    4) 1) "127.0.0.1"
 *       2) (integer) 6384
 *       3) "4caa2af3d2964d918973a1503c8d1c01ab27c8ab"
 * 2) 1) (integer) 5461
 *    2) (integer) 10922
 *    3) 1) "127.0.0.1"
 *       2) (integer) 6380
 *       3) "adfa843776c17f3c551c9875fa99e25f19374038"
 *    4) 1) "127.0.0.1"
 *       2) (integer) 6382
 *       3) "b813a415ca0a3db64f03656b23de6918159844ed"
 * 3) 1) (integer) 10923
 *    2) (integer) 16383
 *    3) 1) "127.0.0.1"
 *       2) (integer) 6381
 *       3) "56c76bf132903d7d6595e97d0fdacd288e7ade69"
 *    4) 1) "127.0.0.1"
 *       2) (integer) 6383
 *       3) "9d2a2290f26efc5c15509ce2ad12306bba3df3bb"
 * 其结构如下：
 * list[Cluster]
 *      --slot begin
 *      --slot end
 *      --list[node]
 *          --node ip
 *          --node port
 *          --node id
 *      
 */
public class ProxyClusterSlotInfo {
    public List<ClusterSlot> getClusterSlotList() {
        return clusterSlotList;
    }
    
    public void setClusterSlotList(final List<ClusterSlot> clusterSlotList) {
        this.clusterSlotList = clusterSlotList;
    }
    
    /**
     * 分片
     */
    private List<ClusterSlot> clusterSlotList = new ArrayList<>();
    
    public static class ClusterSlot {
        private int slotBegin=0;
        private int slotEnd=16384;
    
        public int getSlotBegin() {
            return slotBegin;
        }
    
        public void setSlotBegin(final int slotBegin) {
            this.slotBegin = slotBegin;
        }
    
        public int getSlotEnd() {
            return slotEnd;
        }
    
        public void setSlotEnd(final int slotEnd) {
            this.slotEnd = slotEnd;
        }
    
        public List<SimpleNode> getSimpleNodes() {
            return simpleNodes;
        }
    
        public void setSimpleNodes(final List<SimpleNode> simpleNodes) {
            this.simpleNodes = simpleNodes;
        }
    
        private List<SimpleNode> simpleNodes = new ArrayList<>();
        
        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(String.format("  1) (integer) %s",slotBegin));
            stringBuilder.append(String.format("    2) (integer) %s",slotEnd));
            for (int i = 0; i < simpleNodes.size(); i++) {
                stringBuilder.append(String.format("    %s) (integer) %s",i+3,simpleNodes.get(i).toString()));
            }
            return stringBuilder.toString();
        }
    }
    
    public static class SimpleNode {
        private String ip;
        private int port;
        private String id;
    
        public SimpleNode(final String ip, final int port, final String id) {
            this.ip = ip;
            this.port = port;
            this.id = id;
        }
    
        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(String.format("  1) %s",ip));
            stringBuilder.append(String.format("        2) %s",port));
            stringBuilder.append(String.format("        3) %s",id));
            return stringBuilder.toString();
        }
    }
    
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0;i < clusterSlotList.size();i++){
            stringBuilder.append((i+1)+") "+clusterSlotList.get(i).toString());
        }
        return stringBuilder.toString();
    }
}

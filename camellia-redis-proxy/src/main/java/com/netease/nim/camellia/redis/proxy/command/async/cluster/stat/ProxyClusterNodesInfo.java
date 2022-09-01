package com.netease.nim.camellia.redis.proxy.command.async.cluster.stat;

import java.util.ArrayList;
import java.util.List;

public class ProxyClusterNodesInfo {
    private List<ClusterNode> nodes = new ArrayList<>();
    
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < nodes.size(); i++) {
            stringBuilder.append(nodes.get(i).toString()+"\n");
        }
        return stringBuilder.toString();
    }
    
    public List<ClusterNode> getNodes() {
        return nodes;
    }
    
    public void setNodes(final List<ClusterNode> nodes) {
        this.nodes = nodes;
    }
    
    public static class ClusterNode {
        private String id;
        private String ip;
        private String role;
        private String masterId;
        private int connectCount;
        private String range_start;
        private String range_end;
        private String slot_begin;
        private String slot_end;
    
        public String getId() {
            return id;
        }
    
        public void setId(final String id) {
            this.id = id;
        }
    
        public String getIp() {
            return ip;
        }
    
        public void setIp(final String ip) {
            this.ip = ip;
        }
    
        public String getRole() {
            return role;
        }
    
        public void setRole(final String role) {
            this.role = role;
        }
    
        public String getMasterId() {
            return masterId;
        }
    
        public void setMasterId(final String masterId) {
            this.masterId = masterId;
        }
    
        public int getConnectCount() {
            return connectCount;
        }
    
        public void setConnectCount(final int connectCount) {
            this.connectCount = connectCount;
        }
    
        public String getRange_start() {
            return range_start;
        }
    
        public void setRange_start(final String range_start) {
            this.range_start = range_start;
        }
    
        public String getRange_end() {
            return range_end;
        }
    
        public void setRange_end(final String range_end) {
            this.range_end = range_end;
        }
    
        public String getSlot_begin() {
            return slot_begin;
        }
    
        public void setSlot_begin(final String slot_begin) {
            this.slot_begin = slot_begin;
        }
    
        public String getSlot_end() {
            return slot_end;
        }
    
        public void setSlot_end(final String slot_end) {
            this.slot_end = slot_end;
        }
    
        @Override
        public String toString() {
            String copyMasterId = "-";
            if("slave".equals(this.role)){
                copyMasterId = this.masterId;
            }
            return id + " " + ip + " " + role + " " + copyMasterId + " " + range_start + " " + range_end + " " + connectCount + " connected" + " " + slot_begin + "-" + slot_end;
        }
    }
}

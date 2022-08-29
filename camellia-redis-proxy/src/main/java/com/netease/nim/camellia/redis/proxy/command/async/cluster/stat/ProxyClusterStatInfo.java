package com.netease.nim.camellia.redis.proxy.command.async.cluster.stat;

public class ProxyClusterStatInfo {
    
    private String id;
    private String cluster_state = "OK";
    private int cluster_slots_assigned = 16384;
    private int cluster_slots_ok = 16384;
    private int cluster_slots_pfail = 0;
    private int cluster_slots_fail = 0;
    private int cluster_known_nodes = 1;
    private int cluster_size = 1;
    private int cluster_current_epoch = 1;
    private int cluster_my_epoch = 1;
    private int cluster_stats_messages_ping_sent = 1;
    private int cluster_stats_messages_pong_sent = 1;
    private int cluster_stats_messages_meet_sent = 1;
    private int cluster_stats_messages_sent = 1;
    private int cluster_stats_messages_ping_received = 1;
    private int cluster_stats_messages_pong_received = 1;
    private int cluster_stats_messages_received = 1;
    
    @Override
    public String toString() {
        return "cluster_state:" + cluster_state + "\r\n cluster_slots_assigned:" + cluster_slots_assigned + "\r\n cluster_slots_ok:" + cluster_slots_ok + "\r\n cluster_slots_pfail:" + cluster_slots_pfail + "\r\n cluster_slots_fail:" + cluster_slots_fail + "\r\n cluster_known_nodes:" + cluster_known_nodes + "\r\n cluster_size:" + cluster_size + "\r\n cluster_current_epoch:" + cluster_current_epoch + "\r\n cluster_my_epoch:" + cluster_my_epoch + "\r\n cluster_stats_messages_ping_sent:" + cluster_stats_messages_ping_sent + "\r\n cluster_stats_messages_pong_sent:" + cluster_stats_messages_pong_sent + "\r\n cluster_stats_messages_meet_sent:" + cluster_stats_messages_meet_sent + "\r\n cluster_stats_messages_sent:" + cluster_stats_messages_sent + "\r\n cluster_stats_messages_ping_received:" + cluster_stats_messages_ping_received + "\r\n cluster_stats_messages_pong_received:" + cluster_stats_messages_pong_received + "\r\n cluster_stats_messages_received:" + cluster_stats_messages_received + "\r\n";
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(final String id) {
        this.id = id;
    }
    
    public String getCluster_state() {
        return cluster_state;
    }
    
    public void setCluster_state(final String cluster_state) {
        this.cluster_state = cluster_state;
    }
    
    public int getCluster_slots_assigned() {
        return cluster_slots_assigned;
    }
    
    public void setCluster_slots_assigned(final int cluster_slots_assigned) {
        this.cluster_slots_assigned = cluster_slots_assigned;
    }
    
    public int getCluster_slots_ok() {
        return cluster_slots_ok;
    }
    
    public void setCluster_slots_ok(final int cluster_slots_ok) {
        this.cluster_slots_ok = cluster_slots_ok;
    }
    
    public int getCluster_slots_pfail() {
        return cluster_slots_pfail;
    }
    
    public void setCluster_slots_pfail(final int cluster_slots_pfail) {
        this.cluster_slots_pfail = cluster_slots_pfail;
    }
    
    public int getCluster_slots_fail() {
        return cluster_slots_fail;
    }
    
    public void setCluster_slots_fail(final int cluster_slots_fail) {
        this.cluster_slots_fail = cluster_slots_fail;
    }
    
    public int getCluster_known_nodes() {
        return cluster_known_nodes;
    }
    
    public void setCluster_known_nodes(final int cluster_known_nodes) {
        this.cluster_known_nodes = cluster_known_nodes;
    }
    
    public int getCluster_size() {
        return cluster_size;
    }
    
    public void setCluster_size(final int cluster_size) {
        this.cluster_size = cluster_size;
    }
    
    public int getCluster_current_epoch() {
        return cluster_current_epoch;
    }
    
    public void setCluster_current_epoch(final int cluster_current_epoch) {
        this.cluster_current_epoch = cluster_current_epoch;
    }
    
    public int getCluster_my_epoch() {
        return cluster_my_epoch;
    }
    
    public void setCluster_my_epoch(final int cluster_my_epoch) {
        this.cluster_my_epoch = cluster_my_epoch;
    }
    
    public int getCluster_stats_messages_ping_sent() {
        return cluster_stats_messages_ping_sent;
    }
    
    public void setCluster_stats_messages_ping_sent(final int cluster_stats_messages_ping_sent) {
        this.cluster_stats_messages_ping_sent = cluster_stats_messages_ping_sent;
    }
    
    public int getCluster_stats_messages_pong_sent() {
        return cluster_stats_messages_pong_sent;
    }
    
    public void setCluster_stats_messages_pong_sent(final int cluster_stats_messages_pong_sent) {
        this.cluster_stats_messages_pong_sent = cluster_stats_messages_pong_sent;
    }
    
    public int getCluster_stats_messages_meet_sent() {
        return cluster_stats_messages_meet_sent;
    }
    
    public void setCluster_stats_messages_meet_sent(final int cluster_stats_messages_meet_sent) {
        this.cluster_stats_messages_meet_sent = cluster_stats_messages_meet_sent;
    }
    
    public int getCluster_stats_messages_sent() {
        return cluster_stats_messages_sent;
    }
    
    public void setCluster_stats_messages_sent(final int cluster_stats_messages_sent) {
        this.cluster_stats_messages_sent = cluster_stats_messages_sent;
    }
    
    public int getCluster_stats_messages_ping_received() {
        return cluster_stats_messages_ping_received;
    }
    
    public void setCluster_stats_messages_ping_received(final int cluster_stats_messages_ping_received) {
        this.cluster_stats_messages_ping_received = cluster_stats_messages_ping_received;
    }
    
    public int getCluster_stats_messages_pong_received() {
        return cluster_stats_messages_pong_received;
    }
    
    public void setCluster_stats_messages_pong_received(final int cluster_stats_messages_pong_received) {
        this.cluster_stats_messages_pong_received = cluster_stats_messages_pong_received;
    }
    
    public int getCluster_stats_messages_received() {
        return cluster_stats_messages_received;
    }
    
    public void setCluster_stats_messages_received(final int cluster_stats_messages_received) {
        this.cluster_stats_messages_received = cluster_stats_messages_received;
    }
}

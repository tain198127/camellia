package com.netease.nim.camellia.redis.proxy.command.async.cluster.impl;

import com.netease.nim.camellia.redis.proxy.command.Command;
import com.netease.nim.camellia.redis.proxy.command.async.cluster.ClusterProxy;
import com.netease.nim.camellia.redis.proxy.command.async.cluster.stat.ProxyClusterNodesInfo;
import com.netease.nim.camellia.redis.proxy.command.async.cluster.stat.ProxyClusterNodesInfo.ClusterNode;
import com.netease.nim.camellia.redis.proxy.command.async.cluster.stat.ProxyClusterSlotInfo;
import com.netease.nim.camellia.redis.proxy.command.async.cluster.stat.ProxyClusterSlotInfo.ClusterSlot;
import com.netease.nim.camellia.redis.proxy.command.async.cluster.stat.ProxyClusterSlotInfo.SimpleNode;
import com.netease.nim.camellia.redis.proxy.command.async.cluster.stat.ProxyClusterStatInfo;
import com.netease.nim.camellia.redis.proxy.command.async.cluster.stat.ProxyStat;
import com.netease.nim.camellia.redis.proxy.command.async.info.ProxyInfoUtils;
import com.netease.nim.camellia.redis.proxy.reply.BulkReply;
import com.netease.nim.camellia.redis.proxy.reply.ErrorReply;
import com.netease.nim.camellia.redis.proxy.reply.Reply;
import com.netease.nim.camellia.redis.proxy.util.ErrorLogCollector;
import com.netease.nim.camellia.redis.proxy.util.RedisClusterCRC16Utils;
import com.netease.nim.camellia.redis.proxy.util.Utils;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 实现proxy集群的redis协议，注意该协议返回的是proxy做成的集群的信息。所有命令不会穿透到redis-cluster
 */
public final class ClusterProxyGroupsImpl implements ClusterProxy {
    
    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>(8), new DefaultThreadFactory("camellia-proxy-cluster"));
    private static Object lock = new Object();
    private static volatile ProxyStat stat;
    
    
    
    @Override
    public ProxyStat id() {
        if(null == stat){
            synchronized (lock){
                if(null == stat){
                    stat = new ProxyStat();
                    stat.setStatInfo(new ProxyClusterStatInfo());
                    stat.getStatInfo().setId(UUID.randomUUID().toString());
                    stat.getStatInfo().setCluster_state("OK");
                    stat.setNodesInfo(new ProxyClusterNodesInfo());
                    ClusterNode myNode = new ClusterNode();
                    myNode.setId(stat.getStatInfo().getId());
                    myNode.setConnectCount(1);
                    myNode.setIp("127.0.0.1:26380@26479");
                    myNode.setMasterId(stat.getStatInfo().getId());
                    myNode.setRange_end("6");
                    myNode.setRange_start("5");
                    myNode.setRole("myself,master");
                    myNode.setSlot_begin("0");
                    myNode.setSlot_end("16383");
                    ArrayList<ClusterNode> list = new ArrayList<>();
                    list.add(myNode);
                    stat.getNodesInfo().setNodes(list);
                    stat.setSlotInfo(new ProxyClusterSlotInfo());
                    stat.getSlotInfo().setClusterSlotList(new ArrayList<>());
                    stat.getSlotInfo().getClusterSlotList().add(new ClusterSlot());
                    stat.getSlotInfo().getClusterSlotList().get(0).setSlotBegin(0);
                    stat.getSlotInfo().getClusterSlotList().get(0).setSlotEnd(16383);
                    stat.getSlotInfo().getClusterSlotList().get(0).setSimpleNodes(new ArrayList<>());
                    stat.getSlotInfo().getClusterSlotList().get(0).getSimpleNodes().add(new SimpleNode("127.0.0.1",26380,stat.getStatInfo().getId()));
                    
                }
            }
            return stat;
        }
        return stat;
    }
    
    @Override
    public String info() {
        return stat.getStatInfo().toString();
//        StringBuilder builder = new StringBuilder();
//        builder.append("cluster_state:ok\r\n");
//        builder.append("cluster_slots_assigned:16384\r\n");
//        builder.append("cluster_slots_ok:16384\r\n");
//        builder.append("cluster_slots_pfail:0\r\n");
//        builder.append("cluster_slots_fail:0\r\n");
//        builder.append("cluster_known_nodes:1\r\n");
//        builder.append("cluster_size:1\r\n");
//        builder.append("cluster_current_epoch:6\r\n");
//        builder.append("cluster_my_epoch:2\r\n");
//        builder.append("cluster_stats_messages_ping_sent:22222\r\n");
//        builder.append("cluster_stats_messages_pong_sent:22222\r\n");
//        builder.append("cluster_stats_messages_meet_sent:1\r\n");
//        builder.append("cluster_stats_messages_sent:7\r\n");
//        builder.append("cluster_stats_messages_ping_received:2\r\n");
//        builder.append("cluster_stats_messages_pong_received:2\r\n");
//        builder.append("cluster_stats_messages_received:2\r\n");
//        return builder.toString();
    }
    
    @Override
    public String myID() {
        StringBuilder stringBuilder = new StringBuilder();
        
        stringBuilder.append(stat.getStatInfo().getId()) ;
        return stringBuilder.toString();
    }
    
    @Override
    public String nodes() {
        return stat.getNodesInfo().toString();
//        return "61518001ccc1c25fd3cd88508671cfe9a4ecbcf0 127.0.0.1:6380@16380 myself,master - 0 1661441117000 2 connected 5461-10922\n" + "afa13cd9b740bc4361dcef17ae2af4341beb8edb 127.0.0.1:6379@16379 master - 0 1661441119975 1 connected 0-5460\n" + "9b99800a522802de1009e792cf9b460b3607f66d 127.0.0.1:6384@16384 slave 61518001ccc1c25fd3cd88508671cfe9a4ecbcf0 0 1661441118000 2 connected\n" + "a4707b106c076b7b86a3d3bd974b7e68188b001c 127.0.0.1:6381@16381 master - 0 1661441117962 3 connected 10923-16383\n" + "42c0d981e30ccd13820ec14235d85ea10eb5d0f9 127.0.0.1:6382@16382 slave a4707b106c076b7b86a3d3bd974b7e68188b001c 0 1661441119000 3 connected\n" + "71391083b7c17675a715fae3687eb84fa93f1878 127.0.0.1:6383@16383 slave afa13cd9b740bc4361dcef17ae2af4341beb8edb 0 1661441119000 1 connected";
    }
    
    @Override
    public String slots() {
        return stat.getSlotInfo().toString();
//        return "1) \r\n 1) (integer) 0\n" + "   2) (integer) 5460\n" + "   3) 1) \"127.0.0.1\"\n" + "      2) (integer) 6379\n" + "      3) \"afa13cd9b740bc4361dcef17ae2af4341beb8edb\"\n" + "   4) 1) \"127.0.0.1\"\n" + "      2) (integer) 6383\n" + "      3) \"71391083b7c17675a715fae3687eb84fa93f1878\"\n" + "2) 1) (integer) 5461\n" + "   2) (integer) 10922\n" + "   3) 1) \"127.0.0.1\"\n" + "      2) (integer) 6380\n" + "      3) \"61518001ccc1c25fd3cd88508671cfe9a4ecbcf0\"\n" + "   4) 1) \"127.0.0.1\"\n" + "      2) (integer) 6384\n" + "      3) \"9b99800a522802de1009e792cf9b460b3607f66d\"\n" + "3) 1) (integer) 10923\n" + "   2) (integer) 16383\n" + "   3) 1) \"127.0.0.1\"\n" + "      2) (integer) 6381\n" + "      3) \"a4707b106c076b7b86a3d3bd974b7e68188b001c\"\n" + "   4) 1) \"127.0.0.1\"\n" + "      2) (integer) 6382\n" + "      3) \"42c0d981e30ccd13820ec14235d85ea10eb5d0f9\"";
    }
    
    public String bumpEpoch() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String addSlots(final String[] slots) {
        return null;
    }
    
    @Override
    public String countKeysInSlot(final String slot) {
        return null;
    }
    
    @Override
    public String countFailureReports(final String nodeId) {
        return null;
    }
    
    @Override
    public String delSlots(final String[] slots) {
        return null;
    }
    
    public String countKeysInSlot() {
        throw new UnsupportedOperationException();
    }
    
    public String countFailureReports() {
        throw new UnsupportedOperationException();
    }
    
    public String delSlots() {
        throw new UnsupportedOperationException();
    }
    
    public String failover() {
        throw new UnsupportedOperationException();
    }
    
    public String flushSlots() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String forget(final String nodeId) {
        return null;
    }
    
    @Override
    public String getKeysInSlot(final String[] param) {
        return null;
    }
    
    @Override
    public Long keySlot(final String key) {
        return Long.valueOf(RedisClusterCRC16Utils.getSlot(key.getBytes()));
    }
    
    @Override
    public String meet(final String ip, final String port) {
        return null;
    }
    
    @Override
    public String replicas(final String nodeId) {
        return null;
    }
    
    @Override
    public String replicate(final String nodeId) {
        return null;
    }
    
    @Override
    public String reset(final String type) {
        return null;
    }
    
    @Override
    public String saveConfig() {
        return null;
    }
    
    @Override
    public String setConfigEpoch(final String epoch) {
        return null;
    }
    
    @Override
    public String setSlot(final String slot, final String type, final String nodeId) {
        return null;
    }
    
    @Override
    public String slaves(final String node_id) {
        return null;
    }
    
    public String forget() {
        throw new UnsupportedOperationException();
    }
    
    public String getKeysInSlot() {
        throw new UnsupportedOperationException();
    }
    
    public String keySlot() {
        throw new UnsupportedOperationException();
    }
    
    public String meet() {
        throw new UnsupportedOperationException();
    }
    
    public String replicas() {
        throw new UnsupportedOperationException();
    }
    
    public String replicate() {
        throw new UnsupportedOperationException();
    }
    
    public String reset() {
        throw new UnsupportedOperationException();
    }
    
    public String saveconfig() {
        throw new UnsupportedOperationException();
    }
    
    public String setConfigEpoch() {
        throw new UnsupportedOperationException();
    }
    
    public String setSlot() {
        throw new UnsupportedOperationException();
    }
    
    public String slaves() {
        throw new UnsupportedOperationException();
    }
    
    private Reply generateInfoReply(Command command) {
        try {
            StringBuilder builder = new StringBuilder();
            byte[][] objects = command.getObjects();
            if (objects.length == 1) {
                return ErrorReply.SYNTAX_ERROR;
                
            } else {
                if (objects.length == 2) {
                    String section = Utils.bytesToString(objects[1]);
                    if (section.equalsIgnoreCase("INFO")) {
                        builder.append(info()).append("\r\n");
                    } else if (section.equalsIgnoreCase("MYID")) {
                        builder.append(myID()).append("\r\n");
                    } else if (section.equalsIgnoreCase("NODES")) {
                        builder.append(nodes()).append("\r\n");
                    } else if (section.equalsIgnoreCase("SLOTS")) {
                        builder.append(slots()).append("\r\n");
                    }
                } else {
                    return ErrorReply.NOT_SUPPORT;
                }
            }
            return new BulkReply(Utils.stringToBytes(builder.toString()));
        } catch (Exception e) {
            ErrorLogCollector.collect(ProxyInfoUtils.class, "getInfoReply error", e);
            return new ErrorReply("generate proxy info error");
        }
    }
}

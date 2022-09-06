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
    
    ClusterNode mockNode1(){
        ClusterNode myNode = new ClusterNode();
        myNode.setId(UUID.randomUUID().toString());
        myNode.setConnectCount(1);
        myNode.setIp("127.0.0.1:26380@26479");
        myNode.setMasterId(stat.getStatInfo().getId());
        myNode.setRange_end("6");
        myNode.setRange_start("5");
        myNode.setRole("master");
        myNode.setSlot_begin("5461");
        myNode.setSlot_end("10922");
        return myNode;
    }
    ClusterNode mockNode2(){
        ClusterNode myNode = new ClusterNode();
        myNode.setId(UUID.randomUUID().toString());
        myNode.setConnectCount(1);
        myNode.setIp("127.0.0.1:26380@26479");
        myNode.setMasterId(stat.getStatInfo().getId());
        myNode.setRange_end("6");
        myNode.setRange_start("5");
        myNode.setRole("master");
        myNode.setSlot_begin("10923");
        myNode.setSlot_end("16383");
        return myNode;
    }
    
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
                    myNode.setSlot_end("5460");
                    ArrayList<ClusterNode> list = new ArrayList<>();
                    list.add(myNode);
                    list.add(mockNode1());
                    list.add(mockNode2());
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
    }
    
    @Override
    public String slots() {
        return stat.getSlotInfo().toString();
    }
    
    public String bumpEpoch() {
        throw new UnsupportedOperationException("not support");
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
        throw new UnsupportedOperationException("not support");
    }
    
    public String countFailureReports() {
        throw new UnsupportedOperationException("not support");
    }
    
    public String delSlots() {
        throw new UnsupportedOperationException("not support");
    }
    
    public String failover() {
        throw new UnsupportedOperationException("not support");
    }
    
    public String flushSlots() {
        throw new UnsupportedOperationException("not support");
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

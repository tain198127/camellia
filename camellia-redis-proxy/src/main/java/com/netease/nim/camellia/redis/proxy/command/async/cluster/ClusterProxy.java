package com.netease.nim.camellia.redis.proxy.command.async.cluster;

import com.netease.nim.camellia.redis.proxy.command.async.cluster.stat.ProxyClusterStatInfo;

/**
 * 实现Cluster的协议的接口
 */
public interface ClusterProxy {
    /**
     * 生成id,每次调用都需要调用该方法
     * @return
     */
    ProxyClusterStatInfo id();
    /**
     * summary: Provides info about Redis Cluster proxy node state
     * 
     * @return
     */
    String info() ;
    
    /**
     * summary: Return the node id
     * @return
     */
    String myID() ;
    
    /**
     * summary: Get Cluster config for the node
     * @return
     */
    String nodes();
    
    /**
     * summary: Get array of Cluster slot to node mappings
     * @return
     */String slots() ;
    
    /**
     * summary: Advance the cluster config epoch
     * @return
     */
    String bumpEpoch() ;
    
    //以下命令可能会改变集群状态
    /**
     * summary: Assign new hash slots to receiving node
     * since: 3.0.0
     * @return
     */
    String addSlots(String[] slots) ;
    
    /**
     * summary: Return the number of local keys in the specified hash slot
     * @return
     */
    String countKeysInSlot(String slot) ;
    
    /**
     * summary: Return the number of failure reports active for a given node
     * @return
     */
    String countFailureReports(String nodeId) ;
    
    /**
     * summary: Set hash slots as unbound in receiving node
     * @return
     */
    String delSlots(String[] slots) ;
    
    /**
     * TODO
     * summary: Forces a replica to perform a manual failover of its master.
     * @return
     */
    String failover() ;
    
    /**
     * summary: Delete a node's own slots information
     * @return
     */
    String flushSlots() ;
    
    /**
     * summary: Remove a node from the nodes table
     * @return
     */
    String forget(String nodeId) ;
    
    /**
     * summary: Return local key names in the specified hash slot
     * @return
     */
    String getKeysInSlot(String[] param) ;
    
    /**
     * summary: Returns the hash slot of the specified key
     * @return
     */
    String keySlot(String key) ;
    
    /**
     * summary: Force a node cluster to handshake with another node
     * @param ip
     * @param port
     * @return
     */
    String meet(String ip, String port) ;
    
    /**
     * summary: List replica nodes of the specified master node
     * @param nodeId
     * @return
     */
    String replicas(String nodeId) ;
    
    /**
     * summary: Reconfigure a node as a replica of the specified master node
     * @param nodeId
     * @return
     */
    String replicate(String nodeId) ;
    
    /**
     * summary: Reset a Redis Cluster node
     * @param type
     * @return
     */
    String reset(String type) ;
    
    /**
     * summary: Forces the node to save cluster state on disk
     * @return
     */
    String saveConfig() ;
    
    /**
     * summary: Set the configuration epoch in a new node
     * @return
     */
    String setConfigEpoch(String epoch) ;
    
    /**
     * summary: Bind a hash slot to a specific node
     * @return
     */
    String setSlot(String slot,String type, String nodeId) ;
    
    /**
     * List replica nodes of the specified master node
     * @return
     */
    String slaves(String node_id) ;
}

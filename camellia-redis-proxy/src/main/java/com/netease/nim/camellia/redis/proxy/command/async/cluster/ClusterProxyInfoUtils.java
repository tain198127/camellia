package com.netease.nim.camellia.redis.proxy.command.async.cluster;

import com.netease.nim.camellia.redis.proxy.command.Command;
import com.netease.nim.camellia.redis.proxy.command.async.cluster.impl.ClusterProxyGroupsImpl;
import com.netease.nim.camellia.redis.proxy.reply.BulkReply;
import com.netease.nim.camellia.redis.proxy.reply.ErrorReply;
import com.netease.nim.camellia.redis.proxy.reply.Reply;
import com.netease.nim.camellia.redis.proxy.util.ErrorLogCollector;
import com.netease.nim.camellia.redis.proxy.util.Utils;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class ClusterProxyInfoUtils {
    //TODO 这里需要根据环境进行配置
    private static ClusterProxy _proxy = new ClusterProxyGroupsImpl();
    private static final Logger logger = LoggerFactory.getLogger(ClusterProxyInfoUtils.class);
    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(8), new DefaultThreadFactory("cluster-info"));
    private static Map<String,Function<byte[][],Object>> functionMap = new HashMap<>();
    static {
        functionMap.put(ClusterCommand.INFO, bytes -> _proxy.info());
        functionMap.put(ClusterCommand.MYID,bytes -> _proxy.myID());
        functionMap.put(ClusterCommand.NODES,bytes -> _proxy.nodes());
        functionMap.put(ClusterCommand.SLOTS,bytes -> _proxy.slots());
        functionMap.put(ClusterCommand.KEYSLOT, objects -> _proxy.keySlot(Utils.bytesToString(objects[2])));
        functionMap.put(ClusterCommand.MEET,bytes -> _proxy.meet(Utils.bytesToString(bytes[2]),Utils.bytesToString(bytes[3])));
    }
    
    /**
     * 获取集群信息
     * @param cmd
     * @return
     */
    public static CompletableFuture<Reply> getInfoReply(Command cmd){
        _proxy.id();
        CompletableFuture<Reply> future = new CompletableFuture<>();
        try {
            executor.submit(() -> {
                Reply reply = generateInfoReply(cmd);
                future.complete(reply);
            });
            return future;
        } catch (Exception e) {
            ErrorLogCollector.collect(ClusterProxyInfoUtils.class, "submit generateInfoReply task error", e);
            future.complete(ErrorReply.TOO_BUSY);
            return future;
        }
    }
    
    /**
     * 扩展点，可以根据具体实现进行配置
     * @param proxy
     */
    public static void rImpl(ClusterProxy proxy){
        _proxy = proxy;
    }
    
    Function<String,Byte[][]> function;
    public static Reply generateInfoReply(Command command) {
        try {
            StringBuilder builder = new StringBuilder();
            byte[][] objects = command.getObjects();
            if(logger.isDebugEnabled()) {
                for (int i = 0; i < objects.length; i++) {
                    logger.debug("{} \n",Utils.bytesToString(objects[i]));
                    
                }
            }
            if (objects.length == 1) {
                return ErrorReply.SYNTAX_ERROR;
                
            } else {
                String section = Utils.bytesToString(objects[1]).toUpperCase();
                if(functionMap.containsKey(section)){
                    builder.append(functionMap.get(section).apply(objects));
                }
                
                else {
                    if(logger.isDebugEnabled()) {
                        for (int i = 0; i < objects.length; i++) {
                            logger.error("{} \n",Utils.bytesToString(objects[i]));
                            
                        }
                    }
                    return ErrorReply.NOT_SUPPORT;
                }
            }
            return new BulkReply(Utils.stringToBytes(builder.toString()));
        } catch (Exception e) {
            ErrorLogCollector.collect(ClusterProxyInfoUtils.class, "getInfoReply error", e);
            return new ErrorReply("generate proxy info error");
        }
    }
    
    /**
     * 加入某个集群
     * @param command
     * @return
     */
//    public static Reply joinCluster(Command command){
//        StringBuilder builder = new StringBuilder();
//        byte[][] objects = command.getObjects();
//        if(logger.isDebugEnabled()) {
//            for (int i = 0; i < objects.length; i++) {
//                logger.error("{} \n",Utils.bytesToString(objects[i]));
//            }
//        }
//        if (objects.length == 1) {
//            return ErrorReply.SYNTAX_ERROR;
//        }
//    }
    
    /**
     * 下线前必须执行
     * @param command
     * @return
     */
//    public static Reply disconectCluster(Command command){
//        
//    }
    
}

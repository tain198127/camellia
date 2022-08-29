package com.netease.nim.camellia.redis.proxy.command.async.cluster;

import com.netease.nim.camellia.redis.proxy.command.Command;
import com.netease.nim.camellia.redis.proxy.command.async.cluster.impl.ClusterProxyGroupsImpl;
import com.netease.nim.camellia.redis.proxy.command.async.info.ProxyInfoUtils;
import com.netease.nim.camellia.redis.proxy.reply.BulkReply;
import com.netease.nim.camellia.redis.proxy.reply.ErrorReply;
import com.netease.nim.camellia.redis.proxy.reply.Reply;
import com.netease.nim.camellia.redis.proxy.util.ErrorLogCollector;
import com.netease.nim.camellia.redis.proxy.util.Utils;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ClusterProxyInfoUtils {
    //TODO 这里需要根据环境进行配置
    private static ClusterProxy _proxy = new ClusterProxyGroupsImpl();
    private static final Logger logger = LoggerFactory.getLogger(ClusterProxyInfoUtils.class);
    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(8), new DefaultThreadFactory("cluster-info"));
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
            ErrorLogCollector.collect(ProxyInfoUtils.class, "submit generateInfoReply task error", e);
            future.complete(ErrorReply.TOO_BUSY);
            return future;
        }
    }
    public static void setClusterImpl(ClusterProxy proxy){
        _proxy = proxy;
    }
    public static Reply generateInfoReply(Command command) {
        try {
            StringBuilder builder = new StringBuilder();
            byte[][] objects = command.getObjects();
            if (objects.length == 1) {
                return ErrorReply.SYNTAX_ERROR;
                
            } else {
                if (objects.length == 2) {
                    String section = Utils.bytesToString(objects[1]);
                    if (section.equalsIgnoreCase("INFO")) {
                        builder.append(_proxy.info());
                    } else if (section.equalsIgnoreCase("MYID")) {
                        builder.append(_proxy.myID());
                    } else if (section.equalsIgnoreCase("NODES")) {
                        builder.append(_proxy.nodes());
                    } else if (section.equalsIgnoreCase("SLOTS")) {
                        builder.append(_proxy.slots());
                    }
                }else if(objects.length == 3){
                    String section = Utils.bytesToString(objects[1]);
                    if(section.equalsIgnoreCase("KEYSLOT")){
                        builder.append(_proxy.keySlot(Utils.bytesToString(objects[2])));
                    }
                }
                else {
                    if(logger.isDebugEnabled()) {
                        for (int i = 0; i < objects.length; i++) {
                            logger.debug("%s \n",Utils.bytesToString(objects[i]));
                            for(int j=0;j < objects[i].length;j++){
                                logger.debug("  -->%s\n",Utils.bytesToString(objects[j]));
                            }
                        }
                    }
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

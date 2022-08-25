package com.netease.nim.camellia.redis.proxy.command.async.cluster;

import com.netease.nim.camellia.redis.proxy.command.Command;
import com.netease.nim.camellia.redis.proxy.command.async.info.ProxyInfoUtils;
import com.netease.nim.camellia.redis.proxy.reply.BulkReply;
import com.netease.nim.camellia.redis.proxy.reply.ErrorReply;
import com.netease.nim.camellia.redis.proxy.reply.Reply;
import com.netease.nim.camellia.redis.proxy.util.ErrorLogCollector;
import com.netease.nim.camellia.redis.proxy.util.Utils;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ClusterProxyInfoUtils {
    
    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(8), new DefaultThreadFactory("cluster-info"));
    /**
     * 获取集群信息
     * @param cmd
     * @return
     */
    public static CompletableFuture<Reply> getInfoReply(Command cmd){
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
    public static String getInfo(){
        return "cluster_state:ok\n" + "cluster_slots_assigned:16384\n" + "cluster_slots_ok:16384\n" + "cluster_slots_pfail:0\n" + "cluster_slots_fail:0\n" + "cluster_known_nodes:6\n" + "cluster_size:3\n" + "cluster_current_epoch:6\n" + "cluster_my_epoch:2\n" + "cluster_stats_messages_ping_sent:39443\n" + "cluster_stats_messages_pong_sent:38796\n" + "cluster_stats_messages_meet_sent:1\n" + "cluster_stats_messages_sent:78240\n" + "cluster_stats_messages_ping_received:38796\n" + "cluster_stats_messages_pong_received:39444\n" + "cluster_stats_messages_received:78240";
    }
    public static String MYID(){
        return "\"61518001ccc1c25fd3cd88508671cfe9a4ecbcf0\"";
    }
    public static String NODES(){
        return "61518001ccc1c25fd3cd88508671cfe9a4ecbcf0 127.0.0.1:6380@16380 myself,master - 0 1661441117000 2 connected 5461-10922\n" + "afa13cd9b740bc4361dcef17ae2af4341beb8edb 127.0.0.1:6379@16379 master - 0 1661441119975 1 connected 0-5460\n" + "9b99800a522802de1009e792cf9b460b3607f66d 127.0.0.1:6384@16384 slave 61518001ccc1c25fd3cd88508671cfe9a4ecbcf0 0 1661441118000 2 connected\n" + "a4707b106c076b7b86a3d3bd974b7e68188b001c 127.0.0.1:6381@16381 master - 0 1661441117962 3 connected 10923-16383\n" + "42c0d981e30ccd13820ec14235d85ea10eb5d0f9 127.0.0.1:6382@16382 slave a4707b106c076b7b86a3d3bd974b7e68188b001c 0 1661441119000 3 connected\n" + "71391083b7c17675a715fae3687eb84fa93f1878 127.0.0.1:6383@16383 slave afa13cd9b740bc4361dcef17ae2af4341beb8edb 0 1661441119000 1 connected";
    }
    public static String SLOTS(){
        return "1) \n 1) (integer) 0\n" + "   2) (integer) 5460\n" + "   3) 1) \"127.0.0.1\"\n" + "      2) (integer) 6379\n" + "      3) \"afa13cd9b740bc4361dcef17ae2af4341beb8edb\"\n" + "   4) 1) \"127.0.0.1\"\n" + "      2) (integer) 6383\n" + "      3) \"71391083b7c17675a715fae3687eb84fa93f1878\"\n" + "2) 1) (integer) 5461\n" + "   2) (integer) 10922\n" + "   3) 1) \"127.0.0.1\"\n" + "      2) (integer) 6380\n" + "      3) \"61518001ccc1c25fd3cd88508671cfe9a4ecbcf0\"\n" + "   4) 1) \"127.0.0.1\"\n" + "      2) (integer) 6384\n" + "      3) \"9b99800a522802de1009e792cf9b460b3607f66d\"\n" + "3) 1) (integer) 10923\n" + "   2) (integer) 16383\n" + "   3) 1) \"127.0.0.1\"\n" + "      2) (integer) 6381\n" + "      3) \"a4707b106c076b7b86a3d3bd974b7e68188b001c\"\n" + "   4) 1) \"127.0.0.1\"\n" + "      2) (integer) 6382\n" + "      3) \"42c0d981e30ccd13820ec14235d85ea10eb5d0f9\"";
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
                        builder.append(getInfo()).append("\r\n");
                    } else if (section.equalsIgnoreCase("MYID")) {
                        builder.append(MYID()).append("\r\n");
                    } else if (section.equalsIgnoreCase("NODES")) {
                        builder.append(NODES()).append("\r\n");
                    } else if (section.equalsIgnoreCase("SLOTS")) {
                        builder.append(SLOTS()).append("\r\n");
                    }
                }  else {
                    return ErrorReply.SYNTAX_ERROR;
                }
            }
            return new BulkReply(Utils.stringToBytes(builder.toString()));
        } catch (Exception e) {
            ErrorLogCollector.collect(ProxyInfoUtils.class, "getInfoReply error", e);
            return new ErrorReply("generate proxy info error");
        }
    }
    
}

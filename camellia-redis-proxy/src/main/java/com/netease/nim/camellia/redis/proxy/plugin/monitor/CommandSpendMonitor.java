package com.netease.nim.camellia.redis.proxy.plugin.monitor;

import com.netease.nim.camellia.core.util.CamelliaMapUtils;
import com.netease.nim.camellia.redis.proxy.monitor.model.BidBgroupSpendStats;
import com.netease.nim.camellia.redis.proxy.monitor.model.SpendStats;
import com.netease.nim.camellia.redis.proxy.util.MaxValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * 命令的耗时
 */
public class CommandSpendMonitor {

    private static final Logger logger = LoggerFactory.getLogger(CommandSpendMonitor.class);

    private static final ConcurrentHashMap<String, LongAdder> commandSpendCountMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, LongAdder> commandSpendTotalMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, MaxValue> commandSpendMaxMap = new ConcurrentHashMap<>();

    public static void incr(Long bid, String bgroup, String command, long spendNanoTime) {
        try {
            String key = bid + "|" + bgroup + "|" + command;
            CamelliaMapUtils.computeIfAbsent(commandSpendCountMap, key, k -> new LongAdder()).increment();
            CamelliaMapUtils.computeIfAbsent(commandSpendTotalMap, key, k -> new LongAdder()).add(spendNanoTime);
            MaxValue maxValue = CamelliaMapUtils.computeIfAbsent(commandSpendMaxMap, key, k -> new MaxValue());
            maxValue.update(spendNanoTime);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static class CommandSpendStats {
        public List<BidBgroupSpendStats> bidBgroupSpendStatsList = new ArrayList<>();
        public List<SpendStats> spendStatsList = new ArrayList<>();
    }

    public static CommandSpendStats collect() {
        List<BidBgroupSpendStats> list = new ArrayList<>();
        ConcurrentHashMap<String, AtomicLong> spendSumMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, SpendStats> spendStatsMap = new ConcurrentHashMap<>();
        for (Map.Entry<String, LongAdder> entry : commandSpendCountMap.entrySet()) {
            String key = entry.getKey();
            long count = entry.getValue().sumThenReset();
            if (count == 0) continue;
            MaxValue nanoMax = commandSpendMaxMap.get(key);
            double maxSpendMs = 0;
            if (nanoMax != null) {
                maxSpendMs = nanoMax.getAndSet(0) / 1000000.0;
            }
            double avgSpendMs = 0;
            LongAdder nanoSum = commandSpendTotalMap.get(key);
            long sum = 0;
            if (nanoSum != null) {
                sum = nanoSum.sumThenReset();
                avgSpendMs = sum / (1000000.0 * count);
            }
            String[] split = key.split("\\|");
            Long bid = null;
            if (!split[0].equals("null")) {
                bid = Long.parseLong(split[0]);
            }
            String bgroup = null;
            if (!split[1].equals("null")) {
                bgroup = split[1];
            }
            String command = split[2];
            BidBgroupSpendStats bidBgroupSpendStats = new BidBgroupSpendStats();
            bidBgroupSpendStats.setBid(bid);
            bidBgroupSpendStats.setBgroup(bgroup);
            bidBgroupSpendStats.setCommand(command);
            bidBgroupSpendStats.setAvgSpendMs(avgSpendMs);
            bidBgroupSpendStats.setMaxSpendMs(maxSpendMs);
            bidBgroupSpendStats.setCount(count);
            list.add(bidBgroupSpendStats);

            SpendStats spendStats = CamelliaMapUtils.computeIfAbsent(spendStatsMap, command, string -> {
                SpendStats bean = new SpendStats();
                bean.setCommand(string);
                return bean;
            });
            spendStats.setCount(spendStats.getCount() + bidBgroupSpendStats.getCount());
            if (bidBgroupSpendStats.getMaxSpendMs() > spendStats.getMaxSpendMs()) {
                spendStats.setMaxSpendMs(bidBgroupSpendStats.getMaxSpendMs());
            }
            CamelliaMapUtils.computeIfAbsent(spendSumMap, command, k -> new AtomicLong()).addAndGet(sum);
        }
        for (Map.Entry<String, SpendStats> entry : spendStatsMap.entrySet()) {
            String command = entry.getKey();
            SpendStats spendStats = entry.getValue();
            AtomicLong sum = spendSumMap.get(command);
            if (sum == null) {
                spendStats.setAvgSpendMs(0.0);
            } else {
                spendStats.setAvgSpendMs(sum.get() / (1000000.0 * spendStats.getCount()));
            }
        }
        CommandSpendStats commandSpendStats = new CommandSpendStats();
        commandSpendStats.bidBgroupSpendStatsList = list;
        commandSpendStats.spendStatsList = new ArrayList<>(spendStatsMap.values());
        return commandSpendStats;
    }
}

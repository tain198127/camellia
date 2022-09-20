package com.netease.nim.camellia.redis.proxy.plugin.converter;

import com.netease.nim.camellia.redis.proxy.command.Command;
import com.netease.nim.camellia.redis.proxy.command.CommandContext;
import com.netease.nim.camellia.redis.proxy.conf.ProxyDynamicConf;
import com.netease.nim.camellia.redis.proxy.enums.RedisCommand;
import com.netease.nim.camellia.redis.proxy.plugin.*;
import com.netease.nim.camellia.redis.proxy.upstream.utils.PubSubUtils;
import com.netease.nim.camellia.redis.proxy.util.BeanInitUtils;

/**
 * Created by caojiajun on 2022/9/14
 */
public class ConverterProxyPlugin implements ProxyPlugin {

    private Converters converters;

    @Override
    public void init(ProxyBeanFactory factory) {
        ConverterConfig converterConfig = new ConverterConfig();
        String keyConverterClassName = ProxyDynamicConf.getString("converter.key.className", null);
        if (keyConverterClassName != null) {
            KeyConverter keyConverter = (KeyConverter)factory.getBean(BeanInitUtils.parseClass(keyConverterClassName));
            converterConfig.setKeyConverter(keyConverter);
        }
        String stringConverterClassName = ProxyDynamicConf.getString("converter.string.className", null);
        if (stringConverterClassName != null) {
            StringConverter stringConverter = (StringConverter)factory.getBean(BeanInitUtils.parseClass(stringConverterClassName));
            converterConfig.setStringConverter(stringConverter);
        }
        String hashConverterClassName = ProxyDynamicConf.getString("converter.hash.className", null);
        if (hashConverterClassName != null) {
            HashConverter hashConverter = (HashConverter)factory.getBean(BeanInitUtils.parseClass(hashConverterClassName));
            converterConfig.setHashConverter(hashConverter);
        }
        String setConverterClassName = ProxyDynamicConf.getString("converter.set.className", null);
        if (setConverterClassName != null) {
            SetConverter setConverter = (SetConverter)factory.getBean(BeanInitUtils.parseClass(setConverterClassName));
            converterConfig.setSetConverter(setConverter);
        }
        String zsetConverterClassName = ProxyDynamicConf.getString("converter.zset.className", null);
        if (zsetConverterClassName != null) {
            ZSetConverter zsetConverter = (ZSetConverter)factory.getBean(BeanInitUtils.parseClass(zsetConverterClassName));
            converterConfig.setzSetConverter(zsetConverter);
        }
        String listConverterClassName = ProxyDynamicConf.getString("converter.list.className", null);
        if (listConverterClassName != null) {
            ListConverter listConverter = (ListConverter)factory.getBean(BeanInitUtils.parseClass(listConverterClassName));
            converterConfig.setListConverter(listConverter);
        }
        converters = new Converters(converterConfig);
    }

    @Override
    public ProxyPluginOrder order() {
        return new ProxyPluginOrder() {
            @Override
            public int request() {
                return BuildInProxyPluginEnum.CONVERTER_PLUGIN.getRequestOrder();
            }

            @Override
            public int reply() {
                return BuildInProxyPluginEnum.CONVERTER_PLUGIN.getReplyOrder();
            }
        };
    }

    @Override
    public ProxyPluginResponse executeRequest(ProxyRequest request) {
        Command command = request.getCommand();
        if (command != null) {
            converters.convertRequest(request.getCommand());
        }
        return ProxyPluginResponse.SUCCESS;
    }

    @Override
    public ProxyPluginResponse executeReply(ProxyReply reply) {
        if (reply.isFromPlugin()) return ProxyPluginResponse.SUCCESS;
        Command command = reply.getCommand();
        if (command != null) {
            converters.convertReply(command, reply.getReply());
        } else {
            //如果是subscribe的响应，不是[请求-响应]的模式，则没有command实体
            RedisCommand redisCommand = reply.getRedisCommand();
            CommandContext commandContext = reply.getCommandContext();
            if (redisCommand == RedisCommand.SUBSCRIBE || redisCommand == RedisCommand.PSUBSCRIBE
                    || redisCommand == RedisCommand.UNSUBSCRIBE || redisCommand == RedisCommand.PUNSUBSCRIBE) {
                PubSubUtils.checkKeyConverter(redisCommand, commandContext, converters.getKeyConverter(), reply.getReply());
            }
        }
        return ProxyPluginResponse.SUCCESS;
    }
}

package cn.bubi.sdk.core.seq.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/12/14 下午5:36.
 */
public class RedisClient{

    private String seqAddressPrefix = "seqManagerAddress:";
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisClient.class);
    private ShardedJedisPool pool = null;
    private List<RedisConfig> redisConfigs;

    public RedisClient(List<RedisConfig> redisConfigs){
        this.redisConfigs = redisConfigs;
    }

    public void init(){
        if (pool == null) {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(10000);
            config.setMaxIdle(2000);
            config.setMaxWaitMillis(1000 * 100);
            config.setTestOnBorrow(true);
            config.setTestOnReturn(true);

            List<JedisShardInfo> shards = redisConfigs.stream().map(redisConfig -> {
                JedisShardInfo shardInfo = new JedisShardInfo(redisConfig.getHost(), redisConfig.getPort());
                shardInfo.setPassword(redisConfig.getPassword());
                return shardInfo;
            }).collect(Collectors.toList());

            pool = new ShardedJedisPool(config, shards);
        }
    }

    public void setSeq(String address, Long value){
        if (value == null) {
            return;
        }
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.set(seqAddressPrefix + address, value.toString());
        } catch (Exception e) {
            LOGGER.error("", e);
        } finally {
            pool.returnResource(jedis);
        }
    }

    public Long getSeq(String address){
        ShardedJedis jedis = null;
        try {
            jedis = pool.getResource();
            String value = jedis.get(seqAddressPrefix + address);
            if (value != null) {
                return Long.valueOf(value);
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        } finally {
            pool.returnResource(jedis);
        }
        return null;
    }

    public ShardedJedisPool getPool(){
        return pool;
    }
}
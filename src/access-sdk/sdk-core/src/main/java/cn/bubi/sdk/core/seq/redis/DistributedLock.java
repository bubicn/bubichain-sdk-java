package cn.bubi.sdk.core.seq.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisException;

import java.util.List;
import java.util.UUID;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/12/14 下午5:36.
 */
public class DistributedLock{

    private String lockPrefix = "seqManagerLock:";
    private static final Logger LOGGER = LoggerFactory.getLogger(DistributedLock.class);
    private final ShardedJedisPool jedisPool;

    public DistributedLock(ShardedJedisPool jedisPool){
        this.jedisPool = jedisPool;
    }

    /**
     * 非公平性自旋获取锁
     *
     * @param lockName       锁的key
     * @param acquireTimeout 获取超时时间
     * @param timeout        锁的超时时间
     * @return 锁标识
     */
    public String lockWithTimeout(String lockName, long acquireTimeout, long timeout){
        Jedis conn = null;
        String retIdentifier = null;
        ShardedJedis shardedJedis = jedisPool.getResource();
        try {
            // 随机生成一个value
            String identifier = UUID.randomUUID().toString();
            // 锁名，即key值
            String lockKey = lockPrefix + lockName;
            // 超时时间，上锁后超过此时间则自动释放锁
            int lockExpire = (int) (timeout / 1000);
            // 获取连接
            conn = shardedJedis.getShard(lockKey);

            // 获取锁的超时时间，超过这个时间则放弃获取锁
            long end = System.currentTimeMillis() + acquireTimeout;
            while (System.currentTimeMillis() < end) {
                if (conn.setnx(lockKey, identifier) == 1) {
                    conn.expire(lockKey, lockExpire);
                    // 返回value值，用于释放锁时间确认
                    retIdentifier = identifier;
                    return retIdentifier;
                }
                // 返回-1代表key没有设置超时时间，为key设置一个超时时间
                if (conn.ttl(lockKey) == -1) {
                    conn.expire(lockKey, lockExpire);
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (JedisException e) {
            LOGGER.error("lockWithTimeout found exception", e);
        } finally {
            if (conn != null) {
                jedisPool.returnResource(shardedJedis);
            }
        }
        return retIdentifier;
    }

    /**
     * 释放锁
     *
     * @param lockName   锁的key
     * @param identifier 释放锁的标识
     */
    public boolean releaseLock(String lockName, String identifier){
        Jedis conn = null;
        String lockKey = lockPrefix + lockName;
        boolean retFlag = false;
        ShardedJedis shardedJedis = jedisPool.getResource();
        try {
            conn = shardedJedis.getShard(lockKey);
            while (true) {
                // 监视lock，准备开始事务
                conn.watch(lockKey);
                // 通过前面返回的value值判断是不是该锁，若是该锁，则删除，释放锁
                if (identifier.equals(conn.get(lockKey))) {
                    Transaction transaction = conn.multi();
                    transaction.del(lockKey);
                    List<Object> results = transaction.exec();
                    if (results == null) {
                        continue;
                    }
                    retFlag = true;
                }
                conn.unwatch();
                break;
            }
        } catch (JedisException e) {
            LOGGER.error("releaseLock found exception", e);
        } finally {
            if (conn != null) {
                jedisPool.returnResource(shardedJedis);
            }
        }
        return retFlag;
    }
}
package cn.bubi.sdk.core.transaction;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/11/1 下午5:15.
 * 提供一个带缓冲区的缓存交易容器,默认4000条
 */
public class TransactionContent{

    private static final int QUEUE_LEN = 4;

    private static final BufferMap<String, Transaction> CACHE = new BufferMap<>(QUEUE_LEN);

    public static void put(String hash, Transaction transaction){
        CACHE.put(hash, transaction);
    }

    public static Transaction get(String hash){
        return CACHE.get(hash);
    }

    /**
     * 注意这里的queueLen为实际缓存大小queueLen*1000
     */
    public static void changeQueueLen(int queueLen){
        CACHE.queueLen = queueLen;
    }


    private static class BufferMap<K, V>{
        private int queueLen = 4;
        ArrayList<HashMap<K, V>> queue = new ArrayList<>();
        private final int bufferSize = 1000;

        private BufferMap(int queueLen){
            this.queueLen = queueLen;
            queue.add(new HashMap<>());
        }

        private void put(K key, V value){

            if (queue.get(queue.size() - 1).size() >= bufferSize) {
                if (queue.size() < queueLen) {
                    queue.add(new HashMap<>());
                } else {
                    HashMap<K, V> remove = queue.remove(0);
                    remove.clear();
                    queue.add(remove);
                }
            }

            queue.get(queue.size() - 1).put(key, value);
        }

        private V get(K key){
            for (HashMap<K, V> hm : queue) {
                if (hm.containsKey(key)) {
                    return hm.get(key);
                }

            }
            return null;
        }
    }

}

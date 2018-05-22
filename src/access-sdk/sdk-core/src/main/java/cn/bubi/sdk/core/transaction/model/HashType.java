package cn.bubi.sdk.core.transaction.model;

import cn.bubi.encryption.utils.HashUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/11/30 下午12:02.
 */
public enum HashType{
    SHA256(0),
    SM3(1),;

    private int value;

    HashType(int value){
        this.value = value;
    }

    private static final Map<Integer, HashType> TYPE_MAPPING = new HashMap<>();

    static{
        for (HashType hashType : HashType.values()) {
            TYPE_MAPPING.put(hashType.getValue(), hashType);
        }
    }

    public static HashType getHashType(int value){
        HashType hashType = TYPE_MAPPING.get(value);
        if (hashType == null) throw new RuntimeException("not support hash type :" + value);
        return hashType;
    }

    public String hash2Hex(byte[] origin){
        try {
            return HashUtil.GenerateHashHex(origin, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int getValue(){
        return value;
    }
}

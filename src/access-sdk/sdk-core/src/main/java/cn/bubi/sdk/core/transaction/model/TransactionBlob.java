package cn.bubi.sdk.core.transaction.model;

import cn.bubi.access.utils.io.ByteBlob;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/25 上午11:20.
 */
public class TransactionBlob{

    private String hash;
    private ByteBlob bytesBlob;

    public TransactionBlob(byte[] bytes, HashType hashType){
        this.bytesBlob = ByteBlob.wrap(bytes);
        this.hash = hashType.hash2Hex(bytes);
    }

    public String getHash(){
        return hash;
    }

    public ByteBlob getBytes(){
        return bytesBlob;
    }

    public String getHex(){
        return bytesBlob.toHexString();
    }

}

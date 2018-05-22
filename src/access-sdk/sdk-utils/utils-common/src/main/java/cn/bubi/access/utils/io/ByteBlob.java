package cn.bubi.access.utils.io;

import cn.bubi.access.utils.codec.HexUtils;
import cn.bubi.access.utils.spring.Base64Utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * BytesBlob 二进制字节块是对字节数组的包装，目的是提供一种不可变的二进制数据结构；
 *
 * @author haiq
 */
public class ByteBlob{

    private byte[] bytes;

    private ByteBlob(byte[] bytes){
        this.bytes = bytes;
    }


    public static ByteBlob wrap(byte[] bytes){
        byte[] replica = Arrays.copyOf(bytes, bytes.length);
        return new ByteBlob(replica);
    }


    public void writeTo(OutputStream out){
        try {
            out.write(bytes);
        } catch (IOException e) {
            throw new RuntimeIOException(e.getMessage(), e);
        }
    }

    public InputStream asInputStream(){
        return new ByteArrayInputStream(bytes);
    }

    public ByteBuffer asReadOnlyBuffer(){
        return ByteBuffer.wrap(bytes).asReadOnlyBuffer();
    }

    public byte[] toBytes(){
        return Arrays.copyOf(bytes, bytes.length);
    }

    public String toHexString(){
        return HexUtils.encode(bytes);
    }

    public String toBase64String(){
        return Base64Utils.encodeToString(bytes);
    }

    public String toBase64UrlString(){
        return Base64Utils.encodeToUrlSafeString(bytes);
    }

    public static ByteBlob parseHexString(String hexString){
        byte[] bytes = HexUtils.decode(hexString);
        return wrap(bytes);
    }

    public static ByteBlob parseBase64String(String base64String){
        byte[] bytes = Base64Utils.decodeFromString(base64String);
        return wrap(bytes);
    }

    public static ByteBlob parseBase64UrlString(String base64String){
        byte[] bytes = Base64Utils.decodeFromUrlSafeString(base64String);
        return wrap(bytes);
    }
}

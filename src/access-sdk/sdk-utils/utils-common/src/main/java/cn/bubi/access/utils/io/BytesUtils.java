package cn.bubi.access.utils.io;


import cn.bubi.access.utils.IllegalDataException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 二进制工具类；
 *
 * @author haiq
 */
public class BytesUtils{

    public static final int MAX_BUFFER_SIZE = 100 * 1024 * 1024;
    public static final int BUFFER_SIZE = 1024;

    private BytesUtils(){
    }

    public static boolean compare(byte[] bytes1, byte[] bytes2){
        if (bytes1.length != bytes2.length) {
            return false;
        }
        for (int i = 0; i < bytes1.length; i++) {
            if (bytes1[i] != bytes2[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 将输入流的所有内容都读入到字节数组返回；
     * <p>
     * 如果输入流的长度超出 MAX_BUFFER_SIZE 定义的值，则抛出 IllegalArgumentException ;
     *
     * @param in
     * @return
     * @throws IOException
     */
    public static byte[] copyToBytes(InputStream in) throws IOException{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int len = 0;
        long size = 0;
        while ((len = in.read(buffer)) > 0) {
            size += len;
            if (size > MAX_BUFFER_SIZE) {
                throw new IllegalArgumentException(
                        "The size of the InputStream exceed the max buffer size [" + MAX_BUFFER_SIZE + "]!");
            }
            out.write(buffer, 0, len);
        }
        return out.toByteArray();
    }

    /**
     * 将输入流复制到输出流；
     *
     * @param in      输入流；
     * @param out     输出流；
     * @param maxSize 最大字节大小；
     * @return 返回实际复制的字节数；
     * @throws IOException
     */
    public static int copy(InputStream in, OutputStream out, int maxSize) throws IOException{
        byte[] buffer = new byte[BUFFER_SIZE];
        int len = 0;
        int left = maxSize;
        int readLen = buffer.length;
        while (left > 0) {
            readLen = Math.min(left, buffer.length);
            len = in.read(buffer, 0, readLen);
            if (len > 0) {
                out.write(buffer, 0, len);
                left = left - len;
            } else {
                break;
            }
        }
        return maxSize - left;
    }

    /**
     * 将 int 值转为4字节的二进制数组；
     *
     * @param value
     * @return 转换后的二进制数组，高位在前，低位在后；
     */
    public static byte[] toBytes(int value){
        byte[] bytes = new byte[4];
        toBytes(value, bytes, 0);
        return bytes;
    }

    /**
     * 将 int 值转为4字节的二进制数组；
     *
     * @param value 要转换的int整数；
     * @param bytes 要保存转换结果的二进制数组；转换结果将从高位至低位的顺序写入数组从 0 开始的4个元素；
     */
    public static void toBytes(int value, byte[] bytes){
        toBytes(value, bytes, 0);
    }

    /**
     * 将 int 值转为4字节的二进制数组；
     *
     * @param value  要转换的int整数；
     * @param bytes  要保存转换结果的二进制数组；转换结果将从高位至低位的顺序写入数组从 offset 指定位置开始的4个元素；
     * @param offset 写入转换结果的起始位置；
     */
    public static void toBytes(int value, byte[] bytes, int offset){
        bytes[offset] = (byte) ((value >> 24) & 0x00FF);
        bytes[offset + 1] = (byte) ((value >> 16) & 0x00FF);
        bytes[offset + 2] = (byte) ((value >> 8) & 0x00FF);
        bytes[offset + 3] = (byte) (value & 0x00FF);
    }

    /**
     * 按从高位到低位的顺序将指定二进制数组从位置 0 开始的 4 个字节转换为 int 整数；
     *
     * @param bytes 要转换的二进制数组；
     * @return 转换后的 int 整数；
     */
    public static int toInt(byte[] bytes){
        int value = 0;
        value = (value | (bytes[0] & 0xFF)) << 8;
        value = (value | (bytes[1] & 0xFF)) << 8;
        value = (value | (bytes[2] & 0xFF)) << 8;
        value = value | (bytes[3] & 0xFF);

        return value;
    }

    /**
     * 按从高位到低位的顺序将指定二进制数组从 offset 参数指定的位置开始的 4 个字节转换为 int 整数；
     *
     * @param bytes  要转换的二进制数组；
     * @param offset 要读取数据的开始位置
     * @return 转换后的 int 整数；
     */
    public static int toInt(byte[] bytes, int offset){
        int value = 0;
        value = (value | (bytes[offset] & 0xFF)) << 8;
        value = (value | (bytes[offset + 1] & 0xFF)) << 8;
        value = (value | (bytes[offset + 2] & 0xFF)) << 8;
        value = value | (bytes[offset + 3] & 0xFF);

        return value;
    }

    /**
     * 从指定的输入流中读入4个字节，由前到后按由高位到低位的方式转为 int 整数；
     *
     * @param in
     * @return
     * @throws IOException
     */
    public static int readInt(InputStream in) throws IOException{
        byte[] buf = new byte[4];
        if (in.read(buf) < 4) {
            throw new IllegalDataException("No enough data to read as integer from the specified input stream!");
        }
        return toInt(buf);
    }
}

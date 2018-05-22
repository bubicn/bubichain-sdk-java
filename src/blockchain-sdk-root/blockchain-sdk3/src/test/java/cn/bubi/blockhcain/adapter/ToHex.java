package cn.bubi.blockhcain.adapter;

/** 
 * reference apache commons <a 
 * href="http://commons.apache.org/codec/">http://commons.apache.org/codec/</a> 
 *  
 * @author Aub 
 *  
 */  
public class ToHex {  
  
    /** 
     * 用于建立十六进制字符的输出的小写字符数组 
     */  
    private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5',  
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };  
  
    /** 
     * 用于建立十六进制字符的输出的大写字符数组 
     */  
    private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5',  
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };  
  
    /** 
     * 将字节数组转换为十六进制字符数组 
     *  
     * @param data 
     *            byte[] 
     * @return 十六进制char[] 
     */  
    public static char[] encodeHex(byte[] data, int start, int end, boolean bis_big) {  
        return encodeHex(data, start, end, bis_big, true);  
    }
    
    /** 
     * 将字节数组转换为十六进制字符数组 
     *  
     * @param data 
     *            byte[] 
     * @return 十六进制char[] 
     */  
    public static char[] encodeHex(byte[] data) {  
        return encodeHex(data, 0, data.length, false, true);  
    }  
  
    /** 
     * 将字节数组转换为十六进制字符数组 
     *  
     * @param data 
     *            byte[] 
     * @param toLowerCase 
     *            <code>true</code> 传换成小写格式 ， <code>false</code> 传换成大写格式 
     * @return 十六进制char[] 
     */  
    public static char[] encodeHex(byte[] data, int start, int end, boolean bis_big, boolean toLowerCase) {  
        return encodeHex(data, start, end, bis_big, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);  
    }  
  
    /** 
     * 将字节数组转换为十六进制字符数组 
     *  
     * @param data 
     *            byte[] 
     * @param toDigits 
     *            用于控制输出的char[] 
     * @return 十六进制char[] 
     */  
    protected static char[] encodeHex(byte[] data, int start, int end, boolean bis_big, char[] toDigits) {  
        int l = end - start;  
        char[] out = new char[l << 1];  
        // two characters form the hex value.  
        for (int i = start, j = 0; i < end; i++) {  
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];  
            if (i == end - 1 && bis_big) {
            	out[j++] = toDigits[0x0F & (0 == (data[i] & 0x01) ? (data[i] + 1) : (data[i]))];
            }
            else {
            	out[j++] = toDigits[0x0F & data[i]];
            }
        }  
        return out;  
    }  
  
    /** 
     * 将字节数组转换为十六进制字符串 
     *  
     * @param data 
     *            byte[] 
     * @return 十六进制String 
     */  
    public static String encodeHexStr(byte[] data, int start, int end, boolean bis_big) {  
        return encodeHexStr(data, start, end, bis_big, true);  
    }  
    
    /** 
     * 将字节数组转换为十六进制字符串 
     *  
     * @param data 
     *            byte[] 
     * @return 十六进制String 
     */  
    public static String encodeHexStr(byte[] data) {
        return encodeHexStr(data, 0, data.length, false, true);  
    }  
  
    /** 
     * 将字节数组转换为十六进制字符串 
     *  
     * @param data 
     *            byte[] 
     * @param toLowerCase 
     *            <code>true</code> 传换成小写格式 ， <code>false</code> 传换成大写格式 
     * @return 十六进制String 
     */  
    public static String encodeHexStr(byte[] data, int start, int end, boolean bis_big, boolean toLowerCase) {  
        return encodeHexStr(data, start, end, bis_big, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);  
    }  
  
    /** 
     * 将字节数组转换为十六进制字符串 
     *  
     * @param data 
     *            byte[] 
     * @param toDigits 
     *            用于控制输出的char[] 
     * @return 十六进制String 
     */  
    protected static String encodeHexStr(byte[] data, int start, int end, boolean bis_big, char[] toDigits) {  
        return new String(encodeHex(data, start, end, bis_big, toDigits));  
    }  
  
    /** 
     * 将十六进制字符数组转换为字节数组 
     *  
     * @param data 
     *            十六进制char[] 
     * @return byte[] 
     * @throws RuntimeException 
     *             如果源十六进制字符数组是一个奇怪的长度，将抛出运行时异常 
     */  
    public static byte[] decodeHex(char[] data) {  
  
        int len = data.length;  
  
        if ((len & 0x01) != 0) {  
            throw new RuntimeException("Odd number of characters.");  
        }  
  
        byte[] out = new byte[len >> 1];  
  
        // two characters form the hex value.  
        for (int i = 0, j = 0; j < len; i++) {  
            int f = toDigit(data[j], j) << 4;  
            j++;  
            f = f | toDigit(data[j], j);  
            j++;  
            out[i] = (byte) (f & 0xFF);  
        }  
  
        return out;  
    }  
  
    /** 
     * 将十六进制字符转换成一个整数 
     *  
     * @param ch 
     *            十六进制char 
     * @param index 
     *            十六进制字符在字符数组中的位置 
     * @return 一个整数 
     * @throws RuntimeException 
     *             当ch不是一个合法的十六进制字符时，抛出运行时异常 
     */  
    protected static int toDigit(char ch, int index) {  
        int digit = Character.digit(ch, 16);  
        if (digit == -1) {  
            throw new RuntimeException("Illegal hexadecimal character " + ch  
                    + " at index " + index);  
        }  
        return digit;  
    }  
    
    public static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
	 public static String bytesToHex(byte[] raw) {
	        if ( raw == null ) {
	            return null;
	        }
	        final StringBuilder hex = new StringBuilder(2 * raw.length);
	        for (final byte b : raw) {
	            hex.append(Character.forDigit((b & 0xF0) >> 4, 16))
	            .append(Character.forDigit((b & 0x0F), 16));
	        }
	        return hex.toString();
	    }
  
    public static void main(String[] args) {  
        String srcStr = "待转换字符串";  
        String encodeStr = encodeHexStr(srcStr.getBytes());  
        String decodeStr = new String(decodeHex(encodeStr.toCharArray()));  
        System.out.println("转换前：" + srcStr);  
        System.out.println("转换后：" + encodeStr);  
        System.out.println("还原后：" + decodeStr);  
    }  
  
}  
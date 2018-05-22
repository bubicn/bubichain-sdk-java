package cn.bubi.encryption.utils;

import cfca.sadk.algorithm.common.Mechanism;

public class HashUtil{

    /**
     * generate hex string of hash
     *
     * @param type 0(SHA256) or 1(SM3)
     * @return hex string of hash
     */
    public static String GenerateHashHex(byte[] src, Integer type) throws Exception{
        byte[] hash;
        if (type == 0) {
            hash = cfca.sadk.util.HashUtil.RSAHashMessageByBC(src, new Mechanism(Mechanism.SHA256), false);
        } else if (type == 1) {
            hash = cfca.sadk.util.HashUtil.SM2HashMessageByBCWithoutZValue(src);
        } else {
            throw new RuntimeException("type is invalid");
        }
        return HexFormat.byteToHex(hash).toLowerCase();
    }

}

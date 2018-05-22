package cn.bubi.access.utils.blockchain;

import cn.bubi.access.utils.spring.Base64Utils;
import cn.bubi.encryption.BubiKey;
import cn.bubi.encryption.BubiKeyType;

/**
 * v3 {布比私钥，布比公钥，布比地址}
 */
public class SecureKeyGenerator{


    public static BlockchainKeyPair generateBubiKeyPair(){
        return BlockchainKeyPairFactory.generateBubiKeyPair(BubiKeyType.ED25519);
    }

    public static String getPublicKey(String privateKey){
        return BlockchainKeyPairFactory.getPublicKeyV3(privateKey);
    }

    public static BlockchainKeyPair generateCfcaAddress(String base64CfcaDigest){
        try {
            byte[] cfcaDigest = Base64Utils.decodeFromString(base64CfcaDigest);

            BubiKey bubiKey = new BubiKey(cfcaDigest);
            String address = bubiKey.getB16Address();
            String publicKey = bubiKey.getB16PublicKey();

            return new BlockchainKeyPair(null, publicKey, address);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error occurred on generate CFCA address! --[" + e.getClass().getName() + "] --" + e.getMessage(), e);
        }
    }

}

package cn.bubi.encryption;

public enum BubiKeyType{
    /**
     * ED25519算法
     */
    ED25519,
    /**
     * 国家标准SM2算法，用国家推荐的椭圆曲线参数，也是CFCA所用的参数
     */
    ECCSM2,
    /**
     * RSA签名算法
     */
    RSA,
    /**
     * CFCA签名算法
     */
    CFCA
}

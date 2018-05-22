package cn.bubi.sdk.core.pool;

import cn.bubi.sdk.core.spi.BcOperationService;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/11/20 上午10:10.
 * 账户池工厂，负责初始化账户池
 */
public interface SponsorAccountFactory{

    /**
     * 初始化账户池
     *
     * @param BcOperationService 操作对象
     * @param address            初始化发起人信息
     * @param publicKey          初始化发起人信息
     * @param privateKey         初始化发起人信息
     * @param size               账户池大小
     * @param filePath           存储文件路径，必须给绝对路径，如果不传默认config/sponsorAccountPool.txt
     * @param sponsorAccountMark 所有发起人账户的metadata标志位，key=$$$SponsorAccountPoolMark$$$
     * @return 账户池
     */
    SponsorAccountPool initPool(BcOperationService operationService, String address, String publicKey, String privateKey, Integer size, String filePath, String sponsorAccountMark);


}

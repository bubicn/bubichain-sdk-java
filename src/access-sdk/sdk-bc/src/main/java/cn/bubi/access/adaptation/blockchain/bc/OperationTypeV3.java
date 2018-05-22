package cn.bubi.access.adaptation.blockchain.bc;

/**
 * 3.0
 */
public enum OperationTypeV3{

    CREATE_ACCOUNT(1), // 创建账号
    ISSUE_ASSET(2), // 发行资产
    PAYMENT(3), // 转移资产
    SET_METADATA(4), // 设置metadata
    SET_SIGNER_WEIGHT(5), // set_signer_weight
    SET_THRESHOLD(6), // set_threshold
    ;

    private int value;

    private OperationTypeV3(int value){
        this.value = value;
    }

    public int intValue(){
        return value;
    }

    /**
     * 返回指定值对应的操作类型；如果没有，则返回 null；
     *
     * @param value
     * @return
     */
    public static OperationTypeV3 getOperationType(int value){
        for (OperationTypeV3 type : OperationTypeV3.values()) {
            if (type.intValue() == value) {
                return type;
            }
        }
        return null;
    }
}

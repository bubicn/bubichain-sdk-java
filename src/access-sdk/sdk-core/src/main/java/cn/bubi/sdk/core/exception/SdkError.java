package cn.bubi.sdk.core.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/24 下午3:07.
 */
public enum SdkError{

    SUCCESS(0, "成功"),

    OPERATION_ERROR_NOT_DESC_ADDRESS(10001, "操作必须设置目标地址"),
    OPERATION_ERROR_STATUS(10002, "操作状态异常"),
    OPERATION_ERROR_NOT_CONTRACT_ADDRESS(10003, "合约地址不能为空"),
    OPERATION_ERROR_ISSUE_SOURCE_ADDRESS(10004, "资产发行方不能为空"),
    OPERATION_ERROR_ISSUE_CODE(10005, "资产code不能为空"),
    OPERATION_ERROR_PAYMENT_AMOUNT_ZERO(10006, "转账金额必须大于0"),
    OPERATION_ERROR_SET_METADATA_EMPTY(10007, "设置metadata不能为空"),
    OPERATION_ERROR_SET_SIGNER_WEIGHT(10008, "必须设置masterWeight或signer"),
    OPERATION_ERROR_SET_THRESHOLD(10009, "必须设置txThreshold或typeThresholds"),
    OPERATION_ERROR_ISSUE_AMOUNT_ZERO(10010, "发行资产量不能小于0"),
    OPERATION_ERROR_TX_THRESHOLD_LT_ZERO(10011, "txThreshold不能小于0"),
    OPERATION_ERROR_MASTER_WEIGHT_LT_ZERO(10012, "masterWeight不能小于0"),
    OPERATION_ERROR_SINGER_WEIGHT_LT_ZERO(10013, "签名者权重不能小于0"),
    OPERATION_ERROR_TX_THRESHOLD_TYPE_LT_ZERO(10014, "具体操作门限不能小于0"),
    OPERATION_ERROR_SET_SIGNER_ADDRESS_NOT_EMPTY(10015, "添加签名者地址不能为空"),
    OPERATION_ERROR_TX_THRESHOLD_TYPE_NOT_NULL(10016, "具体门限操作类型不能为空"),

    TRANSACTION_ERROR_SPONSOR(10100, "交易发起人不能为空"),
    TRANSACTION_ERROR_SIGNATURE(10101, "交易签名列表不能为空"),
    TRANSACTION_ERROR_STATUS(10102, "交易状态异常"),
    TRANSACTION_ERROR_TIMEOUT(10103, "交易失败!未收到任何通知"),
    TRANSACTION_ERROR_BLOB_NOT_NULL(10104, "blob必须非空"),
    TRANSACTION_ERROR_PUBLIC_KEY_NOT_EMPTY(10105, "公钥不能为空"),
    TRANSACTION_ERROR_BLOB_REPEAT_GENERATOR(10106, "不要重复生成blob"),
    TRANSACTION_ERROR_OPERATOR_NOT_EMPTY(10107, "操作列表不能为空"),
    TRANSACTION_ERROR_PRIVATE_KEY_NOT_EMPTY(10108, "私钥不能为空"),


    PARSE_URI_ERROR(90001, "解析uri出错,请检查"),
    PARSE_IP_ERROR(90002, "解析ip出错,请检查"),
    NODE_MANAGER_INIT_ERROR(90003, "初始化节点管理器失败,请确认至少有一个节点可访问!"),
    RPC_INVOKE_ERROR_TIMEOUT(90004, "访问底层超时!"),
    EVENT_ERROR_SIGNATURE_VERIFY_FAIL(90005, "本地签名验证失败"),
    EVENT_ERROR_NOT_FOUND_HANDLE(90006, "没有发现注册的事件处理器"),
    SIGNATURE_ERROR_PUBLIC_PRIVATE(90007, "签名异常!请确认公钥或私钥正确性"),
    EVENT_ERROR_ROUTER_HOST_FAIL(90008, "路由节点失败!请确认监听配置和访问配置能够对应"),
    TRANSACTION_SYNC_TIMEOUT(90009, "超时异常:超出24小时未通知，交易同步器自动移除"),
    REDIS_ERROR_LOCK_TIMEOUT(90010, "获取redis锁超时！"),

    ERROR(99999, "内部错误"),;

    private int code;

    private String description;

    private static Map<Integer, SdkError> maps = new HashMap<>();

    static{
        for (SdkError error : SdkError.values()) {
            maps.put(error.getCode(), error);
        }
    }

    private SdkError(int code, String desc){
        this.code = code;
        this.description = desc;
    }

    public int getCode(){
        return code;
    }

    public String getDescription(){
        return description;
    }

    public static boolean isError(int code){
        return code != SUCCESS.code && maps.containsKey(code);
    }

    public static String getDescription(int code){
        SdkError error = maps.get(code);
        if (error == null) {
            return null;
        }
        return error.getDescription();
    }
}

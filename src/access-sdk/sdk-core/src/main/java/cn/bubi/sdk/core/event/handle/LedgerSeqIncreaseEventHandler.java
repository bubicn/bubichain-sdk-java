//package cn.bubi.sdk.core.event.handle;
//
//import cn.bubi.sdk.core.balance.NodeManager;
//import cn.bubi.sdk.core.event.bottom.TxFailManager;
//import cn.bubi.sdk.core.event.message.LedgerSeqEventMessage;
//import cn.bubi.sdk.core.event.source.LedgerSeqIncreaseEventSource;
//import cn.bubi.sdk.core.utils.GsonUtil;
//
///**
// * @author xiezhengchao@bubi.cn
// * @since 17/10/25 上午10:20.
// * seq增加通知器,监听所有节点
// */
//public class LedgerSeqIncreaseEventHandler implements EventHandler{
//
//    private TxFailManager txFailManager;
//    private NodeManager nodeManager;
//    private final Object lock = new Object();
//
//    public LedgerSeqIncreaseEventHandler(TxFailManager txFailManager, NodeManager nodeManager){
//        this.txFailManager = txFailManager;
//        this.nodeManager = nodeManager;
//    }
//
//    @Override
//    public String eventSourceCode(){
//        return LedgerSeqIncreaseEventSource.CODE;
//    }
//
//    @Override
//    public void onEvent(String message){
//        LedgerSeqEventMessage executedEventMessage = GsonUtil.fromJson(message, LedgerSeqEventMessage.class);
//
//        synchronized (lock) {
//            // 通知节点访问控制器
//            nodeManager.notifySeqUpdate(executedEventMessage.getHost(), executedEventMessage.getSeq());
//
//            // 通知失败交易控制器
//            txFailManager.notifySeqUpdate(executedEventMessage.getSeq());
//        }
//
//    }
//
//
//}

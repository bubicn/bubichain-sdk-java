package cn.bubi.sdk.core.event.bottom;

import cn.bubi.access.utils.spring.StringUtils;
import cn.bubi.blockchain.adapter.BlockChainAdapter;
import cn.bubi.blockchain.adapter3.Chain;
import cn.bubi.blockchain.adapter3.Overlay;
import cn.bubi.sdk.core.event.EventBusService;
import cn.bubi.sdk.core.event.message.LedgerSeqEventMessage;
import cn.bubi.sdk.core.event.message.TransactionExecutedEventMessage;
import cn.bubi.sdk.core.event.source.EventSourceEnum;
import cn.bubi.sdk.core.exception.SdkError;
import cn.bubi.sdk.core.exception.SdkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 底层mq消息处理器
 */
public class BlockchainMqHandler{

    private static final Logger LOGGER = LoggerFactory.getLogger(BlockchainMqHandler.class);
    private static final Pattern URI_PATTERN = Pattern.compile("(ws://)(.*)(:[\\d+])");

    private BlockChainAdapter mQBlockChainExecute;
    private TxMqHandleProcess mqHandleProcess;
    private EventBusService eventBusService;
    private String eventUri;
    private String host;


    public BlockchainMqHandler(String eventUri, TxMqHandleProcess mqHandleProcess, EventBusService eventBusService) throws SdkException{
        this.eventUri = eventUri;
        this.host = getHostByUri(eventUri);
        this.mqHandleProcess = mqHandleProcess;
        this.eventBusService = eventBusService;
    }

    private static String getHostByUri(String eventUri) throws SdkException{
        try {
            Matcher matcher = URI_PATTERN.matcher(eventUri);
            if (!matcher.find()) {
                throw new SdkException(SdkError.PARSE_URI_ERROR);
            }
            return matcher.group(2);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SdkException(SdkError.PARSE_URI_ERROR);
        }
    }

    public void init(){
        // 初始化链接
        BlockChainAdapter mQBlockChainExecute = new BlockChainAdapter(eventUri);

        // 接收握手消息
        mQBlockChainExecute.AddChainMethod(Overlay.ChainMessageType.CHAIN_HELLO_VALUE, this :: onHelloCallback);

        // 交易
        mQBlockChainExecute.AddChainMethod(Overlay.ChainMessageType.CHAIN_TX_STATUS_VALUE, this :: txCallback);

        // 区块seq
        mQBlockChainExecute.AddChainMethod(Overlay.ChainMessageType.CHAIN_LEDGER_HEADER_VALUE, this :: ledgerSeqCallback);

        Overlay.ChainHello.Builder chain_hello = Overlay.ChainHello.newBuilder();
        chain_hello.setTimestamp(new Date().getTime());
        mQBlockChainExecute.Send(Overlay.ChainMessageType.CHAIN_HELLO_VALUE, chain_hello.build().toByteArray());
        this.mQBlockChainExecute = mQBlockChainExecute;
    }

    public void destroy(){
        if (mQBlockChainExecute != null) {
            mQBlockChainExecute.Stop();
        }
    }


    /**
     * 接收交易结果
     */
    private void txCallback(byte[] msg, int length){
        try {
            Overlay.ChainTxStatus chainTx = Overlay.ChainTxStatus.parseFrom(msg);
            TransactionExecutedEventMessage message = new TransactionExecutedEventMessage();
            message.setHash(chainTx.getTxHash());
            message.setErrorCode(String.valueOf(chainTx.getErrorCode().getNumber()));
            message.setSponsorAddress(chainTx.getSourceAddress());
            message.setSequenceNumber(chainTx.getSourceAccountSeq());

            switch (chainTx.getStatus().getNumber()) {
                case Overlay.ChainTxStatus.TxStatus.COMPLETE_VALUE:
                    // 成功
                    message.setSuccess(true);
                    LOGGER.debug("交易成功errorcode：" + chainTx.getErrorCode().getNumber() + ",status=" + chainTx.getStatus() + " ,交易hash:" + chainTx.getTxHash());
                    break;
                case Overlay.ChainTxStatus.TxStatus.FAILURE_VALUE:
                    // 失败
                    message.setSuccess(false);
                    message.setErrorMessage(chainTx.getErrorDesc());
                    LOGGER.debug("交易失败errorcode：" + chainTx.getErrorCode().getNumber() + ",chainTx.getErrorDesc():" + chainTx.getErrorDesc() + ",status=" + chainTx.getStatus() + " ,交易hash:" + chainTx.getTxHash());
                    break;
                default:
                    break;
            }

            if (StringUtils.isEmpty(message.getSponsorAddress())) {
                LOGGER.error("received empty source address. TransactionExecutedEventMessage : " + message);
                return;
            }

            if (message.getSuccess() != null) {
                // 交给后置处理器处理
                mqHandleProcess.process(message);
            }
        } catch (Exception e) {
            LOGGER.error("接受交易结果异常", e);
        }
    }

    /**
     * 接收seq增加
     */
    private void ledgerSeqCallback(byte[] msg, int length){
        try {
            Chain.LedgerHeader ledger_header = Chain.LedgerHeader.parseFrom(msg);
            LOGGER.trace("================" + ledger_header.toString());

            LedgerSeqEventMessage seqEventMessage = new LedgerSeqEventMessage();
            seqEventMessage.setHost(host);
            seqEventMessage.setSeq(ledger_header.getSeq());

            eventBusService.publishEvent(EventSourceEnum.LEDGER_SEQ_INCREASE.getEventSource(), seqEventMessage);

        } catch (Exception e) {
            LOGGER.error("接收seq增加异常", e);
        }
    }


    /**
     * 接收握手消息
     */
    private void onHelloCallback(byte[] msg, int length){
        LOGGER.info("!!!!!!!!!!!!!!!!!receive hello successful , to host:" + host);
    }

}

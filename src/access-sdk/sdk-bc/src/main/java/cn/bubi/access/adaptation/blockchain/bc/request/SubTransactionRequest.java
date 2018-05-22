package cn.bubi.access.adaptation.blockchain.bc.request;
/**
 * 提交交易
 * @author ko12
 *
 */
public class SubTransactionRequest {
	private TransactionRequest[] items;

	public TransactionRequest[] getItems() {
		return items;
	}

	public void setItems(TransactionRequest[] items) {
		this.items = items;
	}

}

package cn.bubi.blockhcain.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.bubi.blockchain.adapter.BlockChainAdapter;

public class ChainMessageEx extends BlockChainAdapter{
	private boolean bhello_;
	private Logger logger_;
	public ChainMessageEx(String uri_address) {
		super(uri_address);
		logger_ = LoggerFactory.getLogger(BlockChainAdapter.class);
	}
	public boolean isBhello_() {
		return bhello_;
	}
	public void setBhello_(boolean bhello_) {
		this.bhello_ = bhello_;
	}
}
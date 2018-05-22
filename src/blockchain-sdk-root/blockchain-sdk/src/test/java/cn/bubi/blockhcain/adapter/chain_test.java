/*
Copyright Bubi Technologies Co., Ltd. 2017 All Rights Reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package cn.bubi.blockhcain.adapter;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import cn.bubi.baas.utils.encryption.BubiKey;
import cn.bubi.baas.utils.encryption.BubiKeyType;
import cn.bubi.blockchain.adapter.BlockChainAdapter;
import cn.bubi.blockchain.adapter.BlockChainAdapterProc;
import cn.bubi.blockchain.adapter.Message;

public class chain_test {
	private ChainMessageEx chain_message_one_;
	private TestThread test_thread = new TestThread();
	private Object object_;
	private Logger logger_;
	private long seq_;
	private static Map<String, Long> tx_times;
	public static void main(String[] argv) {
		tx_times = new ConcurrentHashMap<String, Long>();
		chain_test test = new chain_test();
		test.Initialize();
		System.out.println("*****************start chain_message successfully******************");
		try {
			Thread.sleep(100000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		test.Stop();
	}
	public void Stop() {
		chain_message_one_.Stop();
		test_thread.Stop();
	}
	//@Test
	public void Initialize() {
		
		logger_ = LoggerFactory.getLogger(BlockChainAdapter.class);
		object_ = new Object();
		chain_message_one_ = new ChainMessageEx("ws://192.168.10.120:7053");
		chain_message_one_.AddChainResponseMethod(Message.ChainMessageType.CHAIN_HELLO_VALUE, new BlockChainAdapterProc() {
			public void ChainMethod (byte[] msg, int length) {
				OnChainHello(chain_message_one_, msg, length);
			}
		});
		chain_message_one_.AddChainMethod(Message.ChainMessageType.CHAIN_TX_STATUS_VALUE, new BlockChainAdapterProc() {
			public void ChainMethod (byte[] msg, int length) {
				OnChainTxStatus(msg, length);
			}
		});
		chain_message_one_.AddChainMethod(Message.ChainMessageType.CHAIN_PEER_MESSAGE.getNumber(), new BlockChainAdapterProc() {
			public void ChainMethod (byte[] msg, int length) {
				OnChainPeerMessage(msg, length);
			}
		});
		
		if (!chain_message_one_.isBhello_()) {
			Message.ChainHello.Builder chain_hello = Message.ChainHello.newBuilder();
			chain_hello.setTimestamp(System.currentTimeMillis());
			if (!chain_message_one_.Send(Message.ChainMessageType.CHAIN_HELLO.getNumber(), chain_hello.build().toByteArray())) {
				logger_.error("send hello failed");
			}
		}
	}
	@SuppressWarnings("unused")
	private void OnChainHello(ChainMessageEx chain_message, byte[] msg, int length) {
		try {
			Message.ChainStatus chain_status = Message.ChainStatus.parseFrom(msg);
			logger_.info("=================receive hello info============");
			//chain_message_.Stop();
		} catch (InvalidProtocolBufferException e) {
			logger_.error(e.getMessage());
			//e.printStackTrace();
		}
		chain_message.setBhello_(true);
	}
	
	private void OnChainPeerMessage(byte[] msg, int length) {
		try {
			Message.ChainPeerMessage chain_peer_message = Message.ChainPeerMessage.parseFrom(msg);
			logger_.info("=================receive peer message info============");
			logger_.info(chain_peer_message.toString());
			//chain_message_.Stop();
		} catch (InvalidProtocolBufferException e) {
			logger_.error(e.getMessage());
		}
		synchronized(object_) {
			object_.notifyAll();
		}
	}
	
	private void OnChainTxStatus(byte[] msg, int length) {
		try {
			Message.ChainTxStatus chain_tx_status = Message.ChainTxStatus.parseFrom(msg);
			if (chain_tx_status.getStatus() == Message.ChainTxStatus.TxStatus.FAILURE || chain_tx_status.getStatus() == Message.ChainTxStatus.TxStatus.COMPLETE) {
				if (tx_times.containsKey(chain_tx_status.getTxHash())) {
					String hash = chain_tx_status.getTxHash();
					long start = tx_times.get(hash);
					long end = System.nanoTime();
					System.out.println(hash + ":" + start + "," +  end + "," + (end - start));
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	class TestThread implements Runnable {
		boolean enabled_ = true;
		Thread testThead_;
		TestThread() {
			testThead_ = new Thread(this);
			testThead_.start();
		}

		@Override
		public void run() {
			while(enabled_) {
				System.out.println("===============================111111");
				try {
					Thread.sleep(5000);
					
					String privateKey = "privbtZ1Fw5RRWD4ZFR6TAMWjN145zQJeJQxo3EXAABfgBjUdiLHLLHF";
					String address = "bubiV8i2558GmfnBREe87ZagdkKsfeJh5HYjcNpa";
					String httpRequest = "http://192.168.10.120:19333";
					String url = httpRequest + "/getAccount?address=" + address;
					String txSeq = null;
					txSeq = HttpKit.post(url, "");
					
					JSONObject tx = JSONObject.parseObject(txSeq);
					String seq_str = tx.getJSONObject("result").getString("tx_seq");
					seq_ = Long.parseLong(seq_str);
					
					BubiKey bubikey_new = new BubiKey(BubiKeyType.ED25519);
					
					Message.Transaction.Builder tran = Message.Transaction.newBuilder();
					tran.setSourceAddress(address);
					tran.setFee(1000);
					tran.setSequenceNumber(seq_ + 1);
					tran.setMetadata(ByteString.copyFromUtf8(String.valueOf(System.currentTimeMillis())));
					Message.Operation.Builder oper = tran.addOperationsBuilder();
					oper.setType(Message.Operation.Type.PAYMENT);
	                oper.getPaymentBuilder().setDestAddress(bubikey_new.getB58Address());
	                oper.getPaymentBuilder().getAssetBuilder().getPropertyBuilder().setType(Message.AssetProperty.Type.IOU);
	                oper.getPaymentBuilder().getAssetBuilder().getPropertyBuilder().setIssuer(address);
	                oper.getPaymentBuilder().getAssetBuilder().getPropertyBuilder().setCode("coin");
	                oper.getPaymentBuilder().getAssetBuilder().setAmount(1);
					
					Message.Signature.Builder sign = Message.Signature.newBuilder();
					BubiKey bubiKey = new BubiKey(privateKey);
					sign.setPublicKey(bubiKey.getB58PublicKey());
					sign.setSignData(ByteString.copyFrom(bubiKey.sign(tran.build().toByteArray())));
					
					Message.TransactionEnv.Builder env = Message.TransactionEnv.newBuilder(); 
					env.setTransaction(tran.build());
					env.addSignatures(sign);
					
					String hash = Encrypt.SHA256(tran.build().toByteArray());
					tx_times.put(hash, System.nanoTime());
					if (!chain_message_one_.Send(Message.ChainMessageType.CHAIN_SUBMITTRANSACTION_VALUE, env.build().toByteArray())) {
						System.out.println("send transaction failed");
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		public void Stop() {
			enabled_ = false;
			try {
				testThead_.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

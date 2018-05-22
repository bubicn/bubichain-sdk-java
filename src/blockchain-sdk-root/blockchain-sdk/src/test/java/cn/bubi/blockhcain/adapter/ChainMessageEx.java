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
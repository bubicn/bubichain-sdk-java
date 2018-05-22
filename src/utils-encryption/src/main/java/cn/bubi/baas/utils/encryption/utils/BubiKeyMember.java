package cn.bubi.baas.utils.encryption.utils;

import cn.bubi.baas.utils.encryption.BubiKeyType;

public class BubiKeyMember {
	private byte[] raw_skey_ = null;
	private byte[] raw_pkey_ = null;
	private BubiKeyType type_ = null;
	
	public byte[] getRaw_skey_() {
		return raw_skey_;
	}
	public void setRaw_skey_(byte[] raw_skey_) {
		this.raw_skey_ = raw_skey_;
	}
	public byte[] getRaw_pkey_() {
		return raw_pkey_;
	}
	public void setRaw_pkey_(byte[] raw_pkey_) {
		this.raw_pkey_ = raw_pkey_;
	}
	public BubiKeyType getType_() {
		return type_;
	}
	public void setType_(BubiKeyType type_) {
		this.type_ = type_;
	}
}

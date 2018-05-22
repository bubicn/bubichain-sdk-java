package cn.bubi.baas.utils.encryption.utils;

import cfca.sadk.algorithm.sm2.SM2PublicKey;
import cfca.sadk.algorithm.util.BigIntegerUtil;
import cfca.sadk.org.bouncycastle.asn1.ASN1Integer;
import cfca.sadk.org.bouncycastle.asn1.ASN1Sequence;

public class Common {
	public static byte[] getSM2PublicKey(SM2PublicKey pubKey) {
		byte[] raw_pkey = new byte[65];
		byte[] x = pubKey.getPubX();
		byte[] y = pubKey.getPubY();
		
		raw_pkey[0] = 4;
		System.arraycopy(x, 0, raw_pkey, 1, 32);
		System.arraycopy(y, 0, raw_pkey, 33, 32);
		
		return raw_pkey;
	}
	
	public static SM2PublicKey getSM2PublicKey(byte[] raw_pkey) {
		byte[] x = new byte[32];
		byte[] y = new byte[32];
		System.arraycopy(raw_pkey, 1, x, 0, 32);
		System.arraycopy(raw_pkey, 33, y, 0, 32);
		
		SM2PublicKey publicKey = new SM2PublicKey(x, y);
		
		return publicKey;
	}
	
	public static byte[] ASN1toRS(byte[] asn1RS) {
		ASN1Sequence sequence = ASN1Sequence.getInstance(asn1RS);
		ASN1Integer R = (ASN1Integer)sequence.getObjectAt(0);
		ASN1Integer S = (ASN1Integer)sequence.getObjectAt(1);
		byte[] r = BigIntegerUtil.asUnsigned32ByteArray(R.getPositiveValue());
		byte[] s = BigIntegerUtil.asUnsigned32ByteArray(S.getPositiveValue());
		byte[] signature = new byte[64];
		System.arraycopy(r, 0, signature, 0, 32);
		System.arraycopy(s, 0, signature, 32, 32);
		return signature;
	}
}

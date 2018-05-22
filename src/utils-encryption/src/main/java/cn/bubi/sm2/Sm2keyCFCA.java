package cn.bubi.sm2;

import java.math.BigInteger;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;

import cn.bubi.sm2.Sm2Key;

public class Sm2keyCFCA extends Sm2Key {
	//private Sm2Key skey_;

	private static byte[] cfcaid = "1234567812345678".getBytes();

	// 产生一个随机的私钥
	public Sm2keyCFCA() {
		super(CfcaCurve());
	}

	// 从已有的字符串私钥构造对象
	public Sm2keyCFCA(byte[] keybyte) {
		super(keybyte, CfcaCurve());
	}

	/// 签名函数
	public byte[] Sign(byte[] msg) {
		return Sign(cfcaid, msg);
	}
	
	


	// 验证签名
	// pkey: 公钥的16进制格式
	// msg: 需要验证的消息
	// signature: 签名内容
	public static boolean Verify(byte[] msg, byte[] pkey, byte[] sigbyte) {
		return Sm2Key.Verify(msg, pkey, sigbyte, cfcaid, CfcaCurve());
	}

	// CFCA所选择的椭圆曲线参数
	public static ECParameterSpec CfcaCurve() {
		// 素数P
		BigInteger p = new BigInteger("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFF", 16);

		// 基于素数P的有限域
		ECFieldFp gfp = new ECFieldFp(p);

		// 在有限域上的椭圆曲线y2 = x3 + ax + b
		EllipticCurve ellipticCurve = new EllipticCurve(gfp,
				new BigInteger("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFC", 16),
				new BigInteger("28E9FA9E9D9F5E344D5A9E4BCF6509A7F39789F515AB8F92DDBCBD414D940E93", 16));

		// 基点G
		ECPoint G = new ECPoint(new BigInteger("32C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7", 16),
				new BigInteger("BC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0", 16));

		// G的阶
		BigInteger n = new BigInteger("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFF7203DF6B21C6052B53BBF40939D54123", 16);

		// 设置基点
		ECParameterSpec ecParameterSpec = new ECParameterSpec(ellipticCurve, G, n, 1);
		return ecParameterSpec;
	}

}

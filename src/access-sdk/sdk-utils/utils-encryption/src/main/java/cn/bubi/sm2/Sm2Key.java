package cn.bubi.sm2;

import cn.bubi.sm3.SM3Digest;

import java.math.BigInteger;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.util.Random;

/*
 根据《SM2椭圆曲线公钥密码算法 》（国家密码管理局 2010年12月）编写。
 注意：全是中文注释
 布比（北京）网络技术有限公司
 */

public class Sm2Key {
	// 私钥
	private BigInteger dA_;

	// 椭圆曲线参数
	ECParameterSpec spec_;

	// 公钥点
	ECPoint pA_;

	// nlen_ 有限域阶n的字节数
	int nlen_;

	// 根据已有私钥对象构建
	public Sm2Key(byte[] skeybyte, ECParameterSpec spec) {
		byte[] buff = new byte[skeybyte.length + 1];
		buff[0] = 0;
		System.arraycopy(skeybyte, 0, buff, 1, skeybyte.length);
		dA_ = new BigInteger(buff);
		spec_ = spec;
		pA_ = PointMul(dA_, spec_.getGenerator(), spec_.getCurve());
		byte[] tmp = ((ECFieldFp) (spec_.getCurve().getField())).getP().toByteArray();
		if (tmp[0] == 0) {
			nlen_ = tmp.length - 1;
		} else {
			nlen_ = tmp.length;
		}
	}

	// 随机产生一个私钥
	public Sm2Key(ECParameterSpec spec) {
		spec_ = spec;

		byte[] tmp = ((ECFieldFp) (spec_.getCurve().getField())).getP().toByteArray();
		if (tmp[0] == 0) {
			nlen_ = tmp.length - 1;
		} else {
			nlen_ = tmp.length;
		}

		// 随机私钥
		Random random = new Random();
		ECFieldFp fp = (ECFieldFp) spec_.getCurve().getField();
		int bitlen = fp.getP().bitLength();
		do {

			dA_ = new BigInteger(bitlen, random);
			if (dA_.compareTo(BigInteger.ZERO) <= 0) {
				continue;
			}

			pA_ = PointMul(dA_, spec_.getGenerator(), spec_.getCurve());

			if (pA_.getAffineX().bitLength() <= nlen_ - 8 || pA_.getAffineY().bitLength() <= nlen_ - 8) {
				continue;
			}
			break;
		} while (true);

	}

	// 得到公钥
	public byte[] GetPkeyByte() {
		BigInteger x = pA_.getAffineX();
		BigInteger y = pA_.getAffineY();

		byte[] xbyte = x.toByteArray();
		byte[] ybyte = y.toByteArray();

		byte[] out = new byte[2 * nlen_ + 1];
		byte PC = 4;

		out[0] = PC;
		if (xbyte[0] == 0) {
			System.arraycopy(xbyte, 1, out, 2 + nlen_ - xbyte.length, xbyte.length - 1);
		} else {
			System.arraycopy(xbyte, 0, out, 1 + nlen_ - xbyte.length, xbyte.length);
		}

		if (ybyte[0] == 0) {
			System.arraycopy(ybyte, 1, out, 2 + 2 * nlen_ - ybyte.length, ybyte.length - 1);
		} else {
			System.arraycopy(ybyte, 0, out, 1 + 2 * nlen_ - ybyte.length, ybyte.length);
		}

		return out;
	}

	private static ECPoint PkeyByte2Point(ECParameterSpec spec, byte[] pb) {
		BigInteger P = ((ECFieldFp) spec.getCurve().getField()).getP();

		byte PC = pb[0];
		int nlen = (int) Math.ceil(P.bitLength() / 8);
		byte[] xb = new byte[nlen + 1];
		byte[] yb = new byte[nlen + 1];

		switch (PC) {
		case 2:
			// BigInteger a = spec.getCurve().getA();
			// BigInteger b = spec.getCurve().getB();
			// alpha = (Xp3 + aXp +b)mod p
			// BigInteger alpha = x.pow(3).add(a.multiply(x)).add(b).mod(P);
			break;
		case 3:
			break;
		case 4:
			System.arraycopy(pb, 1, xb, 1, nlen);
			System.arraycopy(pb, 1 + nlen, yb, 1, nlen);
		default:
			break;
		}

		BigInteger x = new BigInteger(xb);
		BigInteger y = new BigInteger(yb);
		return new ECPoint(x, y);
	}

	public byte[] GetSkeyByte() {

		byte[] da = dA_.toByteArray();
		byte[] out = new byte[nlen_];
		if (da[0] == 0) {
			System.arraycopy(da, 1, out, 1 + nlen_ - da.length, da.length - 1);
		} else {
			System.arraycopy(da, 0, out, nlen_ - da.length, da.length);
		}
		return out;
	}

	// 签名函数
	public byte[] Sign(byte[] id, byte[] msg) {
		// 得到M^ = ZA||M
		ECPoint pA = PointMul(dA_, spec_.getGenerator(), spec_.getCurve());

		byte[] ZA = GetZA(spec_, pA, id);

		// System.out.println("ZA=" + bytesToHex(ZA));

		byte[] M = new byte[msg.length + ZA.length];
		System.arraycopy(ZA, 0, M, 0, ZA.length);
		System.arraycopy(msg, 0, M, ZA.length, msg.length);

		// 第二步 e=Hv(M^)
		byte[] ebytes = SM3Digest.Hash(M);

		BigInteger e = new BigInteger(bytesToHex(ebytes), 16);
		BigInteger n = spec_.getOrder();

		while (true) {
			// 第三步 产生随机数k [1,n-1]
			Random random = new Random();
			BigInteger K = new BigInteger(n.bitLength() - 1, random);

			if (K.compareTo(n) == 0 || K.compareTo(BigInteger.ZERO) == 0) {
				continue;
			}

			// 第四步 计算pt1(x1,y1) = [K]G这个点
			ECPoint G = spec_.getGenerator();
			ECPoint pt1 = PointMul(K, G, spec_.getCurve());
			BigInteger x1 = pt1.getAffineX();

			// 第五步 计算 r = (e + x1) mod n
			BigInteger r = x1.add(e).mod(n);
			r = r.add(n).mod(n);

			// System.out.println("r=" + r.toString(16));
			// 确保r!=0 且 r+k!=n 也就是 (r+k) != 0 mod n
			if (r.add(K).mod(n).compareTo(BigInteger.ZERO) == 0) {
				continue;
			}

			// 第六步 计算 s = ((1 + d)^-1 * (k - rd)) mod n
			BigInteger tmp1 = dA_.add(BigInteger.ONE).modInverse(n);
			BigInteger tmp2 = K.subtract(r.multiply(dA_).mod(n)).mod(n);
			BigInteger s = tmp1.multiply(tmp2).mod(n);
			s = s.add(n).mod(n);
			if (s.compareTo(BigInteger.ZERO) == 0) {
				continue;
			}

			ECFieldFp fp = (ECFieldFp) spec_.getCurve().getField();
			byte[] pb = fp.getP().toByteArray();
			int len = pb[0] == 0 ? pb.length - 1 : pb.length;

			byte[] rb = r.toByteArray();
			byte[] sb = s.toByteArray();
			byte[] sig = new byte[2 * len];

			if (rb[0] == 0) {
				System.arraycopy(rb, 1, sig, len - rb.length + 1, rb.length - 1);
			} else {
				System.arraycopy(rb, 0, sig, len - rb.length, rb.length);
			}

			if (sb[0] == 0) {
				System.arraycopy(sb, 1, sig, 2 * len - sb.length + 1, sb.length - 1);
			} else {
				System.arraycopy(sb, 0, sig, 2 * len - sb.length, sb.length);
			}

			return sig;
		}

	}

	// 签名验证
	private static boolean Verify(ECParameterSpec spec, byte[] id, byte[] msg, ECPoint pkey, Sm2Signature sig) {

		do {
			// 第一步 r在[1,n-1]范围
			BigInteger order = spec.getOrder();
			if (sig.r.compareTo(order) >= 0 || sig.r.compareTo(BigInteger.ONE) < 0) {
				break;
			}

			// 第一步 s在[1,n-1]范围
			if (sig.s.compareTo(order) >= 0 || sig.s.compareTo(BigInteger.ONE) < 0) {
				break;
			}

			// 第三步 计算M^ = ZA||M
			byte[] ZA = GetZA(spec, pkey, id);
			byte[] M = new byte[ZA.length + msg.length];
			System.arraycopy(ZA, 0, M, 0, ZA.length);
			System.arraycopy(msg, 0, M, 32, msg.length);

			// 第四步 计算e=Hv(M^)
			byte[] stre = SM3Digest.Hash(M);
			BigInteger e = new BigInteger(bytesToHex(stre), 16);

			// 第五步 计算t=(r'+s')mod n
			BigInteger t = sig.r.add(sig.s).mod(order);
			if (t.compareTo(BigInteger.ZERO) == 0) {
				break;
			}

			// 第六步 计算(x1,y1) = [s]G + [t]PA
			ECPoint G = spec.getGenerator();
			ECPoint tmp1 = PointMul(sig.s, G, spec.getCurve());
			ECPoint tmp2 = PointMul(t, pkey, spec.getCurve());
			ECPoint tmPoint = PointAdd(tmp1, tmp2, spec.getCurve());

			// 第七步 R=(e' + x1') 验证R==r'?
			BigInteger R = e.add(tmPoint.getAffineX()).mod(order);
			if (R.compareTo(sig.r) != 0) {
				break;
			}

			return true;

		} while (false);
		return false;
	}

	protected static boolean Verify(byte[] msg, byte[] pkey, byte[] strsig, byte[] id, ECParameterSpec spec) {
		ECFieldFp fp = (ECFieldFp) spec.getCurve().getField();
		byte[] pb = fp.getP().toByteArray();
		int len = pb[0] == 0 ? pb.length - 1 : pb.length;
		byte[] r = new byte[len + 1];
		byte[] s = new byte[len + 1];
		System.arraycopy(strsig, 0, r, 1, len);
		System.arraycopy(strsig, len, s, 1, len);
		Sm2Signature sig = new Sm2Signature(new BigInteger(r), new BigInteger(s));

		ECPoint pt = PkeyByte2Point(spec, pkey);
		return Verify(spec, id, msg, pt, sig);
	}

	//
	public static String bytesToHex(byte[] raw) {
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final byte b : raw) {
			hex.append(Character.forDigit((b & 0xF0) >> 4, 16)).append(Character.forDigit((b & 0x0F), 16));
		}
		return hex.toString();
	}

	// 计算ZA,椭圆曲线参数，公钥点，身份标识
	private static byte[] GetZA(ECParameterSpec spec, ECPoint pA, byte[] id) {

		byte[] za = new byte[256];
		int etlen = id.length * 8;
		// 拼接ENTLA
		int pos = 0;
		za[0] = (byte) (etlen >> 8);
		za[1] = (byte) (etlen & 0xFf);

		pos += 2;
		// 拼接用户ID
		System.arraycopy(id, 0, za, pos, id.length);
		pos += id.length;

		// 拼接a
		byte[] a = spec.getCurve().getA().toByteArray();
		System.arraycopy(a, 0, za, pos, a.length);
		pos += a.length;

		// 拼接b
		byte[] b = spec.getCurve().getB().toByteArray();
		System.arraycopy(b, 0, za, pos, b.length);
		pos += b.length;

		// 拼接xG
		byte[] xG = spec.getGenerator().getAffineX().toByteArray();
		System.arraycopy(xG, 0, za, pos, xG.length);
		pos += xG.length;
		// System.out.println("xG=" + bytesToHex(xG));

		// 拼接yG
		byte[] yG = spec.getGenerator().getAffineY().toByteArray();
		System.arraycopy(yG, 0, za, pos, yG.length);
		pos += yG.length;
		// System.out.println("yG=" + bytesToHex(yG));

		// 拼接xA
		byte[] xA = pA.getAffineX().toByteArray();
		System.arraycopy(xA, 0, za, pos, xA.length);
		pos += xA.length;

		// 拼接yA
		byte[] yA = pA.getAffineY().toByteArray();
		System.arraycopy(yA, 0, za, pos, yA.length);
		pos += yA.length;

		byte[] tmp = new byte[pos];
		System.arraycopy(za, 0, tmp, 0, pos);

		return SM3Digest.Hash(tmp);
	}

	// 定义点加运算
	private static ECPoint PointAdd(ECPoint p1, ECPoint p2, EllipticCurve curve) {
		BigInteger t = new BigInteger("00", 16);
		ECFieldFp fd = (ECFieldFp) curve.getField();
		BigInteger p = fd.getP();

		// 相同点相加
		if (p1.equals(p2)) {
			BigInteger a = curve.getA();
			// t = (3*x1^2 + a)/(2*y1);
			BigInteger tmp1 = p1.getAffineX().pow(2).multiply(new BigInteger("3", 10)).add(a);

			BigInteger tmp2 = p1.getAffineY().multiply(new BigInteger("2", 10));

			t = tmp2.modInverse(p).multiply(tmp1).mod(p);

			BigInteger x3 = t.pow(2).subtract(p1.getAffineX().multiply(new BigInteger("2", 10)));

			BigInteger y3 = p1.getAffineX().subtract(x3).multiply(t).subtract(p1.getAffineY());

			x3 = x3.add(p).mod(p);
			y3 = y3.add(p).mod(p);

			ECPoint pt = new ECPoint(x3, y3);
			return pt;
		}

		// 互逆相加
		if (p1.getAffineX().equals(p2.getAffineX()) && p1.getAffineY().add(p2.getAffineY()).equals(0)) {
			return new ECPoint(BigInteger.ZERO, BigInteger.ZERO);
		} else {
			// 相异非互逆
			// t = (p2.y - p1.y)/(p2.x - p1.x);
			BigInteger tmp1 = p2.getAffineY().subtract(p1.getAffineY());
			BigInteger tmp2 = p2.getAffineX().subtract(p1.getAffineX());
			t = tmp2.modInverse(p).multiply(tmp1).mod(p);

			BigInteger x3 = t.pow(2).subtract(p1.getAffineX()).subtract(p2.getAffineX());

			BigInteger y3 = p1.getAffineX().subtract(x3).multiply(t).subtract(p1.getAffineY());

			x3 = x3.add(p).mod(p);
			y3 = y3.add(p).mod(p);

			ECPoint pt = new ECPoint(x3, y3);
			return pt;
		}
	}

	// 定义倍点运算
	private static ECPoint PointMul(BigInteger k, ECPoint pt, EllipticCurve curve) {
		int len = k.bitLength();
		ECPoint Q = new ECPoint(pt.getAffineX(), pt.getAffineY());

		for (int i = len - 2; i >= 0; i--) {
			Q = PointAdd(Q, Q, curve);
			if (k.testBit(i)) {
				Q = PointAdd(Q, pt, curve);
			}
		}
		return Q;
	}
}

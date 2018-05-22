package cn.bubi.sm2;

import java.math.BigInteger;

public class Sm2Signature {
	public Sm2Signature(BigInteger rr, BigInteger ss) {
		// TODO Auto-generated constructor stub
		r = rr;
		s = ss;
	}

	public Sm2Signature() {
		r = BigInteger.ZERO;
		s = BigInteger.ZERO;
	}

	public Sm2Signature(byte[] input) {
		int rlen = input[0];
		byte[] rb = new byte[rlen];
		byte[] sb = new byte[input.length - 1 - rlen];
		System.arraycopy(input, 1, rb, 0, rlen);
		System.arraycopy(input, 1 + rlen, sb, 0, sb.length);
		r = new BigInteger(rb);
		s = new BigInteger(sb);
	}

	public BigInteger r;
	public BigInteger s;
}

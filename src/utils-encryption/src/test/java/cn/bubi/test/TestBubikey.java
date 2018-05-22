package cn.bubi.test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cfca.sadk.algorithm.util.FileUtil;
import cn.bubi.baas.utils.encryption.BubiKey;
import cn.bubi.baas.utils.encryption.BubiKey3;
import cn.bubi.baas.utils.encryption.BubiKeyType;
import cn.bubi.baas.utils.encryption.CertFileType;
import cn.bubi.baas.utils.encryption.utils.Base58;
import cn.bubi.baas.utils.encryption.utils.HashUtil;
import cn.bubi.baas.utils.encryption.utils.HexFormat;
import cn.bubi.baas.utils.encryption.utils.HttpKit;
import cn.bubi.blockchain.adapter3.Chain.*;

public class TestBubikey {
	public static void main(String[] args) throws IllegalArgumentException, Exception {
		System.out.println(BubiKey3.getBubiAddress("b0044d494944777a434341717567417749424167494645424a7a4e3349774451594a4b6f5a496876634e41514546425141775744454c4d416b474131554542684d43513034784d44417542674e5642416f544a304e6f6157356849455a70626d467559326c68624342445a584a3061575a7059324630615739754945463164476876636d6c30655445584d4255474131554541784d4f51305a445153425552564e5549453944515445774868634e4d5463784d5441794d444d784e6a45355768634e4d546b784d5441794d444d784e6a4535576a42374d517377435159445651514745774a6a626a45584d4255474131554543684d4f51305a445153425552564e5549453944515445784454414c42674e564241735442454a56516b6b784644415342674e5642417354433056756447567963484a706332567a4d5334774c41594456515144464355774e444641546a6b784e4451774d544178545545314f5578584d455577526b42305a584e30514441774d4441774d4441324d4947664d413047435371475349623344514542415155414134474e4144434269514b42675144547a382f566c4d6462583643704d57626a7a426d7a78596a526445306e677839534d4a624663657549375263506777616f526d2b7765537132514846446a794e466c4157667731447231443535565965634d373459617576524b525871662b533545583631795a64414d6b7245705641476141683561482b4a6939723431492f444a6357696a574c7736676777337231454e4f4b564b687259554f7772306477734b4b316d556b43567a514944415141426f3448304d4948784d42384741315564497751594d426141464d39776e5748726e5877757550664c416b4433435a332b4d3353414d45674741315564494152424d443877505159495949456368753871415145774d5441764267677242674546425163434152596a6148523063446f764c3364336479356a5a6d4e684c6d4e766253356a6269393163793931637930784e43356f644730774f51594456523066424449774d4441756f4379674b6f596f6148523063446f764c33566a636d777559325a6a5953356a6232307559323476556c4e424c324e79624449314d7a51324c6d4e796244414c42674e564851384542414d43412b6777485159445652304f42425945465079314e4478676130504242794b41426c384244617441344846674d423047413155644a5151574d425147434373474151554642774d434267677242674546425163444244414e42676b71686b6947397730424151554641414f434151454169635855527657762f322b706d6747515766703343706d41357447714664766f663445465a4544382b55354968676c4247676252786244496741496645362f7657587067722f59786f72556c31305155387955536744436e6a385a596c767354612b2b554a49683872384f6864614e512f77755a525461646861436d396a537153346c3137622b62773774457965794f5a514c7969795165517863315a77772b536430633442436948393256357754795159376d334c38323147376d51555a774e32412f61475934576b5255756a6f3359733379692f7258484d46353361764a3549464653443733757a366a3652684c38454b3549576453442f387462734c71486738356347724463514c46416f69746c644c52676b3654395053414b7a636c4a554e73744d2b526b387541514f2b636170764d78662b4c32524d2b79526e42543256793878624f612b302b49617356514862446d513d3d74504326"));
//		for (int i = 0; i < 256; i++) {
//			for (int j = 0; j < 256; j++) {
//				for (int k = 0; j < 256; j++) {
//					String src = Character.toString((char)i) + Character.toString((char)j) + Character.toString((char)k);
//					if (HexFormat.isHexString(Base58.encode(src.getBytes()))) {
//						System.out.println(src + "'s base65 "+ Base58.encode(src.getBytes()) + " is hex string");
//					}
//				}
//			}
//		}
//		String bSkey = "c00205ce8de2892b26c3b95caf3404831d6e913c655d85b517f076380ebfcbef47ff8f";
//		System.out.println(BubiKey.getB16PublicKey(bSkey));
//		
//		cfca.sadk.util.Signature signature = new cfca.sadk.util.Signature();
		
		// test signature and verify
//		System.out.println("================ test b58 ed25519 ==================");
//		testB58_ED25519_3();
//		System.out.println();
//		
//		System.out.println("\n\n================ test b58 eccsm2 ==================");
//		testB58_ECCSM2_3();
//		System.out.println();
//		
//		System.out.println("\n\n================ test b58 cfca pfx ==================");
//		testB58_CFCA_pfx_3();
//		System.out.println();
//		
//		System.out.println("\n\n================ test b58 cfca sm2 ==================");
//		testB58_CFCA_SM2_3();
//		System.out.println();
//		
//		System.out.println("\n\n================ test b58 cfca jks ==================");
//		testB58_CFCA_JKS_3();
		
//		// test signature and verify
//		System.out.println("================ test b58 ed25519 ==================");
//		testB58_ED25519();
//		System.out.println("================ test b16 ed25519 ==================");
//		testB16_ED25519();
//		System.out.println("");
//		
//		System.out.println("\n\n================ test b58 eccsm2 ==================");
//		testB58_ECCSM2();
//		System.out.println("================ test b16 eccsm2 ==================");
//		testB16_ECCSM2();
//		System.out.println("");
//		
//		System.out.println("\n\n================ test b58 cfca pfx ==================");
//		testB58_CFCA_pfx();
//		System.out.println("================ test b16 cfca pfx ==================");
//		testB16_CFCA_pfx();
//		System.out.println("");
//		
//		System.out.println("\n\n================ test b58 cfca sm2 ==================");
//		testB58_CFCA_SM2();
//		System.out.println("================ test b16 cfca sm2 ==================");
//		testB16_CFCA_SM2();
//		System.out.println("");
//		
//		System.out.println("\n\n================ test b58 cfca jks ==================");
//		testB58_CFCA_JKS();
//		System.out.println("================ test b16 cfca jks ==================");
//		testB16_CFCA_JKS();
//		
//		// test create account transaction
//		System.out.println("\n\n================ teat create account ==================");
//		String url = "http://127.0.0.1:29333";
//		String privateKey = "c00205ce8de2892b26c3b95caf3404831d6e913c655d85b517f076380ebfcbef47ff8f";
//		String publicKey = "b0020413806c5dd6fd473ab3ea7c484453ba2c07b41ff4d13832a49488bc3eec27c25f5c9f477eeb0e9953de10a8f3df956a979f4750b51efc59db6e7c2a998fdbda0acd";
//		String address = "a0025e6de5a793da4b5b00715b7774916c06e9a72b7c18";
//		
//		// create A
//		System.out.println("create A: ");
//		BubiKey bubiKey_new = TestCreateAccount(url, address, privateKey, publicKey, address, privateKey, publicKey, 11, 11, BubiKeyType.ED25519, CertFileType.SM2, 
//				"D:/bubi/Peer2.SM2", "cfca1234");
//		Thread.sleep(2000);
//		
//		// A create D
//		System.out.println("create D: ");
//		BubiKey bubiKey_D = TestCreateAccount(url, address, privateKey, publicKey, address, privateKey, publicKey, 0, 0, BubiKeyType.ED25519, CertFileType.SM2, 
//				"D:/bubi/Peer1.SM2", "cfca1234");
//		Thread.sleep(2000);
//		
//		
//		System.out.println();
//		System.out.println();
//		System.out.println("Create B");
//		// A create B
//		BubiKey bubiKey_one = TestCreateAccount(url, bubiKey_new.getB16Address(), bubiKey_new.getB16PrivKey(), 
//				bubiKey_new.getB16PublicKey(), bubiKey_new.getB16Address(), bubiKey_new.getB16PrivKey(), 
//				bubiKey_new.getB16PublicKey(), 10, 10, BubiKeyType.CFCA, CertFileType.SM2, "D:/bubi/Peer3.SM2", "cfca1234");
//		
//		Thread.sleep(2000);
//		
//		System.out.println();
//		System.out.println();
//		System.out.println("B set D signer weight 11");
//		// B set signer weight D 11
//		TestSetSignerWeight(url, bubiKey_one.getB16Address(), bubiKey_one.getB16PrivKey(), bubiKey_one.getB16PublicKey(), bubiKey_D.getB16Address(), 11);
//		
//		Thread.sleep(3000);
//		
//		System.out.println();
//		System.out.println();
//		System.out.println("Create C");
//		// B create C with A signature but no B signature 
//		BubiKey bubiKey_two = TestCreateAccount(url, bubiKey_one.getB16Address(), bubiKey_one.getB16PrivKey(), bubiKey_one.getB16PublicKey(), bubiKey_new.getB16Address(), bubiKey_new.getB16PrivKey(), 
//				bubiKey_new.getB16PublicKey(), 10, 10, BubiKeyType.CFCA, CertFileType.SM2, "D:/bubi/Peer4.SM2", "cfca1234");
	}
	
	public static void TestSetSignerWeight(String url, String srcAddress, String srcPrivate, String srcPublic, String signAddress,
			int signWeight) {
		try {
			// get hash type
			String getHello = url + "/hello";
			String hello = HttpKit.post(getHello, "");
			JSONObject ho = JSONObject.parseObject(hello);
			Integer hash_type = ho.containsKey("hash_type") ? ho.getInteger("hash_type") : 0;
			
			// getAccount
			String getAccount = url + "/getAccount?address=" + srcAddress;
			String txSeq = HttpKit.post(getAccount, "");
			JSONObject tx = JSONObject.parseObject(txSeq);
			String seq_str = tx.getJSONObject("result").containsKey("nonce") ? tx.getJSONObject("result").getString("nonce") : "0";
			long nonce = Long.parseLong(seq_str);
			
			// use src account sign
			BubiKey bubiKey_src = new BubiKey(srcPrivate, srcPublic);
			
			// generate transaction
			Transaction.Builder tran = Transaction.newBuilder();
			tran.setSourceAddress(srcAddress);
			tran.setNonce(nonce + 1);
			Operation.Builder oper = tran.addOperationsBuilder();
			oper.setType(Operation.Type.SET_SIGNER_WEIGHT);
			OperationSetSignerWeight.Builder signerWeight = OperationSetSignerWeight.newBuilder();
			Signer.Builder signer = signerWeight.addSignersBuilder();
			signer.setAddress(signAddress);
			signer.setWeight(signWeight);
			oper.setSetSignerWeight(signerWeight);
			
			// generate hex string of transaction's hash
			String hash = HashUtil.GenerateHashHex(tran.build().toByteArray(), hash_type);
			System.out.println("hash: " + hash);
			
			// add transaction with signature
			JSONObject request = new JSONObject();
			JSONArray items = new JSONArray();
			JSONObject item = new JSONObject();
			item.put("transaction_blob", cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(tran.build().toByteArray()));
			JSONArray signatures = new JSONArray();
			JSONObject signature = new JSONObject();
			signature.put("sign_data", cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(bubiKey_src.sign(tran.build().toByteArray())));
			signature.put("public_key", srcPublic);
			signatures.add(signature);
			item.put("signatures", signatures);
			items.add(item);
			request.put("items", items);
			
			String submitTransaction = url + "/submitTransaction";
			String result = HttpKit.post(submitTransaction, request.toJSONString());
			System.out.println(result);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static BubiKey TestCreateAccount(String url, String srcAddress, String srcPrivate, String srcPublic, String signerAddress,
			String signerPrivate, String signerPublic, int masterWeight, int threshold, BubiKeyType algorithm, CertFileType certFileType, String certFile, String password) {
		BubiKey bubikey_new = null;
		try {
			// get hash type
			String getHello = url + "/hello";
			String hello = HttpKit.post(getHello, "");
			JSONObject ho = JSONObject.parseObject(hello);
			Integer hash_type = ho.containsKey("hash_type") ? ho.getInteger("hash_type") : 0;
			
			// getAccount
			String getAccount = url + "/getAccount?address=" + srcAddress;
			String txSeq = HttpKit.post(getAccount, "");
			JSONObject tx = JSONObject.parseObject(txSeq);
			String seq_str = tx.getJSONObject("result").containsKey("nonce") ? tx.getJSONObject("result").getString("nonce") : "0";
			long nonce = Long.parseLong(seq_str);
			
			// generate new Account address, PrivateKey, publicKey
			if (algorithm == BubiKeyType.CFCA) {
				byte fileData[] = FileUtil.getBytesFromFile(certFile);
				bubikey_new = new BubiKey(certFileType, fileData, password);
			}
			else {
				bubikey_new = new BubiKey(algorithm);
			}
			String newAccountAddress = bubikey_new.getB16Address();
			
			// use src account sign
			BubiKey bubiKey_src = new BubiKey(srcPrivate, srcPublic);
			BubiKey bubiKey_sign = new BubiKey(signerPrivate, signerPublic);
			
			// generate transaction
			Transaction.Builder tran = Transaction.newBuilder();
			tran.setSourceAddress(srcAddress);
			tran.setNonce(nonce + 1);
			Operation.Builder oper = tran.addOperationsBuilder();
			oper.setType(Operation.Type.CREATE_ACCOUNT);
			OperationCreateAccount.Builder createAccount = OperationCreateAccount.newBuilder();
			AccountPrivilege.Builder accountPrivilege = AccountPrivilege.newBuilder();
			accountPrivilege.setMasterWeight(masterWeight);
			AccountThreshold.Builder accountThreshold = AccountThreshold.newBuilder();
			accountThreshold.setTxThreshold(threshold);
			accountPrivilege.setThresholds(accountThreshold);
			
			createAccount.setPriv(accountPrivilege);
			createAccount.setDestAddress(newAccountAddress);
			oper.setCreateAccount(createAccount);
			
			// generate hex string of transaction's hash
			String hash = HashUtil.GenerateHashHex(tran.build().toByteArray(), hash_type);
			System.out.println("hash: " + hash);
			
			// add transaction with signature
			JSONObject request = new JSONObject();
			JSONArray items = new JSONArray();
			JSONObject item = new JSONObject();
			item.put("transaction_blob", cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(tran.build().toByteArray()));
			JSONArray signatures = new JSONArray();
			JSONObject signature = new JSONObject();
			signature.put("sign_data", cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(bubiKey_sign.sign(tran.build().toByteArray())));
			signature.put("public_key", signerPublic);
			signatures.add(signature);
			item.put("signatures", signatures);
			items.add(item);
			request.put("items", items);
			System.out.println("src sign_data: " + cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(bubiKey_src.sign(tran.build().toByteArray())));
			System.out.println("src public key: " + bubiKey_src.getB16PublicKey());
			System.out.println("Transaction: " + tran);
			System.out.println("Signatures: " + signatures);
			
			String submitTransaction = url + "/submitTransaction";
			String result = HttpKit.post(submitTransaction, request.toJSONString());
			System.out.println(result);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return bubikey_new;
	}
	
	public static void testB58_ED25519() {
		try {
			System.out.println("=========================BubiKeyType==================================");
			BubiKey bubiKey = new BubiKey(BubiKeyType.ED25519);
			System.out.println("bubuKey1 private key: " + bubiKey.getB58PrivKey());
			System.out.println("bubuKey1 public key: " + bubiKey.getB58PublicKey());
			System.out.println("bubuKey1 address: " + bubiKey.getB58Address());
			
			System.out.println("bubuKey1 static public key: " + BubiKey.getB58PublicKey(bubiKey.getB58PrivKey()));
			System.out.println("bubuKey1 static address: " + BubiKey.getB58Address(bubiKey.getB58PublicKey()));
			
			System.out.println("=========================PrivateKey==================================");
			BubiKey bubiKey2 = new BubiKey(bubiKey.getB58PrivKey(), null);
			System.out.println("bubuKey1 static public key: " + BubiKey.getB58PublicKey(bubiKey.getB58PrivKey()));
			System.out.println("bubuKey2 private key: " + bubiKey2.getB58PrivKey());
			System.out.println("bubuKey2 public key: " + bubiKey2.getB58PublicKey());
			System.out.println("bubuKey2 address: " + bubiKey2.getB58Address());
			
			String src = "test";
			byte[] sign = bubiKey2.sign(src.getBytes());
			byte[] sign_static = BubiKey.sign(src.getBytes(),bubiKey.getB58PrivKey(), bubiKey.getB58PublicKey());
			System.out.println("signature: " + cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(sign));
			System.out.println("static signature: " + cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(sign_static));
			System.out.println("verify: " + BubiKey.verify(src.getBytes(), sign, bubiKey.getB58PublicKey()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testB16_ED25519() {
		try {
			System.out.println("=========================BubiKeyType==================================");
			BubiKey bubiKey = new BubiKey(BubiKeyType.ED25519);
			System.out.println("bubuKey1 private key: " + bubiKey.getB16PrivKey());
			System.out.println("bubuKey1 public key: " + bubiKey.getB16PublicKey());
			System.out.println("bubuKey1 address: " + bubiKey.getB16Address());
			
			System.out.println("bubuKey1 static public key: " + BubiKey.getB16PublicKey(bubiKey.getB16PrivKey()));
			System.out.println("bubuKey1 static address: " + BubiKey.getB16Address(bubiKey.getB16PublicKey()));
			
			System.out.println("=========================PrivateKey==================================");
			BubiKey bubiKey2 = new BubiKey(bubiKey.getB16PrivKey(), null);
			System.out.println("bubuKey2 private key: " + bubiKey2.getB16PrivKey());
			System.out.println("bubuKey2 public key: " + bubiKey2.getB16PublicKey());
			System.out.println("bubuKey2 address: " + bubiKey2.getB16Address());
			
			String src = "test";
			byte[] sign = bubiKey2.sign(src.getBytes());
			byte[] sign_static = BubiKey.sign(src.getBytes(), bubiKey.getB16PrivKey(), bubiKey.getB16PublicKey());
			System.out.println("signature: " + cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(sign));
			System.out.println("static signature: " + cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(sign_static));
			System.out.println("verify: " + bubiKey2.verify(src.getBytes(), sign));
			System.out.println("static verify: " + BubiKey.verify(src.getBytes(), sign, bubiKey.getB16PublicKey()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testB58_ECCSM2() {
		try {
			System.out.println("=========================BubiKeyType==================================");
			BubiKey bubiKey = new BubiKey(BubiKeyType.ECCSM2);
			System.out.println("bubuKey1 private key: " + bubiKey.getB58PrivKey());
			System.out.println("bubuKey1 public key: " + bubiKey.getB58PublicKey());
			System.out.println("bubuKey1 address: " + bubiKey.getB58Address());
			System.out.println("bubuKey1 static public key: " + BubiKey.getB58PublicKey(bubiKey.getB58PrivKey()));
			System.out.println("bubuKey1 static address: " + BubiKey.getB58Address(bubiKey.getB58PublicKey()));
			
			System.out.println("=========================PrivateKey==================================");
			BubiKey bubiKey2 = new BubiKey(bubiKey.getB58PrivKey(), bubiKey.getB58PublicKey());
			System.out.println("bubuKey2 private key: " + bubiKey2.getB58PrivKey());
			System.out.println("bubuKey2 public key: " + bubiKey2.getB58PublicKey());
			System.out.println("bubuKey2 address: " + bubiKey2.getB58Address());
			
			Boolean verifyResult = false;
			System.out.println(verifyResult);
			String src = "test";
			byte[] sign = bubiKey2.sign(src.getBytes());
			byte[] sign_static = BubiKey.sign(src.getBytes(),bubiKey.getB58PrivKey(), bubiKey.getB58PublicKey());
			System.out.println("signature: " + cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(sign));
			System.out.println("static signature: " + cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(sign_static));
			System.out.println("verify: " + bubiKey2.verify(src.getBytes(), sign));
			System.out.println("static verify: " + BubiKey.verify(src.getBytes(), sign, bubiKey.getB58PublicKey()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testB16_ECCSM2() {
		try {
			System.out.println("=========================BubiKeyType==================================");
			BubiKey bubiKey = new BubiKey(BubiKeyType.ECCSM2);
			System.out.println("bubuKey1 private key: " + bubiKey.getB16PrivKey());
			System.out.println("bubuKey1 public key: " + bubiKey.getB16PublicKey());
			System.out.println("bubuKey1 address: " + bubiKey.getB16Address());
			System.out.println("bubuKey1 static public key: " + BubiKey.getB16PublicKey(bubiKey.getB16PrivKey()));
			System.out.println("bubuKey1 static address: " + BubiKey.getB16Address(bubiKey.getB16PublicKey()));
			
			System.out.println("=========================PrivateKey==================================");
			BubiKey bubiKey2 = new BubiKey(bubiKey.getB16PrivKey(), bubiKey.getB16PublicKey());
			System.out.println("bubuKey2 private key: " + bubiKey2.getB16PrivKey());
			System.out.println("bubuKey2 public key: " + bubiKey2.getB16PublicKey());
			System.out.println("bubuKey2 address: " + bubiKey2.getB16Address());
			
			String src = "test";
			byte[] sign = bubiKey2.sign(src.getBytes());
			byte[] sign_static = BubiKey.sign(src.getBytes(), bubiKey.getB16PrivKey(), bubiKey.getB16PublicKey());
			System.out.println("signature: " + cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(sign));
			System.out.println("static signature: " + cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(sign_static));
			System.out.println("verify: " + bubiKey2.verify(src.getBytes(), sign));
			System.out.println("static verify: " + BubiKey.verify(src.getBytes(), sign, bubiKey.getB16PublicKey()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testB58_CFCA_pfx() {
		
		try {
			System.out.println("=========================cert file data==================================");
			byte fileData[] = FileUtil.getBytesFromFile("D:/mytest_ex.pfx");
			BubiKey bubiKey = new BubiKey(CertFileType.PFX, fileData, "111111");
			System.out.println("bubuKey1 private key: " + bubiKey.getB58PrivKey());
			System.out.println("bubuKey1 public key: " + bubiKey.getB58PublicKey());
			System.out.println("bubuKey1 address: " + bubiKey.getB58Address());
			
			System.out.println("bubuKey1 static address: " + BubiKey.getB58Address(bubiKey.getB58PublicKey()));
			
			System.out.println("=========================PrivateKey PublicKey==================================");
			BubiKey bubiKey2 = new BubiKey(bubiKey.getB58PrivKey(), bubiKey.getB58PublicKey());
			System.out.println("bubuKey2 private key: " + bubiKey2.getB58PrivKey());
			System.out.println("bubuKey2 public key: " + bubiKey2.getB58PublicKey());
			System.out.println("bubuKey2 address: " + bubiKey2.getB58Address());
			
			System.out.println("=========================cert file path==================================");
			BubiKey bubiKey3 = new BubiKey(CertFileType.PFX, "D:/mytest_ex.pfx", "111111");
			System.out.println("bubuKey3 private key: " + bubiKey3.getB58PrivKey());
			System.out.println("bubuKey3 public key: " + bubiKey3.getB58PublicKey());
			System.out.println("bubuKey3 address: " + bubiKey3.getB58Address());
			
			String src = "test";
			byte[] sign = bubiKey2.sign(src.getBytes());
			byte[] sign_static = BubiKey.sign(src.getBytes(),bubiKey.getB58PrivKey(), bubiKey.getB58PublicKey());
			System.out.println("signature: " + cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(sign));
			System.out.println("static signature: " + cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(sign_static));
			System.out.println("verify: " + bubiKey2.verify(src.getBytes(), sign));
			System.out.println("static verify: " + BubiKey.verify(src.getBytes(), sign, bubiKey.getB58PublicKey()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testB16_CFCA_pfx() {
		
		try {
			System.out.println("=========================cert file data==================================");
			byte fileData[] = FileUtil.getBytesFromFile("D:/test.pfx");
			BubiKey bubiKey = new BubiKey(CertFileType.PFX, fileData, "11111111");
			System.out.println("bubuKey1 private key: " + bubiKey.getB16PrivKey());
			System.out.println("bubuKey1 public key: " + bubiKey.getB16PublicKey());
			System.out.println("bubuKey1 address: " + bubiKey.getB16Address());
			
			System.out.println("bubuKey1 static address: " + BubiKey.getB16Address(bubiKey.getB16PublicKey()));
			
			System.out.println("=========================PrivateKey PublicKey==================================");
			BubiKey bubiKey2 = new BubiKey(bubiKey.getB16PrivKey(), bubiKey.getB16PublicKey());
			System.out.println("bubuKey2 private key: " + bubiKey2.getB16PrivKey());
			System.out.println("bubuKey2 public key: " + bubiKey2.getB16PublicKey());
			System.out.println("bubuKey2 address: " + bubiKey2.getB16Address());
			
			System.out.println("=========================cert file path==================================");
			BubiKey bubiKey3 = new BubiKey(CertFileType.PFX, "D:/test.pfx", "11111111");
			System.out.println("bubuKey3 private key: " + bubiKey3.getB16PrivKey());
			System.out.println("bubuKey3 public key: " + bubiKey3.getB16PublicKey());
			System.out.println("bubuKey3 address: " + bubiKey3.getB16Address());
			
			String src = "test";
			byte[] sign = bubiKey2.sign(src.getBytes());
			byte[] sign_static = BubiKey.sign(src.getBytes(), bubiKey.getB16PrivKey(), bubiKey.getB16PublicKey());
			System.out.println("signature: " + cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(sign));
			System.out.println("static signature: " + cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(sign_static));
			System.out.println("verify: " + bubiKey2.verify(src.getBytes(), sign));
			System.out.println("static verify: " + BubiKey.verify(src.getBytes(), sign, bubiKey.getB16PublicKey()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testB58_CFCA_SM2() {	
		try {
			System.out.println("=========================cert file data==================================");
			byte fileData[] = FileUtil.getBytesFromFile("D:/bubi/GenesisAccount.SM2");
			BubiKey bubiKey = new BubiKey(CertFileType.SM2, fileData, "cfca1234");
			System.out.println("bubuKey1 private key: " + bubiKey.getB58PrivKey());
			System.out.println("bubuKey1 public key: " + bubiKey.getB58PublicKey());
			System.out.println("bubuKey1 address: " + bubiKey.getB58Address());
			
			System.out.println("bubuKey1 static address: " + BubiKey.getB58Address(bubiKey.getB58PublicKey()));
			
			System.out.println("=========================PrivateKey PublicKey==================================");
			BubiKey bubiKey2 = new BubiKey(bubiKey.getB58PrivKey(), bubiKey.getB58PublicKey());
			System.out.println("bubuKey2 private key: " + bubiKey2.getB58PrivKey());
			System.out.println("bubuKey2 public key: " + bubiKey2.getB58PublicKey());
			System.out.println("bubuKey2 address: " + bubiKey2.getB58Address());
			
			System.out.println("=========================cert file path==================================");
			BubiKey bubiKey3 = new BubiKey(CertFileType.SM2, "D:/bubi/GenesisAccount.SM2", "cfca1234");
			System.out.println("bubuKey3 private key: " + bubiKey3.getB58PrivKey());
			System.out.println("bubuKey3 public key: " + bubiKey3.getB58PublicKey());
			System.out.println("bubuKey3 address: " + bubiKey3.getB58Address());
			
			String src = "test";
			byte[] sign = bubiKey2.sign(src.getBytes());
			byte[] sign_static = BubiKey.sign(src.getBytes(),bubiKey.getB58PrivKey(), bubiKey.getB58PublicKey());
			System.out.println("signature: " + cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(sign));
			System.out.println("static signature: " + cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(sign_static));
			System.out.println("verify: " + bubiKey2.verify(src.getBytes(), sign));
			System.out.println("static verify: " + BubiKey.verify(src.getBytes(), sign, bubiKey.getB58PublicKey()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testB16_CFCA_SM2() {	
		try {
			System.out.println("=========================cert file data==================================");
			byte fileData[] = FileUtil.getBytesFromFile("D:/bubi/GenesisAccount.SM2");
			BubiKey bubiKey = new BubiKey(CertFileType.SM2, fileData, "cfca1234");
			System.out.println("bubuKey1 private key: " + bubiKey.getB16PrivKey());
			System.out.println("bubuKey1 public key: " + bubiKey.getB16PublicKey());
			System.out.println("bubuKey1 address: " + bubiKey.getB16Address());
			
			System.out.println("bubuKey1 static address: " + BubiKey.getB16Address(bubiKey.getB58PublicKey()));
			
			System.out.println("=========================PrivateKey PublicKey==================================");
			BubiKey bubiKey2 = new BubiKey(bubiKey.getB16PrivKey(), bubiKey.getB16PublicKey());
			System.out.println("bubuKey2 private key: " + bubiKey2.getB16PrivKey());
			System.out.println("bubuKey2 public key: " + bubiKey2.getB16PublicKey());
			System.out.println("bubuKey2 address: " + bubiKey2.getB16Address());
			
			System.out.println("=========================cert file path==================================");
			BubiKey bubiKey3 = new BubiKey(CertFileType.SM2, "D:/bubi/GenesisAccount.SM2", "cfca1234");
			System.out.println("bubuKey3 private key: " + bubiKey3.getB16PrivKey());
			System.out.println("bubuKey3 public key: " + bubiKey3.getB16PublicKey());
			System.out.println("bubuKey3 address: " + bubiKey3.getB16Address());
			
			String src = "test";
			byte[] sign = bubiKey2.sign(src.getBytes());
			byte[] sign_static = BubiKey.sign(src.getBytes(), bubiKey.getB16PrivKey(), bubiKey.getB16PublicKey());
			System.out.println("signature: " + cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(sign));
			System.out.println("static signature: " + cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(sign_static));
			System.out.println("verify: " + bubiKey2.verify(src.getBytes(), sign));
			System.out.println("static verify: " + BubiKey.verify(src.getBytes(), sign, bubiKey.getB16PublicKey()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testB58_CFCA_JKS() {	
		try {
			System.out.println("=========================cert file data==================================");
			byte fileData[] = FileUtil.getBytesFromFile("D:/peer.jks");
			BubiKey bubiKey = new BubiKey(fileData, "123456", "client");
			System.out.println("bubuKey1 private key: " + bubiKey.getB58PrivKey());
			System.out.println("bubuKey1 public key: " + bubiKey.getB58PublicKey());
			System.out.println("bubuKey1 address: " + bubiKey.getB58Address());
			
			System.out.println("bubuKey1 static address: " + BubiKey.getB58Address(bubiKey.getB58PublicKey()));
			
			System.out.println("=========================PrivateKey PublicKey==================================");
			BubiKey bubiKey2 = new BubiKey(bubiKey.getB58PrivKey(), bubiKey.getB58PublicKey());
			System.out.println("bubuKey2 private key: " + bubiKey2.getB58PrivKey());
			System.out.println("bubuKey2 public key: " + bubiKey2.getB58PublicKey());
			System.out.println("bubuKey2 address: " + bubiKey2.getB58Address());
			
			System.out.println("=========================cert file path==================================");
			BubiKey bubiKey3 = new BubiKey("D:/peer.jks", "123456", "client");
			System.out.println("bubuKey3 private key: " + bubiKey3.getB58PrivKey());
			System.out.println("bubuKey3 public key: " + bubiKey3.getB58PublicKey());
			System.out.println("bubuKey3 address: " + bubiKey3.getB58Address());
			
			String src = "test";
			byte[] sign = bubiKey2.sign(src.getBytes());
			byte[] sign_static = BubiKey.sign(src.getBytes(),bubiKey.getB58PrivKey(), bubiKey.getB58PublicKey());
			System.out.println("signature: " + cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(sign));
			System.out.println("static signature: " + cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(sign_static));
			System.out.println("verify: " + bubiKey2.verify(src.getBytes(), sign));
			System.out.println("static verify: " + BubiKey.verify(src.getBytes(), sign, bubiKey.getB58PublicKey()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testB16_CFCA_JKS() {	
		try {
			byte fileData[] = FileUtil.getBytesFromFile("D:/peer.jks");
			BubiKey bubiKey = new BubiKey(fileData, "123456", "client");
			System.out.println("bubuKey1 private key: " + bubiKey.getB16PrivKey());
			System.out.println("bubuKey1 public key: " + bubiKey.getB16PublicKey());
			System.out.println("bubuKey1 address: " + bubiKey.getB16Address());
			
			System.out.println("bubuKey1 static address: " + BubiKey.getB16Address(bubiKey.getB58PublicKey()));
			
			System.out.println("=========================PrivateKey PublicKey==================================");
			BubiKey bubiKey2 = new BubiKey(bubiKey.getB16PrivKey(), bubiKey.getB16PublicKey());
			System.out.println("bubuKey2 private key: " + bubiKey2.getB16PrivKey());
			System.out.println("bubuKey2 public key: " + bubiKey2.getB16PublicKey());
			System.out.println("bubuKey2 address: " + bubiKey2.getB16Address());
			
			System.out.println("=========================cert file path==================================");
			BubiKey bubiKey4 = new BubiKey("D:/peer.jks", "123456", "client");
			System.out.println("bubuKey1 private key: " + bubiKey4.getB16PrivKey());
			System.out.println("bubuKey1 public key: " + bubiKey4.getB16PublicKey());
			System.out.println("bubuKey1 address: " + bubiKey4.getB16Address());
			
			String src = "test";
			byte[] sign = bubiKey2.sign(src.getBytes());
			byte[] sign_static = BubiKey.sign(src.getBytes(), bubiKey.getB16PrivKey(), bubiKey.getB16PublicKey());
			System.out.println("signature: " + cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(sign));
			System.out.println("static signature: " + cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(sign_static));
			System.out.println("verify: " + bubiKey2.verify(src.getBytes(), sign));
			System.out.println("static verify: " + BubiKey.verify(src.getBytes(), sign, bubiKey.getB16PublicKey()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testB58_ED25519_3() {
		try {
			System.out.println("=========================BubiKeyType==================================");
			BubiKey3 bubiKey = new BubiKey3(BubiKeyType.ED25519);
			System.out.println("bubuKey1 private key: " + bubiKey.getEncPrivateKey());
			System.out.println("bubuKey1 public key: " + bubiKey.getEncPublicKey());
			System.out.println("bubuKey1 address: " + bubiKey.getBubiAddress());
			
			System.out.println("bubuKey1 static public key: " + BubiKey3.getEncPublicKey(bubiKey.getEncPrivateKey()));
			System.out.println("bubuKey1 static address: " + BubiKey3.getBubiAddress(bubiKey.getEncPublicKey()));
			
			System.out.println("=========================PrivateKey==================================");
			BubiKey3 bubiKey2 = new BubiKey3(bubiKey.getEncPrivateKey(), null);
			System.out.println("bubuKey1 static public key: " + BubiKey3.getEncPublicKey(bubiKey.getEncPrivateKey()));
			System.out.println("bubuKey2 private key: " + bubiKey2.getEncPrivateKey());
			System.out.println("bubuKey2 public key: " + bubiKey2.getEncPublicKey());
			System.out.println("bubuKey2 address: " + bubiKey2.getBubiAddress());
			
			String src = "test";
			byte[] sign = bubiKey2.sign(src.getBytes());
			byte[] sign_static = BubiKey3.sign(src.getBytes(),bubiKey.getEncPrivateKey(), bubiKey.getEncPublicKey());
			System.out.println("signature: " + cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(sign));
			System.out.println("static signature: " + cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(sign_static));
			System.out.println("verify: " + BubiKey3.verify(src.getBytes(), sign, bubiKey.getEncPublicKey()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testB58_ECCSM2_3() {
		try {
			System.out.println("=========================BubiKeyType==================================");
			BubiKey3 bubiKey = new BubiKey3(BubiKeyType.ECCSM2);
			System.out.println("bubuKey1 private key: " + bubiKey.getEncPrivateKey());
			System.out.println("bubuKey1 public key: " + bubiKey.getEncPublicKey());
			System.out.println("bubuKey1 address: " + bubiKey.getBubiAddress());
			System.out.println("bubuKey1 static public key: " + BubiKey3.getEncPublicKey(bubiKey.getEncPrivateKey()));
			System.out.println("bubuKey1 static address: " + BubiKey3.getBubiAddress(bubiKey.getEncPublicKey()));
			
			System.out.println("=========================PrivateKey==================================");
			BubiKey3 bubiKey2 = new BubiKey3(bubiKey.getEncPrivateKey(), bubiKey.getEncPublicKey());
			System.out.println("bubuKey2 private key: " + bubiKey2.getEncPrivateKey());
			System.out.println("bubuKey2 public key: " + bubiKey2.getEncPublicKey());
			System.out.println("bubuKey2 address: " + bubiKey2.getBubiAddress());
			
			Boolean verifyResult = false;
			System.out.println(verifyResult);
			String src = "test";
			byte[] sign = bubiKey2.sign(src.getBytes());
			byte[] sign_static = BubiKey3.sign(src.getBytes(),bubiKey.getEncPrivateKey(), bubiKey.getEncPublicKey());
			System.out.println("signature: " + cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(sign));
			System.out.println("static signature: " + cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(sign_static));
			System.out.println("verify: " + bubiKey2.verify(src.getBytes(), sign));
			System.out.println("static verify: " + BubiKey3.verify(src.getBytes(), sign, bubiKey.getEncPublicKey()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testB58_CFCA_pfx_3() {
		
		try {
			System.out.println("=========================cert file data==================================");
			byte fileData[] = FileUtil.getBytesFromFile("D:/mytest_ex.pfx");
			BubiKey3 bubiKey = new BubiKey3(CertFileType.PFX, fileData, "111111");
			System.out.println("bubuKey1 private key: " + bubiKey.getEncPrivateKey());
			System.out.println("bubuKey1 public key: " + bubiKey.getEncPublicKey());
			System.out.println("bubuKey1 address: " + bubiKey.getBubiAddress());
			
			System.out.println("bubuKey1 static address: " + BubiKey3.getBubiAddress(bubiKey.getEncPublicKey()));
			
			System.out.println("=========================PrivateKey PublicKey==================================");
			BubiKey3 bubiKey2 = new BubiKey3(bubiKey.getEncPrivateKey(), bubiKey.getEncPublicKey());
			System.out.println("bubuKey2 private key: " + bubiKey2.getEncPrivateKey());
			System.out.println("bubuKey2 public key: " + bubiKey2.getEncPublicKey());
			System.out.println("bubuKey2 address: " + bubiKey2.getBubiAddress());
			
			System.out.println("=========================cert file path==================================");
			BubiKey3 bubiKey3 = new BubiKey3(CertFileType.PFX, "D:/mytest_ex.pfx", "111111");
			System.out.println("bubuKey3 private key: " + bubiKey3.getEncPrivateKey());
			System.out.println("bubuKey3 public key: " + bubiKey3.getEncPublicKey());
			System.out.println("bubuKey3 address: " + bubiKey3.getBubiAddress());
			
			String src = "test";
			byte[] sign = bubiKey2.sign(src.getBytes());
			byte[] sign_static = BubiKey3.sign(src.getBytes(),bubiKey.getEncPrivateKey(), bubiKey.getEncPublicKey());
			System.out.println("signature: " + cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(sign));
			System.out.println("static signature: " + cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(sign_static));
			System.out.println("verify: " + bubiKey2.verify(src.getBytes(), sign));
			System.out.println("static verify: " + BubiKey3.verify(src.getBytes(), sign, bubiKey.getEncPublicKey()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testB58_CFCA_SM2_3() {	
		try {
			System.out.println("=========================cert file data==================================");
			byte fileData[] = FileUtil.getBytesFromFile("D:/bubi/GenesisAccount.SM2");
			BubiKey3 bubiKey = new BubiKey3(CertFileType.SM2, fileData, "cfca1234");
			System.out.println("bubuKey1 private key: " + bubiKey.getEncPrivateKey());
			System.out.println("bubuKey1 public key: " + bubiKey.getEncPublicKey());
			System.out.println("bubuKey1 address: " + bubiKey.getBubiAddress());
			
			System.out.println("bubuKey1 static address: " + BubiKey3.getBubiAddress(bubiKey.getEncPublicKey()));
			
			System.out.println("=========================PrivateKey PublicKey==================================");
			BubiKey3 bubiKey2 = new BubiKey3(bubiKey.getEncPrivateKey(), bubiKey.getEncPublicKey());
			System.out.println("bubuKey2 private key: " + bubiKey2.getEncPrivateKey());
			System.out.println("bubuKey2 public key: " + bubiKey2.getEncPublicKey());
			System.out.println("bubuKey2 address: " + bubiKey2.getBubiAddress());
			
			System.out.println("=========================cert file path==================================");
			BubiKey3 bubiKey3 = new BubiKey3(CertFileType.SM2, "D:/bubi/GenesisAccount.SM2", "cfca1234");
			System.out.println("bubuKey3 private key: " + bubiKey3.getEncPrivateKey());
			System.out.println("bubuKey3 public key: " + bubiKey3.getEncPublicKey());
			System.out.println("bubuKey3 address: " + bubiKey3.getBubiAddress());
			
			String src = "test";
			byte[] sign = bubiKey2.sign(src.getBytes());
			byte[] sign_static = BubiKey3.sign(src.getBytes(),bubiKey.getEncPrivateKey(), bubiKey.getEncPublicKey());
			System.out.println("signature: " + cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(sign));
			System.out.println("static signature: " + cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(sign_static));
			System.out.println("verify: " + bubiKey2.verify(src.getBytes(), sign));
			System.out.println("static verify: " + BubiKey3.verify(src.getBytes(), sign, bubiKey.getEncPublicKey()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testB58_CFCA_JKS_3() {	
		try {
			System.out.println("=========================cert file data==================================");
			byte fileData[] = FileUtil.getBytesFromFile("D:/peer.jks");
			BubiKey3 bubiKey = new BubiKey3(fileData, "123456", "client");
			System.out.println("bubuKey1 private key: " + bubiKey.getEncPrivateKey());
			System.out.println("bubuKey1 public key: " + bubiKey.getEncPublicKey());
			System.out.println("bubuKey1 address: " + bubiKey.getBubiAddress());
			
			System.out.println("bubuKey1 static address: " + BubiKey3.getBubiAddress(bubiKey.getEncPublicKey()));
			
			System.out.println("=========================PrivateKey PublicKey==================================");
			BubiKey3 bubiKey2 = new BubiKey3(bubiKey.getEncPrivateKey(), bubiKey.getEncPublicKey());
			System.out.println("bubuKey2 private key: " + bubiKey2.getEncPrivateKey());
			System.out.println("bubuKey2 public key: " + bubiKey2.getEncPublicKey());
			System.out.println("bubuKey2 address: " + bubiKey2.getBubiAddress());
			
			System.out.println("=========================cert file path==================================");
			BubiKey3 bubiKey3 = new BubiKey3("D:/peer.jks", "123456", "client");
			System.out.println("bubuKey3 private key: " + bubiKey3.getEncPrivateKey());
			System.out.println("bubuKey3 public key: " + bubiKey3.getEncPublicKey());
			System.out.println("bubuKey3 address: " + bubiKey3.getBubiAddress());
			
			String src = "test";
			byte[] sign = bubiKey2.sign(src.getBytes());
			byte[] sign_static = BubiKey3.sign(src.getBytes(),bubiKey.getEncPrivateKey(), bubiKey.getEncPublicKey());
			System.out.println("signature: " + cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(sign));
			System.out.println("static signature: " + cn.bubi.baas.utils.encryption.utils.HexFormat.byteToHex(sign_static));
			System.out.println("verify: " + bubiKey2.verify(src.getBytes(), sign));
			System.out.println("static verify: " + BubiKey3.verify(src.getBytes(), sign, bubiKey.getEncPublicKey()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

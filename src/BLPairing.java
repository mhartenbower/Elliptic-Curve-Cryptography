import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.ElementPowPreProcessing;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.jpbc.PairingPreProcessing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.util.math.BigIntegerUtils;


public class BLPairing {
	public Pairing pairing;
	public static Field zr, g1, gt;
	public static Element pk_a, sk_a, isk_a, isk_b, pk_b, sk_b, ownersk_a, g, k, g_k, z_k, e, rka_b;
	public static Element ciphertext;
	public static Element c1, c2, reencrypt;
	public static Element decrypt;
	public static Element decrypt_user1;
	public byte[] array;
	public static ArrayList<Element> ciphertext1, ciphertext2, reencrypttext;
	public static byte[] result;
	public static int x;
	public static ArrayList<String> decoded;
	
	public void pairing() throws IOException{
		ciphertext1 = new ArrayList<Element>();
		ciphertext2 = new ArrayList<Element>();
		decoded = new ArrayList<String>();
		reencrypttext = new ArrayList<Element>();
		//Get the curve parameters
		PairingParameters curveParams = PairingFactory.getPairingParameters("a_181_603.properties");
		this.pairing = PairingFactory.getPairing(curveParams);
		
		//Initialize the parameters for second-level encryption
		g1 = pairing.getG1();
	    gt = pairing.getGT();
	    zr = pairing.getZr();
	    g = g1.newRandomElement().getImmutable();
		ElementPowPreProcessing gPre = g.getElementPowPreProcessing();
	    k= zr.newRandomElement().getImmutable();
	    g_k = gPre.powZn(k).getImmutable();
		z_k = pairing.pairing(g, g_k).getImmutable();
					      
		//Generate data owner keys
		sk_a = pairing.getZr().newRandomElement().getImmutable(); //private key
		pk_a = gPre.powZn(sk_a).getImmutable();
		isk_a = sk_a.invert().getImmutable(); //invert the secret key to calculate the proxy re-encryption key
		
		
		//Generate user keys (USER1)
		sk_b = pairing.getZr().newRandomElement().getImmutable(); //private key
		pk_b = gPre.powZn(sk_b).getImmutable();
		isk_b = sk_b.invert().getImmutable();
		
		//Generate proxy re-encryption keys
		rka_b = pk_b.powZn(isk_a).getImmutable();
		
		/*
		//Encrypt an integer
		e = pairing.getGT().newRandomElement();
		e.set(100);
		*/
		
		//Encrypt a file
		e = gt.newRandomElement();
		nBytes bytes = new nBytes();
		File fout = new File("data10.txt");
		long length = fout.length();
		int blockSize = 8; //this doesn't work if it's less than the size of the text file ***TOFIX***
		long blocks = (long)Math.ceil((double)length/(double)blockSize);
		System.out.println("Number of blocks :" + blocks);
		InputStream in = new FileInputStream(fout);
		for(int i = 0; i < blocks; i++){
			array = new byte[blockSize];
			array = bytes.readFile(blockSize, in);
			//for(int j = 0; j < array.length; j++){ //print out the array
				//System.out.print(array[j] + " ");
			//}
			System.out.println();
			e.setFromBytes(array);
			
			//Enecrypt e using second level encryption
			c1 = pk_a.powZn(k);
			c2 = z_k.mul(e);
			//ciphertext1.add(pk_a.powZn(k));
			//ciphertext2.add(z_k.mul(e));
			
			//Re-Encryption
			//for(int j = 0; j < ciphertext1.size(); j++){
				reencrypt = pairing.pairing(c1, rka_b);
				//reencrypttext.add(pairing.pairing(ciphertext1.get(i), rka_b));
			//}
			
			//Decrypt using first level decryption
			//decoded = new ArrayList<String>();
			//for(int i = 0; i < ciphertext1.size(); i++){
				Element ialpha = reencrypt.powZn(isk_b);
				decrypt_user1 = c2.div(ialpha);
				//System.out.println("Length" +decrypt_user1.getLengthInBytes());
				result = new byte[decrypt_user1.getLengthInBytes()];
				result = decrypt_user1.toBytes();
				bytes.writeFile(new String(result, "UTF-8"));
			//}
			
		}
		in.close();
		
		System.out.println("Complete");
				
		/* Check to ensure encryption works - second level decryption
		//Decrypt e
		Element alpha = pairing.pairing(c1, g);
		Element ialpha = alpha.powZn(isk_a);
		decrypt = c2.div(ialpha);
		*/
		
		
	}
	
	public static void main(String[] args) throws IOException{
		BLPairing pairing = new BLPairing();
		pairing.pairing();
		
		System.out.println("------------------------");
		System.out.print("Data Owner Public Key:");
		System.out.println(pk_a);
		System.out.println("------------------------");
		System.out.print("Data Owner Private Key:");
		System.out.println(sk_a);
		System.out.println("------------------------");
		System.out.print("User1 Public Key:");
		System.out.println(pk_b);
		System.out.println("------------------------");
		System.out.print("User1 Private Key:");
		System.out.println(sk_b);
		System.out.println("------------------------");
		System.out.print("User1 Proxy Re-Encryption Key:");
		System.out.println(rka_b);
		System.out.println("Message being encrypted: " + e);
		//System.out.println("Cipher Text 1: " + ciphertext1.get(0));
		//System.out.println("Cipher Text 2: " + ciphertext2.get(0));
		//System.out.println("New C1: " + reencrypttext.get(0));
		System.out.println("Number of bytes read: " + x);
		//for(int i = 0; i < 152; i++){
		//System.out.println("Line number: " + i + " " + result[i]);
		//}
		for(int i = 0; i < decoded.size(); i++){
			System.out.print(decoded.get(i));
		}
		//System.out.println("decoded: " + decoded); //MUST CHANGE THIS TO MATCH ORIGINAL FILE
		
	}

}

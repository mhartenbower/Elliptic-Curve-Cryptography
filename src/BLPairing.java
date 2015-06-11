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
	public static String decoded;
	
	public void pairing() throws IOException{
		ciphertext1 = new ArrayList<Element>();
		ciphertext2 = new ArrayList<Element>();
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
		File fout = new File("test.txt");
		long length = fout.length();
		int blockSize = 8; //this doesn't work if it's less than the size of the text file ***TOFIX***
		long blocks = (long)Math.ceil((double)length/(double)blockSize);
		System.out.println("Number of blocks :" + blocks);
		int offset = 0;
		InputStream in = new FileInputStream(fout);
		for(int i = 0; i < blocks; i++){
			array = new byte[blockSize];
			array = bytes.readFile(blockSize, in, offset);
			for(int j = 0; j < array.length; j++){ //print out the array
				System.out.print(array[j] + " ");
			}
			System.out.println();
			x = e.setFromBytes(array);
			offset += blockSize;
		}
		in.close();
		
		System.out.println("File read complete");

		//Encrypt e using second level encryption
		ciphertext1.add(pk_a.powZn(k));
		ciphertext2.add(z_k.mul(e));
		
		/* Check to ensure encryption works - second level decryption
		//Decrypt e
		Element alpha = pairing.pairing(c1, g);
		Element ialpha = alpha.powZn(isk_a);
		decrypt = c2.div(ialpha);
		*/
		
		//Re-Encryption
		reencrypttext.add(pairing.pairing(ciphertext1.get(0), rka_b));
		
		//Decrypt using first level decryption
		Element ialpha = reencrypttext.get(0).powZn(isk_b);
		decrypt_user1 = ciphertext2.get(0).div(ialpha);
		System.out.println("Length" +decrypt_user1.getLengthInBytes());
		result = new byte[152];
		result = decrypt_user1.toBytes();
		decoded = new String(result, "UTF-8");
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
		System.out.println("Cipher Text 1: " + ciphertext1.get(0));
		System.out.println("Cipher Text 2: " + ciphertext2.get(0));
		System.out.println("New C1: " + reencrypttext.get(0));
		System.out.println("Number of bytes read: " + x);
		//for(int i = 0; i < 152; i++){
		//System.out.println("Line number: " + i + " " + result[i]);
		//}
		System.out.println(decoded);
		
	}

}

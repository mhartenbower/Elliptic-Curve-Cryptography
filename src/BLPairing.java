import java.math.BigInteger;

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
	public static Element pk_a, sk_a, isk_a, pk_b, sk_b, ownersk_a, g, k, g_k, z_k, e, rka_b;
	
	public void pairing(){
		
		//Get the curve parameters
		PairingParameters curveParams = PairingFactory.getPairingParameters("a_181_603.properties");
		this.pairing = PairingFactory.getPairing(curveParams);
		
		//Initialize the parameters for first-level encryption
		g1 = pairing.getG1();
	    gt = pairing.getGT();
	    zr = pairing.getZr();
	    g = g1.newRandomElement().getImmutable();
		ElementPowPreProcessing gPre = g.getElementPowPreProcessing();

	    k= zr.newRandomElement();
	    g_k = gPre.powZn(k);
		z_k = pairing.pairing(g, g_k);
					      
		//Generate data owner keys
		sk_a = pairing.getZr().newRandomElement().getImmutable(); //private key
		pk_a = gPre.powZn(sk_a).getImmutable();
		isk_a = sk_a.invert().getImmutable(); //invert the secret key to calculate the proxy re-encryption key
		
		//Generate user keys (USER1)
		sk_b = pairing.getZr().newRandomElement().getImmutable(); //private key
		pk_b = gPre.powZn(sk_b).getImmutable();
		
		//Generate proxy re-encryption keys
		rka_b = pk_b.powZn(isk_a).getImmutable();
		
		//Encrypt an integer
		e = g1.newRandomElement();
		//e.set(5);
	}
	
	
	
	
	public static void main(String[] args){
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
	}

}

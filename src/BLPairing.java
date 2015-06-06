import java.math.BigInteger;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.ElementPowPreProcessing;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.jpbc.PairingPreProcessing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.util.math.BigIntegerUtils;


public class BLPairing {
	public Pairing pairing;
	public static Element pk;
	
	
	public void pairing(){
		PairingParameters curveParams = PairingFactory.getPairingParameters("a_181_603.properties");
		this.pairing = PairingFactory.getPairing(curveParams);
		
		Element g = pairing.getG1().newRandomElement().getImmutable();
		
		ElementPowPreProcessing gPre = g.getElementPowPreProcessing();
		
		PairingPreProcessing gPairing = pairing.getPairingPreProcessingFromElement(g);
		
		Element x = pairing.getZr().newRandomElement();
		
		
		pk = gPre.powZn(x);
		PairingPreProcessing pkPairing = pairing.getPairingPreProcessingFromElement(pk);

	}
	
	
	
	
	public static void main(String[] args){
		BLPairing pairing = new BLPairing();
		pairing.pairing();
		System.out.println(pk);
	}

}

package Utils;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import ArithmeticObjects.*;
import CryptoPrim.*;

/**
 * This class defines and implements ProtInfo object, which will contain all data
 * extracted from protInfo.xml file. It holds values as ByteTrees for later use.
 * @author amirz
 *
 */
public class ProtocolInformationFiles {
        // private members
        private byte[] f;
        private double protocolVersion;
        private int sessionIdentifier;
        private int numberOfParties;
        private int mixServersThreshold;
        private int bitsInRandomVector;
        private int statisticalError;
        private int bitsOfChallenge;   
        private HashFunction hashFunctionForRO;
        private PRG hashFunctionForPRG;
        private ModularGroup group;
        private int widthOfCipherPlainTexts;


        /**
         * Constructor, Retrieves and stores all public parameters from an XML protocol info file.
         * @param fileName
         * @throws Exception 
         */
        public ProtocolInformationFiles(String fileName) throws Exception{

                
                File fXmlFile = new File(fileName);
                this.f =  new ByteTree(fXmlFile).getByteRepOfTree();
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(fXmlFile);
                doc.getDocumentElement().normalize();
                Element protocolcInfoElement = doc.getDocumentElement();
               
                //extract protocol version
                String value = getTagValue("version", protocolcInfoElement);
                this.protocolVersion = Double.parseDouble(value);
                
                // extract the globally unique session identifier tied to the generation of a particular joint public key
                value = getTagValue("sid", protocolcInfoElement);
                this.sessionIdentifier = Integer.parseInt(value);

                // extract number of parties
                value = getTagValue("nopart", protocolcInfoElement);
                this.numberOfParties = Integer.parseInt(value);

                // extract threshold
                value = getTagValue("thres", protocolcInfoElement);
                this.mixServersThreshold = Integer.parseInt(value);
                
                // extract the number of bits in each component of random vectors used for batching in proofs of shuffles and proofs of correct decryption
                value = getTagValue("vbitlenro", protocolcInfoElement);
                this.bitsInRandomVector = Integer.parseInt(value);
                
                // extract statistical error
                value = getTagValue("statdist", protocolcInfoElement);
                this.statisticalError = Integer.parseInt(value);
                
                // extract random bits and challanges length
                value = getTagValue("cbitlenro", protocolcInfoElement);
                this.bitsOfChallenge = Integer.parseInt(value);

                // extract RO hashfunction
                value = getTagValue("rohash", protocolcInfoElement);
                this.hashFunctionForRO = new HashFunction(value);
                //this.hashFunctionForRO = value;
                
                // extract PRG hashfunction and create PRG
                value = getTagValue("prg", protocolcInfoElement);
                this.hashFunctionForPRG = new PRG(new HashFunction(value));
                // extract pgroup
                value = getTagValue("pgroup", protocolcInfoElement);
                this.group = modulPGroupTree.getTheNChild(1);
                // parse the group identifier
                int index = value.indexOf("::");
                String groupComment = value.substring(0, index-1);
                value = value.substring(index+2);
                value = value.replaceAll("\n", "");
                // umrashal modgroup
                if (groupComment.startsWith("ModPGroup")) { 
                	ByteTree modulPGroupTree = new ByteTree(value);
                	modulPGroupTree = MarshallingGroups.unMarshalModGroup(modulPGroupTree); 
                	this.group = modulPGroupTree.getTheNChild(1);
                }
                else {
                	ByteTree ECqPGroupTree = new ByteTree(value);
                	this.group = MarshallingGroups.unMarshalECqPGroupTree(ECqPGroupTree);
                }

                // extract default width of ciphertexts processed by mix-net
                value = getTagValue("width", protocolcInfoElement);
                this.widthOfCipherPlainTexts = Integer.parseInt(value);

               

                

                
}
        
   

/**
 * Extracts value for tag in given XML element.
 * @param sTag
 * @param eElement
 * @return string with value
 */
private static String getTagValue(String sTag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);

        return nValue.getNodeValue();
}

/**
 * Returns the version of Verificatum used during the execution that produced the proof.
 */
public double protocolVersion(){
        return protocolVersion;
}

/**
 * Returns the globally unique session identifier tied to the generation of a particular joint public key
 */
public int sessionIdentifier(){
        return sessionIdentifier;
}


/**
 * Returns the number of parties participating in the mix-net 
 */
public int getNumParties(){
        return numberOfParties;
}

/**
 * Returns the maximum number if mix-servers that must be corrupted to break the privacy of the senders.
 */

public int getThreshold() {
        return mixServersThreshold;
}
/**
 * Returns the number of max ciphers
 */
public int getMaxCiphers() {
        return widthOfCipherPlainTexts;
}
/**
 * Returns the number of random bits in each vector used for batching in proofs.   
 */
public int getNumRandomBits() {
        return bitsInRandomVector;
}
/**
 * returns the acceptable statistical error when sampling random
 * values. 
 */
public int getStatError() {
        return statisticalError;
}
/**
 * Returns the number of bits used in the challenge of the verifier
 * in zero-knowledge proofs
 */
public int getNumChallengeBits() {
        return bitsOfChallenge;
}
/**
 * Returns the HashFunction to be used in the RandomOracle instances.
 * @return
 */
public HashFunction getROHash() {
        return hashFunctionForRO;
}
/**
 * Returns a PRG instance created with the hash function described in the file.
 * @return
 */
public PRG getPrg() {
        return hashFunctionForPRG;
}

/**
 * Returns the underlying modular group used in the proofs.
 */
public ModularGroup getGroup() {
        return group;
}
/**
 * Returns a byte array containing the entire xml file.
 * @return
 */
public byte[] getF(){
        return f;
}

}

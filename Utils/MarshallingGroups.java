package utils;

import java.io.UnsupportedEncodingException;

import CryptoPrim.HashFunction;

import ArithmeticObjects.*;
import ArithmeticUtils.BigNumber;


/**
 * This class defines specific unmarshalling functions, used to extract certain objects
 * from their marhsalled representation in a ByteTree = node(str,obj).
 * str is a ByteTree representing a string which declares the instance of obj. 
 * @author amirzell
 *
 */
public class MarshallingGroups {
/**
 * Receives a ByteTree bt as input, and returns the instance of the crypt
 * function represented by bt.
 */
    //    public static Object unmarshalHashFunc(ByteTree bt) throws UnsupportedEncodingException{
      //          return new HashFunction(bt.getString());
       // }
        
        /**
         * Receives a marshalled ByteTree representing a Modular group and returns
         * the specific modular group represented.
         * @param bt
         * @return ModularGroup
         * @throws Exception - if error occurs in ModularGroup constructor
         */
        public static BTGroup<T> unMarshalGroup(String marshalledGroup) throws Exception {
        	
            // parse the group identifier
            int index = marshalledGroup.indexOf("::");
            String groupComment = marshalledGroup.substring(0, index-1);
            String groupData = marshalledGroup.substring(index+2);
            groupData = marshalledGroup.replaceAll("\n", "");
            // umrashal modgroup
            if (groupComment.startsWith("ModPGroup")) { 
            	 ByteTree unMarshallGroupBt = ByteTree.generateEmptyNode();
            	 ByteTree pgroupDataBt = new ByteTree(groupData);
                 ByteTree pgroupTypeStr = new ByteTree ("verificatum.arithm.ModPGroup");
                 BigNumber p = pgroupDataBt.getTheNChild(0).getBigNum();
                 BigNumber q = pgroupDataBt.getTheNChild(1).getBigNum();
                 BigNumber g = pgroupDataBt.getTheNChild(2).getBigNum();
                 ModularGroup mdGroup = new ModularGroup (p,q,g);
                 //unMarshalPgroupBt.addMultChildren(pgroupTypeStr,pgroupDataBt);
                 return mdGroup;
            }
            else {
            	ByteTree ecqPGroupDataBt = new ByteTree(groupData);
            	//this.group = MarshallingGroups.unMarshalECqPGroupTree(ECqPGroupTree);
            	ElipticCurve ecqGroup = new ElipticCurve(groupData);
            	return ecqGroup;
            }
               
        }
        
        /**
         * Receives a marshalled ByteTree representing an ElipticCurve group and returns
         * the specific modular group represented. Gq=unmarshal(s)
         * @param bt
         * @return ElipticCurveGroup
         * @throws Exception - if error occurs in ElipticCurve constructor
         */
   
        
        
        /**
         * Receives an unmarshalled (Gq) ElipticCurve group and returns
         * the HASCII string (s) representation of it, such as s=marshal(Gq).
         * @param bt
         * @return ElipticCurveGroup
         * @throws Exception - if error occurs in ElipticCurve constructor
         */
        public static String MarshalECqPGroup(ByteTree pgroupDataBt) throws Exception {
        	 ByteTree unMarshalPgroupBt = ByteTree.generateEmptyNode();
             ByteTree pgroupTypeStr = new ByteTree ("verificatum.arithm.ECqPGroup");
             ElipticCurve ecGroup = new ElipticCurve (pgroupDataBt.getString());
             unMarshalPgroupBt.addMultChildren(pgroupTypeStr,pgroupDataBt);
             return unMarshalPgroupBt;
        }
  
}
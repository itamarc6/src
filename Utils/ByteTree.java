package Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.*;
import ArithmeticUtils.Array;
import ArithmeticUtils.BigNumber;

/**
 * ByteTree is a class which implements a byte tree structure in order to use it in mix-net
 * @author nirhagin
 *
 */
public class ByteTree  {
	private byte[] byteRepOfTree;
	private boolean isLeaf;
	private int sizeOfData;
	private int amountOfDirectChildren;
	private List<ByteTree> directChildren;
	private byte [] treeData;
	private String string;
	private BigNumber bigNum;
	
    /**
	 * creates a new byte tree object from an ASCII string
	 * @param string
	 * @throws IOException
	 * @return a new byte tree 
     * @throws UnsupportedEncodingException 
	 */
    public ByteTree (String string) throws UnsupportedEncodingException  {//Contractor which gets a ascii string and sends it as byte array to this()
		
		this(string.getBytes("ASCII"));
	}
	
    /**
     * crteates a byte tree from a given byte array
     * update the relevant fields according to the bytes in the array
     * @param byteArr
     * @return new byte tree that represents the bytes in the array
     * @throws IOException
     */
	public ByteTree(byte[] byteArr) {
		//makes a copy of the original byte arry to be saved in the private field
		 byte [] copy = byteArr.clone();
		 this.byteRepOfTree= copy;
		 //checks if the byte array represents a leaf
		 if (byteArr[0]==1){
			 this.isLeaf = true;
			 byte [] sizeByte = new byte [4]; 
			 System.arraycopy(byteArr, 1, sizeByte, 0, 4);
			 this.sizeOfData = getIntFromByteArr (sizeByte); // calculates the size of the data
			 this.treeData = new byte [this.sizeOfData];
			 System.arraycopy(byteArr, 5, this.treeData, 0, this.sizeOfData); // creates an array only from the data
			 try {
				this.string = new String (this.treeData, "US-ASCII");
			} catch (UnsupportedEncodingException e) {
				System.out.println("UnsupportedEncodingException");
				e.printStackTrace();
			}// creates the string that represent the data
			 this.bigNum = new BigNumber (this.treeData);// creates the big number that represent the data
			 this.amountOfDirectChildren=0;//sets as 0 - leaf does not have children
			 
		 }
		 else if (byteArr[0]==0){
			 this.isLeaf = false; //sets to false which represents a node
			 byte [] sizeByte = new byte [4];
			 System.arraycopy(byteArr, 1, sizeByte, 0, 4);
			 this.amountOfDirectChildren = getIntFromByteArr (sizeByte); // calculates the number of children
			 this.directChildren = new ArrayList<ByteTree> ();
			 this.string=null; //sets as null - node does not have data
			 this.bigNum = null;//sets as null- node does not have data
			 byte [] newArr = new byte [this.byteRepOfTree.length-5];
			 System.arraycopy(byteArr, 5, newArr,0,newArr.length);// copy the initial representation of the byte tree without 
			//the first 5 bytes
			 for(int k=0;k<this.amountOfDirectChildren;k++){
				int lastIndexOfChild;
				try {
					lastIndexOfChild = getLastIndex(newArr, 0);
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
                byte [] child = new byte [lastIndexOfChild];
                for (int i=0;i<lastIndexOfChild;i++){//creates a byte array only from the bytes of the k child
                	child[i]=newArr[i];
                }
                this.directChildren.add(new ByteTree(child));//adds the child as a byte tree to the list of children
                System.arraycopy(newArr, lastIndexOfChild, newArr,0,newArr.length-lastIndexOfChild);
					 
			 }
			 
			 
		 	}
		 	else assert (byteArr[0]==0||byteArr[0]==1):"input is incorrect";
		 
	 }
	 
    /**
     * extracts from the list of children the byte tree which represents the i child
     * @param i - the index of the child which the user wants to extract
     * @return byte tree which represents the i child of this
     */
	public ByteTree getTheNChild (int i){
		return this.directChildren.get(i);
	}
	
	

    /**
     * search for the last index of a node/leaf
     * iterates over all the children of a node and finds the last index in the array
     * if newArr root represents a leaf it finds the last index of its data
     * @param newArr - byte array that contains the relevant node and more
     * @param first - the point that the relevant node/leaf starts
     * @return the index of the node/leaf which starts at first
     * @throws IOException
     */
	private int getLastIndex(byte[] newArr, int first) throws IOException {
		if (newArr[first]==1){
			byte [] size = new byte [4];
			for(int i =0;i<4;i++){
				size [i]=newArr[first+i+1];
			}
			int sizeInt = getIntFromByteArr (size);
			return (first + 1 + 4 + sizeInt );
		}
		else if (newArr[first]==0){
			byte [] size = new byte [4];
			for(int i =0;i<4;i++){
				size [i]=newArr[first+i+1];
			}
			//checks how many children there is to this node
			int sizeInt = getIntFromByteArr (size);
			first=first +5;
			//iterates over all the children of the node
			for (int j=0;j<sizeInt;j++){
				first = getLastIndex (newArr,first);
			}
			return first;
		}
		else throw new IOException ("Incorrect input, can not create a byte tree");
	}

    /**
     * 
     * @return the initial representation of this as array of bytes
     */
	public byte[] getByteRepOfTree() {
		return byteRepOfTree;
	}

    /**
     * 
     * @return true if this is a leaf and false otherwise
     */
	public boolean isLeaf() {
		return isLeaf;
	}

    /**
     * 
     * @return int that represents the size of the data if this is a leaf and 0 otherwise
     */
	public int getSizeOfData() {
		return sizeOfData;
	}

    /**
     * @return int that represents the amount of the children if this is a node and 0 otherwise
     */
	public int getAmountOfDirectChildren() {
		return amountOfDirectChildren;
	}

    /**
     * 
     * @return a list that contains all the children of this in the right order
     * the list elements are byte trees
     */
	public List<ByteTree> getDirectChildren() {
		 return this.directChildren;
	}

    
	/**
	 * 
	 * @return byte array which represents the data of the byte tree if this is a leaf and null otherwise
	 */
	public byte[] getTreeData() {
		return treeData;
	}

    /**
     * 
     * @return string which represents the data of the byte tree if this is a leaf and null otherwise
     */
	public String getString() {
		return string;
	}

	/**
     * 
     * @return BigNumber which represents the data of the byte tree if this is a leaf and null otherwise
     */
	public BigNumber getBigNum() {
		return bigNum;
	}

    /**
     * private methods that converts a byte array into an integer
     * @param sizeByte - a byte array
     * @return an integer that its representation in bytes is the byte array
     */
	private int getIntFromByteArr(byte[] sizeByte) {
		ByteBuffer buffer = ByteBuffer.wrap(sizeByte);
        return buffer.getInt();
	}

	/**
	 * this method gets a byte array that contains data and generate a new byte tree from it
	 * @param data a byte array that contains the data
	 * @return a byte tree which contains one leaf that its data is the data in the array
	 * @throws IOException
	 */
    public static ByteTree generateNewLeaf (byte [] data) {
    	byte [] temp = new byte [5+data.length];//creates a byte array in size of data + marks
    	temp[0]=(byte)1; //mark as leaf
    	byte [] lengthOfDataInBytes = intToByteArray (data.length);
    	System.arraycopy(lengthOfDataInBytes, 0, temp, 1, 4);
    	System.arraycopy(data, 0, temp, 5, data.length);
    	return new ByteTree (temp);
    	
    }
    
    public static ByteTree generateNewLeaf (String str){
    	return generateNewLeaf(TypeConvertionUtils.stringToByteArr(str));
    }
    
    
    /**
     * static method that generate a new byte tree that contains only an empty node
     * @return a byte tree with an empty node
     * @throws IOException
     */
    public static ByteTree generateEmptyNode () {
    	byte [] temp = new byte [5];
    	temp [0]=(byte)0;//mark as node
    	// set the amount of children as 0
    	temp [1]=(byte)0;
    	temp [2]=(byte)0;
    	temp [3]=(byte)0;
    	temp [4]=(byte)0;
    	
    	return new ByteTree (temp);
    	
    }
    
    /**
     * checks if this is a leaf
     * if true- throws exception
     * else- iterates over all the children that were given by the user and add them as new children to this
     * uses the method add child 
     * @param children 
     * @return this with the new children
     * @throws IOException
     */
    public ByteTree addMultChildren (ByteTree... children){
    	if (this.isLeaf==true)
			try {
				throw new IOException ("Incorrect input, can not add a child to a leaf");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else if (children.length ==0) return this;
    	else {
    		for(ByteTree b : children){//iterates over all the parameters and adds them to this
    			this.addChild(b);
    		}
    		return this;
    	}
		return null;
    	
    }
    
    
    /**
     * checks if this is a leaf
     * if true throwchild to this exception
     * else add the byte tree that was given as a new 
     * @param child- a byte tree that the user wants to add to this  
     * @return this with the given byte tree as one of its children
     * @throws IOException
     */
    public ByteTree addChild (ByteTree child){
    	if (this.isLeaf == true)
			try {
				throw new IOException ("Incorrect input, can not add a child to a leaf");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else {
    		this.amountOfDirectChildren=this.amountOfDirectChildren +1; //sets the amount of children
    		this.directChildren.add(child);
    		byte [] newAmount = intToByteArray(this.amountOfDirectChildren);
    		byte [] newRep = new byte [this.byteRepOfTree.length + child.byteRepOfTree.length];
    		System.arraycopy(this.byteRepOfTree, 0, newRep, 0, 1);//copy the mark
    		System.arraycopy(newAmount, 0, newRep, 1, 4);//set the 4 bytes of the amount of children
    		System.arraycopy(this.byteRepOfTree, 5, newRep, 5, this.byteRepOfTree.length-5);
    		System.arraycopy(child.byteRepOfTree, 0, newRep, this.byteRepOfTree.length, child.byteRepOfTree.length);
    		this.byteRepOfTree=newRep;//sets the new byte array as the initial representation of the byte tree
    		return this;
    	}
		return child;
    }
    
    
    /**
     * this method replaces the child in index with the given byte tree
     * if index is not exists in children list the method throw exception
     * @param tree the new child
     * @param index the index of the child that the user wants to replace
     * @return this with tree as the child in the index place
     * @throws IOException
     */
    public ByteTree replaceChildInTree (ByteTree tree, int index) throws IOException{
    	//checks if this is a leaf
    	if (this.isLeaf)throw new IOException ("Incorrect input, tree is a leaf"); 
    	//checks if index is in the range of this amount of children
    	else if (index<0 || index>this.directChildren.size()-1)throw new IOException ("Incorrect input,index does not exists"); 
    		 else {
    			 int lenOfCurrentRep = this.byteRepOfTree.length;
    			 int lenOfReplacedChild = this.directChildren.get(index).byteRepOfTree.length;
    			 int lenOfNewChild = tree.byteRepOfTree.length;
    			 byte [] newRep = new byte [(lenOfCurrentRep-lenOfReplacedChild)+lenOfNewChild];
    			 
    			 System.arraycopy(this.byteRepOfTree, 0, newRep, 0, 5);
    			 int count =5;
    			 for (int i=0;i<index;i++){
    				 System.arraycopy(this.directChildren.get(i).byteRepOfTree, 0, newRep, count, this.directChildren.get(i).byteRepOfTree.length);
    				 count = count + this.directChildren.get(i).byteRepOfTree.length;
    			 }
    			 System.arraycopy(tree.byteRepOfTree, 0, newRep, count, tree.byteRepOfTree.length);
    			 count = count + tree.byteRepOfTree.length;
    			 for (int j=index+1;j<this.directChildren.size();j++){
    				 System.arraycopy(this.directChildren.get(j).byteRepOfTree, 0, newRep, count, this.directChildren.get(j).byteRepOfTree.length);
    				 count = count + this.directChildren.get(j).byteRepOfTree.length;
    			 }
    			 this.directChildren.set(index, tree);
    			 this.byteRepOfTree = newRep;
    			 return this;
    		 }
    }

	/**
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static ByteTree loadFromFile(String path){
		try{
			FileInputStream fis = new FileInputStream(path + ".bt");
			byte[] b = new byte[fis.available()];
			fis.read(b);
			ByteTree BT = new ByteTree(b);
			fis.close();
			return BT;
		} catch (Exception e){
			System.err.println("ERROR: failed reading from 'bt' file.");
			return null;
		}
	}
	
	public static ByteTree loadFromFile(String path, int l){
		if (l < 10)
			return loadFromFile(path + "0" + l);
		else
			return loadFromFile(path + l);
	}


	/**
     * 
     * @param value - the int that the user wants to convert
     * @return a byte array that represents the int in bytes
     */
    private static  byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }
    
    
    public static ByteTree byteTreeFromBooleanArr (Array<Boolean> arr){
    	int len = arr.length ();
    	byte [] data = new byte [len];
    	int i=0;
    	Iterator<Boolean> iter = arr.iterator();
    	while (iter.hasNext()){
    		if (iter.next()) data[i]=(byte)1;
    		else data[i]=(byte)0;
    		i++;
    	}
    	
			return generateNewLeaf(data);
		
    }
    
    public static  byte[] HexToByte(String hex)  {  
                    int len = hex.length();  
                    byte[] value= new byte[len / 2];  
                    for (int i = 0; i < len; i += 2)  
                      {  
                        value[i / 2] = (byte) ((Character.digit(hex  
                            .charAt(i), 16) << 4) + Character.digit(hex  
                            .charAt(i + 1), 16));  
  
                      }  
                    
                    return value;  
    }  
    
    

    
	
}
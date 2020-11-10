package serialization;

import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

public class SerialObject {
    /** The name of the object to be stored. */
	String fileName;
	/** The properties object. */
	Properties prop;

	public SerialObject(String fileName) {
		this.fileName = fileName;
	}
	
	public void write() {
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			this.prop = System.getProperties();
			for (Enumeration<?> names = prop.propertyNames(); names.hasMoreElements(); ) {
				String property = (String)names.nextElement();
				System.out.println(property+": "+prop.getProperty(property));
			}
			oos.writeObject(prop);
			oos.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void read() {
		try {
		      FileInputStream fis = new FileInputStream(fileName);
		      ObjectInputStream ois = new ObjectInputStream(fis);
		      this.prop = (Properties) ois.readObject();
		      for (Enumeration<?> names = prop.propertyNames(); names.hasMoreElements(); ) {
		    	  String property = (String)names.nextElement();
		    	  System.out.println(property+": "+prop.getProperty(property));
		      }
		      ois.close();
			} catch(Exception e) {
				e.printStackTrace();
			}		
	}
	
	public Properties readProp() {
		try {
			FileInputStream fis = new FileInputStream(fileName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Properties aProp = (Properties) ois.readObject();
			ois.close();
			return aProp;
		} catch(Exception e) {
			e.printStackTrace();
		}	
        return null;
	}
	
	public Hashtable<String,String> diff(Properties aProp) {
		this.prop = System.getProperties();
		Hashtable<String,String> res = new Hashtable<String,String>();
		for (Enumeration<?> names = prop.propertyNames(); names.hasMoreElements(); ) {
			String property = (String)names.nextElement();
			String value1 = prop.getProperty(property);
			String value2 = aProp.getProperty(property);
			if (!value1.equals(value2)) { //add to hashtable
				res.put(property,value1+" / "+value2);
			}
		}		
		return res;
	}
	
	public static void main(String args[]) {
		//Verify the number of arguments
		if (args.length != 2) {
			System.err.println("Usage: java serialization.SerialObject fileName read");
			System.err.println("       java serialization.SerialObject fileName write");
			System.err.println("       java serialization.SerialObject fileName diff");
			System.exit(-1);
		}
		SerialObject so = new SerialObject(args[0]);
		if (args[1].equals("read")) {
			so.read();
		} else if (args[1].equals("write")) { 
			so.write();
		} else if (args[1].equals("diff")) {
		    Properties aProp = so.readProp();
		    Hashtable<String,String> hash = so.diff(aProp);
		    for (String key: hash.keySet()) {
		    	System.out.println(key+": "+hash.get(key));
		    }
		} else {
			System.err.println("Unknown command: "+args[1]);
		}

	}
	
	
}

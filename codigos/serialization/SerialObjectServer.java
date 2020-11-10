package serialization;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

public class SerialObjectServer {

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(4444);
            while(true) {
                Socket socket = server.accept();
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Properties aProp = (Properties) ois.readObject();
                System.out.println("Received call, performing diff");
                Hashtable<String,String> diff = diff(aProp);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(diff);
                ois.close();
                oos.close();
                socket.close();
            }
        } catch (Exception e) {
            System.err.println("Server failed");
        }
    }

    public static Hashtable<String,String> diff(Properties aProp) {
        Properties prop = System.getProperties();
        Hashtable<String,String> res = new Hashtable<>();
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
}

package serialization;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Properties;

public class SerialObjectClient extends SerialObject {

    public void serverdiff(Properties aProp) {
        try {
            InetAddress host = InetAddress.getLocalHost();
            Socket socket = new Socket(host.getHostName(), 4444);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(aProp);
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Hashtable<String,String> diff = (Hashtable<String,String>) ois.readObject();
            for (String key: diff.keySet()) {
                System.out.println(key+": "+diff.get(key));
            }
            ois.close();
            oos.close();
            socket.close();
        } catch (Exception e) {
        }
    }

    public SerialObjectClient(String fileName) {
        super(fileName);
    }

    public static void main(String args[]) {
        //Verify the number of arguments
        if (args.length != 2) {
            System.err.println("Usage: java serialization.SerialObject fileName read");
            System.err.println("       java serialization.SerialObject fileName write");
            System.err.println("       java serialization.SerialObject fileName diff");
            System.err.println("       java serialization.SerialObject fileName serverdiff");
            System.exit(-1);
        }
        SerialObjectClient so = new SerialObjectClient(args[0]);
        if (args[1].equals("read")) {
            so.read();
        } else if (args[1].equals("write")) {
            so.write();
        } else if (args[1].equals("serverdiff")) {
            Properties aProp = so.readProp();
            so.serverdiff(aProp);
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

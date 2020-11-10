package thread;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Simple multi-threaded TCP server that returns the sent message in upper case.
 */
public class TCPServerThreaded extends Thread {
	/** Well-known server port. */
	public static int serverPort = 9000;

	/** Client socket for the thread. */
	Socket server;

	/**
	 * Creates a new TCPServerThreaded worker thread.
	 * @param server The client socket for this object.
	 */
	public TCPServerThreaded (Socket server){
		this.server = server;
	}

	public void run() {
		try {
			// Create a BufferedReader object to read strings from the socket. (read strings FROM CLIENT)
			BufferedReader br = new BufferedReader(new InputStreamReader(server.getInputStream()));
			String input = br.readLine();
			//Create output stream to write to/send TO CLIENT
			DataOutputStream output = new DataOutputStream(server.getOutputStream());
			//Keep repeating until the command "quit" is read.
			while (!input.equals("quit")) {
				//Convert input to upper case and echo back to client.
				System.out.println("From client "+ server.getInetAddress().getHostAddress() +":"+server.getPort()+": " +input);
				output.writeBytes(input.toUpperCase() + "\n");
				input = br.readLine();
			}
			System.out.println("Connection closed from "+ server.getInetAddress().getHostAddress()+":"+server.getPort());
			//Close current connection
			br.close();
			output.close();
			server.close();
		} catch (IOException e) {
			//Print exception info
			e.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	public static void main (String args[]) throws Exception {
        //Dispatcher socket
		ServerSocket serverSocket = new ServerSocket(serverPort);
		//Waits for a new connection. Accepts connection from multiple clients
		while (true) {
			System.out.println("Waiting for connection at port "+serverPort+".");
			//Worker socket
			Socket s = serverSocket.accept();
			System.out.println("Connection established from " + s.getInetAddress().getHostAddress() + ", local port: "+s.getLocalPort()+", remote port: "+s.getPort()+".");
			//Invoke the worker thread
			TCPServerThreaded worker = new TCPServerThreaded(s);
			worker.start();
		}
	}
}

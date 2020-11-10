package client;

import java.io.*;
import java.net.*;
import java.util.Date;


/**
 * Simple TCP client. Use the command "quit" to end the communication.
 *
 */
public class TCPClient {
	/** Well-known server port. */
	public static int serverPort = 9000;
	/** Hostname. */
	public static String hostname = "192.168.0.15";

	public static void main (String args[]) throws Exception {
		// Connect to the server process running at localhost:serverPort
		Socket s = new Socket(hostname, serverPort);

		// The next 2 lines create a output stream we can
		// write to.  (To write TO SERVER)
		OutputStream os= s.getOutputStream();
		DataOutputStream serverWriter = new DataOutputStream(os);

		// The next 2 lines create a buffer reader that
		// reads from the standard input. (to read stream FROM SERVER)
		InputStreamReader isrServer = new InputStreamReader(s.getInputStream());
		BufferedReader serverReader = new BufferedReader(isrServer);

		//Create buffer reader to read input from user. Read the user input to string 'sentence'
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		String sentence;

		//Read the sentence
		System.out.print("Client$ ");
		sentence = inFromUser.readLine();

		//Get a time stamp
		Date timeStamp = new Date();
		//Keep repeating until the command "bye" is read.
		while (!sentence.equals("quit")) {
			// Send a user input to server
			serverWriter.writeBytes(sentence +"\n");
			// Server should convert to upper case and reply.
			// Read server's reply below and output to screen.
			String response = serverReader.readLine();
			//Get a new time stamp and calculate elapsed time
			long elapsed =  (new Date()).getTime() - timeStamp.getTime();
			System.out.println("Response: " +response+" in "+ elapsed +" miliseconds.");
			//read user input again
			System.out.print("Client$ ");
			sentence = inFromUser.readLine();
			//Get the time stamp again
			timeStamp = new Date();
		}
		// Send an quit command to server to end communication.
		serverWriter.writeBytes("quit\n");
		//Close communication
		serverWriter.close();
		isrServer.close();
		s.close();
	}
}

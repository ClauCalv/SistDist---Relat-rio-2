package nio;

import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;


/**
 * Simple Java Non-blocking IO TCP server.
 *
 */
public class TCPServerNIO {
	/** Well-known server port. */
	public static int serverPort = 9000;

	public static void main(String[] args) {
		try {
			//Channel for server socket
			ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.socket().bind(new InetSocketAddress(serverPort));
			String serverAddress = serverSocketChannel.socket().getInetAddress().getHostAddress().toString();
			System.out.println("Waiting for connection at port "+serverPort+".");
			//Configure the server socket as non-blocking
			serverSocketChannel.configureBlocking(false);
			//Creates the selector
			Selector selector = Selector.open();
			//Register the server for incoming connections
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT, serverAddress);
			//Loop for accepting new connections
			while (true) {
				// Waiting for events
				selector.select();
				// Get keys
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = keys.iterator();
				// For each keys
				while(iterator.hasNext()) {
					//Get the key
					SelectionKey key = (SelectionKey) iterator.next();
					// Remove the current key
					iterator.remove();
					/* if isAccetable = true, then a client required a connection */
					if (key.isAcceptable()) {
						// get client socket channel
						SocketChannel client = serverSocketChannel.accept();
						//Get the remote IP address
						String address = client.socket().getInetAddress().getHostAddress();
						System.out.println("Connection established from " + address + ", local port: "+serverPort+", remote port: " + client.socket().getPort()+".");
						//Configure as non-blocking
						client.configureBlocking(false);
						// recording to the selector (reading)
						client.register(selector, SelectionKey.OP_READ, address);
					}
					/* if isReadable = true, then the server is ready to read. */
					if (key.isReadable()) {
						// get client socket channel
						SocketChannel client = (SocketChannel) key.channel();
						//Get the remote IP address
						String address = client.getRemoteAddress().toString();
						// Allocate a buffer
						ByteBuffer buffer = ByteBuffer.allocate(65536);
						try {
							//Copy the data to the buffer
							int read = client.read(buffer);
							//If there is data on buffer do
							if (read > 0) {
								//make buffer ready for read
								buffer.flip();
								//Create a new string input
								StringBuffer input = new StringBuffer();
								// read 1 byte at a time
								while(buffer.hasRemaining()){
									input.append((char) buffer.get());
								}
								//Verify if a "quit" command was sent
								if (input.toString().equals("quit\n")) {
									System.out.println("Connection closed from "+ address);
									//Close current communication
									key.cancel();
									client.close();
								} else { //Otherwise, sent the response in upper case letters
									//Convert input to upper case and echo back to client.
									System.out.print("From client "+ address+": " +input);
									//To uppercase
									String upperCase = input.toString().toUpperCase();
									//make buffer ready for writing
									buffer.clear();
									//Put the string on the buffer
									buffer.put(upperCase.getBytes());
									//Flip the buffer
									buffer.flip();
									//Write the data to the socket channel
									client.write(buffer);
								}
							}
						} catch (Exception e) {
							key.cancel();
							client.close();
							e.printStackTrace();
						} //inactive client
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

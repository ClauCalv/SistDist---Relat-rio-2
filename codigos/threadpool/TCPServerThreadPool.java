package threadpool;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.List;
import java.util.ArrayList;


/**
 * Simple multi-threaded TCP server that returns the sent message in upper case.
 */
public class TCPServerThreadPool{
	/** Well-known server port. */
	public static int serverPort = 9000;

  private class TCPThread extends Thread { // Deveria ter colocado em outro arquivo? Provavelmente sim.

    private boolean isStopped = false;

    public void run() {
      while(!isStopped()){
        try{
          work(taskQueue.take());
        } catch(Exception e){
        //log or otherwise report exception,
        //but keep pool thread alive.
        }
      }
    }

    private void work(Socket server){
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

    public synchronized void doStop(){
        isStopped = true;
        this.interrupt(); //break pool thread out of dequeue() call.
    }

    public synchronized boolean isStopped(){ return isStopped; }

  }

  private final BlockingQueue<Socket> taskQueue;
  private final List<TCPThread> threads;
  private boolean isStopped = false;

  public TCPServerThreadPool(int noOfThreads, int maxNoOfTasks){

    taskQueue = new ArrayBlockingQueue<>(maxNoOfTasks);
    threads = new ArrayList<TCPThread>();

    for(int i=0; i<noOfThreads; i++)
        threads.add(new TCPThread());

    for(TCPThread thread : threads)
        thread.start();

  }

  public synchronized void work(Socket socket) throws Exception{
    if(isStopped) throw
      new IllegalStateException("ThreadPool is stopped");

    taskQueue.put(socket);
  }

  public synchronized void stop(){
    isStopped = true;
    for(TCPThread thread : threads)
      thread.doStop();
  }

	@SuppressWarnings("resource")
	public static void main (String args[]) throws Exception {

    TCPServerThreadPool ts = new TCPServerThreadPool(10, 20);
        //Dispatcher socket
		ServerSocket serverSocket = new ServerSocket(serverPort);
		//Waits for a new connection. Accepts connection from multiple clients
		while (true) {
			System.out.println("Waiting for connection at port "+serverPort+".");
			//Worker socket
			Socket s = serverSocket.accept();
			System.out.println("Connection established from " + s.getInetAddress().getHostAddress() + ", local port: "+s.getLocalPort()+", remote port: "+s.getPort()+".");
			//Invoke the worker thread
			ts.work(s);
		}

    //ts.stop(); Unreachable por causa do while(true)
	}
}

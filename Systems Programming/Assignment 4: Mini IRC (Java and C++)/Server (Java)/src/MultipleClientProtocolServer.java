import java.io.*;
import java.net.*;

public class MultipleClientProtocolServer implements Runnable {

	private ServerSocket serverSocket;
	private int listenPort;
	private ServerProtocolFactory factory;
	
	public MultipleClientProtocolServer(int port, ServerProtocolFactory p)
	{
		serverSocket = null;
		listenPort = port;
		factory = p;
	}
	
	public void run()
	{
		try {
			serverSocket = new ServerSocket(listenPort);
			System.out.println("Listening...");
		}
		catch (IOException e) {
			System.out.println("Cannot listen on port " + listenPort);
		}
		
		while (Driver.run && !serverSocket.isClosed())
		{
			try {
				ConnectionHandler newConnection = new ConnectionHandler(serverSocket.accept(), factory.create());
				new Thread(newConnection).start();
			}
			catch (IOException e)
			{
				System.out.println("Failed to accept on port " + listenPort);
			}
		}
		try {
			close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Closes the connection
	public void close() throws IOException
	{
		serverSocket.close();
	}

}

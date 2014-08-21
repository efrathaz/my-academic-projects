import java.io.*;
import java.net.*;

public class ConnectionHandler implements Runnable {

	private BufferedReader in;
	private PrintWriter out;
	Socket clientSocket;
	ServerProtocol protocol;
	
	public ConnectionHandler(Socket acceptedSocket, ServerProtocol p)
	{
		in = null;
		out = null;
		clientSocket = acceptedSocket;
		protocol = p;
		System.out.println("Accepted connection from client!");
		p.setCn(this);
	}
	
	public synchronized void print(String msg){
		out.println(msg);
	}

	public void run()
	{
		try {
			initialize();
		}
		catch (IOException e) {
			System.out.println("Error in initializing I/O");
		}
		try {
			process();
		} 
		catch (IOException e) {
			System.out.println("Error in I/O");
		} 
		System.out.println("Connection closed - bye bye...");
		close();

	}
	
	public void process() throws IOException
	{
		String msg;
		while ((msg = in.readLine()) != null)
		{
			String response = protocol.processMessage(msg);
			if (response != null)
			{
				print(response);
			}
			if (protocol.isEnd(msg))
			{
				break;
			}
		}
	}
	
	// Starts listening
	public void initialize() throws IOException
	{
		// Initialize I/O
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(),"UTF-8"));
		out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(),"UTF-8"), true);
		System.out.println("I/O initialized");
		print("Welcome to the SPL Internet Relay Chat Network. Your host is /"+clientSocket.getInetAddress()+ " " +clientSocket.getPort());
	}
	
	// Closes the connection
	public void close()
	{
		try {
			if (in != null)
			{
				in.close();
			}
			if (out != null)
			{
				out.close();
			}
			clientSocket.close();
		} catch (IOException e)
		{
			System.out.println("Exception in closing I/O");
		}
	}
}

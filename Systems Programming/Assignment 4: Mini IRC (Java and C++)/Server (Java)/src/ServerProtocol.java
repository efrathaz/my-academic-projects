public interface ServerProtocol {
		
	String processMessage(String msg);
	boolean isEnd(String msg);
	void setCn(ConnectionHandler _cn);

}

import java.util.Enumeration;
import java.util.Hashtable;

public class Channel {
	
	private String channelName;
	private String operator;
	private Hashtable<String,MiniIRCProtocol> users;
	
	public Channel(String _channelName , MiniIRCProtocol _operator){
		channelName = _channelName;
		users = new Hashtable<String,MiniIRCProtocol>();
		operator = _operator.getNickname();
		users.put(_operator.getNickname(),_operator);
	}
		
	public void add(String nickname , MiniIRCProtocol p ){
		users.put(nickname,p);
	}
	
	public void remove(String nickname){
		users.remove(nickname);
	}
	
	public String getName(){
		String ans = ""+channelName;
		return ans;
	}
	
	public void print(String msg){
		Enumeration<MiniIRCProtocol>  e;
		e = users.elements();
		
		while(e.hasMoreElements()){
			e.nextElement().getConnectionHandler().print(msg);
		}
	}
	
	public String list(){
		String list = channelName;
		String nic;
		Enumeration<MiniIRCProtocol>  e;
		e = users.elements();
		while(e.hasMoreElements()){
			nic = e.nextElement().getNickname();
			if(nic.equals(operator)){
				list = list +" @"+nic;
			}
			else{
				list = list +" "+nic;
			}
		}
		return list;
	}
	
}
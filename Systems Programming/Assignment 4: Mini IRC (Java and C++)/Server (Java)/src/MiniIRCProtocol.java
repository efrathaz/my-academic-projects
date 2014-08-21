import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;

public class MiniIRCProtocol implements ServerProtocol {
	private static Hashtable<String,Channel> channels = new Hashtable<String,Channel>();
	private static Hashtable<String,MiniIRCProtocol> nicknames = new Hashtable<String,MiniIRCProtocol>();
	private String nickname;
	private String username;
	private Channel chan;
	ConnectionHandler cn;
	
	public MiniIRCProtocol() {
		cn = null;
		nickname = null;
		username = null;
		chan = null;
	}
	
	public void setCn(ConnectionHandler _cn){
		cn =_cn;
	}
	
	public ConnectionHandler getConnectionHandler(){
		return cn;
	}
	
	public String getNickname(){
		return ""+nickname;
	}
		
	public String processMessage(String msg)
	{
		String ans = null;
		Scanner Msg = new Scanner(msg);
		String command = "";
		if (Msg.hasNext()){
			command = Msg.next();
		}
		String parameter = "";
		if (Msg.hasNext()){
			parameter = Msg.next();
		}
		while (Msg.hasNext()){
			parameter = parameter+" "+ Msg.next();
		}
		Msg.close();
		
		if(nickname == null && !"NICK".equals(command)){
			return "451 ERR_NOTREGISTERED : You have not registerd";
		}
		else if(nickname != null && username == null && !"USER".equals(command)){
			return "451 ERR_NOTREGISTERED : You have not registerd";
		}
		else{
			switch (command) {
			case "NICK":
				ans = nickMessage(parameter);
				break;
	        case "USER":
	            ans = userMessage(parameter);
	            break;
	        case "QUIT": 
	            ans = quitMessage(parameter);
	            break;
	        case "JOIN":
	            ans = joinMessage(parameter);
	            break;
	        case "PART":
	            ans = partMessage(parameter);
	            break;
	        case "NAMES":
	            ans = namesMessage(parameter);
	            break;
	        case "LIST":
	            ans = listMessage(parameter);
	            break;
	        default:
	        	if(chan!=null){
	        		chan.print(nickname +" wrote: " +msg);
	        	}
			}
		}
		return ans;
	}
	
	public boolean isEnd(String msg)
	{
		Scanner s = new Scanner(msg);
		String ans = "";
		if (s.hasNext()){
			ans = s.next();
		}
		s.close();
		return "QUIT".equals(ans);
	}
	
	public String nickMessage(String parameter){
		String reply = null;
		if(parameter.equals("")){
			reply = "431 ERR_NONICKNAMEGIVEN : no nickname given";
		}
		else {
			synchronized(nicknames){
				if(nicknames.containsKey(parameter)){
					reply = "433 ERR_NICKNAMEINUSE : " + parameter + " nickname is already in use";
				}
				else{
					if(nickname!=null){
						nicknames.remove(nickname);
					}
					nickname = parameter;
					nicknames.put(parameter, this);
					reply = "401 RPL_NICKNAMEACCEPTED";
				}
			}
		}
		return reply;
	}
	
	public String userMessage(String parameter){
		String reply;
		if(username != null){
			reply = "462 ERR_ALREADYREGISTRED : You may not reregister";
					}
		else if(parameter.equals("")){
			reply = "461 ERR_NEEDMOREPARAMS USER : Not enough parameters";
		}
		else{
			username = parameter;
			reply = "402 RPL_USERACCEPTED";
		}
		return reply;
	}
	
	public String quitMessage(String parameter){
		if(chan!=null ){
			if(parameter.equals("")){
				chan.print(nickname +" has left the channel");
			}
			else{
				chan.print(nickname + " has quit the conversation: " + parameter);
			}
			chan.remove(nickname);
		}
		nicknames.remove(nickname);
		return null;
	}
	
	public String joinMessage(String parameter){
		String reply = null;
		if(chan == null || !parameter.equals(chan.getName())){
			if(parameter.equals("")|| parameter.charAt(0)!='#'){
				reply = "461 ERR NEEDMOREPARAMS JOIN : Not enough parameters. chanel = " + parameter;
			}
			else{ 
				synchronized(channels){
					if (chan != null){
						partMessage(chan.getName());
					}
					if (channels.containsKey(parameter)){
						(chan = channels.get(parameter)).add(nickname, this);
					}
					else{
						chan = new Channel(parameter,this);
						channels.put(parameter, chan);
					}
					cn.print("353 RPL_NAMREPLY " + chan.list());
					reply = "366 RPL_ENDOFNAMES "+chan.getName()+" :End of /NAMES list";
				}
			}
		}
		return reply;
	}
	
	public String partMessage(String parameter){
		String reply = null;
		if(parameter.equals("")){
			reply ="461 ERR_NEEDMOREPARAMS PART : Not enough parameters";
		}
		else if(parameter.equals(chan.getName())){
			if(chan != null){
				chan.remove(nickname);
				chan = null;
			}
			reply = "405 RPL_PARTSUCCESS";
		}
		else{
			reply = "403 ERR_NOSUCHCHANNEL "+parameter+" :No such channel";
		}
		return reply;
	}
	
	public String namesMessage(String parameter){
		String reply = null;
		if(channels.containsKey(parameter)){
			cn.print("353 RPL_NAMREPLY " + channels.get(parameter).list());
			reply ="366 RPL_ENDOFNAMES "+parameter+" :End of /NAMES list";
		}
		else if(channels.containsKey("")){
			Enumeration<Channel>  e;
			e = channels.elements();
			while(e.hasMoreElements()){
				cn.print("353 RPL_NAMREPLY " + e.nextElement().list());
			}
			reply ="366 RPL_ENDOFNAMES "+parameter+" :End of /NAMES list";
		}
		else{
			reply = "403 ERR_NOSUCHCHANNEL "+parameter+" :No such channel";
		}
		return reply;	
	}
	
	public String listMessage(String parameter){
		cn.print("321 RPL_LISTSTART");
		Enumeration<Channel>  e;
		e = channels.elements();
		while(e.hasMoreElements()){
			cn.print("322 RPL_LIST " + e.nextElement().getName());
		}
		return "323 RPL_LISTEND :End of /LIST";	
	}

}

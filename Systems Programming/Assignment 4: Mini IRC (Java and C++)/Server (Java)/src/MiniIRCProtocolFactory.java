public class MiniIRCProtocolFactory implements ServerProtocolFactory {

	public ServerProtocol create(){
		return new MiniIRCProtocol();
	}
}

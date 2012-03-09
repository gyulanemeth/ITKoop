package server.cooproject.itk.hu;

import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.factory.JWebSocketFactory;
import org.jwebsocket.kit.WebSocketServerEvent;
import org.jwebsocket.listener.WebSocketServerTokenEvent;
import org.jwebsocket.listener.WebSocketServerTokenListener;
import org.jwebsocket.server.TokenServer;
import org.jwebsocket.token.Token;


public class coopMessageListener implements WebSocketServerTokenListener{
	private TokenServer _tokenServer;
	
	/**
	 * Built in
	 * @return
	 */
	public TokenServer getTokenServer(){
		return _tokenServer;
	}
	
	public void init(){
		try{
			/* Start the factory */
			JWebSocketFactory.start();
			_tokenServer = (TokenServer) JWebSocketFactory.getServer("coop1");
			if(_tokenServer != null ){
				//System.out.println("Server is running!");
				_tokenServer.addListener(this);
			}else{
				//System.out.println("Server is not running!");
			}
			
		}catch(Exception e){
			System.out.println("Error:"+e.getMessage());
			
		}
		
		
	}
	
	@Override
	public void processClosed(WebSocketServerEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processOpened(WebSocketServerEvent arg0) {
		System.out.println("New client connected to our websocket server");
		
	}

	@Override
	public void processPacket(WebSocketServerEvent arg0, WebSocketPacket arg1) {
		System.out.println("Packet received: "+arg1.getString());
		
	}

	@Override
	public void processToken(WebSocketServerTokenEvent tEvent, Token receivedToken) {
		System.out.println("New token: "+receivedToken.getNS()+" -> "+receivedToken.getType());
		/* simple echo */
		  // create a response token
	      Token echoResponse = tEvent.createResponse(receivedToken);
	      echoResponse.setString("code", "-1");
	      echoResponse.setString("msg", "Ping - Pong");
	      tEvent.sendToken(echoResponse);
	      /* Simple broadcast */
	      Token brToken = tEvent.createResponse(receivedToken);
	      brToken.setString("code", "255");
	      brToken.setString("msg", "This is a broadcast message");
	      _tokenServer.broadcastToken(brToken);
	}
	
	
}

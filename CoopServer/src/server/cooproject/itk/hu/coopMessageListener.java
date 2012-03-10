package server.cooproject.itk.hu;

import org.apache.log4j.Logger;
import org.jwebsocket.api.PluginConfiguration;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.factory.JWebSocketFactory;
import org.jwebsocket.kit.PlugInResponse;
import org.jwebsocket.kit.WebSocketServerEvent;
import org.jwebsocket.listener.WebSocketServerTokenEvent;
import org.jwebsocket.listener.WebSocketServerTokenListener;
import org.jwebsocket.logging.Logging;
import org.jwebsocket.plugins.TokenPlugIn;
import org.jwebsocket.server.TokenServer;
import org.jwebsocket.token.Token;


public class coopMessageListener implements WebSocketServerTokenListener{

	private static Logger log = Logger.getLogger(coopMessageListener.class.getName());

	public coopMessageListener() {
		super();
		log.info("Coop Server listener successfully loaded");
	}

	@Override
	public void processClosed(WebSocketServerEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processOpened(WebSocketServerEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processPacket(WebSocketServerEvent arg0, WebSocketPacket arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processToken(WebSocketServerTokenEvent aEvent, Token aToken) {
		// Dolgozzuk fel a letezo fieldeket
		int cType = 0;
		if(aToken.getString("type") != null){
			cType = Integer.parseInt(aToken.getString("type"));
		}
		String cSenderName = aToken.getString("sender");
		String cMessage = aToken.getString("message");
		//DEBUG, amig nem refactoraljak klienseket
		if(cMessage == null){
			cMessage = aToken.getString("msg");
		}
		//Loggoljuk
		log.info("New token received from "+cSenderName+" and the message is "+cMessage);
		// dolgozzuk fel type alapjan
		switch(cType){
			case 2:
					break;
		
			//Nem jot kuldott, biztos elnezte. Hat adjuk a tudtara asszertiv kommunikacioval
			default: handleUnknowTypeField(aEvent,aToken,cType);
		}
		
	}
	
	
	/**
	 * Ismeretlen type field a jsonben valaszoljuk a feladonak.
	 * @param aEvent A websocketservertokeEvent
	 * @param aToken Maga a token
	 * @param cType A hibasnak/ismeretlennek itelt type mezo tartalma
	 */
private void handleUnknowTypeField(WebSocketServerTokenEvent aEvent, Token aToken, int cType){
	log.warn("Message with invalid type field "+cType);
	Token dResponse = aEvent.createResponse(aToken);
	//REMOVEME: transition phase miatt
	dResponse.setString("sender","CooProjectServer");
	dResponse.setString("msg", "Ne haragudj, de elrontottad a type("+cType+") mezo erteket! Es az msg fieldet sem kene feldolgoznod ...");
	dResponse.setString("message", "Ne haragudj, de elrontottad a type("+cType+") mezo erteket!");
	aEvent.sendToken(dResponse);
}
	
	
}

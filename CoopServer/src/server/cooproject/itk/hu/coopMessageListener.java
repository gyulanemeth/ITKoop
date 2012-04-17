package server.cooproject.itk.hu;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.factory.JWebSocketFactory;
import org.jwebsocket.kit.WebSocketServerEvent;
import org.jwebsocket.listener.WebSocketServerTokenEvent;
import org.jwebsocket.listener.WebSocketServerTokenListener;
import org.jwebsocket.server.TokenServer;
import org.jwebsocket.token.Token;
import org.jwebsocket.token.TokenFactory;

import com.mongodb.*;
import org.bson.types.*;

import shared.cooproject.itk.hu.messenger;

public class coopMessageListener implements WebSocketServerTokenListener {

	private static TokenServer _tServer = (TokenServer) JWebSocketFactory
			.getServer("ts0");
	private static Logger log = Logger.getLogger(coopMessageListener.class
			.getName());
	private messenger _messageHandler;// ez kezeli az uzeneteket
	private userManager _users;
	/**
	 * Default constructor Itt inicializaljuk a user map-et, valamint
	 * kapcsolodunk a mongodbhez
	 */
	public coopMessageListener() {
		super();
		log.info("Coop Server listener successfully loaded");
		_users = new userManager();
		this._messageHandler = new messenger(_tServer, "CoopProjectServer",
				"127.0.0.1", "coproject", "objects", this);
	}

	/**
	 * Beepitett fv, ha zarodik a kapcsolat akkor kuldunk broadcastot.
	 */
	public void processClosed(WebSocketServerEvent aEvent) {
		// Broadcastolj, ha lelep valaki
		log.info(aEvent.getSessionId().toString() + " left the server");
		_messageHandler.userLeft(_users.getUsername(aEvent.getConnector()));
		_users.userLeft(aEvent.getSessionId());
	}

	/**
	 * Ha megnyilik egy kapcsolat. Szamunkra kb useless, mivel ez meg handshake
	 * elott van, igy nem tudunk se usernevet, se semmit :(
	 */
	@Override
	public void processOpened(WebSocketServerEvent aEvent) {
		// ha valaki csatlakozik, küldjüki ki neki a welcome objecteket unicast
		// üzenetben
		this._messageHandler.sendAllObjects(aEvent.getConnector());

	}

	/**
	 * Packet feldolgozas
	 */
	@Override
	public void processPacket(WebSocketServerEvent aEvent, WebSocketPacket arg1) {
	}

	/**
	 * A packetbol kinyert token feldolgozasa
	 */
	@Override
	public void processToken(WebSocketServerTokenEvent aEvent, Token aToken) {
		// TODO: Username ellenorzes
		// updateUsername(aEvent, cSenderName);// updateljuk a sessionId - nev
		// parost
		this._messageHandler.processMessage(aEvent.getConnector(), aToken);
	}

	/**
	 * Adott connectorhoz tartozo user beloggolt-e mar?
	 * @param c A kuldo connector
	 * @return true ha beloggolt, false ha nem
	 */
	public boolean isLoggedIn(WebSocketConnector c, Token aToken) {
		//return this._users.isLoggedIn(c);
		//TODO:removeme
		//only for testing
		log.info("Isloggedin  "+this._users.getUsername(c)+":"+this._users.isLoggedIn(c,aToken));
		return true;
	}

	/**
	 * Adott connector tartozo login event kezelese
	 * @param c A
	 * @param username
	 */
	public void handleLogin(WebSocketConnector c, String username) {
		log.info("Login message from  "+this._users.getUsername(c));
		this._users.handleLogin(c,username);
		
	}

}

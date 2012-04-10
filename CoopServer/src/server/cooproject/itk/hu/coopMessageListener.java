package server.cooproject.itk.hu;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
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

public class coopMessageListener implements WebSocketServerTokenListener {

	private static TokenServer _tServer = (TokenServer) JWebSocketFactory
			.getServer("ts0");
	private static Logger log = Logger.getLogger(coopMessageListener.class
			.getName());
	private HashMap<String, String> _users;
	DB _mongo;// A mongodb

	/**
	 * Default constructor Itt inicializaljuk a user map-et, valamint
	 * kapcsolodunk a mongodbhez
	 */
	public coopMessageListener() {
		super();
		log.info("Coop Server listener successfully loaded");
		_users = new HashMap<String, String>();
		/* Kapcsolodjunk a mongodb-hez */
		try {
			Mongo m = new Mongo("127.0.0.1");
			_mongo = m.getDB("coproject");// Itt mar leteznie kell! use
											// coproject
			log.info("Successfully connected to MongoDB server!");
		} catch (Exception e) {
			log.warn("Error while connectiong to mongoDB! Please check the server!");
			// A Mongo konstruktor valójában akkor sem dob exceptiont, ha nem fut MongoDB a gépen,
			// csak akkor dob kivételt, ha ki akarunk nyerni adatot.
		}
	}

	/**
	 * Beepitett fv, ha zarodik a kapcsolat akkor kuldunk broadcastot.
	 */
	public void processClosed(WebSocketServerEvent aEvent) {
		// Broadcastolj, ha lelep valaki
		log.info(aEvent.getSessionId().toString() + " left the server");
		String username = _users.get(aEvent.getSessionId());
		_users.remove(aEvent.getSessionId());
		Token broadcastMessage = TokenFactory.createToken();
		LinkedList<Token> messageList = new LinkedList<Token>();
		Token dResponse = getMessageBone(1000);
		dResponse.setString("message", username + " left the server");// chat
		messageList.add(dResponse);
		broadcastMessage.setList("messages",messageList);
		_tServer.broadcastToken(broadcastMessage);																		// message
	}

	/**
	 * Ha megnyilik egy kapcsolat. Szamunkra kb useless, mivel ez meg handshake
	 * elott van, igy nem tudunk se usernevet, se semmit :(
	 */
	@Override
	public void processOpened(WebSocketServerEvent aEvent) {
		// Broadcastolj, ha belep valaki
		/*
		 * A szerzo kommentje: Mivel semmi authunk nincs, igy fogalmunk sincs
		 * hogy mi az uj user neve Tehat, a kovetkezo lehetosegeink vannak 1,
		 * Hagyjuk igy, hogy csak jelezzuk ha valaki joinol 2, Lesz egy join
		 * type, amire kesobb lehet szurni, es broadcastolni 3, Az csinaljuk,
		 * hogy ha ujkent kerul be hashmapbe akkor kuldunk rol
		 */

		// ha valaki csatlakozik, küldjüki ki neki a welcome objecteket unicast üzenetben
		userJoined(aEvent);
		// majd ha lesz rendesebb autentikáció meg ilyesmi, akkor majd átírjuk, de addig csak szebb így.
		// meg persze jó lenne egy arrayben átküldeni az objektumokat.
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
		//Mivel arrayben gyunnek majd a uzenetek...
		//Nyilvan a kod ezen resze nem tesztelt, mivel nincs mivel
		@SuppressWarnings("unchecked")
		List<Token> messageList = (aToken.getList("messages") == null? new LinkedList<Token>() : aToken.getList("messages")); 
		Iterator<Token> it = messageList.iterator();
		while(it.hasNext())
			processMessage(aEvent,it.next());
	}

	
	private void processMessage(WebSocketServerTokenEvent aEvent,Token aToken){
		// Dolgozzuk fel a letezo fieldeket
		int cType = 0;
		if (aToken.getString("type") != null) {
			cType = Integer.parseInt(aToken.getString("type"));
		}
		String cSenderName = aToken.getString("sender");
		String cMessage = aToken.getString("message");
		// Loggoljuk
		log.info("New token received from " + cSenderName
				+ " and the message is " + cMessage);
		updateUsername(aEvent, cSenderName);// updateljuk a sessionId - nev
											// parost
		boolean should_be_broadcasted = true;// mindent broadcastolunk, kiveve
												// amit nem :(
		// dolgozzuk fel type alapjan
		switch (cType) {
		// Objektum mozgatasa
		case 2:
			should_be_broadcasted = true;
			// Lehamozzuk a savePosrol a tobbit
			@SuppressWarnings("unchecked")
			Map<String, String> message = aToken.getMap("message");
			if (message.get("savePos").toString().equals("true")) {
				saveToken("objects", aToken);// Mentsuk aToken-t, az objects
												// collectionbe
			}
			break;
		case 3:
				Token createMessage = createObject(aToken);
				_tServer.broadcastToken(createMessage);
			break;
		// Egy chat message. Egyelore csak broadcastoljuk
		case 1000:
			should_be_broadcasted = true;
			break;

		// Nem jot kuldott, biztos elnezte. Hat adjuk a tudtara asszertiv
		// kommunikacioval
		default:
			handleUnknowTypeField(aEvent, aToken, cType);
			break;
		}
		// Ha broadcastolni kell
		if (should_be_broadcasted) {
			Token broadcastMessage = TokenFactory.createToken();
			LinkedList<Token> messageList = new LinkedList<Token>();
			messageList.add(aToken);
			broadcastMessage.setList("messages",messageList);
			_tServer.broadcastToken(broadcastMessage);
		}
		
	}
	
	/**
	 * Ismeretlen type field a jsonben valaszoljuk a feladonak.
	 * 
	 * @param aEvent
	 *            A websocketservertokeEvent
	 * @param aToken
	 *            Maga a token
	 * @param cType
	 *            A hibasnak/ismeretlennek itelt type mezo tartalma
	 */
	private void handleUnknowTypeField(WebSocketServerTokenEvent aEvent,
			Token aToken, int cType) {
		log.warn("Message with invalid type field " + cType);
		Token wrongMessageTypeMessage = TokenFactory.createToken();
		LinkedList<Token> messageList = new LinkedList<Token>();
		Token dResponse = aEvent.createResponse(aToken);
		dResponse.setString("sender", "CooProjectServer");
		dResponse.setString("message", "Ne haragudj, de elrontottad a type("
				+ cType + ") mezo erteket!");
		messageList.add(dResponse);
		wrongMessageTypeMessage.setList("messages",messageList);
		aEvent.sendToken(wrongMessageTypeMessage);
	}

	/**
	 * Arra szolgal, hogy updatelje a usernevet ha valtozik, vagy hozzaadja a
	 * maphez ha meg nincs benne
	 * 
	 * @param aEvent
	 *            Az event object
	 * @param username
	 *            A jsonbol kinyert username
	 */
	private void updateUsername(WebSocketServerTokenEvent aEvent,
			String username) {
		// REMOVEME: csak ameddig kliens nem kul usernevet
		if (username == null)
			username = aEvent.getSessionId();
		// Ha nincs benne, rakjuk bele es broadcast
		if (!_users.containsKey(aEvent.getSessionId())) {
			_users.put(aEvent.getSessionId(), username);
			log.info("New record in _users map : " + aEvent.getSessionId()
					+ " - " + username);
			Token usernameUpdatedMessage = TokenFactory.createToken();
			LinkedList<Token> messageList = new LinkedList<Token>();
			Token dResponse = getMessageBone(1000);
			dResponse.setString("type","1000");//chat message
			dResponse.setString("sender", "CooProjectServer");
			dResponse.setString("message", username + " joined");// chat message
			messageList.add(dResponse);
			usernameUpdatedMessage.setList("messages",messageList);
			_tServer.broadcastToken(usernameUpdatedMessage);
			sendUserList(aEvent);//Amig nem regisztral be, addig nincs ertelme user listet kuldeni.
		} else {
			if (_users.get(aEvent.getSessionId()) != username) {
				log.info("_users map updated with " + aEvent.getSessionId()
						+ " - " + username);
				_users.put(aEvent.getSessionId(), username);
			}
		}

	}

	/**
	 * Elmenti az aTokenben talalhato informaciokat az adatbazisba
	 * 
	 * @param _collection
	 *            a collection neve, ahova menteni akarunk
	 * @param aToken
	 *            a kapott token
	 * @return objectID
	 */
	private String saveToken(String _collection, Token aToken) {
		DBCollection _c = _mongo.getCollection(_collection);//
		// Hozzuk letre a db objektumot
		@SuppressWarnings("unchecked")
		Map<String, String> message = (aToken.getMap("message") == null? new HashMap<String,String>() : aToken.getMap("message"));
		log.info("Saving token message:" + message.toString());
		// Kesobb, ha letisztul az uzenetkuldes, hasznalhatjuk a JSON.parse
		// parancsot is, es akkor nem kell ennyit bohockodni :(
		// Nyerjuk ki a nekunk kello infokat
		String objId = message.get("objId").toString();

		BasicDBObject d = new BasicDBObject();

		d.put("x", (message.get("x") == null? "0" : message.get("x").toString()));
		d.put("y", (message.get("y") == null? "0" : message.get("y").toString()));
		d.put("z", (message.get("z") == null? "0" : message.get("z").toString()));
		// data
		if(message.get("data")!=null) {
			d.put("data", message.get("data").toString());
		}

		// Nem biztos, hogy mongo dob exception, de azert hatha
		try {
			//létezik-e már az adott id-jű dokumentum?
			DBObject doc=_c.findOne(new BasicDBObject("_id", new ObjectId(objId)));
			if(doc != null) {
				_c.update(doc, new BasicDBObject().append("$set", d));
				log.info("Old object successfully updated: "+d.toString());
				return ((ObjectId)doc.get( "_id" )).toString();
			} else {
				_c.insert(d);
				log.info("New object successfully inserted: " + d.toString());
				return ((ObjectId)d.get( "_id" )).toString();
			}
		} catch (Exception e) {
			log.warn("Error while saving aToken: " + d.toString());
			return null;
		}
	}
	/**
	 * Letrehozzuk az objectumot, es legyartjuk a valaszuzenetet
	 * @param aToken a letrehozando objectet tartalmazo message
	 * @return a valasz Token
	 */

	private Token createObject(Token aToken){
		Token response = getMessageBone(2);
		String createdAt = aToken.getString("timestamp");
		@SuppressWarnings("unchecked")
		Map<String, String> message = aToken.getMap("message");
		message.put("createdAt",createdAt);
		message.put("savePos","false");
		String objId = saveToken("objects",aToken);
		message.put("objId",objId);
		response.setMap("message",message);
		Token createMessage = TokenFactory.createToken();
		LinkedList<Token> messageList = new LinkedList<Token>();
		messageList.add(response);
		createMessage.setList("message",messageList);
		return createMessage;
		
	}
	/**
	 * Kikuldi a helloobjecteket!(az utolso 3 elmentett uzenetet a dbbol)
	 * 
	 * @param aEvent
	 *            A csatlakozasi event
	 * @param _collection
	 *            a collection neve, ahonnan ki akarjuk kuldeni
	 */
	private void sendHelloObjects(WebSocketServerEvent aEvent,
			String _collection) {
		// Szerezzuk meg a collectiont
		DBCollection _c = _mongo.getCollection(_collection);
		//legyen inkább az összes:
		DBCursor cur = _c.find().sort(new BasicDBObject("_id", -1));
		// gyartsunk responsokat, es kuldjuk ki!
		log.info("Sending welcome packets");
		Token helloObjects = TokenFactory.createToken();
		LinkedList<Token> objectList = new LinkedList<Token>();
		while (cur.hasNext()) {
			Token dResponse = getTokenFromMongoDBObject(cur.next());
			//log.info(dResponse.toString());
			objectList.add(dResponse);
		}
		helloObjects.setList("messages",objectList);
		_tServer.sendToken(aEvent.getConnector(), helloObjects);
	}

	/**
	 * Mongodb objectbol Token-t csinalunk. Ez is deprecated lesz, amint
	 * letisztul a json forgalom, es hasznalhatjuk a beepitett konvertert!
	 * 
	 * @param o
	 *            Mongodb object
	 * @return Token amiben a message van
	 */
	private Token getTokenFromMongoDBObject(DBObject o) {
		Token r = getMessageBone(2);
		/*
		 * Vegulis egyszerusitettem db schemat, mert rohadt szarul van
		 * implementalva javaban mongodb driver igy ahhoz hogy kiszedjek egy
		 * array elemet, ki kell szednem az arrayt mint list, vegigiteralni
		 * rajta, azokbol kiszedni egy basicBsonobjectet, majd azokban nezni
		 * hogy mi a kulcs, es az ertek
		 */
		// log.info("Mongodb row:"+o.toString());
		Map<String, String> message = new HashMap<String, String>();
		message.put("objId", o.get("_id").toString());	//a doksi szerint objId-nek hívjuk az üzenetben, de a DBben az objectid-t kezeljük.
		if (o.get("x") != null)
			message.put("x", o.get("x").toString());
		if (o.get("y") != null)
			message.put("y", o.get("y").toString());
		if (o.get("z") != null)
			message.put("z", o.get("z").toString());
		if (o.get("data") != null)
			message.put("data", o.get("data").toString());
		r.setMap("message", message);
		return r;

	}
	
	/**
	 * Join event
	 * @param aEvent A csatlakozo user TokenEventje
	 */
	private void userJoined(WebSocketServerEvent aEvent){
		sendHelloObjects(aEvent, "objects");	
	}
	
	/**
	 * Kikuldjuk az aktualis user listet
	 * @param aEvent a csatlakozo event
	 */
	@SuppressWarnings("unchecked")
	private void sendUserList(WebSocketServerEvent aEvent){
		log.info("Sending out user list");
		Token usersMessage = TokenFactory.createToken();
		Iterator<?> it = _users.entrySet().iterator();
		LinkedList<Token> userList = new LinkedList<Token>();
		while (it.hasNext()) {
			Map.Entry<String,String> pairs = (Entry<String, String>)it.next();
			HashMap<String,String> u = new HashMap<String,String>();
			Token userMessage = getMessageBone(1001);
			u.put("user",pairs.getValue());
			userMessage.setMap("message",u);
			userList.add(userMessage);
	    }
		System.out.println(userList.toString());
		usersMessage.setList("messages",userList);//mert emptystring nevu array kicsit odabaszna....
		_tServer.sendToken(aEvent.getConnector(), usersMessage);
	}
	/**
	 * 
	 *Megcsinalja a szofisztikalt uzenetunk vazat
	 * @param type az uzenet tipusa
	 * @return TOKEN a default json uzenetformatum alapjan
	 */
	private Token getMessageBone(int type){
		// A recept
		// Fogj 1 message-t
		Token simpleMessage = TokenFactory.createToken();
		// Rakj bele typeot
		simpleMessage.setInteger("type",type);
		// Sendert
		simpleMessage.setString("sender", "CooProjectServer");
		// Timestampet
		double timestamp = System.currentTimeMillis()/1000;
		simpleMessage.setDouble("timestamp",timestamp);
		return simpleMessage;
		
	}
	

}

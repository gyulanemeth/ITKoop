package chatdesktop;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import org.jwebsocket.api.WebSocketClientEvent;
import org.jwebsocket.api.WebSocketClientTokenListener;
import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.client.token.BaseTokenClient;
import org.jwebsocket.kit.WebSocketException;
import org.jwebsocket.token.Token;
import org.jwebsocket.token.TokenFactory;
/**
 * kliens szerver kapcsolat kezelese, krealja, kuldi az uzeneteket, fogadja, kezeli azokat
 * @author Sallai Gergely
 */
public class JWSClient implements WebSocketClientTokenListener{
    
    private static JWSClient _instance = null;
    private ChatDesktop form;
    private BaseTokenClient tClient;
    private Canvas canvas;
    private String userName;
    
    private JWSClient() {
        tClient = new BaseTokenClient();
        tClient.addTokenClientListener(this);
    }
    public static synchronized JWSClient getInstance() {
        if (_instance == null) {
            _instance = new JWSClient();
           }
        return _instance;
    }
    /**
     * lehet favagas, de oda kell vhogy adni neki a canvast, aminek kuldi az uzeneteket
     * @param canvas ezen van minden grafikus cumo
     */
    public void setCanvas(Canvas canvas){
        this.canvas=canvas;
    }
    /**
     * Elozohoz hasonloan a desktopot is oda adjuk neki
     * @param form 
     */
    void setDesktop(ChatDesktop form) {
        this.form=form;
    }
    
    /**
     * Kivulrol hivhato, connectet es autentikaciot kezdemenyez
     * @param name bejelentkezési név, el is tároljuk
     * @param pass jelszo...
     * @param secure secure WebSocketet hasznaljunk vagy sem
     * @return true ha minden ok false ha nem
     */
    public boolean login(String name, String pass, boolean secure) {
        this.userName = name;
        try {
            System.out.println("Connecting");
            if(secure)
                tClient.open("wss://nemgy.itk.ppke.hu:61155");
            else
                tClient.open("ws://nemgy.itk.ppke.hu:61160");
            System.out.println("Open");
            
            sendLogin(pass);
            return true;
        } catch (WebSocketException ex) {
            Logger.getLogger(JWSClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    /**
     * Disconnectel a szerverrol, lezarja a kapcsolatot, semmi tobb.
     * @return true ha nincs hiba, false ha van
     */
    public boolean disconnect(){
        try {
            tClient.disconnect();
            tClient.close();
        } catch (WebSocketException ex) {
            Logger.getLogger(JWSClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
    /**
     * Ez a belso method rendezi a konkret autentikaciot
     * 0 type-u message a login request
     * @param pass jelszooo
     * @return true ha nincs hiba, false ha van
     */
    private boolean sendLogin(String pass)
    {
        try {
            Token token = getMessageBone(0);
            
            tClient.sendToken(token);
            System.out.println("Logging in...");
            return true;
        } catch (WebSocketException ex) {
            Logger.getLogger(JWSClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    /**
     * Egyszeru chat uzenet kuldese, type 1000-el.
     * @param userName sajat nevunk, amivel bejelentkeztunk
     * @param text a szoveg amit kuldeni szeretnenk.
     * @return true ha minden ok, false ha para van.
     */
    public boolean sendText(String userName, String text) {
        try {
            Token token = getMessageBone(1000);
            token.setString("message", text);
            
            System.out.println("OUT Text: "+token.toString());
            tClient.sendToken(token);
            handleChatMessage(userName, text);
            return true;
        } catch (WebSocketException ex) {
            Logger.getLogger(JWSClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    /**
     * Mozgatasi esemeny kuldese a szervernek (type 2 vagy 4)
     * @param userName kuldo neve (mi nevunk)
     * @param objId az objektum azonositoja
     * @param x az objektum uj X koordinataja
     * @param y az objektum uj Y koordinataja
     * @param savepos uzenet a szervernek h eltarolja a DB-ben a valtozast
     *  vagy meg ne, mert nem fejeztuk be a mozgatast
     * @return ha minden rendben akkor true, ha hiba van akkor false
     */
    public boolean sendMoveObject(String userName, String objId, Integer x, Integer y, Boolean savepos){
        try {
            //Map<String, String> message = new HashMap<>();
            Map message = new HashMap();
            message.put("userName", userName);
            message.put("objId", objId);
            message.put("x", x.toString());
            message.put("y", y.toString());
            message.put("savePos", savepos.toString());
            
            Token token;
            if(savepos)
                token = getMessageBone(2);
            else
                token = getMessageBone(4);
            token.setMap("message", message);
            
            System.out.println("OUT Move: "+token.toString());
            tClient.sendToken(token);
            return true;
        } catch (WebSocketException ex) {
            Logger.getLogger(JWSClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    /**
     * Uj objektum letrehozasanak kerese a szervertol (type 3)
     * @param exampleString Az uj object szovege
     * @param x koordinata
     * @param y koordinata
     * @param z koordinata
     * @return true ha ok, false ha hiba
     */
    public boolean sendCreateObject(String exampleString, Integer x,Integer y,Integer z){
        try {
            Token token = getMessageBone(3);
            Map<String, String> message = new HashMap<>();
            message.put("data", exampleString);
            message.put("x", x.toString());
            message.put("y", y.toString());
            message.put("z", z.toString());            
            token.setMap("message", message);
            
            System.out.println("OUT Create: "+token.toString());
            tClient.sendToken(token);
            return true;
        } catch (WebSocketException ex) {
            Logger.getLogger(JWSClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    /**
     * Objektum torles kerese. type 5
     * @param objectId Objektum azonositoja
     * @return true ha minden ok, false ha hiba van
     */
    public boolean sendDeleteObject(String objectId){
        try {
            Token token = getMessageBone(5);
            
            Map message = new HashMap();
            message.put("objId", objectId);
            token.setMap("message", message);
            
            System.out.println("OUT Delete: "+token.toString());
            tClient.sendToken(token);
            return true;
        } catch (WebSocketException ex) {
            Logger.getLogger(JWSClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    /**
     * Objektum modositas kuldese. (type 1)
     * @param objId
     * @param exampleString
     * @return true ha ok, false ha hiba
     */
    public boolean sendModifyObject(String objId, String exampleString){
        try {
            Token token = getMessageBone(1);
            Map<String, String> message = new HashMap<>();
            message.put("objId", objId);
            message.put("data", exampleString);
            message.put("exampleString", exampleString);
            token.setMap("message", message);
            
            tClient.sendToken(token);
            System.out.println("OUT Modify: "+token.toString());
            return true;
        } catch (WebSocketException ex) {
            Logger.getLogger(JWSClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    /**
     * Bejovo token/csomag szetvalogatasa.
     * A megfelelo helyre kuldi feldolgozasra.
     * @param wsce WebSocket event
     * @param token maga a bejovo csomag
     */
    @Override
    public void processToken(WebSocketClientEvent wsce, Token token) {
        System.out.println("IN: "+token.toString());
        if(token.getString("type") != null &&
            token.getString("type").equals("welcome")) return; //csak a default welcome jott
        if(token.getString("type") != null &&
            token.getString("type").equals("event")) return; //csak a vmi nemtom milyen event jott

        // Dolgozzuk fel a letezo fieldeket
        try{
            // uzenet tipusat kiszedjuk
            int cType = 0;
            // favagas.. biztosa megyunk, vagy stringkent vagy intkent benne lesz
            if (token.getString("type") != null) {
                cType = Integer.parseInt(token.getString("type"));
            }
            if (token.getInteger("type") != null) {
                cType = token.getInteger("type");
            }
            String cSenderName = token.getString("sender");
            String cMessage = token.getString("message");
            // dolgozzuk fel type alapjan
            switch (cType) {
                // loginEvent
                case 0:
                    handleLogin(token);
                    break;
                // ModidifyObject (NINCS WIKIN, csak emailben)
                case 1:
                    System.out.println("It's a modify");
                    handleModify(token);
                    break;
                // MoveObject + mentes
                case 2:
                    System.out.println("It's a move");
                    handleMove(token);
                    break;
                // Create Object.
                case 3:
                    System.out.println("It's a create");
                    handleCreate(token);
                    break;
                // MoveObject mentes nelkul.
                case 4:
                    System.out.println("It's a move");
                    handleMove(token);
                    break;
                // Delete Object
                case 5:
                    System.out.println("It's a delete");
                    handleDelete(token);
                    break;
                // Egy chat message.
                case 1000:
                    System.out.println("It's a chat");
                    handleChatMessage(cSenderName, cMessage);
                    break;
            }
        }catch(Exception e){
            Logger.getLogger(JWSClient.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Default event, nem hasznalom...
     * @param wsce 
     */
    @Override
    public void processOpening(WebSocketClientEvent wsce) {
        //throw new UnsupportedOperationException("Not supported yet.");
        System.out.println("processOpening");
    }
    
    /**
     * Default event, nem hasznalom...
     * @param wsce 
     */
    @Override
    public void processOpened(WebSocketClientEvent wsce) {
        //throw new UnsupportedOperationException("Not supported yet.");
        System.out.println("processOpened");
    }
    
    /**
     * Default event, nem hasznalom...
     * @param wsce
     * @param wsp 
     */
    @Override
    public void processPacket(WebSocketClientEvent wsce, WebSocketPacket wsp) {      
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * A kapcsolat lezarulasakor hivodik.
     * Nem igazan hasznalom, csak jelzem h volt ilyen is.
     * @param wsce 
     */
    @Override
    public void processClosed(WebSocketClientEvent wsce) {
        System.out.println("Kiléptünk!");
    }
    
    /**
     * Default event, nem hasznalom...
     * @param wsce 
     */
    @Override
    public void processReconnecting(WebSocketClientEvent wsce) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    //-----------------------------------------------------------------
    /// HANDLERS
    /// Uzenet kezelo metodusok, belso hasznalatra.
    /// Eredetileg kulon osztalyban volt, de ide kerult a sok kavarodas
    /// es fejetlenseg miatt, es itt maradt.. :(
    
    /**
     * Chat uzenetek kezelese.
     * Szolunk az ablaknak h van uj uzenet.
     * @param sender
     * @param message 
     */
    private void handleChatMessage(final String sender,final String message) {
        form.newMessage(sender, message);{         
            Platform.runLater(new Runnable() { 
                @Override
                public void run() {
                     form.addMember(sender);
                }
            }); 

        }
    }
        
    
    /**
     * Bejovo mozgatasi esemeny kezelese.
     * Kiszedjuk a token tartalmat, megnezzuk hogy van-e mar ilyen azonositoju
     * objektum nalunk, ha van akkor a megadott helyre mozgatjuk,
     * ha nincs akkor a szerver szolt h kene egy uj objektum itt.
     * Ezert letrehozzuk.
     * @param token esemenyt tartalmazo csomag..
     */
    private void handleMove(Token token) {
        Map message=token.getMap("message");
        final String objId = message.get("objId").toString();
        final String data=message.get("data")==null? " ":message.get("data").toString();
        final int x=Integer.parseInt(message.get("x") == null? "0" : message.get("x").toString());
        final int y=Integer.parseInt(message.get("y") == null? "0" : message.get("y").toString());
        final int z=Integer.parseInt(message.get("z") == null? "0" : message.get("z").toString());
        if(canvas.containsObject(objId)){
            Node rec = canvas.getObject(objId);
            rec.move(x, y);
        }else{
            Platform.runLater(new Runnable() { 
                @Override
                public void run() {
                    canvas.initRectangleNode(_instance, objId, x, y, z, data);
                }
            });
        }
    }
    
    /**
     * Bejovo modositas kerelem kezelese.
     * Ha van ilyen objektumunk, akkor modositjuk a tartalmat,
     * ha nincs akkor eldobjuk az uzenetet.
     * @param token esemenyt tartalmazo csomag..
     */
    private void handleModify(Token token) {
        Map message = token.getMap("message");
        String objId = message.get("objId").toString();
        String data = message.get("data")==null? " ":message.get("data").toString();
        //z=Integer.parseInt(message.get("z") == null? "0" : message.get("z").toString());
        if(canvas.containsObject(objId)){
            Node rec = canvas.getObject(objId);
            rec.setText(data);
        }
    }
    
    /**
     * Bejovo torles kerelem kezelese.
     * Ha van ilyen objektum, akkor toroljuk,
     * ha nincs akkor eldobjuk a csomagot.
     * @param token esemenyt tartalmazo csomag
     */
    private void handleDelete(Token token) {
        Map message = token.getMap("message");
        final String objId = message.get("objId").toString();
        if(canvas.containsObject(objId)){
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Node deleted=canvas.getObject(objId);            
                    canvas.remove(deleted);
                }
            });
            
        }
    }
    
    /**
     * Bejovo objektum letrehozasi kerelem.
     * Ez a mostani verzioban nem hasznalt, ilyen uzenetet nem kuld a szerver.
     * @param token 
     */
    private void handleCreate(Token token) {
        Map message = token.getMap("message");
        final String objId = message.get("objId").toString();
        final String data = message.get("data")==null? " ":message.get("data").toString();
        final int x=Integer.parseInt(message.get("x")==null? "0": message.get("x").toString());
        final int y=Integer.parseInt(message.get("y") == null? "0" : message.get("y").toString());
        final int z=Integer.parseInt(message.get("z") == null? "0" : message.get("z").toString());
        //double timestamp = Double.parseDouble(message.get("timestamp").toString());
        if(canvas.containsObject(objId)){
            Node rec = canvas.getObject(objId);
            rec.move(x, y);
            rec.setText(data);
        }else{
            Platform.runLater(new Runnable() { 
            @Override
            public void run() {
                canvas.initRectangleNode(_instance, objId, x, y, z, data);
                }
            }); 
        }
    }
    
    /**
     * Login uzenet feldolgozasa.
     * Ebben a verzioban mar nem jon felenk ilyen uzenet,
     * eldobjuk a csomagot.
     * @param aToken 
     */
    private void handleLogin(Token aToken) {
		String username = aToken.getString("sender");
    }
    
    /**
     * Uzenet vaz gyarto metodus.
     * Adott type-hoz kreal egy uzenet vazat, amibe lehet pakolni majd
     * @param type Ilyen tipusu lesz az uzenet.
     * @return A kesz uzenet vaza.
     */
    private Token getMessageBone(int type) {
	// A recept
        // Fogj 1 message-t
        Token simpleMessage = TokenFactory.createToken();
        // Rakj bele typeot
        simpleMessage.setInteger("type", type);
        simpleMessage.setString("type", Integer.toString(type));
        // Sendert
        simpleMessage.setString("sender", this.userName);
        // Timestampet
        double timestamp = System.currentTimeMillis() / 1000;
        simpleMessage.setDouble("timestamp", timestamp);
        simpleMessage.setString("timestamp", Double.toString(timestamp));
        return simpleMessage;
    }

}
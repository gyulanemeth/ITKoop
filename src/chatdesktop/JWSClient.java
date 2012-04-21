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
 *
 * @author Sallai Gergely
 */
public class JWSClient implements WebSocketClientTokenListener{
    
    private static JWSClient _instance = null;
    private ChatDesktop chatDesktop;
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
    public void setCanvas(Canvas canvas){
        this.canvas=canvas;
    }
    
    public boolean login(String name, String pass, boolean secure) {
        this.userName = name;
        try {
            System.out.println("Connecting");
            if(secure)
                tClient.open("wss://nemgy.itk.ppke.hu:61155");
            else
                tClient.open("ws://nemgy.itk.ppke.hu:61160");
            System.out.println("Open");
            //System.out.println(tClient.getClientId().toString());
            //Itt van egy kis hiba
            //tClient.login(name, pass);
            return true;
        } catch (WebSocketException ex) {
            Logger.getLogger(JWSClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
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
    
    public boolean sendMoveObject(String userName, String objId, Integer x, Integer y, Boolean savepos){
        try {
            Map<String, String> message = new HashMap<>();
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
    
    public boolean sendDeleteObject(String objectId){
        /*try {
            // TODO milyen TYPE?
            Map<String, String> message = new HashMap<>();
            message.put("objId", objectId);
            
            Token token = getMessageBone(?)
            token.setMap("message", message);
            
            System.out.println("OUT Delete: "+token.toString());
            tClient.sendToken(token);
            return true;
        } catch (WebSocketException ex) {
            Logger.getLogger(JWSClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }*/
        return false;
    }
    
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
    
    @Override
    public void processToken(WebSocketClientEvent wsce, Token token) {
        System.out.println("IN: "+token.toString());
        if(token.getString("type").equals("welcome")) return; //csak a default welcome jott
        try{
            // Dolgozzuk fel a letezo fieldeket
            int cType = 0;
            if (token.getString("type") != null) {
                //cType = Integer.parseInt(token.getString("type"));
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
                    handleModify(token);
                    break;
                // MoveObject + mentes
                case 2:
                    handleMove(token);
                    break;
                // Create Object!
                case 3:
                    handleCreate(token);
                    break;
                // MoveObject mentes nelkul!
                case 4:
                    handleMove(token);
                    break;
                // Egy chat message. Egyelore csak broadcastoljuk
                case 1000:
                    handleChatMessage(cSenderName, cMessage);
                    break;
            }
        }catch(Exception e){
            Logger.getLogger(JWSClient.class.getName()).log(Level.SEVERE, null, e);
        }
        /*System.out.println(token.toString());
        String type =token.getString("type");
        Map message;
        final String objId;
        final String data;
        final int x,y,z;
        String userName;
        try{
            if(type.equals("welcome")) return;
            String sender=token.getString("sender") == null? "unkown host" : token.getString("sender").toString();
            switch(Integer.parseInt(type)){ 
                case 1000://CHAT
                    message = token.getMap("message");
                    userName = message.get("userName") == null? "*noname*" : message.get("userName").toString();
                    String textMessage = message.get("msg") == null? "_-_" : message.get("msg").toString();
                    chatDesktop.newMessage(userName, textMessage);
                    /*OLD
                    String textMessage=token.getString("message") == null? " " : token.getString("message").toString();
                    chatDesktop.chat.addText(sender, textMessage);*/
                    /*break;
                case 2://MOVE
                    message=token.getMap("message");
                    objId = message.get("objId").toString();
                    data=message.get("data")==null? " ":message.get("data").toString();
                    x=Integer.parseInt(message.get("x") == null? "0" : message.get("x").toString());
                    y=Integer.parseInt(message.get("y") == null? "0" : message.get("y").toString());
                    z=Integer.parseInt(message.get("z") == null? "0" : message.get("z").toString());
                    
                    if(canvas.containsObject(objId)){
                        GraphRectangle rec = canvas.getObject(objId);
                        rec.setX(x);
                        rec.setY(y);
                    }else{
                        Platform.runLater(new Runnable() { 
                            @Override
                            public void run() {
                                canvas.initRectangleNode(_instance, objId, x, y, z, data);
                            }
                        });
                    }
                    break;
                case 3://CREATE
                    message = token.getMap("message");
                    objId = message.get("objId").toString();
                    data = message.get("data")==null? " ":message.get("data").toString();
                    x=Integer.parseInt(message.get("x")==null? "0": message.get("x").toString());
                    y=Integer.parseInt(message.get("y") == null? "0" : message.get("y").toString());
                    z=Integer.parseInt(message.get("z") == null? "0" : message.get("z").toString());
                    if(canvas.containsObject(objId)){
                        GraphRectangle rec = canvas.getObject(objId);
                        rec.setX(x);
                        rec.setY(y);
                        rec.text.setText(data);
                    }else{
                        Platform.runLater(new Runnable() { 
                            @Override
                            public void run() {
                                canvas.initRectangleNode(_instance, objId, x, y, z, data);
                            }
                        }); 
                    }
                    break;
                case 4://DELETE
                    message = token.getMap("message");
                    objId = message.get("objId").toString();
                    if(canvas.containsObject(objId)){
                        Platform.runLater(new Runnable() { 
                            @Override
                            public void run() {
                                canvas.deleteObject(objId);
                            }
                        }); 
                    }
                    break;
                case 5://MODIFY
                    message = token.getMap("message");
                    objId = message.get("objId").toString();
                    data = message.get("data")==null? " ":message.get("data").toString();
                    //z=Integer.parseInt(message.get("z") == null? "0" : message.get("z").toString());
                    if(canvas.containsObject(objId)){
                        GraphRectangle rec = canvas.getObject(objId);
                        rec.text.setText(data);
                    }
                    break;
                default:
                    System.out.println(token.toString());
                    break;
            }
        }catch(Exception e){
            Logger.getLogger(JWSClient.class.getName()).log(Level.SEVERE, null, e);
        }*/
        
    }

    @Override
    public void processOpening(WebSocketClientEvent wsce) {
        //throw new UnsupportedOperationException("Not supported yet.");
        System.out.println("processOpening");
    }

    @Override
    public void processOpened(WebSocketClientEvent wsce) {
        //throw new UnsupportedOperationException("Not supported yet.");
        System.out.println("processOpened");
    }

    @Override
    public void processPacket(WebSocketClientEvent wsce, WebSocketPacket wsp) {
        
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void processClosed(WebSocketClientEvent wsce) {
        //throw new UnsupportedOperationException("Not supported yet.");
        System.out.println("Kiléptünk!");
    }

    @Override
    public void processReconnecting(WebSocketClientEvent wsce) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    void setDesktop(ChatDesktop chatdesktop) {
        this.chatDesktop=chatdesktop;
    }
    
    
    //-----------------------------------------------------------------
    /// HANDLES
    
    private void handleChatMessage(String sender, String message) {
        this.chatDesktop.newMessage(sender, message);
    }
    
    private void handleMove(Token token) {
        Map message=token.getMap("message");
        final String objId = message.get("objId").toString();
        final String data=message.get("data")==null? " ":message.get("data").toString();
        final int x=Integer.parseInt(message.get("x") == null? "0" : message.get("x").toString());
        final int y=Integer.parseInt(message.get("y") == null? "0" : message.get("y").toString());
        final int z=Integer.parseInt(message.get("z") == null? "0" : message.get("z").toString());
                    
        if(canvas.containsObject(objId)){
            GraphRectangle rec = canvas.getObject(objId);
            rec.setX(x);
            rec.setY(y);
        }else{
            Platform.runLater(new Runnable() { 
                @Override
                public void run() {
                    canvas.initRectangleNode(_instance, objId, x, y, z, data);
                }
            });
        }
    }
    
    private void handleModify(Token token) {
        Map message = token.getMap("message");
        String objId = message.get("objId").toString();
        String data = message.get("data")==null? " ":message.get("data").toString();
        //z=Integer.parseInt(message.get("z") == null? "0" : message.get("z").toString());
        if(canvas.containsObject(objId)){
            GraphRectangle rec = canvas.getObject(objId);
            rec.text.setText(data);
        }
    }
    
    private void handleCreate(Token token) {
        Map message = token.getMap("message");
        final String objId = message.get("objId").toString();
        final String data = message.get("data")==null? " ":message.get("data").toString();
        final int x=Integer.parseInt(message.get("x")==null? "0": message.get("x").toString());
        final int y=Integer.parseInt(message.get("y") == null? "0" : message.get("y").toString());
        final int z=Integer.parseInt(message.get("z") == null? "0" : message.get("z").toString());
        //double timestamp = Double.parseDouble(message.get("timestamp").toString());
        if(canvas.containsObject(objId)){
            GraphRectangle rec = canvas.getObject(objId);
            rec.setX(x);
            rec.setY(y);
            rec.text.setText(data);
        }else{
            Platform.runLater(new Runnable() { 
            @Override
            public void run() {
                canvas.initRectangleNode(_instance, objId, x, y, z, data);
                }
            }); 
        }
    }
    
    private void handleLogin(Token aToken) {
		String username = aToken.getString("sender");
                // TODO
    }
    
    private Token getMessageBone(int type) {
	// A recept
        // Fogj 1 message-t
        Token simpleMessage = TokenFactory.createToken();
        // Rakj bele typeot
        simpleMessage.setInteger("type", type);
        // Sendert
        simpleMessage.setString("sender", this.userName);
        // Timestampet
        double timestamp = System.currentTimeMillis() / 1000;
        simpleMessage.setDouble("timestamp", timestamp);
        return simpleMessage;
    }
}

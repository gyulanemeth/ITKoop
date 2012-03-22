package chatdesktop;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.paint.Color;
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
    
    public boolean login(String name, String pass, boolean secure) {
        try {
            System.out.println("Connecting");
            if(secure)
                tClient.open("wss://nemgy.itk.ppke.hu:61155");
            else
                tClient.open("ws://nemgy.itk.ppke.hu:61150");
            System.out.println("Open");
            //Itt van egy kis hiba
            //tClient.login(name, pass);
            return true;
        } catch (WebSocketException ex) {
            Logger.getLogger(JWSClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean sendText(String userName, String text) {
        try {
            Token message = TokenFactory.createToken();
            message.setString("userName", userName);
            message.setString("msg", text);
            message.setString("timeStamp", "time");
            
            Token token = TokenFactory.createToken();
            //token.setInteger("type", 1000);
            token.setString("type", "1000");
            token.setString("sender", tClient.getClientId());
            token.setToken("message", message);
            
            tClient.sendToken(token);
            return true;
        } catch (WebSocketException ex) {
            Logger.getLogger(JWSClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean sendMoveObject(String userName, String objId, int x, int y, boolean savepos){
        try {
            Token message = TokenFactory.createToken();
            message.setString("userName", userName);
            message.setString("objId", objId);
            message.setInteger("x", x);
            message.setInteger("y", y);
            message.setBoolean("savePos", savepos);
            message.setString("timeStamp", "time");
            
            Token token = TokenFactory.createToken();
            token.setInteger("type", 2);
            token.setString("sender", tClient.getClientId());
            token.setToken("message", message);
            
            tClient.sendToken(token);
            return true;
        } catch (WebSocketException ex) {
            Logger.getLogger(JWSClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean sendCreateObject(String exampleString, int x,int y,int z){
        try {
            Token message = TokenFactory.createToken();
            message.setString("exampleString", exampleString);
            message.setInteger("x", x);
            message.setInteger("y", y);
            message.setInteger("z", z);
            
            Token token = TokenFactory.createToken();
            token.setInteger("type", 3);
            token.setString("sender", tClient.getClientId());
            token.setToken("message", message);
            
            tClient.sendToken(token);
            return true;
        } catch (WebSocketException ex) {
            Logger.getLogger(JWSClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean sendDeleteObject(int objectId){
        try {
            Token message = TokenFactory.createToken();
            message.setInteger("objId", objectId);
            
            Token token = TokenFactory.createToken();
            token.setInteger("type", 4);
            token.setString("sender", tClient.getClientId());
            token.setToken("message", message);
            
            tClient.sendToken(token);
            return true;
        } catch (WebSocketException ex) {
            Logger.getLogger(JWSClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean sendModifyObject(int objId, String exampleString){
        //try {
            Token message = TokenFactory.createToken();
            message.setInteger("objId", objId);
            message.setString("exampleString", exampleString);
            
            Token token = TokenFactory.createToken();
            token.setInteger("type", 5);
            token.setString("sender", tClient.getClientId());
            token.setToken("message", message);
            
            //tClient.sendToken(token);
            System.out.println(token.toString());
            return true;
       /* } catch (WebSocketException ex) {
            Logger.getLogger(JWSClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }*/
    }
    
    @Override
    public void processToken(WebSocketClientEvent wsce, Token token) {
        //throw new UnsupportedOperationException("Not supported yet.");
        System.out.println("processToken");
        System.out.println(token.toString());
        String ID =token.getString("type");   
        try{
            String userName=token.getString("sender") == null? "noname" : token.getString("sender").toString();
            switch(Integer.parseInt(ID)){ 
                case 1000:
                    String textMessage=token.getString("message") == null? " " : token.getString("message").toString();
                    chatDesktop.chat.addText(userName, textMessage);
                    break;
                case 2:
                    Map message=token.getMap("message");
                    final String objId = message.get("objId").toString();
                    final String data=message.get("data")==null? " ":message.get("data").toString();
                    final int x=Integer.parseInt(message.get("x") == null? "0" : message.get("x").toString());
                    final int y=Integer.parseInt(message.get("y") == null? "0" : message.get("y").toString());
                    int z=Integer.parseInt(message.get("z") == null? "0" : message.get("z").toString());
                    if(chatDesktop.canvas.objects.containsKey(objId)){
                        chatdesktop.Rectangle rec=((chatdesktop.Rectangle)chatDesktop.canvas.objects.get(objId));
                        rec.setX(x);
                        rec.setY(y);
                    }else{
                        System.out.println("itt vagyunk");
                        final javafx.scene.shape.Rectangle rec=new javafx.scene.shape.Rectangle(x,y, 5,10);
                        rec.setFill(Color.RED);
                        Platform.runLater(new Runnable() { 
                            @Override
                            public void run() {
                                chatDesktop.canvas.initRectangle(_instance, objId, x, y, 40, 30, data);
                            }
                        });
                    }
                    break;
                case 3:
                    //létrehoz
                    break;
                default:
                    System.out.println(token.toString());
                    break;
            }
            }
        catch(Exception e){
            e.printStackTrace();
        }
        
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

    
}

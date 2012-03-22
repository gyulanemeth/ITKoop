package chatdesktop;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jwebsocket.client.token.BaseTokenClient;
import org.jwebsocket.kit.WebSocketException;
import org.jwebsocket.token.Token;
import org.jwebsocket.token.TokenFactory;

/**
 *
 * @author Sallai Gergely
 */
public class JWSCHandler {
    
    private JWSClient clientListener;
    //private CGITokenClient tClient;
    private BaseTokenClient tClient;
    
    private static JWSCHandler _instance = null;
    private JWSCHandler() {
        clientListener = JWSClient.getInstance();
        //tClient = new CGITokenClient();
        tClient = new BaseTokenClient();
        //tClient.addListener(clientListener);
        tClient.addTokenClientListener(clientListener);
        
    }
    public static synchronized JWSCHandler getInstance() {
        if (_instance == null) {
            _instance = new JWSCHandler();
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
            Logger.getLogger(JWSCHandler.class.getName()).log(Level.SEVERE, null, ex);
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
            token.setInteger("type", 1000);
            token.setString("sender", tClient.getClientId());
            token.setToken("message", message);
            
            tClient.sendToken(token);
            return true;
        } catch (WebSocketException ex) {
            Logger.getLogger(JWSCHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean sendMoveObject(String userName, int objId, int x, int y, boolean savepos){
        try {
            Token message = TokenFactory.createToken();
            message.setString("userName", userName);
            message.setInteger("objId", objId);
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
        try {
            Token message = TokenFactory.createToken();
            message.setInteger("objId", objId);
            message.setString("exampleString", exampleString);
            
            Token token = TokenFactory.createToken();
            token.setInteger("type", 5);
            token.setString("sender", tClient.getClientId());
            token.setToken("message", message);
            
            tClient.sendToken(token);
            return true;
        } catch (WebSocketException ex) {
            Logger.getLogger(JWSClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
}

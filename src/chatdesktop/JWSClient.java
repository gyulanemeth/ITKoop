package chatdesktop;

import org.jwebsocket.api.WebSocketClientEvent;
import org.jwebsocket.api.WebSocketClientTokenListener;
import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.token.Token;

/**
 *
 * @author Sallai Gergely
 */
public class JWSClient implements WebSocketClientTokenListener{
    
    private static JWSClient _instance = null;
    private JWSClient() {   }
    public static synchronized JWSClient getInstance() {
        if (_instance == null) {
            _instance = new JWSClient();
           }
        return _instance;
    }
    
    
    @Override
    public void processToken(WebSocketClientEvent wsce, Token token) {
        //throw new UnsupportedOperationException("Not supported yet.");
        System.out.println("processToken");
        
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
        throw new UnsupportedOperationException("Not supported yet.");
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

    
}
/*public class JWSClient implements WebSocketClientListener{
    
    private static JWSClient _instance = null;
    private JWSClient() {   }
    public static synchronized JWSClient getInstance() {
        if (_instance == null) {
            _instance = new JWSClient();
           }
        return _instance;
    }
    
    public boolean sendText(BaseTokenClient client, String userName, String text) {
        try {
            JSONObject message = new JSONObject();
            message.append("userName", userName);
            message.append("msg", text);
            message.append("timeStamp", "time");
            
            JSONObject json = new JSONObject();
            json.append("type", 1000);
            json.append("sender", client.getClientId());
            json.append("message", message);
            
            client.broadcastText(json.toString());
            //client.sendToken(json);
            return true;
        } catch (JSONException | WebSocketException ex) {
            Logger.getLogger(JWSClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean sendMoveObject(BaseTokenClient client, String userName, int objId, int x, int y, boolean savepos){
        try {
            JSONObject message = new JSONObject();
            message.append("userName", userName);
            message.append("objId", objId);
            message.append("x", x);
            message.append("y", y);
            message.append("savePos", savepos);
            message.append("timeStamp", "time");
            
            JSONObject json = new JSONObject();
            json.append("type", 2);
            json.append("sender", client.getClientId());
            json.append("message", message);
            
            client.broadcastText(json.toString());
            return true;
        } catch (WebSocketException | JSONException ex) {
            Logger.getLogger(JWSClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public boolean sendCreateObject(BaseTokenClient client, String exampleString, int x,int y,int z){
        try {
            JSONObject message = new JSONObject();
            message.append("exampleString", exampleString);
            message.append("x", x);
            message.append("y", y);
            message.append("z", z);
            
            JSONObject json = new JSONObject();
            json.append("type", 3);
            json.append("sender", client.getClientId());
            json.append("message", message);
            
            client.broadcastText(json.toString());
            //client.sendToken(json);
            return true;
        } catch (JSONException | WebSocketException ex) {
            Logger.getLogger(JWSClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
    }
    
    public boolean sendDeleteObject(BaseTokenClient client, int objectId){
        try {
            JSONObject message = new JSONObject();
            message.append("objId", objectId);
            
            JSONObject json = new JSONObject();
            json.append("type", 4);
            json.append("sender", client.getClientId());
            json.append("message", message);
            
            client.broadcastText(json.toString());
            //client.sendToken(json);
            return true;
        } catch (JSONException | WebSocketException ex) {
            Logger.getLogger(JWSClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
            
    public boolean sendModifyObject(BaseTokenClient client, int objId, String exampleString){
        try {
            JSONObject message = new JSONObject();
            message.append("objId", objId);
            message.append("exampleString", exampleString);
            
            JSONObject json = new JSONObject();
            json.append("type", 5);
            json.append("sender", client.getClientId());
            json.append("message", message);
            
            client.broadcastText(json.toString());
            //client.sendToken(json);
            return true;
        } catch (JSONException | WebSocketException ex) {
            Logger.getLogger(JWSClient.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
            
    @Override
    public void processOpening(WebSocketClientEvent wsce) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void processOpened(WebSocketClientEvent wsce) {
        System.out.println("ProcessOpened");
    }

    @Override
    public void processPacket(WebSocketClientEvent wsce, WebSocketPacket wsp) {
        wsp.getString();
        
        
    }

    @Override
    public void processClosed(WebSocketClientEvent wsce) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void processReconnecting(WebSocketClientEvent wsce) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    void login(BaseTokenClient cc, String name, String passw) throws WebSocketException {
        //cc.open("ws://nemgy.itk.ppke.hu:8787/jWebSocket");
        cc.open("ws://localhost:8787/jWebSocket");
        //cc.login(name, passw);
    }
    
}*/

/*package chatdesktop;

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

/*public class JWSCHandler {
    
    private JWSClient clientListener;
    //private CGITokenClient tClient;
    
    
    private static JWSCHandler _instance = null;
    private ChatDesktop chatdesktop;
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

    

    void setDesktop(ChatDesktop chatdesktop) {
        this.chatdesktop=chatdesktop;
        clientListener.setDesktop(chatdesktop);
    }
}
*/
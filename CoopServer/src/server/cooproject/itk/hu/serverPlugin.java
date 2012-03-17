package server.cooproject.itk.hu;

import org.jwebsocket.api.PluginConfiguration;
import org.jwebsocket.api.WebSocketEngine;
import org.jwebsocket.factory.JWebSocketFactory;
import org.jwebsocket.plugins.TokenPlugIn;
import org.jwebsocket.server.TokenServer;

public class serverPlugin extends TokenPlugIn {

	/**
	 * Default konstruktor
	 * @param aConfiguration JwebScoket configuration
	 */
	public serverPlugin(PluginConfiguration aConfiguration) {
		super(aConfiguration);
	}
	/**
	 * Beépitett method, beöltjük a listenerünket
	 */
	public void engineStarted(WebSocketEngine aEngine) {
		TokenServer tokenServer = (TokenServer) JWebSocketFactory
				.getServer("ts0");
		if (tokenServer != null) {
			tokenServer.addListener(new coopMessageListener());
		}
	}

}

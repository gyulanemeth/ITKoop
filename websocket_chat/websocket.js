/**
    A websocket működését kontrolláló globális függvények.
*/

var websocket = null;

  /**
    Ez rossz ötlet volt, használjuk csak a websocket változót.
  */
  function getWS() { return websocket; }

  /**
    Elkészíti a websocket kapcsolatot.
  */
  function doConnect()
  {
    if (window.MozWebSocket)
    {
        logToConsole('This browser supports WebSocket using the MozWebSocket constructor', 'info');
        window.WebSocket = window.MozWebSocket;
    }
    else if (!window.WebSocket)
    {
        logToConsole('This browser does not have support for WebSocket', 'error');
        return;
    }

    var uri = wsUri.value;
    if (uri.indexOf("?") == -1) {
        uri += "?encoding=text";
    } else {
        uri += "&encoding=text";
    }
    websocket = new WebSocket(uri);
    websocket.onopen = function(evt) { onOpen(evt) };
    websocket.onclose = function(evt) { onClose(evt) };
    websocket.onmessage = function(evt) { onMessage(evt) };
    websocket.onerror = function(evt) { onError(evt) };
  }

  /**
    Kapcsolat létrejöttekor...
  */
  function onOpen(evt)
  {
    logToConsole("CONNECTED", 'info');
	message({	
		type: '1001'  //hello message: hack hogy megkapjuk a szervertől az inicializáló adatokat, mert különben cikis, hogy chatüzenetet kell küldeni, hogy adatokat kapjunk. Egy hibaüzenet a használhatóságért.
	});
    setGuiConnected(true);
  }

  /**
    Kapcsolat megszűnésekor...
  */
  function onClose(evt)
  {
    ws=null;
    logToConsole("DISCONNECTED", 'info');
    setGuiConnected(false);
  }

  /**
    Ha üzenet érkezik...
  */
  function onMessage(evt)
  {
    var message=JSON.parse(evt.data);

    if(message.type == 'welcome') { logToConsole("The server welcomes you!", 'server');}
    else if(message.type == '2' ) // valaki mozgatott egy objektumot
    {
      logToConsole("Got message (modifying/initializing object): " + evt.data, "debug");
      canv.moveObject(message.message.objId, message.message.x, message.message.y, message.message.data);
    }
    else if(message.type == '1000' && message.message) { onChatMessage(message); } // chat üzenet
    else if(message.message) { logToConsole(message.sender + ' says: ' + evt.data + ' (aka Message of unknown type:)', 'error'); }
  }

  /**
    Ha valami gebasz van...
  */
  function onError(evt)
  {
    logToConsole('ERROR: ' + evt.data, 'error');
  }

  /**
    Üzenet küldése a websocketen keresztül.
    @param  m a küldeni kívánt JSON objektum.
  */
  function message(m)
  {
    if(!m) return false;

    m.sender=username.value;
    if(!websocket) { logToConsole("I don't think we're connected, boss", "error"); return false; }
    websocket.send(JSON.stringify(m));
    logToConsole("Sent message: "+JSON.stringify(m), "debug");
  }

/**
    A websocket működését kontrolláló globális függvények.
*/

var websocket = null;

  function getWS() {
    return websocket;
  }


  function doConnect()
  {
    if (window.MozWebSocket)
    {
        logToConsole('<span style="color: red;"><strong>Info:</strong> This browser supports WebSocket using the MozWebSocket constructor</span>');
        window.WebSocket = window.MozWebSocket;
    }
    else if (!window.WebSocket)
    {
        logToConsole('<span style="color: red;"><strong>Error:</strong> This browser does not have support for WebSocket</span>');
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

  function onOpen(evt)
  {
    logToConsole("CONNECTED");
    setGuiConnected(true);
  }

  function onClose(evt)
  {
    logToConsole("DISCONNECTED");
    setGuiConnected(false);
  }

  function onMessage(evt)
  {
    var message=JSON.parse(evt.data);

    if(message.type == 'welcome') {
      logToConsole("The server welcomes you!");
    }
    else if(message.type == '2' ) {
        logToConsole("Got message: "+evt.data, "debug");
        canv.moveObject(message.message.objId, message.message.x, message.message.y);
    }
    else if(message.type == '1000' && message.message) {
      onChatMessage(message);
    }
    else if(message.message) {
      logToConsole('Message of unknown type: ' + message.message + '<br /> FROM: ' + message.sender, 'error');
    }
  }

  function onError(evt)
  {
    logToConsole('ERROR: ' + evt.data, 'error');
  }

    function message(m) {
        if(!m) return false;

        m.sender=username.value;

        if(!getWS()) {
            logToConsole("I don't think we're connected, boss", "error");
            return false;
        }

        getWS().send(JSON.stringify(m));

        logToConsole("Sent message: "+JSON.stringify(m), "debug");
    }


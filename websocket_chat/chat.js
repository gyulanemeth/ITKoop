/**
    A GUIra és a chatre vonatkozó globális függvények.
*/

  var secureCb;           // a "Use secure WebSocket" checkbox.
  var secureCbLabel;      // a checkboxhoz tartozó label.
  var wsUri;              // az URI input field.
  var chatLog;            // log a chat üzeneteknek.
  var debugLog;           // log minden másnak.
  var connectBut;         // kapcsolódás gomb.
  var disconnectBut;      // disconnect gomb.
  var username;           // felhasználói név input gomb.
  var sendMessage;        // az üzenet input fieldje.
  var sendBut;            // üzenet küldése gomb.
  var clearChatLogBut;    // chat log törlése gomb.
  var clearDebugLogBut;   // debug log törlése gomb.
  var useDebug;           // "use debug log" checkbox.
  var useDebugLabel;      // label a checkboxnak.
  var doLogging = true;   // logoljunk-é?

  var secureTag = document.createElement('img'); // lakatocska
  secureTag.src="img/lock_icon.gif";

  /**
    A GUI inicializálása, a fenti globális változók beállítása.
  */
  function LilchatInit()
  {
    // megnézzük, van-e websocket support
    if (window.WebSocket){ document.getElementById("webSocketSupp").style.display = "block";}
    else { document.getElementById("noWebSocketSupp").style.display = "block"; }

    // secure connection checkbox
    secureCb = document.getElementById("secureCb");
    secureCb.checked = false;
    secureCb.onclick = toggleTls;
    secureCbLabel = document.getElementById("secureCbLabel")

    // input field a kapcsolódáshoz
    wsUri = document.getElementById("wsUri");
    toggleTls();

    // gomb a csatlakozáshoz
    connectBut = document.getElementById("connect");
    document.getElementById('connForm').onsubmit=function() { doConnect(); return false;}

    // gomb a kijelentkezéshez
    disconnectBut = document.getElementById("disconnect");
    disconnectBut.onclick = doDisconnect;
    document.getElementById("disconnect").onclick = doDisconnect;

    // input a felhasználónévnek
    username=document.getElementById('username');

    // input a chatüzenetekhez
    sendMessage = document.getElementById("sendMessage");

    // üzenetküldés
    sendBut = document.getElementById("send");
    document.getElementById('msgForm').onsubmit =
      function() {
        doChatSend(); // üzenetküldés
        sendMessage.value = ""; // chat-input-textbox törlése
        return false;
      }

    // log a chatüzeneteknek
    chatLog = document.getElementById("chatLog");

    // chatlog törlése
    clearChatLogBut = document.getElementById("clearChatLogBut");
    clearChatLogBut.onclick=function() { clearLog(chatLog);}

    // log a bedug dolgoknak
    debugLog = document.getElementById("debugLog");

    // debuglog törlése
    clearDebugLogBut = document.getElementById("clearDebugLogBut");
    clearDebugLogBut.onclick=function() { clearLog(debugLog);}

    // használjunk-e debugot
    useDebug = document.getElementById("useDebug");
    useDebug.checked = true;
    useDebug.onclick = toggleDebugUse;
    useDebugLabel = document.getElementById("useDebugLabel")

    // connection hinyában néhány input letiltása
    setGuiConnected(false);
  }

  /**
    Attól függően, hogy csatlakozva van-e a kliens, a különböző inputok engedélyezése/tiltása.
  */
  function setGuiConnected(isConnected)
  {
    wsUri.disabled = isConnected;
    connectBut.disabled = isConnected;
    disconnectBut.disabled = !isConnected;
    sendMessage.disabled = !isConnected;
    sendBut.disabled = !isConnected;
    secureCb.disabled = isConnected;
    var labelColor = "black";
    if (isConnected){ labelColor = "#999999"; }
    secureCbLabel.style.color = labelColor;
  }

  /**
    Secure connection ki- és bekapcsolása -> állítgatja a hozzá javasolt címet is.
  */
  function toggleTls()
  {
    var x = (wsUri.value.length < 1? x="ws://localhost:8787" : wsUri.value);
    x=new Uri(x);

    if (secureCb.checked) { x.setProtocol('wss'); x.setPort('9797'); }
    else { x.setProtocol('ws'); x.setPort('8787'); }

    wsUri.value=x.toString();
  }

  /**
    Logolás ki-be kapcsolása.
  */
  function toggleDebugUse()
  {
    if (useDebug.checked) { doLogging = true; debugLog.style.backgroundColor = "white"; }
    else { doLogging = false; debugLog.style.backgroundColor = "LightGray"; }
  }

  /**
    Ha secure connection van, lekérhetjük a lakatot.
  */
  function getSecureTag()
  {
    return (secureCb.checked? secureTag.cloneNode(true) : null)
  }

  /**
    Kapcsolat bontása (websocket becsuk).
  */
  function doDisconnect()
  {
    if(getWS()) getWS().close();
  }

  /**
    Chat üzenet küldése.
  */
  function doChatSend()
  {
    if (!getWS()) return;

    var m = {
        sender: username.value,
        type: '1000',   //chat message
        message: sendMessage.value
    };

    logToConsole('SENT: ' + sendMessage.value, "sent");

    getWS().send(JSON.stringify(m));
  }

  /**
    Chat üzenet fogadása.
  */
  function onChatMessage(message)
  {
    // ezzel azért majd kezdeni kellene valamit....
    var m = (message && message.sender == 'CooProjectServer')? 'server' : 'got';
    logToConsole(message.sender + ' says: ' + message.message, m);
  }


  /**
    Üzenetek kiírása a megfelelő log-ba.
    type = got, server -> chatüzenet
      got: kék
      server: piros
    bármi másnál debug
      sent (chat): zöld
      info: zöld
      error: piros
  */
  function logToConsole(message, type)
  {
    var pre = document.createElement("p");

    if(type) pre.className=type;
    pre.style.wordWrap = "break-word";

    pre.innerHTML = message;

    var sec=getSecureTag();
    if(sec) pre.insertBefore(sec, pre.firstChild);

    if (type && type == 'got') { chatLog.appendChild(pre); chatLog.scrollTop = chatLog.scrollHeight; }
    else if (type == 'server') { chatLog.appendChild(pre); chatLog.scrollTop = chatLog.scrollHeight; }
    else { if (doLogging) {debugLog.appendChild(pre); debugLog.scrollTop = debugLog.scrollHeight; }}

    // hogy ne legyen túl nagy a DOM -> bár azért valahogy a chatüzeneteket legalább jó lenne megtartani...
    while (chatLog.childNodes.length > 100)
    {
      chatLog.removeChild(chatLog.firstChild);
    }

    while (debugLog.childNodes.length > 60)
    {
      debugLog.removeChild(debugLog.firstChild);
    }
  }

  /**
    Paraméterben adott log tartalmának törlése.
  */
  function clearLog(log)
  {
    while (log.childNodes.length > 0){log.removeChild(log.firstChild);}
  }

  window.addEventListener("load", LilchatInit, false);

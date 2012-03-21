/**
    A GUIra és a chatre vonatkozó globális függvények.
*/
  var canv;               // a használt canvasing objektum.

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
  var serverSelect;       // sever választó combobox

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

	// supportolt a canvas element?
	if("HTMLCanvasElement" in window) {
		canv=new canvasing(document.getElementById('canvas_cont'));
	} else {
		document.getElementById('canvas_cont').innerHTML="Your browser doesn't support the HTML5 Canvas element. Unfortunately this means you can't use this thing.";
	}

    // secure connection checkbox
    secureCb = document.getElementById("secureCb");
    secureCb.checked = false;
    secureCb.onclick = toggleTls;
    secureCbLabel=document.getElementById("secureCbLabel");

    // server selection
    wsUri = document.getElementById("wsUri");

	if(servers instanceof Array && servers.length > 0) {	//ha vannak előre definiált szerverek
		serverSelect=document.createElement('SELECT');
		var opt;
		opt=document.createElement('OPTION');
			opt.setAttribute('value', -1 );
			opt.appendChild(document.createTextNode("<other>"));
			opt.server=null;
		serverSelect.appendChild(opt);

		for(i in servers) {
			opt=document.createElement('OPTION');
				if(i == 0) opt.setAttribute('selected', 'true');
				opt.setAttribute('value', i);
				opt.server=servers[i];
				opt.appendChild(document.createTextNode(servers[i].host));
			serverSelect.appendChild(opt);
		}

		wsUri.parentNode.insertBefore(serverSelect, wsUri);
		wsUri.style.display='none';
		serverSelect.onchange=function() {
			if(serverSelect.options[serverSelect.selectedIndex].value == -1) {
				var uri=new Uri().setProtocol( secureCb.checked? 'wss' : 'ws' );
				wsUri.value=uri;
				serverSelect.style.display='block';
				wsUri.style.display='inline';
				wsUri.focus();
			} else {
				serverSelect.style.display='inline';
				wsUri.style.display='none'
				var server=serverSelect.options[serverSelect.selectedIndex].server;
				var uri=new Uri()
					.setHost(server.host)
					.setPort( secureCb.checked? server.sport : server.port )
					.setProtocol( secureCb.checked? 'wss' : 'ws' );
				wsUri.value=uri.toString();
			}
		};
		serverSelect.onchange();
	} else serverSelect = null;
	toggleTls();

    // gomb a csatlakozáshoz
    connectBut = document.getElementById("connect");
    document.getElementById('connForm').onsubmit=function() { doConnect(); return false;}

    // gomb a kijelentkezéshez
    disconnectBut = document.getElementById("disconnect");
    disconnectBut.onclick = doDisconnect;

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
    if(serverSelect) serverSelect.disabled=isConnected;
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
    if(serverSelect && serverSelect.options[serverSelect.selectedIndex].value != -1) {
      serverSelect.onchange();
      return;
    }

    var x = (wsUri.value.length < 1? x="ws://" : wsUri.value);
    x=new Uri(x);

    if (secureCb.checked) x.setProtocol('wss');
    else x.setProtocol('ws');

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


if (window.attachEvent) window.attachEvent("load", LilchatInit);
else window.addEventListener("load", LilchatInit, false);

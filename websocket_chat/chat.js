  var secureCb;
  var secureCbLabel;
  var wsUri;
  var consoleLog;
  var connectBut;
  var disconnectBut;
  var username;
  var sendMessage;
  var sendBut;
  var clearLogBut;

  function echoHandlePageLoad()
  {
    if (window.WebSocket)
    {
      document.getElementById("webSocketSupp").style.display = "block";
    }
    else
    {
      document.getElementById("noWebSocketSupp").style.display = "block";
    }

    secureCb = document.getElementById("secureCb");
    secureCb.checked = false;
    secureCb.onclick = toggleTls;

    secureCbLabel = document.getElementById("secureCbLabel")

    wsUri = document.getElementById("wsUri");
    toggleTls();

    connectBut = document.getElementById("connect");
    document.getElementById('connForm').onsubmit=function() {
        doConnect();
        return false;
    }

    disconnectBut = document.getElementById("disconnect");
    disconnectBut.onclick = doDisconnect;

    username=document.getElementById('username');

    sendMessage = document.getElementById("sendMessage");

    sendBut = document.getElementById("send");
    document.getElementById('msgForm').onsubmit=function() {
        doSend();
        return false;
    }

    consoleLog = document.getElementById("consoleLog");

    clearLogBut = document.getElementById("clearLogBut");
    clearLogBut.onclick = clearLog;

    setGuiConnected(false);

    document.getElementById("disconnect").onclick = doDisconnect;
  }

  function toggleTls()
  {
    if (secureCb.checked)
    {
      wsUri.value = "wss://localhost:8787";
    }
    else
    {
      wsUri.value = "ws://localhost:8787";
    }
  }

  
  function doDisconnect()
  {
    if(getWS()) getWS().close();
  }

  function doSend()
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

  function onChatMessage(message) {
    logToConsole('GOT: ' + message.message + '<br /> FROM: ' + message.sender, 'got');
  }

  function logToConsole(message, type)
  {
    var pre = document.createElement("p");

    if(type) pre.className=type;
    
    pre.style.wordWrap = "break-word";

    pre.innerHTML = message;

    var sec=getSecureTag();
    if(sec) pre.insertBefore(sec, pre.firstChild);

    consoleLog.appendChild(pre);

    while (consoleLog.childNodes.length > 50)
    {
      consoleLog.removeChild(consoleLog.firstChild);
    }

    consoleLog.scrollTop = consoleLog.scrollHeight;
  }


  function setGuiConnected(isConnected)
  {
    wsUri.disabled = isConnected;
    connectBut.disabled = isConnected;
    disconnectBut.disabled = !isConnected;
    sendMessage.disabled = !isConnected;
    sendBut.disabled = !isConnected;
    secureCb.disabled = isConnected;
    var labelColor = "black";
    if (isConnected)
    {
      labelColor = "#999999";
    }
     secureCbLabel.style.color = labelColor;
  }

  function clearLog()
  {
    while (consoleLog.childNodes.length > 0)
    {
      consoleLog.removeChild(consoleLog.lastChild);
    }
  }

  var secureTag = document.createElement('img');
  secureTag.src="img/lock_icon.gif";
  
  function getSecureTag()
  {
    if (secureCb.checked)
    {
      return secureTag.cloneNode(true);
    }
    else
    {
      return null;
    }
  }

  window.addEventListener("load", echoHandlePageLoad, false);

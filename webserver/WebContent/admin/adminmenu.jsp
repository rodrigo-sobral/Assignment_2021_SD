<%@ taglib prefix="s" uri="/struts-tags" %>
	<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
		<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
		<html>

			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
				<title>Menu Admin</title>
				<link rel="shortcut icon" href="resources/images/uc_logo.png">
				<link rel="stylesheet" href="styles/menus_template.css">
				
				<script type="text/javascript">
					var websocket = null;
					window.onload = function() { connect('ws://' + window.location.host + '/webserver/ws'); }
					
					function connect(host) {
						if ('WebSocket' in window) websocket = new WebSocket(host);
						else if ('MozWebSocket' in window) websocket = new MozWebSocket(host);
						else { writeToHistory('Get a real browser which supports WebSocket.'); return; }
						
						websocket.onopen = onOpen;
						websocket.onclose = onClose;
						websocket.onmessage = onMessage;
						websocket.onerror = onError;
					}
					
					function onOpen(event) { 
						console.log("${ask_elections_type}");
						if ("${ask_elections_type}") websocket.send("${ask_elections_type}"); 
						websocket.send("vote_tables"); 
						websocket.send("admin"); 
					}
					function onClose(event) { }
					function onMessage(message) { 
						if (message.data.startsWith("Administradores Ativos")) {
							writeToHistory(message.data); 
						}
					}
					function onError(event) { console.log('WebSocket error (' + event.data + ').'); }
					
					function writeToHistory(text) {
						let historyUsers = document.getElementById('logged_users');
                        while (historyUsers.firstChild) historyUsers.removeChild(historyUsers.firstChild);
                        let line = document.createElement('p');
                        line.style.wordWrap = 'break-word';
                        line.innerHTML = text;
                        historyUsers.appendChild(line);
                        historyUsers.scrollTop = historyUsers.scrollHeight;
					}
				</script>
			</head>

			<body>
				<h1>Menu Administrativo</h1>
				<br><br>
				<div class="container">
					<form action="button1">
						<button>Registar Eleitor</button>
					</form>
					<form action="button2">
						<button>Registar Eleicao</button>
					</form>
					<form action="button3">
						<button>Editar Eleicao</button>
					</form>
					<form action="button4">
						<button>Consultar Eleicoes</button>
					</form>
					<form action="button5">
						<button>Registar Candidaturas</button>
					</form>
					<form action="button6">
						<button>Registar Mesa de Voto</button>
					</form>
					<form action="button7">
						<button>Eliminar Mesas de Voto</button>
					</form>
					<form action="button8">
						<button>Consultar Mesas de Voto</button>
					</form>
					<br><br>
					<div style="white-space: pre-line" id="logged_users"></div>
				</div>
				<noscript>JavaScript must be enabled for WebSockets to work.</noscript>	
			</body>
		</html>
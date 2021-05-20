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

					function onOpen(event) { websocket.send("admin"); websocket.send("vote_tables"); }
					function onClose(event) { }
					function onMessage(message) { 
						if (!message.data.startsWith("Administradores Ativos") && !message.data.includes("Candidatos:")) {
							writeToHistory(message.data); 
						}
					}
					function onError(event) { console.log('WebSocket error (' + event.data + ').'); }

					function writeToHistory(text) {  if (document.getElementById('text_panel')!=null) document.getElementById('text_panel').innerHTML= text; }
				</script>
			</head>
			
			<body>
				<h1>Consultar Mesas de Voto</h1>
				<br><br>
				<div class="container">
					<form>
						<label class="title"><b>Mesas de Voto Criadas</b></label><br>
						<s:textarea id="text_panel" value="%{session.ask_vote_tables}" cols="93" rows="30" disabled="true"/>
                    </form>
                    <form action="admin_menu">
                        <button>Cancelar</button>
                    </form>
				</div>
			</body>
		</html>
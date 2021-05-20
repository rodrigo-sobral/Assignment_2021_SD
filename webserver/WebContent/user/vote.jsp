<%@ taglib prefix="s" uri="/struts-tags" %>
	<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
		<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
				<title>Menu Utilizador</title>
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
			
					function onOpen(event) { websocket.send("${session.logged_cc}"); }
					function onClose(event) { }
					function onMessage(message) { }
					function onError(event) { console.log('WebSocket error.'); }			
				</script>
			</head>
			
			<body>
				<h1>Votar</h1>
				<br><br>
				<div class="container">
					<form action="vote" method="POST">
						<label class="title"><b>Candidaturas Disponiveis</b></label><br>
						<s:textarea value="%{session.ask_candidatures}" cols="93" rows="10" disabled="true"/>
						<input type="text" name="voted_candidate" placeholder="Candidato">
						<button type="submit">Submeter Voto</button>
                    </form>
					<form action = "partilhar_post">
						<input type="submit" id ="botao_fb" value="Partilhar Post">
					</form>
                    <form action="user_menu">
                        <button>Cancelar</button>
                    </form>
				</div>
			</body>
		</html>
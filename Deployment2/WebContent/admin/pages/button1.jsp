<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
	<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
	<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
				<title>eVoting</title>
				<link rel="shortcut icon" href="resources/images/uc_logo.png">
				<link rel="stylesheet" href="styles/menus_template.css">
				<script type="text/javascript">
					var websocket = null;
					window.onload = function() { connect('wss://' + window.location.host + '/webserver/ws'); }
					
					function connect(host) {
						if ('WebSocket' in window) websocket = new WebSocket(host);
						else if ('MozWebSocket' in window) websocket = new MozWebSocket(host);
						else { writeToHistory('Get a real browser which supports WebSocket.'); return; }
						
						websocket.onopen = onOpen;
						websocket.onclose = onClose;
						websocket.onmessage = onMessage;
						websocket.onerror = onError;
					}
					
					function onOpen(event) { websocket.send("admin"); }
					function onClose(event) { websocket.send(""); }
					function onMessage(message) { }
					function onError(event) { console.log('WebSocket error (' + event.data + ').'); }
				</script>
			</head>

			<body>
				<h1>Registar Eleitor</h1>
				<br><br>
				<div class="container">
					<form action="regist_user" method="POST">
						<input type="text" name="user_type" placeholder="(Estudante, Professor, Funcionario)">
						<input type="text" name="name" placeholder="Nome">
						<input type="password" name="password" placeholder="Password">
						<input type="text" name="address" placeholder="Morada">
						<input type="text" name="phone_number" placeholder="Contacto Telefonico">
						<input type="text" name="college" placeholder="Faculdade">
						<input type="text" name="department" placeholder="Departamento">
						<input type="text" name="cc_number" placeholder="Numero Cartao Cidadao">
						<input type="text" name="cc_shelflife" placeholder="Validade Cartao Cidadao">
						<button type="submit">Submeter</button>
                    </form>

                    <form action="admin_menu">
                        <button>Cancelar</button>
                    </form>
				</div>
			</body>
		</html>
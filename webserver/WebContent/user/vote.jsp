<%@ taglib prefix="s" uri="/struts-tags" %>
	<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
		<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
				<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
				<meta property="og:url"           content="https://f23709356624.ngrok.io/webserver" />
				<meta property="og:type"          content="website" />
				<meta property="og:image"         content="https://www.your-domain.com/path/image.jpg" />
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
			
					function onOpen(event) { websocket.send("${session.logged_cc}"); }
					function onClose(event) { }
					function onMessage(message) { }
					function onError(event) { console.log('WebSocket error.'); }
					
					(function(d, s, id) {
						var js, fjs = d.getElementsByTagName(s)[0];
						if (d.getElementById(id)) return;
						js = d.createElement(s); js.id = id;
						js.src = "https://connect.facebook.net/en_US/sdk.js#xfbml=1&version=v3.0";
						fjs.parentNode.insertBefore(js, fjs);
					}(document, 'script', 'facebook-jssdk'));
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
					<br><br>
					<div class="fb-share-button" data-href="https://f23709356624.ngrok.io/webserver" data-layout="button_count"></div>
                    <br><br>
					<form action="user_menu">
                        <button>Cancelar</button>
                    </form>
				</div>
			</body>
		</html>
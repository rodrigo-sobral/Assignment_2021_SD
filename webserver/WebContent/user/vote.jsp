<%@ taglib prefix="s" uri="/struts-tags" %>
	<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
		<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
				<title>Menu Utilizador</title>
				<link rel="shortcut icon" href="resources/images/uc_logo.png">
				<link rel="stylesheet" href="styles/menus_template.css">
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
                    <form action="user_menu">
                        <button>Cancelar</button>
                    </form>
				</div>
			</body>
		</html>
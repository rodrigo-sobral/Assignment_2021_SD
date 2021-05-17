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
				<h1>Autenticacao Eleicao</h1>
				<br><br>
				<div class="container">
					<form action="user_auth" method="POST">
						<label class="title"><b>Eleicoes Disponiveis</b></label><br>
						<s:textarea value="%{ask_elections}" cols="93" rows="10" disabled="true"/>
						<input type="text" name="selected_election" placeholder="Titulo da Eleicao Pretendida">
						<input type="text" name="new_description" placeholder="Numero Cartao de Cidadao">
						<button type="submit">Submeter</button>
                    </form>
                    <form action="user_menu">
                        <button>Cancelar</button>
                    </form>
				</div>
			</body>
		</html>
<%@ taglib prefix="s" uri="/struts-tags" %>
	<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
		<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
				<title>Menu Admin</title>
				<link rel="shortcut icon" href="resources/images/uc_logo.png">
				<link rel="stylesheet" href="styles/menus_template.css">
			</head>
			<body>
				<h1>Registar Candidatura</h1>
				<br><br>
				<div class="container"> 
					<form action="regist_candidature" method="POST">
						<label class="title"><b>Eleicoes Disponiveis</b></label><br>
						<s:textarea value="%{ask_elections}" cols="93" rows="10" disabled="true"/><br><br>
						<label class="title"><b>Candidatos Disponiveis</b></label><br>
						<s:textarea value="%{ask_users}" cols="93" rows="10" disabled="true"/><br>
						<input type="text" name="title" placeholder="Titulo">
						<input type="text" name="election" placeholder="Eleicao">
						<input type="text" name="candidates" placeholder="ID dos Candidatos (ID1, ID2, ID3, ...)">
						<button type="submit">Submeter</button>
                    </form>
					<form action="admin_menu">
                        <button>Cancelar</button>
                    </form>
				</div>
			</body>
		</html>
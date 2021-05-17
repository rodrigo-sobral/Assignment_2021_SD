
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
	<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
	<html>

		<head>
			<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
			<title>Autenticacao</title>
			<link rel="shortcut icon" href="resources/images/uc_logo.png">
			<link rel="stylesheet" href="styles/index.css">
		</head>
		
		<body>				
			<div class="wrapper fadeInDown">
				<div id="formContent">
					<h2 class="active">eVoting</h2>
					<form action="vote_menu" method="post">
						<input type="text" id="CC" class="fadeIn second" name="cc_number" placeholder="Numero Cartao Cidadao">
						<input type="submit" class="fadeIn fourth" value="Autenticar">
					</form>
					<form action="user_menu">
						<input type="submit" class="fadeIn fourth" value="Voltar">
                    </form>
				</div>
			</div>
		</body>
	</html>
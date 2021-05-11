<%@ taglib prefix="s" uri="/struts-tags" %>
	<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
		<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
		<html>

			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
				<title>Autenticacao</title>
				<link rel="shortcut icon" href="resources/images/uc_logo.png">
				<link rel="stylesheet" href="styles/user_auth.css">
			</head>
			
			<body>				
				<div class="wrapper fadeInDown">
					<div id="formContent">
						<h2 class="active">eVoting</h2>
						<!-- Tabs Titles -->
						<form action="login" method="post">
							<input type="text" id="CC" class="fadeIn second" name="cc" placeholder="Numero Cartao Cidadao">
							<input type="submit" class="fadeIn fourth" value="Autenticar">
						</form>
					</div>
				</div>
			</body>
		</html>
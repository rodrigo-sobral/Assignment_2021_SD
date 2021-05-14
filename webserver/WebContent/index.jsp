<%@ taglib prefix="s" uri="/struts-tags" %>
	<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
		<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
				<link rel="shortcut icon" href="../resources/images/uc_logo.png">
				<link rel="stylesheet" href="styles/index.css">
			</head>
			
			<body>				
				<div class="wrapper fadeInDown">
					<div id="formContent">
						<!-- Tabs Titles -->
						<h2 class="active">eVoting</h2>
						<!-- Login Form -->
						<form action="login" method="post">
							<input type="text" id="Login" class="fadeIn second" name="username" placeholder="Nome">
							<input type="text" id="password" class="fadeIn third" name="password" placeholder="Password">
							<input type="submit" class="fadeIn fourth" id ="botao_submit" value="Login">    
                        </form>
                        <form action = "login_fb" method="post">
                            <input type="submit" id="botao_fb" value="Login com o facebook">
                        </form>
					</div>
				</div>
			</body>
		</html>


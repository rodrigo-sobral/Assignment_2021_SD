<%@ taglib prefix="s" uri="/struts-tags" %>
	<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
		<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
		<html>

			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
				<title>Admin Menu</title>
				<link rel="shortcut icon" href="../resources/images/uc_logo.png">
				<link rel="stylesheet" href="../styles/adminmenu.css">
			</head>
			
			<body>
				<h1>Registar Eleitor</h1>
				<br><br>
				<div class="container">
					<form action="regist_eleitor">
                        <input type="text" placeholder="Tipo (Funcionario/Professor/Estudante)">
						<input type="text" placeholder="Nome">
						<input type="password" placeholder="password">
						<input type="text" placeholder="Morada">
						<input type="number" placeholder="Contacto Telefonico">
						<input type="text" placeholder="Faculdade">
						<input type="text" placeholder="Departamento">
						<input type="number" placeholder="Numero Cartao Cidadao">
						<input type="text" placeholder="Validade Cartao Cidadao">
						<button type="submit">Submeter</button>
                    </form>

                    <form action="cancel_admin">
                        <button>Cancelar</button>
                    </form>
				</div>
			</body>
		</html>
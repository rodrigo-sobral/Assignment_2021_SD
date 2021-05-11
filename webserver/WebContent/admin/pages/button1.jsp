<%@ taglib prefix="s" uri="/struts-tags" %>
	<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
		<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
		<html>

			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
				<title>Menu Admin</title>
				<link rel="shortcut icon" href="resources/images/uc_logo.png">
				<link rel="stylesheet" href="styles/regist_template.css">
			</head>

			<body>
				<h1>Registar Eleitor</h1>
				<br><br>
				<div class="container">
					<form action="regist_eleitor">
						<input type="text" name="user_type" placeholder="(Estudante, Professor, Funcioanrio)">

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

                    <form action="admin">
                        <button>Cancelar</button>
                    </form>
				</div>
			</body>
		</html>
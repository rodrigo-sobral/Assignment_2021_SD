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
				<h1>Registar Eleicao</h1>
				<br><br>
				<div class="container">
					<form action="regist_election" method="POST">
						<input type="text" name="election_state" placeholder="(Estudante, Professor, Funcionario)">
						<input type="text" name="title" placeholder="Titulo">
						<input type="text" name="description" placeholder="Descricao">
						<input type="text" name="start_date" placeholder="Data Inicio [dd/mm/aaaa]">
						<input type="text" name="start_hour" placeholder="Hora Inicio [hh:mm]">
						<input type="text" name="end_date" placeholder="Data Fim [dd/mm/aaaa]">
						<input type="text" name="end_hour" placeholder="Hora Fim [hh:mm]">
						<button type="submit">Submeter</button>
                    </form>
                    <form action="admin">
                        <button>Cancelar</button>
                    </form>
				</div>
			</body>
		</html>
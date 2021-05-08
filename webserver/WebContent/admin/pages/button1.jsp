<%@ taglib prefix="s" uri="/struts-tags" %>
	<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
		<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
		<html>

			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
				<title>Admin Menu</title>
				<link rel="shortcut icon" href="../resources/images/uc_logo.png">
				<link rel="stylesheet" href="styles/button1.css">
			</head>
			<!--
			<style>
				body { background-color: #282d3f; }
				form, h1 {
					text-align:center;
					width:50%;
					margin:0 auto;
					font-family: arcadeFont; 
				}

				input {
					width: 100%;
					padding: 12px 20px;
					margin: 8px 0;
					display: inline-block;
					border: 1px solid #ccc;
					box-sizing: border-box;
				}

				button {
					background-color: #03ad80;
					color: white;
					padding: 14px 20px;
					margin: 6px 0;
					cursor: pointer;
					width: 100%;
					border: 3px solid white;
					border-radius: 7px;   
					font-family: arcadeFont; 
				}

				button:hover { opacity: 0.8; }

				.cancelbtn {
					width: auto;
					padding: 10px 18px;
					background-color: #f44336;
				}

				.container { padding: 16px; }

				/* The checking */
				.checking {
					display: block;
					position: relative;
					padding-left: 35px;
					margin-bottom: 12px;
					cursor: pointer;
					font-size: 22px;
					-webkit-user-select: none;
					-moz-user-select: none;
					-ms-user-select: none;
					user-select: none;
				}
				
				.checking input { position: absolute; opacity: 0; cursor: pointer; }
				.checkmark {
					position: absolute;
					top: 0;
					left: 0;
					height: 25px;
					width: 25px;
					background-color: #56628b;
					border-radius: 50%;
				}
				.checking:hover input ~ .checkmark { background-color: #3d4561; }
				.checking input:checked ~ .checkmark { background-color: #03ad80; }
				.checkmark:after {	
					content: "";
					position: absolute;
					display: none;
				}
				.checking input:checked ~ .checkmark:after { display: block; }
				.checking .checkmark:after {
					top: 9px;
					left: 9px;
					width: 8px;
					height: 8px;
					border-radius: 50%;
					background: white; 
				}

				@media screen and (max-width: 300px) { .cancelbtn { width: 100%; } }

				@font-face {
					font-family: arcadeFont;
					src: url("../resources/fonts/Quantico-Regular.ttf");
				}
			</style>
			-->
			<body>
				<h1>Registar Eleitor</h1>
				<br><br>
				<div class="container">
					<form action="regist_eleitor">
                        <label class="checking">Estudante
							<input type="radio" checked="checked" name="radio">
							<span class="checkmark"></span>
						</label>
						<label class="checking">Professor
							<input type="radio" name="radio">
							<span class="checkmark"></span>
						</label>
						<label class="checking">Funcionario
							<input type="radio" name="radio">
							<span class="checkmark"></span>
						</label>
						<input type="text" placeholder="Nome">
						<input type="password" placeholder="Password">
						<input type="text" placeholder="Morada">
						<input type="text" placeholder="Contacto Telefonico">
						<input type="text" placeholder="Faculdade">
						<input type="text" placeholder="Departamento">
						<input type="text" placeholder="Numero Cartao Cidadao">
						<input type="text" placeholder="Validade Cartao Cidadao">
						<button type="submit">Submeter</button>
                    </form>

                    <form action="cancel_admin">
                        <button>Cancelar</button>
                    </form>
				</div>
			</body>
		</html>
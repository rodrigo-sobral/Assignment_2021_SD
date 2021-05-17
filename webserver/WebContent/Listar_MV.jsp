<%@ taglib prefix="s" uri="/struts-tags" %>
	<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
		<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
				<link rel="shortcut icon" href="../resources/images/uc_logo.png">
				<link rel="stylesheet" href="styles/Listar_MV.css">
			</head>
			<form>
				<h1>Mesas de Voto Disponiveis</h1>
				<br><br>
				<div class="container">
					<form action= "votetablechoose" method="post">
						<s:textarea value="%{lista_mesas}" cols="50" rows="30" disabled="true"/>
						<br>	
						<input type="text" name="user_type" placeholder="(Insira a mesa de voto)">
						<button type="submit">Submeter</button>
                    </form>
					<form>
						<button type="submit">Log Out</button>
					</form>
					<form action = "associar_fb" method="post">
						<input type="submit" id="botao_fb" value="Associar com o facebook">
					</form>
				</div>
		
			</form>
		</html>


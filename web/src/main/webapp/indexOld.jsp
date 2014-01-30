<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="helpers.SettingsHelper"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Lazarus!</title>
<link rel="stylesheet" type="text/css" href="css/main.css">
<script type="text/javascript" src="js/jquery-1.9.1.min.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		$("#loginForm").submit(function(event) {
			var form = $(this);
			url = form.attr('action');
			var posting = $.post(url, {
				username : $("#inputUsername").val(),
				password : $("#inputPassword").val()
			});
			posting.done(function(response) {
				jsonResponse = JSON.parse(response);
				if (jsonResponse.result == "OK") {
					$.ajax({
						url : "LoginServlet",
						type : "POST",
						data : {
							token : jsonResponse.data
						},
						success : function(data, textStatus, jqXHR) {
							if (data == "OK")
								window.location.href = "city-reports.jsp";
							else
								alert("No se pudo completar su solicitud");
						},
						error : function(jqXHR, textStatus, errorThrown) {
							alert("No se pudo completar su solicitud");
						}
					});
				} else
					alert(jsonResponse.data);
			});
			return false;
		});
		$("#newUserForm").submit(function(event) {
			var form = $(this);
			url = form.attr('action');
			var posting = $.post(url, {
				username : $("#inputNewUsername").val(),
				password : $("#inputNewPassword").val(),
				email : $("#inputNewEmail").val()
			});
			posting.done(function(response) {
				jsonResponse = JSON.parse(response);
				if (jsonResponse.result == "OK") {
					alert("Usuario creado con éxito");
					$("#inputUsername").val($("#inputNewUsername").val());
					$("#inputPassword").val($("#inputNewPassword").val());
					$("#loginForm").submit();
				} else
					alert(jsonResponse.data);
			});
			return false;
		});
	});

	function loadNewUser() {
		$("#newUserForm").css("display","inline");
	}

	function forgotPassword() {
		if ($("#inputUsername").val() != ""){
			$.ajax({
				url : "<%=SettingsHelper.REST_API_URL + "/users/password"%>",
				type : "POST",
				data : {
					username : $("#inputUsername").val()
				},
				success : function(data, textStatus, jqXHR) {
					jsonResponse = JSON.parse(data);
					if (jsonResponse.result == "OK")
						alert("Se ha enviado un email a su casilla de correo con la nueva contraseña");
					else
						alert(jsonResponse.data);
				},
				error : function(jqXHR, textStatus, errorThrown) {
					alert("No se pudo completar su solicitud");
				}
			});
		}
		else
			alert("Ingrese su nombre de usuario");
	}
</script>
</head>
<body>
	<a style="float: right" href="thanks.jsp">Reconocimientos</a>
	<h2 style="margin-left: 7%">Bienvenido a Lazarus!</h2>
	<form method="post" id="loginForm"
		action="<%=SettingsHelper.REST_API_URL + "/users/login"%>">
		<input type="text" id="inputUsername" placeholder="Ingrese su usuario"
			name="username"><br> <input type="password"
			id="inputPassword" placeholder="Ingrese su contraseña"
			name="password"><br> <input type="submit"
			value="Ingresar!">
	</form>
	<div class="linksDiv">
		<a href="#" onclick="loadNewUser(); return false;">Registrarse</a> <a href="#"
			onclick="forgotPassword(); return false;">Olvidó su contraseña?</a>
	</div>
	<form method="post" id="newUserForm" style="display: none"
		action="<%=SettingsHelper.REST_API_URL + "/users"%>">
		<input type="text" id="inputNewUsername" name="username"
			placeholder="Ingrese su nombre de usuario"><br> <input
			type="email" id="inputNewEmail" placeholder="Ingrese su email"
			name="email"><br> <input type="password"
			id="inputNewPassword" placeholder="Ingrese su contraseña"
			name="password"><br> <input id="submitBtn"
			type="submit" value="Enviar!">
	</form>
</body>
</html>

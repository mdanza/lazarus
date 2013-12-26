<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="helpers.SettingsHelper"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Menú de usuario</title>
<link rel="stylesheet" type="text/css" href="css/main.css">
<script type="text/javascript" src="js/jquery-1.9.1.min.js"></script>
<script type="text/javascript">
	var editable = false;

	$(document).ready(function() {
		if ($("#hiddenToken").val() == "")
			window.location.replace("index.jsp");
		else{
			loadUserData();
			$("#userForm").submit(function(event) {
				var form = $(this);
				url = form.attr('action');
				$.ajax({
					url : url,
					type : "PUT",
					headers: { Authorization: $("#hiddenToken").val()},
					data : {
						username : $("#inputUsername").val(),
						password: $("#inputPassword").val(),
						email: $("#inputEmail").val()
					},
					success : function(data, textStatus, jqXHR) {
						jsonResponse = JSON.parse(data);
						if (jsonResponse.result == "OK"){
							toggleEditability();
							alert("Modificado correctamente");
						}
						else
							alert(jsonResponse.data);
					},
					error : function(jqXHR, textStatus, errorThrown) {
						alert("No se pudo completar su solicitud");
					}
				});
				return false;
			});
		}
	});	
	function loadUserData(){
		$.ajax({
			url : "<%=SettingsHelper.REST_API_URL + "/users"%>",
			type : "GET",
			headers : {
				Authorization : $("#hiddenToken").val()
			},
			success : function(data, textStatus, jqXHR) {
				jsonResponse = JSON.parse(data);
				if (jsonResponse.result == "OK") {
					jsonData = JSON.parse(jsonResponse.data);
					$("#inputUsername").val(jsonData.username);
					$("#inputUsername").attr('readonly', true);
					$("#inputEmail").val(jsonData.email);
					$("#inputEmail").attr('readonly', true);
					if (jsonData.role == "ADMIN")
						loadAdminMenu();
				} else
					window.location.replace("index.jsp");
			},
			error : function(jqXHR, textStatus, errorThrown) {
				alert("No se pudo completar su solicitud");
			}
		});
	}

	function toggleEditability() {
		if (editable) {
			$("#inputPassword").css("display", "none");
			$("#submitBtn").css("display", "none");
			$("#inputEmail").attr('readonly', true);
			$("#toggler").html("Modificar");
		} else {
			$("#inputPassword").css("display", "inline");
			$("#submitBtn").css("display", "inline");
			$("#inputEmail").attr('readonly', false);
			$("#toggler").html("Cancelar");
		}
		editable = !editable;
		return false;
	}

	function loadAdminMenu() {
		$.ajax({
			url : "<%=SettingsHelper.REST_API_URL + "/shapes/status"%>",
			type : "GET",
			headers : {
				Authorization : $("#hiddenToken").val()
			},
			success : function(data, textStatus, jqXHR) {
				jsonResponse = JSON.parse(data);
				if (jsonResponse.result == "OK") {
					jsonData = JSON.parse(jsonResponse.data);
					$('#addressesProgress').html(
							jsonData.addressesUploadPercentage);
					$('#busRoutesProgress').html(
							jsonData.busRoutesMaximalUploadPercentage);
					$('#busStopsProgress').html(
							jsonData.busStopsUploadPercentage);
					$('#controlPointsProgress').html(
							jsonData.controlPointsUploadPercentage);
					$('#cornersProgress')
							.html(jsonData.cornersUploadPercentage);
					$('#streetsProgress')
							.html(jsonData.streetsUploadPercentage);
				} else
					alert(jsonResponse.data);
			},
			error : function(jqXHR, textStatus, errorThrown) {
				alert("No se pudo completar su solicitud");
			}
		});
		$("#adminMenu").css("display", "inline");
	}
</script>
</head>
<body>
	<h2>Sus datos:</h2>
	<form method="post" id="userForm"
		action="<%=SettingsHelper.REST_API_URL + "/users"%>">
		<input type="text" id="inputUsername" name="username"><br>
		<input type="email" id="inputEmail" placeholder="Ingrese su email"
			name="email"><br> <input type="password"
			id="inputPassword" placeholder="Ingrese su contraseña"
			name="password" style="display: none"><br> <input
			id="submitBtn" style="display: none" type="submit" value="Enviar!">
	</form>

	<button id="toggler" onclick="toggleEditability();">Modificar</button>

	<div id="adminMenu" style="display: none">
		<h2>Menú de administración</h2>
		<form id="shapefileUpdateForm"
			action="<%=SettingsHelper.REST_API_URL + "/shapes"%>">
			Datos a actualizar: <select id="inputShapefileType">
				<option value="busRoutes">Destinos ómnibus</option>
				<option value="busStops">Paradas ómnibus</option>
				<option value="controlPoints">Puntos de control</option>
				<option value="addresses">Direcciones</option>
				<option value="streets">Calles</option>
				<option value="corners">Esquinas</option>
			</select> <input id="inputFile" type="file" accept="application/zip">
		</form>
		<h3>Estado de subida de datos</h3>
		<button onclick="loadAdminMenu(); return false;">Refrescar</button>
		<table>
			<thead>
				<tr>
					<td>Tipo de datos</td>
					<td>Progreso (%)</td>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>Destinos</td>
					<td id="busRoutesProgress"></td>
				</tr>
				<tr>
					<td>Paradas de ómnibus</td>
					<td id="busStopsProgress"></td>
				</tr>
				<tr>
					<td>Puntos de control</td>
					<td id="controlPointsProgress"></td>
				</tr>
				<tr>
					<td>Calles</td>
					<td id="streetsProgress"></td>
				</tr>
				<tr>
					<td>Esquinas</td>
					<td id="cornersProgress"></td>
				</tr>
				<tr>
					<td>Direcciones</td>
					<td id="addressesProgress"></td>
				</tr>
			</tbody>
		</table>
	</div>
	<input id="hiddenToken" type="hidden" value="<%= request.getSession().getAttribute("token") != null ? request.getSession().getAttribute("token") : "" %>">
</body>
</html>
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
				form = $(this);
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
	
	function uploadShapefile(){
		form = $("#shapefileUpdateForm");
		url = form.attr('action') + $("#inputShapefileType").val();
		formData = new FormData(form[0]);
		$.ajax({
			url : url,
			type : "POST",
			enctype: "multipart/form-data",
			xhr: function() {
	            var myXhr = $.ajaxSettings.xhr();
	            if(myXhr.upload){ // Check if upload property exists
	                myXhr.upload.addEventListener('#shapefileProgress',progressHandlingFunctionShapefile, false); // For handling the progress of the upload
	            }
	            return myXhr;
	        },
			headers: { Authorization: $("#hiddenToken").val()},
			data : formData,
			success : function(data, textStatus, jqXHR) {
				jsonResponse = JSON.parse(data);
				if (jsonResponse.result == "OK"){
					alert("Subido correctamente");
					$("#shapefileProgress").attr("value", 0);
				}
				else
					alert(jsonResponse.data);
			},
			error : function(jqXHR, textStatus, errorThrown) {
				alert("No se pudo completar su solicitud");
			},
			cache: false,
	        contentType: false,
	        processData: false
		});
	}
	
	function progressHandlingFunctionShapefile(e, progressSelector){
	    if(e.lengthComputable){
	        $('#shapefileProgress').attr({value:e.loaded,max:e.total});
	    }
	}
	
	function progressHandlingFunctionCsv(e, progressSelector){
	    if(e.lengthComputable){
	        $('#csvProgress').attr({value:e.loaded,max:e.total});
	    }
	}
	
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
			        url : "<%=SettingsHelper.REST_API_URL + "/information/status"%>",
					type : "GET",
					headers : {
						Authorization : $("#hiddenToken").val()
					},
					success : function(data, textStatus, jqXHR) {
						jsonResponse = JSON.parse(data);
						if (jsonResponse.result == "OK") {
							jsonData = JSON.parse(jsonResponse.data);
							$('#addressesProgress')
									.html(
											Math
													.round(100 * jsonData.addressesUploadPercentage) / 100);
							$('#busRoutesProgress')
									.html(
											Math
													.round(100 * jsonData.busRoutesMaximalUploadPercentage) / 100);
							$('#busStopsProgress')
									.html(
											Math
													.round(100 * jsonData.busStopsUploadPercentage) / 100);
							$('#controlPointsProgress')
									.html(
											Math
													.round(100 * jsonData.controlPointsUploadPercentage) / 100);
							$('#cornersProgress')
									.html(
											Math
													.round(100 * jsonData.cornersUploadPercentage) / 100);
							$('#streetsProgress')
									.html(
											Math
													.round(100 * jsonData.streetsUploadPercentage) / 100);
						} else
							alert(jsonResponse.data);
					},
					error : function(jqXHR, textStatus, errorThrown) {
						alert("No se pudo completar su solicitud");
					}
				});
		$("#adminMenu").css("display", "inline");
	}
	
	function uploadCsv(){
		form = $("#csvUpdateForm");
		url = form.attr('action') + $("#inputCsvType").val();
		formData = new FormData(form[0]);
		$.ajax({
			url : url,
			type : "POST",
			enctype: "multipart/form-data",
			xhr: function() {
	            var myXhr = $.ajaxSettings.xhr();
	            if(myXhr.upload){ // Check if upload property exists
	                myXhr.upload.addEventListener('#csvProgress',progressHandlingFunctionCsv, false); // For handling the progress of the upload
	            }
	            return myXhr;
	        },
			headers: { Authorization: $("#hiddenToken").val()},
			data : formData,
			success : function(data, textStatus, jqXHR) {
				jsonResponse = JSON.parse(data);
				if (jsonResponse.result == "OK"){
					alert("Subido correctamente");
					$("#csvProgress").attr("value", 0);
				}
				else
					alert(jsonResponse.data);
			},
			error : function(jqXHR, textStatus, errorThrown) {
				alert("No se pudo completar su solicitud");
			},
			cache: false,
	        contentType: false,
	        processData: false
		});
	}
</script>
</head>
<body>
	<a class="logout" href="LogoutServlet">Logout</a>
	<br>
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
	*Solamente se puede actualizar un juego de datos a la vez
	<div>
		<h3>Formato shapefile</h3>
		<form id="shapefileUpdateForm" enctype="multipart/form-data"
			action="<%=SettingsHelper.REST_API_URL + "/information"%>">
			Datos a actualizar: <select id="inputShapefileType">
				<option value="/uploadBusRoutesMaximal">Destinos ómnibus</option>
				<option value="/uploadBusStops">Paradas ómnibus</option>
				<option value="/uploadControlPoints">Puntos de control</option>
				<option value="/uploadAddresses">Direcciones</option>
				<option value="/uploadStreets">Calles</option>
				<option value="/uploadCorners">Esquinas</option>
			</select> <input id="inputFile" name="file" type="file"
				accept="application/zip">
		</form>
		<button onclick="uploadShapefile(); return false;">Actualizar</button>
		<progress id="shapefileProgress"></progress>
		<h3>Estado de subida de datos</h3>
		<button onclick="loadAdminMenu(); return false;">Refrescar</button>
		<br> <br>
		<table class="shapefileProgressTable">
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
	<br>
	<div>
	<h3>Formato csv</h3>
	<form id="csvUpdateForm" enctype="multipart/form-data"
			action="<%=SettingsHelper.REST_API_URL + "/information"%>">
			Datos a actualizar: <select id="inputCsvType">
				<option value="/uploadStreetTitles">Títulos de calle</option>
				<option value="/uploadStreetTypes">Tipos de calle</option>
				<option value="/uploadTaxiServices">Servicios de Taxi</option>
			</select> <input id="inputCsvFile" name="file" type="file"
				accept="text/csv">
		</form>
		<button onclick="uploadCsv(); return false;">Actualizar</button>
		<progress id="csvProgress"></progress>
	</div>
	</div>
		<br>
	<a href="city-reports.jsp">Volver al mapa</a>
	<input id="hiddenToken" type="hidden"
		value="<%=request.getSession().getAttribute("token") != null ? request
					.getSession().getAttribute("token") : ""%>">
</body>
</html>
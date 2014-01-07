<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="helpers.SettingsHelper"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Mapa de reportes</title>
<link rel="stylesheet" type="text/css" href="css/main.css">
<script type="text/javascript" src="js/jquery-1.9.1.min.js"></script>
<script src="https://maps.googleapis.com/maps/api/js?sensor=false"></script>
<script type="text/javascript">
	var mMap; 
	var infoWindow;
	var obstacles = new Array();
	var stops = new Array();
	var selectedObstacleMarker;
	var selectedStopMarker;
	var greenIcon = "https://maps.google.com/mapfiles/ms/icons/green-dot.png";
	var blueIcon = "https://maps.google.com/mapfiles/ms/icons/blue-dot.png";
	var showStops = true;
	var showObstacles = true;
	var obstaclesRequest;
	var stopsRequest;
	$(document).ready(function() {
		if ($("#hiddenToken").val() == "")
			window.location.replace("index.jsp");
		else {
			google.maps.event.addDomListener(window, 'load', initializeMap);
			$("#addObstacleForm").submit(function(event) {
				form = $(this);
				url = form.attr('action');
				$.ajax({
					url : url,
					type : "POST",
					headers: { Authorization: $("#hiddenToken").val()},
					data : {
						coordinates : $("#coordinatesInput").val(),
						radius: $("#radiusInput").val(),
						description: $("#descriptionInput").val()
					},
					success : function(data, textStatus, jqXHR) {
						jsonResponse = JSON.parse(data);
						if (jsonResponse.result == "OK"){
							alert("Agregado correctamente");
							placeObstacle(new google.maps.LatLng($("#coordinatesInput").val().split(",")[0], $("#coordinatesInput").val().split(",")[1]), $("#descriptionInput").val(), parseInt($("#radiusInput").val()), jsonResponse.data);
						}
						else
							alert(jsonResponse.data);
					}
				});
				return false;
			});
			$("#searchCoordinatesForm").submit(function(event) {
				form = $(this);
				url = form.attr('action');
				$.ajax({
					url : url,
					type : "GET",
					headers: { Authorization: $("#hiddenToken").val()},
					data : {
						streetName : $("#inputStreet").val(),
						number: $("#inputNumber").val(),
						letter: $("#inputLetter").val()
					},
					success : function(data, textStatus, jqXHR) {
						jsonResponse = JSON.parse(data);
						if (jsonResponse.result == "OK"){
							var jsonCoords = JSON.parse(jsonResponse.data.replace(/\bNaN\b/g, "null"));
							$("#coordinatesInput").val(jsonCoords.x + "," + jsonCoords.y);
						}
						else
							alert(jsonResponse.data);
					}
				});
				return false;
			});
		}
	});

	function initializeMap() {
		mapCanvas = document.getElementById('map_canvas');
		mapOptions = {
			center : new google.maps.LatLng(-34.894573, -56.153088),
			zoom : 13,
			mapTypeId : google.maps.MapTypeId.ROADMAP
		};
		mMap = new google.maps.Map(mapCanvas, mapOptions);
		google.maps.event.addListener(mMap, 'click', function(event) {
		    $("#coordinatesInput").val(event.latLng.lat() + "," + event.latLng.lng());
		  });
		loadObstacles();
		loadStops();
	}
	function placeObstacle(location, description, mRadius, id) {
		  marker = new google.maps.Marker({
		      position: location,
		      title : description,
		      map : mMap
		  });
		  circle = new google.maps.Circle({
			  map: mMap,
			  radius: mRadius,
			  fillColor: '#FF0000'
		  });
		  marker._customId = id;
		  obstacles.push(marker);
		  circle.bindTo('center', marker, 'position');
		  marker._myCircle = circle;
		  addObstacleInfoWindow(marker);
	}
	function placeStop(location, id, active) {
		  var mIcon;
		  if(active)
			  mIcon = greenIcon;
		  else
			  mIcon = blueIcon;
		  marker = new google.maps.Marker({
		      position: location,
		      map : mMap,
		      icon : mIcon
		  });
		  marker._customId = id;
		  marker._customActive = active;
		  stops.push(marker);
		  addStopInfoWindow(marker);
	}
	function addObstacleInfoWindow(marker) {
		if(infoWindow !== undefined)
			infoWindow.close();
        infoWindow = new google.maps.InfoWindow({
            content: "<button style=\"width: 100%;\" onclick=\"deleteObstacle();\">Borrar obstáculo?</button>"
        });
        google.maps.event.addListener(marker, 'click', function () {
            infoWindow.open(mMap, marker);
            selectedObstacleMarker = marker;
        });
    }
	function addStopInfoWindow(marker) {
		if(infoWindow !== undefined)
			infoWindow.close();
        infoWindow = new google.maps.InfoWindow({
            content: "<button onclick=\"switchStopStatus();\">Cambiar estado de la parada?</button>"
        });
        google.maps.event.addListener(marker, 'click', function () {
            infoWindow.open(mMap, marker);
            selectedStopMarker = marker;
        });
    }
	function deleteObstacle(){
		marker = selectedObstacleMarker;
		$.ajax({
			url : "<%=SettingsHelper.REST_API_URL + "/obstacles/"%>" + marker._customId,
			type : "DELETE",
			headers: { Authorization: $("#hiddenToken").val()},
			success : function(data, textStatus, jqXHR) {
				jsonResponse = JSON.parse(data);
				if (jsonResponse.result == "OK"){
					alert("Borrado correctamente");
					infoWindow.close();
					marker.setMap(null);
					marker._myCircle.setMap(null);
					obstacles.splice(obstacles.indexOf(marker), 1);
				}
				else
					alert(jsonResponse.data);
			}
		});
		return false;
	}
	function switchStopStatus(){
		marker = selectedStopMarker;
		$.ajax({
			url : "<%=SettingsHelper.REST_API_URL + "/bus/all/"%>" + marker._customId,
			type : "POST",
			headers: { Authorization: $("#hiddenToken").val()},
			data: {active : !marker._customActive},
			success : function(data, textStatus, jqXHR) {
				jsonResponse = JSON.parse(data);
				if (jsonResponse.result == "OK"){
					alert("Modificado correctamente");
					infoWindow.close();
					marker._customActive = !marker._customActive;
					if(marker._customActive)
						marker.setIcon(greenIcon);
					else
						marker.setIcon(blueIcon);
				}
				else
					alert(jsonResponse.data);
			}
		});
		return false;
	}
	function findObstacle(markerId){
		return findInArray(markerId, obstacles);
	}
	function findStop(stopId){
		return findInArray(stopId, stops);
	}
	function findInArray(id, array){
		for(var i in array){
			if(array[i].customId == id)
				return array[i];
		}
	}
	function loadObstacles(){
		obstaclesRequest = $.ajax({
			url : "<%=SettingsHelper.REST_API_URL + "/obstacles"%>",
			type : "GET",
			headers : {
				Authorization : $("#hiddenToken").val()
			},
			success : function(data, textStatus, jqXHR) {
				jsonResponse = JSON.parse(data);
				if (jsonResponse.result == "OK") {
					jsonData = JSON.parse(jsonResponse.data);
					for(var i in jsonData) {
						jsonObstacle = jsonData[i];
						placeObstacle(new google.maps.LatLng(parseFloat(jsonObstacle.latitude), parseFloat(jsonObstacle.longitude)), jsonObstacle.description, jsonObstacle.radius, jsonObstacle.id);			
					}
					$("#switchObstacles").attr("disabled", false);
				} else
					window.location.replace("index.jsp");
			}
		});
	}
	function loadStops(){
		stopsRequest = $.ajax({
			url : "<%=SettingsHelper.REST_API_URL + "/bus/all/all-stops"%>",
			type : "GET",
			headers : {
				Authorization : $("#hiddenToken").val()
			},
			success : function(data, textStatus, jqXHR) {
				jsonResponse = JSON.parse(data);
				if (jsonResponse.result == "OK") {
					jsonData = JSON.parse(jsonResponse.data);
					for(var i in jsonData) {
						jsonStop = jsonData[i];
						placeStop(new google.maps.LatLng(parseFloat(jsonStop.latitude), parseFloat(jsonStop.longitude)), jsonStop.locationCode, jsonStop.active);			
					}
					$("#switchStops").attr("disabled", false);
				} else
					window.location.replace("index.jsp");
			}
		});
	}
	
	function showHideStops(){
		showStops = !showStops;
		var map;
		if(showStops){
			$("#switchStops").html("Esconder paradas");
			map = mMap;
		}
		else{
			$("#switchStops").html("Mostrar paradas");
			map = null;
		}
		for(var i in stops)
			stops[i].setMap(map);
		return false;
	}
	
	function showHideObstacles(){
		showObstacles = !showObstacles;
		var map;
		if(showObstacles){
			$("#switchObstacles").html("Esconder obstáculos");
			map = mMap;
		}
		else{
			$("#switchObstacles").html("Mostrar obstáculos");
			map = null;
		}
		for(var i in obstacles){
			obstacles[i].setMap(map);
			obstacles[i]._myCircle.setMap(map);
		}
		return false;
	}
	function checkStreets(){
		$("#streets").html("");
		var street = $("#inputStreet").val();
		if(street.length > 2){
			$.ajax({
				url : "<%=SettingsHelper.REST_API_URL + "/addresses/possibleStreets"%>",
				type : "GET",
				headers: { Authorization: $("#hiddenToken").val()},
				data : {
					name : street
				},
				success : function(data, textStatus, jqXHR) {
					jsonResponse = JSON.parse(data);
					if (jsonResponse.result == "OK"){
						jsonStreets = JSON.parse(jsonResponse.data);
						var options = "";
						for(var i in jsonStreets)
							options += "<option value='" + jsonStreets[i] + "'/>";
						$("#streets").html(options);
					}
					else
						alert("No se encontraron calles que satisfagan su búsqueda");
				}
			});
		}
		return false;
	}
</script>
</head>
<body>
	<a class="logout" href="LogoutServlet">Logout</a>
	<br><br>
	<div id="map_canvas"></div>
	<div id="map_info">
	Las paradas y obstáculos pueden tardar unos minutos en cargarse; los botones se habilitarán cuando estén listos.
	<br>
	<button id="switchObstacles" onclick="showHideObstacles();" disabled="true">Esconder obstáculos</button><button id="switchStops" onclick="showHideStops();" disabled="true">Esconder paradas</button>
	</div>
	<h3>Agregar obstáculos</h3>
	Hacer click en el mapa para cargar las coordenadas, o ingresar una calle y número de puerta
	<form id="addObstacleForm" action="<%=SettingsHelper.REST_API_URL + "/obstacles"%>">
		Coordenadas: <input required type="text" id="coordinatesInput" readonly>
		Radio: <input required type="number" min="0" step="1" pattern="\d+" id="radiusInput">
		Descripción: <input required type="text" maxlength="100" id="descriptionInput">
		<br>
		<input type="submit" value="Agregar">
	</form>
	<div id="addressSearchBarDiv">
		<form id="searchCoordinatesForm" action="<%=SettingsHelper.REST_API_URL + "/addresses/addressNumberToCoordinates"%>">
			<input required type="text" id="inputStreet" list="streets" placeholder="Introduzca una calle" onkeyup="checkStreets();"><br>
			<datalist id="streets">
			</datalist>
			<input required type="number" id="inputNumber" min="0" pattern="\d+" placeholder="Número de puerta"><br>
			<input type="text" id="inputLetter" placeholder="Letra"><br>
			<input type="submit" value="Buscar coordenadas">
		</form>
	</div>
	<br>
	<br>
	<br>
	<div class="linksDiv">
		<a href="user.jsp">Menú de usuario</a>
	</div>
	<input id="hiddenToken" type="hidden"
		value="<%=request.getSession().getAttribute("token") != null ? request
					.getSession().getAttribute("token") : ""%>">
</body>
</html>
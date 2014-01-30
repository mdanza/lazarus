<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="helpers.SettingsHelper"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Mapa de reportes</title>
<link rel="stylesheet" type="text/css" href="css/main.css">
<script type="text/javascript" src="js/jquery-1.9.1.min.js"></script>
<script src="https://www.mapquestapi.com/sdk/js/v7.0.s/mqa.toolkit.js?Fmjtd%7Cluur2962nq%2Cbw%3Do5-90rxh6"></script>
<script type="text/javascript">
	var mMap; 
	var infoWindow;
	var obstacles = new Array();
	var stops = new Array();
	var selectedObstacleMarker;
	var selectedStopMarker;
	var greenIcon = new MQA.Icon("images/green.png", 20, 20);
	var redIcon = new MQA.Icon("images/red.png", 20, 20);
	var orangeIcon = new MQA.Icon("images/orange.png", 20, 20);
	var showStops = true;
	var showObstacles = true;
	var obstaclesRequest;
	var stopsRequest;
	var loadingStreetOptions = false;
	$(document).ready(function() {
		if ($("#hiddenToken").val() == "")
			window.location.replace("index.jsp");
		else {
			MQA.EventUtil.observe(window, 'load', initializeMap);
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
							placeObstacle({lat:$("#coordinatesInput").val().split(",")[0], lng:$("#coordinatesInput").val().split(",")[1]}, $("#descriptionInput").val(), parseInt($("#radiusInput").val()), jsonResponse.data);
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
		MQA.withModule('largezoom','insetmapcontrol','mousewheel', 'shapes', function() {
 			var options={
		       elt:document.getElementById('map_canvas'),       
		       zoom:13,                                  
		       latLng:{lat:-34.894573, lng:-56.153088},  
		       mtype:'map',                              
		       bestFitMargin:0,          
		       zoomOnDoubleClick:true
		     };
		    mMap = new MQA.TileMap(options);
		    mMap.addControl(
		        new MQA.LargeZoom(),
		        new MQA.MapCornerPlacement(MQA.MapCorner.TOP_LEFT, new MQA.Size(5,5))
		    );
		    var insetOptions={
		      size:{width:150, height:125},
		      zoom:3,
		      mapType:'map',
		      minimized:true
		    };
		    mMap.addControl(
		  	     new MQA.InsetMapControl(insetOptions),
		  	     new MQA.MapCornerPlacement(MQA.MapCorner.BOTTOM_RIGHT)
		     );
		    mMap.enableMouseWheelZoom();
		    MQA.EventManager.addListener(mMap, 'click', function(event) {
			    $("#coordinatesInput").val(event.ll.getLatitude() + "," + event.ll.getLongitude());
			  });
			loadObstacles();
			loadStops();
		});
	}
	function placeObstacle(location, description, mRadius, id) {
	      marker = new MQA.Poi(location);
		  marker.setRolloverContent(description);
		  if(mRadius != null && mRadius >= 40){
			var circle = new MQA.CircleOverlay();
		    circle.radiusUnit = "KM";
			circle.radius = mRadius/1000;
			circle.shapePoints=[location.lat, location.lng];
			circle.fillColor="#FF6600";
			circle.fillColorAlpha=.2;
			marker._myCircle = circle;
		 	mMap.addShape(circle);
		  }else
			  marker._myCircle = null;
		  marker._customId = id;
		  obstacles.push(marker);
		  addObstacleInfoWindow(marker);
		  marker.setIcon(orangeIcon);
		  mMap.addShape(marker);
	}
	function placeStop(location, id, active) {
		  var mIcon;
		  if(active)
			  mIcon = greenIcon;
		  else
			  mIcon = redIcon;
		  marker = new MQA.Poi(location);
		  marker.setIcon(mIcon);
		  marker._customId = id;
		  marker._customActive = active;
		  stops.push(marker);
		  addStopInfoWindow(marker);
		  mMap.addShape(marker);
	}
	function addObstacleInfoWindow(marker) {
		marker.setInfoContentHTML("<button style=\"width: 100%;\" onclick=\"deleteObstacle();\">Borrar obstáculo?</button>");
		MQA.EventManager.addListener(marker, 'rolloveropen', function(){
			selectedObstacleMarker = marker;
		});
    }
	function addStopInfoWindow(marker) {
		marker.setInfoContentHTML("<button style=\"width: 100%;\" onclick=\"switchStopStatus();\">Cambiar estado de la parada?</button>");
		MQA.EventManager.addListener(marker, 'rolloveropen', function(){
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
					mMap.removeShape(marker);
					if(marker._myCircle != null)
						mMap.removeShape(marker._myCircle);
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
					//infoWindow.close();
					marker._customActive = !marker._customActive;
					if(marker._customActive)
						marker.setIcon(greenIcon);
					else
						marker.setIcon(redIcon);
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
						placeObstacle({lat:parseFloat(jsonObstacle.latitude), lng:parseFloat(jsonObstacle.longitude)}, jsonObstacle.description, jsonObstacle.radius, jsonObstacle.id);			
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
						placeStop({lat:parseFloat(jsonStop.latitude), lng:parseFloat(jsonStop.longitude)}, jsonStop.locationCode, jsonStop.active);			
					}
					$("#switchStops").attr("disabled", false);
				} else
					window.location.replace("index.jsp");
			}
		});
	}
	
	function showHideStops(){
		showStops = !showStops;
		if(showStops){
			$("#switchStops").html("Esconder paradas");
			for(var i in stops){
				if(stops[i]._customActive == true)
					stops[i].setIcon(greenIcon);
				else
					stops[i].setIcon(redIcon);
				mMap.addShape(stops[i]);
			}
		}
		else{
			$("#switchStops").html("Mostrar paradas");
			for(var i in stops)
				mMap.removeShape(stops[i]);
		}
		return false;
	}
	
	function showHideObstacles(){
		showObstacles = !showObstacles;
		if(showObstacles){
			$("#switchObstacles").html("Esconder obstáculos");
			for(var i in obstacles){
				obstacles[i].setIcon(orangeIcon);
				mMap.addShape(obstacles[i]);
				if(obstacles[i]._myCircle != null)
					mMap.addShape(obstacles[i]._myCircle);
			}
		}
		else{
			$("#switchObstacles").html("Mostrar obstáculos");
			for(var i in obstacles){
				mMap.removeShape(obstacles[i]);
				if(obstacles[i]._myCircle != null)
					mMap.removeShape(obstacles[i]._myCircle);
			}
		}
		return false;
	}
	function checkStreets(){
		var street = $("#inputStreet").val();
		if(street.length > 2 && loadingStreetOptions == false){
			loadingStreetOptions = true;
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
						loadingStreetOptions = false;
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
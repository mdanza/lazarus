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
	var markers = new Array();
	var selectedMarker;
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
							placeMarker(new google.maps.LatLng($("#coordinatesInput").val().split(",")[0], $("#coordinatesInput").val().split(",")[1]), $("#descriptionInput").val(), parseInt($("#radiusInput").val()), jsonResponse.data);
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

	function initializeMap() {
		mapCanvas = document.getElementById('map_canvas');
		mapOptions = {
			center : new google.maps.LatLng(-34.876918, -56.166256),
			zoom : 12,
			mapTypeId : google.maps.MapTypeId.ROADMAP
		};
		mMap = new google.maps.Map(mapCanvas, mapOptions);
		google.maps.event.addListener(mMap, 'click', function(event) {
		    $("#coordinatesInput").val(event.latLng.lat() + "," + event.latLng.lng());
		  });
		loadObstacles();
	}
	function placeMarker(location, description, mRadius, id) {
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
		  markers.push(marker);
		  circle.bindTo('center', marker, 'position');
		  marker._myCircle = circle;
		  addInfoWindow(marker);
	}
	function addInfoWindow(marker) {
		if(infoWindow !== undefined)
			infoWindow.close();
        infoWindow = new google.maps.InfoWindow({
            content: "<button onclick=\"deleteObstacle();\">Borrar obstáculo?</button>"
        });
        google.maps.event.addListener(marker, 'click', function () {
            infoWindow.open(mMap, marker);
            selectedMarker = marker;
        });
    }
	function deleteObstacle(){
		marker = selectedMarker;
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
					markers = markers.splice(markers.indexOf(marker), 1);
				}
				else
					alert(jsonResponse.data);
			},
			error : function(jqXHR, textStatus, errorThrown) {
				alert("No se pudo completar su solicitud");
			}
		});
		return false;
	}
	function findMarker(markerId){
		for(var i in markers){
			if(markers[i].customId == markerId)
				return markers[i];
		}
	}
	function loadObstacles(){
		$.ajax({
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
						placeMarker(new google.maps.LatLng(parseFloat(jsonObstacle.latitude), parseFloat(jsonObstacle.longitude)), jsonObstacle.description, jsonObstacle.radius, jsonObstacle.id);			
					}
				} else
					window.location.replace("index.jsp");
			},
			error : function(jqXHR, textStatus, errorThrown) {
				alert("No se pudo completar su solicitud");
			}
		});
	}
	
</script>
</head>
<body>
	<div id="addressSearchBarDiv"></div>
	<div id="map_canvas"></div>
	<h3>Agregar obstáculos</h3>
	Hacer click en el mapa para cargar las coordenadas
	<form id="addObstacleForm" action="<%=SettingsHelper.REST_API_URL + "/obstacles"%>">
		Coordenadas: <input required type="text" id="coordinatesInput" readonly>
		Radio: <input required type="number" min="0" step="1" pattern="\d+" id="radiusInput">
		Descripción: <input required type="text" maxlength="100" id="descriptionInput">
		<input type="submit" value="Agregar">
	</form>
	<div class="linksDiv">
		<a href="user.jsp">Menú de usuario</a>
	</div>
	<input id="hiddenToken" type="hidden"
		value="<%=request.getSession().getAttribute("token") != null ? request
					.getSession().getAttribute("token") : ""%>">
</body>
</html>
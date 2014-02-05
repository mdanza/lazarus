<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="helpers.SettingsHelper"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Lazarus</title>
    <!-- Bootstrap -->
    <link href="assets/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="assets/css/sticky-footer-navbar.css" rel="stylesheet">
    <link href="assets/css/toastr.css" rel="stylesheet">
    <link href="assets/css/maps.css" rel="stylesheet">
    <link href="assets/css/signin.css" rel="stylesheet">
</head>
<body>
<div id="wrap" class="center-block">
<nav class="navbar navbar-default" role="navigation">
  <div class="container-fluid">
    <!-- Brand and toggle get grouped for better mobile display -->
    <div class="navbar-header">
      <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a class="navbar-brand" href="#">Lazarus</a>
    </div>
    <!-- Collect the nav links, forms, and other content for toggling -->
    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
      <ul class="nav navbar-nav navbar-right">
        <li class="active"><a href="city-reports.jsp">Mapa</a></li>
        <li><a href="user.jsp">Usuario</a></li>
        <li><a href="thanks.jsp">Agradecimientos</a></li>
        <li><a href="admin.jsp">Admimistración</a></li>
      </ul>
    </div><!-- /.navbar-collapse -->
  </div><!-- /.container-fluid -->
</nav>
<div class="container">
  <div class="page-header">
  	<h1>Mapa de Montevideo <small>Obstáculos y paradas</small></h1>
  </div>
</div>
<div class="container">
	<div id="map_canvas"></div>
</div>
<div class="container">
	<button class="btn btn-link" data-toggle="modal" data-target="#searchCoordsModal">Agregar por dirección</button>
	<button class="btn btn-info" data-loading-text="Cargando obstáculos.." id="switchObstacles" onclick="showHideObstacles();">Esconder obstáculos</button>
    <button class="btn btn-info" data-loading-text="Cargando paradas.." id="switchStops" onclick="showHideStops();">Esconder paradas</button>
</div>
<div class="container"></div>
</div>
<div id="footer">
   <div class="container">
     <p class="text-muted">Lazarus</p>
   </div>
</div>
<div class="modal fade" id="addObstacleModal">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title">Agregar obstáculo</h4>
      </div>
      <form id="addObstacleForm" role="form" class="form-signin" action="<%=SettingsHelper.REST_API_URL + "/obstacles"%>">
		<input placeholder="Coordenadas" required="" class="form-control" type="text" id="coordinatesInput" readonly="">
		<input placeholder="Radio" required="" class="form-control" type="number" min="0" step="1" pattern="\d+" id="radiusInput">
		<input placeholder="Descripción" required="" class="form-control" type="text" maxlength="100" id="descriptionInput">
	  </form>
      <div class="modal-footer">
        <button type="button" class="btn btn-danger" data-dismiss="modal">Cancelar</button>
        <button id="addObstcaleBtn" data-loading-text="Espere.." type="button" onclick="addObstacle();" class="btn btn-success">Agrega el obstáculo</button>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
<div class="modal fade" id="searchCoordsModal">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title">Agregar obstáculo por dirección</h4>
      </div>
      <form id="searchCoordinatesForm" class="form-signin" role="form" action="<%=SettingsHelper.REST_API_URL + "/addresses/addressNumberToCoordinates"%>">
		    <div class="input-group">
		      <input required="" autofocus="" class="form-control" type="text" id="inputStreet" placeholder="Introduzca una calle" onkeyup="checkStreets();">
		      <div class="input-group-btn custom-dropdown-button">
		        <button id="dropdownBtn" type="button" data-loading-text="..." class="btn btn-primary dropdown-toggle" data-toggle="dropdown">
				    <span class="caret"></span>
				    <span class="sr-only">Toggle Dropdown</span>
			  	</button>
			  	<ul id="streetsDropdown" class="dropdown-menu" role="menu">
			  	</ul>
		      </div><!-- /btn-group -->
		    </div><!-- /input-group -->
			<input required="" class="form-control" type="number" id="inputNumber" min="0" pattern="\d+" placeholder="Número de puerta">
			<input type="text" class="form-control" id="inputLetter" placeholder="Letra">
    </form>
      <div class="modal-footer">
        <button type="button" class="btn btn-danger" data-dismiss="modal">Cancelar</button>
        <button id="searchCoordsBtn" onclick="searchCoords();" data-loading-text="Espere.." class="btn btn-success" type="button">Buscar coordenadas</button>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
<input id="hiddenToken" type="hidden"
		value="<%=request.getSession().getAttribute("token") != null ? request
					.getSession().getAttribute("token") : ""%>">
<script src="assets/js/jquery-1.9.1.min.js"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="assets/bootstrap/js/bootstrap.min.js"></script>
<script src="assets/js/toastr.min.js"></script>
<script src="https://www.mapquestapi.com/sdk/js/v7.0.s/mqa.toolkit.js?Fmjtd%7Cluur2962nq%2Cbw%3Do5-90rxh6"></script>
<script type="text/javascript">
	toastr.options = {
		  "closeButton": false,
		  "debug": false,
		  "positionClass": "toast-top-right",
		  "onclick": null,
		  "showDuration": "300",
		  "hideDuration": "1000",
		  "timeOut": "5000",
		  "extendedTimeOut": "1000",
		  "showEasing": "swing",
		  "hideEasing": "linear",
		  "showMethod": "fadeIn",
		  "hideMethod": "fadeOut"
		};
	var mMap; 
	var infoWindow;
	var obstacles = new Array();
	var stops = new Array();
	var selectedObstacleMarker;
	var selectedStopMarker;
	var greenIcon = new MQA.Icon("assets/images/green.png", 20, 20);
	var redIcon = new MQA.Icon("assets/images/red.png", 20, 20);
	var orangeIcon = new MQA.Icon("assets/images/orange.png", 20, 20);
	var showStops = true;
	var showObstacles = true;
	var obstaclesRequest;
	var stopsRequest;
	var loadingStreetOptions = false;
	var stopsPageNumber = -1;
	var finishedLoadingStops = false;
	var stopsLoadingTimer;
	
	$(document).ready(function() {
		if ($("#hiddenToken").val() == "")
			window.location.replace("index.jsp");
		else {
			MQA.EventUtil.observe(window, 'load', initializeMap);
			MQA.EventUtil.observe(window, 'resize', initializeMap);
		}
	});
	
	function searchCoords(){
		form = $("#searchCoordinatesForm");
		url = form.attr('action');
		var street = $("#inputStreet").val();
		var num = $("#inputNumber").val();
		if(street === ""){
			toastr.error("La calle no puede estar vacía");
			return;
		}
		if(isNaN(parseInt(num)) || parseInt(num) < 1){
			toastr.error("Número de puerta no puede ser vacío ni menor a 1");
			return;
		}
		$("#searchCoordsBtn").button('loading');
		$.ajax({
			url : url,
			type : "GET",
			headers: { Authorization: $("#hiddenToken").val()},
			data : {
				streetName : street,
				number: num,
				letter: $("#inputLetter").val()
			},
			success : function(data, textStatus, jqXHR) {
				jsonResponse = JSON.parse(data);
				if (jsonResponse.result == "OK"){
					var jsonCoords = JSON.parse(jsonResponse.data.replace(/\bNaN\b/g, "null"));
					$("#coordinatesInput").val(jsonCoords.x + "," + jsonCoords.y);
					$('#searchCoordsModal').modal('hide');
					$('#addObstacleModal').modal();
					$("#searchCoordsBtn").button('reset');
				}
				else{
					$("#searchCoordsBtn").button('reset');
					toastr.error(jsonResponse.data);
				}
			}
		});
	}
	
	function addObstacle(){
		form = $("#addObstacleForm");
		url = form.attr('action');
		var coords = $("#coordinatesInput").val();
		var radius = $("#radiusInput").val();
		var description = $("#descriptionInput").val();
		if(isNaN(parseInt(radius)) || parseInt(radius) < 1){
			toastr.error("Radio debe ser mayor o igual a 1");
			return;
		}
		if(description === "" || description.length > 120){
			toastr.error("Descripción no puede estar vacío y debe ser menor a 120 caracteres");
			return;
		}
		$.ajax({
			url : url,
			type : "POST",
			headers: { Authorization: $("#hiddenToken").val()},
			data : {
				coordinates : coords,
				radius: radius,
				description: description
			},
			success : function(data, textStatus, jqXHR) {
				jsonResponse = JSON.parse(data);
				if (jsonResponse.result == "OK"){
					toastr.success("Agregado correctamente");
					$('#addObstacleModal').modal('hide');
					placeObstacle({lat:$("#coordinatesInput").val().split(",")[0], lng:$("#coordinatesInput").val().split(",")[1]}, $("#descriptionInput").val(), parseInt($("#radiusInput").val()), jsonResponse.data);
				}
				else
					toastr.error(jsonResponse.data);
			}
		});
	}

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
			    $('#addObstacleModal').modal();
			  });
			loadObstacles();
			stopsLoadingTimer = setInterval(loadStops, 2000);
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
		marker.setInfoContentHTML("<button type=\"button\" class=\"btn btn-danger\" onclick=\"deleteObstacle();\">Borrar obstáculo?</button>");
		MQA.EventManager.addListener(marker, 'rolloveropen', function(){
			selectedObstacleMarker = marker;
		});
    }
	function addStopInfoWindow(marker) {
		marker.setInfoContentHTML("<button type=\"button\" class=\"btn btn-danger\" onclick=\"switchStopStatus();\">Cambiar estado de la parada?</button>");
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
					toastr.success("Borrado correctamente");
					mMap.removeShape(marker);
					if(marker._myCircle != null)
						mMap.removeShape(marker._myCircle);
					obstacles.splice(obstacles.indexOf(marker), 1);
				}
				else
					toastr.error(jsonResponse.data);
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
					toastr.success("Modificado correctamente");
					//infoWindow.close();
					marker._customActive = !marker._customActive;
					if(marker._customActive)
						marker.setIcon(greenIcon);
					else
						marker.setIcon(redIcon);
				}
				else
					toastr.error(jsonResponse.data);
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
		$("#switchObstacles").button('loading');
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
					$("#switchObstacles").button('reset');
				} else
					window.location.replace("index.jsp");
			}
		});
	}
	function loadStops(){
		stopsPageNumber = stopsPageNumber + 1; 
		$("#switchStops").button('loading');
		stopsRequest = $.ajax({
			url : "<%=SettingsHelper.REST_API_URL + "/bus/all/all-stops"%>" + "?page=" + stopsPageNumber,
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
				} else{
					$("#switchStops").button('reset');
					clearInterval(stopsLoadingTimer);
				}
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
		$("#dropdownBtn").button('loading');
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
							options += "<li><a href=\"javascript:clickedStreetOption('" + jsonStreets[i] + "');\"</a>" + jsonStreets[i] + "</li>";
						$("#streetsDropdown").html(options);
						$("#dropdownBtn").button('reset');
						setTimeout(function(){
							$("#dropdownBtn").click();
						}, 500);
						loadingStreetOptions = false;
					}
					else{
						toastr.error("No se encontraron calles que satisfagan su búsqueda");
						$("#dropdownBtn").button('reset');
					}
				}
			});
		}
		return false;
	}
	
	function clickedStreetOption(street){
		$("#inputStreet").val(street);
	}
</script>
</body>
</html>
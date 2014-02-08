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
	var servicesUrl;
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
		servicesUrl = $("#servicesUrl").val();
		if ($("#hiddenToken").val() == "")
			window.location.replace("index.jsp");
		else {
			MQA.EventUtil.observe(window, 'load', initializeMap);
			MQA.EventUtil.observe(window, 'resize', resizeMapWidth);
		}
	});
	
	function resizeMapWidth(){
		var mapCanvas = $('#map_canvas');
		mapCanvas.children('div:first-child').css('width', mapCanvas.css('width'));
	}
	
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
					toastr.error("No se encontró la dirección ingresada");
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
			url : servicesUrl + "/obstacles/" + marker._customId,
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
			url : servicesUrl + "/bus/all/" + marker._customId,
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
			url : servicesUrl + "/obstacles",
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
			url : servicesUrl + "/bus/all/all-stops" + "?page=" + stopsPageNumber,
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
					loadStops();
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
				url : servicesUrl + "/addresses/possibleStreets",
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
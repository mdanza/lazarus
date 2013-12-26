<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Mapa de reportes</title>
<link rel="stylesheet" type="text/css" href="css/main.css">
<script type="text/javascript" src="js/jquery-1.9.1.min.js"></script>
<script src="https://maps.googleapis.com/maps/api/js?sensor=false"></script>
<script type="text/javascript">
	$(document).ready(function() {
		if ($("#hiddenToken").val() == "")
			window.location.replace("index.jsp");
		else {
			google.maps.event.addDomListener(window, 'load', initializeMap);
		}
	});

	function initializeMap() {
		mapCanvas = document.getElementById('map_canvas');
		mapOptions = {
			center : new google.maps.LatLng(-34.876918, -56.166256),
			zoom : 12,
			mapTypeId : google.maps.MapTypeId.ROADMAP
		};
		map = new google.maps.Map(mapCanvas, mapOptions);
	}
</script>
</head>
<body>
	<div id="addressSearchBarDiv"></div>
	<div id="map_canvas"></div>
	<div class="linksDiv">
		<a href="user.jsp">Menú de usuario</a>
	</div>
	<input id="hiddenToken" type="hidden"
		value="<%=request.getSession().getAttribute("token") != null ? request
					.getSession().getAttribute("token") : ""%>">
</body>
</html>
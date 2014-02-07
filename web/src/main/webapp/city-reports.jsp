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
    <link href="assets/css/custom.css" rel="stylesheet">
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
     <a href="LogoutServlet" class="text-muted"><span class="pull-right">Logout</span></a>
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
<input type="hidden" id="servicesUrl" value="<%=SettingsHelper.REST_API_URL%>"/>
<script src="assets/js/jquery-1.9.1.min.js"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="assets/bootstrap/js/bootstrap.min.js"></script>
<script src="assets/js/toastr.min.js"></script>
<script src="https://www.mapquestapi.com/sdk/js/v7.0.s/mqa.toolkit.js?Fmjtd%7Cluur2962nq%2Cbw%3Do5-90rxh6"></script>
<script src="assets/js/city-reports.js"></script>
</body>
</html>
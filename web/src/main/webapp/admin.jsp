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
    <link href="assets/css/signin.css" rel="stylesheet">
    <link href="assets/css/custom.css" rel="stylesheet">
</head>
<body>
<div id="wrap">
<div class="container-fluid center-block">
<div class="row">
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
	        <li><a href="city-reports.jsp">Mapa</a></li>
	        <li><a href="user.jsp">Usuario</a></li>
	        <li><a href="thanks.jsp">Agradecimientos</a></li>
	        <li class="active"><a href="admin.jsp">Admimistración</a></li>
	      </ul>
	    </div><!-- /.navbar-collapse -->
	  </div><!-- /.container-fluid -->
	</nav>
</div>
<div class="row">
	<div class="table-responsive col-lg-5">
		<h3>Estado de subida de datos</h3>
			<table class="table table-hover table-striped">
				<thead>
					<tr>
						<td>Tipo de datos</td>
						<td>Progreso de actualización (%)</td>
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
			<button type="button" class="btn btn-primary btn-block" onclick="loadAdminMenu(); return false;">Refrescar</button>
	</div>
	<div class="col-lg-5">
		<!-- Nav tabs -->
		<ul class="nav nav-tabs">
		  <li class="active"><a href="#shapefileTab" data-toggle="tab">Shapefile</a></li>
		  <li><a href="#csvTab" data-toggle="tab">CSV</a></li>
		</ul>
		
		<!-- Tab panes -->
		<div class="tab-content">
		  <div id="shapefileTab" class="tab-pane fade in active">
		      <form class="form-signin" role="form" method="post" id="shapefileUpdateForm" enctype="multipart/form-data"
					action="<%=SettingsHelper.REST_API_URL + "/information"%>">
		        <h2 class="form-signin-heading">Shapefiles</h2>
		        <div class="input-group">
		      		<input required="" id="selectedShapefile" class="form-control" type="text" readonly="" placeholder="Elija un tipo de archivo">
		      		<input type="hidden" id="selectedShapefileType" value="">
			        <div class="input-group-btn custom-dropdown-button">
					  <button class="btn btn-primary dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown">
					    <span class="caret"></span>
					    <span class="sr-only">Toggle Dropdown</span>
					  </button>
					  <ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu1">
					    <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript: selectShapefileType('/uploadBusRoutesMaximal','Destinos ómnibus');">Destinos ómnibus</a></li>
					    <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript: selectShapefileType('/uploadBusStops','Paradas ómnibus');">Paradas ómnibus</a></li>
					    <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript: selectShapefileType('/uploadAddresses','Direcciones');">Direcciones</a></li>
					    <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript: selectShapefileType('/uploadStreets','Calles');">Calles</a></li>
					    <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript: selectShapefileType('/uploadCorners','Esquinas');">Esquinas</a></li>
					  </ul>
				    </div>
			   </div>
		        <input id="inputFile" name="file" class="filestyle" data-classButton="btn btn-default" type="file" required="" accept="application/zip">
		        <button data-loading-text="Espere.." class="btn btn-lg btn-primary btn-block" type="submit" onclick="uploadShapefile(); return false;">Actualizar</button>
		      </form>
		  </div>
		  <div id="csvTab" class="tab-pane fade">
		      <form class="form-signin" role="form" method="post" id="csvUpdateForm" enctype="multipart/form-data"
					action="<%=SettingsHelper.REST_API_URL + "/information"%>">
		        <h2 class="form-signin-heading">CSV</h2>
		        <div class="input-group">
		      		<input required="" id="selectedCsvfile" class="form-control" type="text" readonly="" placeholder="Elija un tipo de archivo">
		      		<input type="hidden" id="selectedCsvType" value="">
			        <div class="input-group-btn custom-dropdown-button">
					  <button class="btn btn-primary dropdown-toggle" type="button" id="dropdownMenu2" data-toggle="dropdown">
					    <span class="caret"></span>
					    <span class="sr-only">Toggle Dropdown</span>
					  </button>
					  <ul class="dropdown-menu" role="menu" aria-labelledby="dropdownMenu2">
					    <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript: selectCsvType('/uploadStreetTitles','Títulos de calle');">Títulos de calle</a></li>
					    <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript: selectCsvType('/uploadStreetTypes','Tipos de calle');">Tipos de calle</a></li>
					    <li role="presentation"><a role="menuitem" tabindex="-1" href="javascript: selectCsvType('/uploadTaxiServices','Servicios de Taxi');">Servicios de Taxi</a></li>
					  </ul>
				   	</div>
				</div>
			   <input id="inputCsvFile" name="file" type="file" class="filestyle" data-classButton="btn btn-default" required="" accept="text/csv">
			   <button data-loading-text="Espere.." class="btn btn-lg btn-primary btn-block" onclick="uploadCsv(); return false;">Actualizar</button>
		      </form>
		  </div>
		</div>
</div>
</div>
</div>
</div>
<div id="footer">
   <div class="container">
     <a href="LogoutServlet" class="text-muted"><span class="pull-right">Logout</span></a>
   </div>
</div>
<input id="hiddenToken" type="hidden"
		value="<%=request.getSession().getAttribute("token") != null ? request
					.getSession().getAttribute("token") : ""%>">
<input type="hidden" id="servicesUrl" value="<%=SettingsHelper.REST_API_URL%>"/>
<script src="assets/js/jquery-1.9.1.min.js"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="assets/bootstrap/js/bootstrap.min.js"></script>
<script src="assets/js/toastr.min.js"></script>
<script src="assets/js/admin.js"></script>
<script src="assets/js/bootstrap-filestyle.min.js"></script>
</body>
</html>
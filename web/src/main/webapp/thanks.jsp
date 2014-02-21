<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Lazarus</title>
    <!-- Bootstrap -->
    <link href="assets/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="assets/css/sticky-footer-navbar.min.css" rel="stylesheet">
</head>
<body>
<div id="wrap">
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
        <li class="active"><a href="thanks.jsp">Agradecimientos</a></li>
      </ul>
    </div><!-- /.navbar-collapse -->
  </div><!-- /.container-fluid -->
</nav>
<div class="container">
		<div class="jumbotron">
			<h1>Agradecemos a:</h1>
			<p>
				<a href="http://www.graphicsfuel.com">http://www.graphicsfuel.com</a>
				por sus íconos
			</p>
			<p>
				<a href="http://www.mapquest.com/">MapQuest</a><img src="http://developer.mapquest.com/content/osm/mq_logo.png"> por sus
				servicios abiertos de navegación, y sus mapas basados en <a href="http://www.openstreetmap.org/">OpenStreetMap</a>  
			</p>
			<p>
				<a href="http://www.montevideo.gub.uy">http://www.montevideo.gub.uy/</a>
				por sus datos abiertos
			</p>
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
<script src="assets/js/jquery-1.9.1.min.js"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="assets/bootstrap/js/bootstrap.min.js"></script>
<script src="assets/js/thanks.min.js"></script>
</body>
</html>
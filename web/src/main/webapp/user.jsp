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
    <link href="assets/css/sticky-footer-navbar.min.css" rel="stylesheet">
    <link href="assets/css/toastr.min.css" rel="stylesheet">
    <link href="assets/css/signin.min.css" rel="stylesheet">
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
        <li class="active"><a href="user.jsp">Usuario</a></li>
        <li><a href="thanks.jsp">Agradecimientos</a></li>
      </ul>
    </div><!-- /.navbar-collapse -->
  </div><!-- /.container-fluid -->
</nav>
<div class="container">
      <form class="form-signin" role="form" method="post" id="userForm" action="<%=SettingsHelper.REST_API_URL + "/users"%>">
        <h2 class="form-signin-heading">Tus datos:</h2>
        <input id="inputUsername" type="text" class="form-control" placeholder="Usuario" required="">
        <input id="inputEmail" type="email" class="form-control" placeholder="Ingrese su email" required="">
        <input id="inputPassword" type="password" class="form-control hidden" placeholder="Nueva contraseña" required="">
        <button id="toggler" type="button" class="btn btn-block btn-lg btn-info" onclick="toggleEditability();">Modificar</button>
        <button id="submitBtn" data-loading-text="Espere.." class="btn btn-lg btn-success btn-block hidden" type="submit">Enviar</button>
      </form>
      <button class="btn btn-block btn-link" type="submit" data-toggle="modal" data-target="#confirmDeactivateAccountModal">Desactivar cuenta</button>
</div>
</div>
<div id="footer">
   <div class="container">
     <a href="LogoutServlet" class="text-muted"><span class="pull-right">Logout</span></a>
   </div>
</div>
<div class="modal fade" id="confirmDeactivateAccountModal">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title">Desactivar cuenta</h4>
      </div>
      <h4 class="text-danger text-center">Está seguro de que desea desactivar su cuenta?</h4>
      <div class="modal-footer">
        <button type="button" class="btn btn-danger" data-dismiss="modal">Cancelar</button>
        <button id="deactivateAccountBtn" data-loading-text="Espere.." type="button" onclick="deactivateAccount();" class="btn btn-success">Desactivar</button>
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
<script src="assets/js/plugins/toastr.min.js"></script>
<script src="assets/js/user.min.js"></script>
</body>
</html>
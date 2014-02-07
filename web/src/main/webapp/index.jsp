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
	<link href="assets/css/signin.css" rel="stylesheet">
	<link href="assets/css/toastr.css" rel="stylesheet">
</head>
<body>
<nav class="navbar navbar-default" role="navigation">
  <div class="container-fluid">
    <!-- Brand and toggle get grouped for better mobile display -->
    <div class="navbar-header">
      <a class="navbar-brand" href="#">Lazarus</a>
    </div>
  </div><!-- /.container-fluid -->
</nav>
<div class="container">
      <form class="form-signin" role="form" method="post" id="loginForm" action="<%=SettingsHelper.REST_API_URL + "/users/login"%>">
        <h2 class="form-signin-heading">Ingresa tus datos</h2>
        <input id="inputUsername" type="text" class="form-control" placeholder="Usuario" required="" autofocus="">
        <input id="inputPassword" type="password" class="form-control" placeholder="Contraseña" required="">
        <button id="loginBtn" data-loading-text="Espere.." class="btn btn-lg btn-primary btn-block" type="submit">Login</button>
        <button id="forgotPasswordBtn" type="button" class="btn btn-link" data-toggle="tooltip" data-placement="bottom" title="Ingrese su nombre de usuario" onclick="forgotPassword(); return false;">Olvidó su contraseña?</button>
        <button type="button" class="btn btn-link" data-toggle="modal" data-target="#newUserModal"><strong>Registrate!</strong></button>
      </form>
</div>
<div class="modal fade" id="newUserModal">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title">Registrarse</h4>
      </div>
      <form class="form-signin" role="form" method="post" id="newUserForm" action="<%=SettingsHelper.REST_API_URL + "/users"%>">
        <input id="inputNewUsername" name="username" type="text" class="form-control" placeholder="Usuario" required="">
        <input type="email" id="inputNewEmail" class="form-control" placeholder="Email" name="email" required="">
        <input id="inputNewPassword" type="password" class="form-control" placeholder="Contraseña" required="" name="password">
      </form>
      <div class="modal-footer">
        <button type="button" class="btn btn-danger" data-dismiss="modal">Cancelar</button>
        <button id="registerBtn" data-loading-text="Espere.." type="button" onclick="registerUser();" class="btn btn-success">Crea tu cuenta</button>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
<input type="hidden" id="servicesUrl" value="<%=SettingsHelper.REST_API_URL%>"/>
<script src="assets/js/jquery-1.9.1.min.js"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="assets/bootstrap/js/bootstrap.min.js"></script>
<script src="assets/js/toastr.min.js"></script>
<script src="assets/js/index.js"></script>
</body>
</html>
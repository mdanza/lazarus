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
<script src="assets/js/jquery-1.9.1.min.js"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="assets/bootstrap/js/bootstrap.min.js"></script>
<script src="assets/js/toastr.min.js"></script>
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
	$(document).ready(function() {
		$('#forgotPasswordBtn').tooltip();
		setCustomValidationMessages();
		$("#loginForm").submit(function(event) {
			$('#loginBtn').button('loading');
			var form = $(this);
			url = form.attr('action');
			var posting = $.post(url, {
				username : $("#inputUsername").val(),
				password : $("#inputPassword").val()
			});
			posting.done(function(response) {
				jsonResponse = JSON.parse(response);
				if (jsonResponse.result == "OK") {
					$.ajax({
						url : "LoginServlet",
						type : "POST",
						data : {
							token : jsonResponse.data
						},
						success : function(data, textStatus, jqXHR) {
							$('#loginBtn').button('reset');
							if (data == "OK")
								window.location.href = "city-reports.jsp";
							else{
								toastr.error("No se pudo completar su solicitud");
								$('#loginBtn').button('reset');
							}
						},
						error : function(jqXHR, textStatus, errorThrown) {
							toastr.error("No se pudo completar su solicitud");
							$('#loginBtn').button('reset');
						}
					});
				} else{
					$('#loginBtn').button('reset');
					toastr.error(jsonResponse.data);
					}
			});
			return false;
		});
	});
	
	function registerUser() {
			$('#registerBtn').button('loading');
			var form = $("#newUserForm");
			url = form.attr('action');
			var username = $("#inputNewUsername");
			var password = $("#inputNewPassword");
			var email = $("#inputNewEmail");
			if(username.get(0).checkValidity() & password.get(0).checkValidity() & email.get(0).checkValidity()){
				//check username
				$.ajax({
					url : "<%=SettingsHelper.REST_API_URL + "/users/username/"%>" + username.val(),
					type : "GET",
					success : function(data, textStatus, jqXHR) {
						jsonResponse = JSON.parse(data);
						if (jsonResponse.result == "OK" && jsonResponse.data == "false"){
							//check email
							$.ajax({
								url : "<%=SettingsHelper.REST_API_URL + "/users/email/"%>" + email.val(),
								type : "GET",
								success : function(data, textStatus, jqXHR) {
									jsonResponse = JSON.parse(data);
									if (jsonResponse.result == "OK" && jsonResponse.data == "false"){
										var posting = $.post(url, {
											username : username.val(),
											password : password.val(),
											email : email.val()
										});
										posting.done(function(response) {
											jsonResponse = JSON.parse(response);
											if (jsonResponse.result == "OK") {
												toastr.success("Usuario creado con éxito");
												$("#inputUsername").val($("#inputNewUsername").val());
												$("#inputPassword").val($("#inputNewPassword").val());
												$("#loginForm").submit();
											} else{
												$('#registerBtn').button('reset');
												toastr.error(jsonResponse.data);
											}
										});
									}
									else{
										$('#registerBtn').button('reset');
										toastr.error("Email ya está en uso");
									}
								},
								error : function(jqXHR, textStatus, errorThrown) {
									$('#registerBtn').button('reset');
									toastr.error("No se pudo completar su solicitud");
								}
							});
						}
						else{
							$('#registerBtn').button('reset');
							toastr.error("Nombre de usuario ya está en uso");
						}
					},
					error : function(jqXHR, textStatus, errorThrown) {
						toastr.error("No se pudo completar su solicitud");
					}
				});
			}else
				$('#registerBtn').button('reset');
			return false;
	}

	function forgotPassword() {
		if ($("#inputUsername").val() != ""){
			toastr.info("Se está procesando su solicitud..");
			$.ajax({
				url : "<%=SettingsHelper.REST_API_URL + "/users/password"%>",
				type : "POST",
				data : {
					username : $("#inputUsername").val()
				},
				success : function(data, textStatus, jqXHR) {
					jsonResponse = JSON.parse(data);
					if (jsonResponse.result == "OK")
						toastr.success("Se ha enviado un email a su casilla de correo con la nueva contraseña");
					else
						toastr.error(jsonResponse.data);
				},
				error : function(jqXHR, textStatus, errorThrown) {
					toastr.error("No se pudo completar su solicitud");
				}
			});
		}
		else
			toastr.warning("Ingrese su nombre de usuario");
	}
	
	function setCustomValidationMessages(){
		var forms = document.getElementsByTagName('form');
		for (var i = 0; i < forms.length; i++) {
		    forms[i].addEventListener('invalid', function(e) {
		        e.preventDefault();
		        toastr.error("Campo " + e.target.placeholder + " vacío o con formato incorrecto");
		    }, true);
		}
	}
</script>
</body>
</html>
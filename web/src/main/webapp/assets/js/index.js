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
	$(document).ready(function() {
		servicesUrl = $("#servicesUrl").val();
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
					url : servicesUrl + "/users/username/" + username.val(),
					type : "GET",
					success : function(data, textStatus, jqXHR) {
						jsonResponse = JSON.parse(data);
						if (jsonResponse.result == "OK" && jsonResponse.data == "false"){
							//check email
							$.ajax({
								url : servicesUrl + "/users/email/" + email.val(),
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
				url : servicesUrl + "/users/password",
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
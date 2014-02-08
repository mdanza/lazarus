var editable = false;
var servicesUrl;
$(document).ready(function() {
	servicesUrl = $("#servicesUrl").val();
	if ($("#hiddenToken").val() == "")
		window.location.replace("index.jsp");
	else{
		loadUserData();
		setCustomValidationMessages();
		$("#userForm").submit(function(event) {
			$('#submitBtn').button('loading');
			form = $(this);
			url = form.attr('action');
			$.ajax({
				url : url,
				type : "PUT",
				headers: { Authorization: $("#hiddenToken").val()},
				data : {
					username : $("#inputUsername").val(),
					password: $("#inputPassword").val(),
					email: $("#inputEmail").val()
				},
				success : function(data, textStatus, jqXHR) {
					jsonResponse = JSON.parse(data);
					if (jsonResponse.result == "OK"){
						toggleEditability();
						toastr.success("Modificado correctamente");
						$('#submitBtn').button('reset');
					}
					else{
						toastr.error("Hubo un error al modificar sus datos");
						$('#submitBtn').button('reset');
					}
				},
				error : function(jqXHR, textStatus, errorThrown) {
					toastr.error("No se pudo completar su solicitud");
					$('#submitBtn').button('reset');
				}
			});
			return false;
		});
	}
});

function loadUserData(){
	$.ajax({
		url : servicesUrl + "/users",
		type : "GET",
		headers : {
			Authorization : $("#hiddenToken").val()
		},
		success : function(data, textStatus, jqXHR) {
			jsonResponse = JSON.parse(data);
			if (jsonResponse.result == "OK") {
				jsonData = JSON.parse(jsonResponse.data);
				$("#inputUsername").val(jsonData.username);
				$("#inputUsername").attr('readonly', true);
				$("#inputEmail").val(jsonData.email);
				$("#inputEmail").attr('readonly', true);
			} else
				window.location.replace("index.jsp");
		},
		error : function(jqXHR, textStatus, errorThrown) {
			toastr.error("No se pudo completar su solicitud");
		}
	});
}

function toggleEditability() {
	if (editable) {
		$("#inputPassword").removeClass("show").addClass("hidden");
		$("#submitBtn").removeClass("show").addClass("hidden");
		$("#inputEmail").attr('readonly', true);
		$("#toggler").html("Modificar");
	} else {
		$("#inputPassword").removeClass("hidden").addClass("show");
		$("#submitBtn").removeClass("hidden").addClass("show");
		$("#inputEmail").attr('readonly', false);
		$("#toggler").html("Cancelar");
	}
	editable = !editable;
	return false;
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
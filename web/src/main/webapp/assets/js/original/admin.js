var editable = false;
var servicesUrl;
$(document).ready(function() {
	servicesUrl = $("#servicesUrl").val();
	if ($("#hiddenToken").val() == "")
		window.location.replace("index.jsp");
	else{
		//initialize file input plugin
		$("#inputFile").filestyle();
		$("#inputCsvFile").filestyle();
		
		loadUserData();
	}
});

function uploadShapefile(){
	var file = $("#inputFile").val();
	var type = $("#selectedShapefile").val();
	if(file == ""){
		toastr.error("No ha seleccionado un archivo");
		return;
	}
	if(type == ""){
		toastr.error("No ha seleccionado un tipo de archivo");
		return;
	}
	form = $("#shapefileUpdateForm");
	url = form.attr('action') + $("#selectedShapefileType").val();
	formData = new FormData(form[0]);
	$.ajax({
		url : url,
		type : "POST",
		enctype: "multipart/form-data",
		headers: { Authorization: $("#hiddenToken").val()},
		data : formData,
		success : function(data, textStatus, jqXHR) {
			jsonResponse = JSON.parse(data);
			if (jsonResponse.result == "OK"){
				toastr.success("Subido correctamente");
				$("#shapefileProgress").attr("value", 0);
			}
			else
				toastr.error("Hubo un error al subir el archivo");
		},
		error : function(jqXHR, textStatus, errorThrown) {
			toastr.error("No se pudo completar su solicitud");
		},
		cache: false,
        contentType: false,
        processData: false
	});
}

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
				if (jsonData.role != "ADMIN")
					window.location.replace("index.jsp");
				else
					loadAdminMenu();
			} else
				window.location.replace("index.jsp");
		},
		error : function(jqXHR, textStatus, errorThrown) {
			toastr.error("No se pudo completar su solicitud");
		}
	});
}

function loadAdminMenu() {
	$.ajax({
		        url : servicesUrl + "/information/status",
				type : "GET",
				headers : {
					Authorization : $("#hiddenToken").val()
				},
				success : function(data, textStatus, jqXHR) {
					jsonResponse = JSON.parse(data);
					if (jsonResponse.result == "OK") {
						jsonData = JSON.parse(jsonResponse.data);
						$('#addressesProgress')
								.html(
										Math
												.round(100 * jsonData.addressesUploadPercentage) / 100);
						$('#busRoutesProgress')
								.html(
										Math
												.round(100 * jsonData.busRoutesMaximalUploadPercentage) / 100);
						$('#busStopsProgress')
								.html(
										Math
												.round(100 * jsonData.busStopsUploadPercentage) / 100);
						$('#cornersProgress')
								.html(
										Math
												.round(100 * jsonData.cornersUploadPercentage) / 100);
						$('#streetsProgress')
								.html(
										Math
												.round(100 * jsonData.streetsUploadPercentage) / 100);
					} else
						toastr.error("No se pudieron cargar los datos");
				},
				error : function(jqXHR, textStatus, errorThrown) {
					toastr.error("No se pudo completar su solicitud");
				}
			});
}

function uploadCsv(){
	var file = $("#inputCsvFile").val();
	var type = $("#selectedCsvfile").val();
	if(file == ""){
		toastr.error("No ha seleccionado un archivo");
		return;
	}
	if(type == ""){
		toastr.error("No ha seleccionado un tipo de archivo");
		return;
	}
	form = $("#csvUpdateForm");
	url = form.attr('action') + $("#selectedCsvType").val();
	formData = new FormData(form[0]);
	$.ajax({
		url : url,
		type : "POST",
		enctype: "multipart/form-data",
		headers: { Authorization: $("#hiddenToken").val()},
		data : formData,
		success : function(data, textStatus, jqXHR) {
			jsonResponse = JSON.parse(data);
			if (jsonResponse.result == "OK"){
				toastr.success("Subido correctamente");
				$("#csvProgress").attr("value", 0);
			}
			else
				toastr.error("Hubo un error al subir el archivo");
		},
		error : function(jqXHR, textStatus, errorThrown) {
			toastr.error("No se pudo completar su solicitud");
		},
		cache: false,
        contentType: false,
        processData: false
	});
}

function selectCsvType(target, selected){
	$("#selectedCsvType").val(target);
	$("#selectedCsvfile").val(selected);
}

function selectShapefileType(target, selected){
	$("#selectedShapefileType").val(target);
	$("#selectedShapefile").val(selected);
}
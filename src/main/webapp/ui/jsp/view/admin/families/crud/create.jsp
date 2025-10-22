<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="breadcrumbs">
	<div class="breadcrumbs-inner">
		<div class="row m-0">
			<div class="col">
				<div class="page-header float-left">
					<div class="page-title">
						<h1 class="text-uppercase">
							<spring:message code="families.admin.create.title" />
						</h1>
					</div>
				</div>
			</div>
			
			<div class="col">
				<div class="float-right calendarBtns">
					<a href="/admin/families" class="btn btn-standard btn-sm">
						<span class="fc-icon fc-icon-chevron-left"></span> <spring:message code="back" />
					</a>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="clearfix"></div>

<div class="content">
	<div class="row h-100">
		<div class="col-md-12 col-xl-3">
				
			<div class="row">
				<div class="col">
					<div class="card">
						<div class="card-body">
							<div class="row">
								<div class="col">
									<div class="img-profile-circle float-left">
										<img class="user-avatar rounded-circle"
											src="/ui/static/images/profile.png" alt="me" height="40" />
									</div>

									<div style="font-size: 12px">${ user.name } ${ user.surnames }</div>
									<div style="font-size: 11px">
										<spring:message code="families.admin.create.title.sub" />
									</div>
								</div>
							</div>

						</div>
					</div>
				</div>
			</div>		
			
			<div class="row">
				<div class="col">
					<div class="card">
						<div class="card-body">
							<form id="createFamilyForm">
								<div class="row">
									<div class="col">
										<span><spring:message code="families.admin.create.info.title" /></span>
									</div>
								</div>
								
								<div class="row mt-2">
									<div class="col">
										<label for="familyNameESInput" class="col-form-label" style="font-size: 12px"><spring:message code="families.admin.create.name.esp" /></label>
										<input type="text" class="form-control form-control-sm" id="familyNameESInput" name="nameES" required>
									</div>
								</div>
								
								<div class="row">
									<div class="col">
										<label for="familyNameFRInput" class="col-form-label" style="font-size: 12px"><spring:message code="families.admin.create.name.fra" /></label>
										<input type="text" class="form-control form-control-sm" id="familyNameFRInput" name="nameFR" required>
									</div>
								</div>
								
								<div class="row">
									<div class="col">
										<div class="custom-control custom-checkbox my-1 mr-sm-2">
										    <input type="checkbox" class="custom-control-input" id="customControlInline" name="common">
										    <label class="custom-control-label mr-3" for="customControlInline" style="font-size: 14px"><spring:message code="families.admin.create.common" /></label>
										</div>
									</div>
								</div>
								
								<div class="row mt-4">
									<div class="col">
										<div class="form-group">
											<button id="submit" type="button" class="btn btn-success btn-sm w-100">
												<spring:message code="submit" />
											</button>
										</div>
									</div>
								</div>
							</form>
						</div>
					</div>
				</div>
			</div>
		</div>

		<div class="col-md-12 col-xl-9 h-100">
			<div class="card">
				<div class="card-body">
					<div class="row">
						<div class="col title mb-0">
							<spring:message code="families.admin.create.table.title" />
						</div>
						<div class="col text-right">
							<button class="btn btn-sm btn-info" data-toggle="modal" data-target=".subfamilies-modal" data-action="create">
								<spring:message code="expenses.personal.create.table.add" />
							</button>
						</div>
					</div>
					
					<div class="table-responsive">
						<table id="dTable"
							class="table table-striped table-borderer dataTable">
							<caption class="d-none">NO SONAR</caption>
							<thead>
								<tr>
									<th id="thNameES"><spring:message code="families.admin.create.name.esp" /></th>
									<th id="thNameFR"><spring:message code="families.admin.create.name.fra" /></th>
									<th id="thSubRoleNames"><spring:message code="families.admin.create.sub.role.names" /></th>
									<th id="thActions" class="all"><spring:message code="families.admin.create.table.actions" /></th>
								</tr>
							</thead>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<!-- MODAL -->
<div id="addSubFamilyModal" class="modal fade subfamilies-modal" tabindex="-1"
	role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
	<div class="modal-dialog modal-lg">
		<div class="modal-content">
			<div class="modal-header">
				<div class="modal-title">
					<h5 id="modalTitle">
						<spring:message code="families.sub.create.modal.title" />
					</h5>
				</div>
			</div>
			<div class="modal-body">
				<form id="addSubFamilyForm" class="needs-validation">
					<div class="row">
						<div class="col">
							<div class="form-group">
								<label for="subFamilyNameES" class="col-form-label"><spring:message code="families.admin.create.name.esp" /></label>
								<input id="subFamilyNameES" name="subFamilyNameES" type="text" class="form-control" required>
							</div>
						</div>
					</div>
					
					<div class="row">
						<div class="col">
							<div class="form-group">
								<label for="subFamilyNameFR" class="col-form-label"><spring:message code="families.admin.create.name.fra" /></label>
								<input id="subFamilyNameFR" name="subFamilyNameFR" type="text" class="form-control" required>
							</div>
						</div>
					</div>
					
					<div class="row">
						<div class="col">
							<label for="subRolesDropdown" class="col-form-label"><spring:message code="families.sub.create.modal.sub.roles" /></label>
							<select id="subRolesDropdown" class="form-control input selectpicker" data-style="userSelectPicker" data-live-search="true" multiple style="width: 100%" name="subRoles">
								<c:forEach items="${subRoles}" var="subRole">
									<option value="${subRole.rol}">				
										<spring:message code="${subRole.rol}" />
									</option>
								</c:forEach>
							</select>	
						</div>
					</div>

					<input id="rowId" name="rowId" class="form-control input" type="hidden" readonly />
				</form>
			</div>
			<div class="modal-footer clearfix">
				<div class="w-100">
					<div class="float-left">
						<button type="button" id="back"
							data-dismiss="modal" class="btn btn-sm">
							<spring:message code="back" />
						</button>
					</div>
					<div class="float-right">
						<button id="add" type="button" class="btn btn-sm btn-success">
							<spring:message code="save" />
						</button>
						
						<button id="update" type="button" class="btn btn-sm btn-success">
							<spring:message code="save" />
						</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- /MODAL -->

<jsp:useBean id="jspUtil" class="com.epm.gestepm.modelapi.common.utils.JspUtil"/>

<script>
	var modalTitle = $("#modalTitle");
	
	$(document).ready(function() {

		/* Select 2 */
		
		$('#subRolesDropdown').selectpicker();
		
		/* End Select 2 */
		
		/* Datatables */
		var table = $('#dTable').DataTable({
			"lengthChange": false,
			"responsive": true,
			"processing": true,
			"language": {
				"url": "/ui/static/lang/datatables/${locale}.json"
			},
			"columns": [
				{ "data": "subFamilyNameES" },
				{ "data": "subFamilyNameFR" },
				{ "data": "subRoleNames", "defaultContent": "-" },
				{ "data": null }
			],
			"columnDefs": [
				{ 
					"className": "text-center", 
					"targets": "_all"
				},
			    { 
    			    "defaultContent": "${tableActionButtons}", 
    			    // "width": "170px",
    			    "orderable": false, 
    			    "targets": -1 
    			}
			],
			"dom": "<'top'i>rt<'bottom'p><'clear'>",
			"drawCallback": function(settings, json) {
				parseActionButtons();
			}
		});
		/* End Datatables */

		$('#dTable').on('draw.dt', function () {
			parseActionButtons();
		});

		$('.subfamilies-modal').on('show.bs.modal', function (event) {

			// Get button that trigger modal
			var button = $(event.relatedTarget);
			var action = button.data('action');
			var rowId = button.data('id');
			setButtonAction(action);

			// Update
			if (action == 'edit') {
				setFormData(rowId);
			}
		});

		$('.subfamilies-modal').on('hidden.bs.modal', function (e) {
			$('#addSubFamilyForm').trigger("reset");
			initModalStyle();
		});

		$('#add').click(function () {

			if (validateModal()) {
				$('#addSubFamilyForm').addClass('was-validated');
			} else {
				$('#addSubFamilyForm').removeClass('was-validated');
				addSubFamily();
			}
		});

		$('#update').click(function () {
			updateSubFamily();
		});

		$('#submit').click(function () {

			if (validateForm()) {
				$('#createFamilyForm').addClass('was-validated');
			} else {
				
				showLoading();
				
				$('#createFamilyForm').removeClass('was-validated');
				var family = getFamily(table.rows().data());
				$.ajax({
					url: '/admin/families/create', 
					type: 'POST',
					data: JSON.stringify(family),
					contentType: 'application/json',
					success: function (json) {
						window.location.replace("/admin/families");
					}, 
					error: function (e) {
						hideLoading();
						showNotify(e.responseText, 'danger');
					}
				});
			}
		});

		$('#addSubFamilyModal').on('hidden.bs.modal', function (e) {
			$('#addSubFamilyForm').trigger("reset");
			$("#subRolesDropdown").val(null).trigger("change"); 
		});
	});

	function validateModal() {
		var nameES = document.getElementById('subFamilyNameES');
		var nameFR = document.getElementById('subFamilyNameFR');
		
		return (!nameES.value.length || !nameFR.value.length);
	}

	function validateForm() {
		var nameES = document.getElementById('familyNameESInput');
		var nameFR = document.getElementById('familyNameFRInput');
		
		return (!nameES.value.length || !nameFR.value.length);
	}

	function getFamily(data) {
		var family = {
			nameES: $('#familyNameESInput').val(),
			nameFR: $('#familyNameFRInput').val(),
			brand: $('#familyBrandInput').val(),
			model: $('#familyModelInput').val(),
			enrollment: $('#familyEnrollmentInput').val(),
			common: $('#customControlInline').prop('checked') ? 1 : 2,
			subfamilies: [] 	
		}

		for (var i = 0; i < data.length; i++) {
			family.subfamilies.push(data[i]);
		}

		return family;
	}

	function addSubFamily() {
		var table = $('#dTable').DataTable();
		var tool = getFormData($('#addSubFamilyForm'));
		table.row.add(tool).draw();
		$('.subfamilies-modal').modal('hide');
	}

	function updateSubFamily() {
		var table = $('#dTable').DataTable();
		var tool = getFormData($('#addSubFamilyForm'));
		var rowId = $('#rowId').val();
		table.row('#row' + rowId).data(tool).draw();
		$('.subfamilies-modal').modal('hide');
	}

	function getFormData(form) {
	    var unindexed_array = form.serializeArray();
	    var indexed_array = {};

	    $.map(unindexed_array, function(n, i){
		    
		    if (n['name'] === 'subRoles') {

			    if (!indexed_array['subRoleNames']) {
			    	indexed_array['subRoleNames'] = '';
				}
				
		    	indexed_array['subRoleNames'] += n['value'].split(';') + ', ';
		    	
			} else {
	        	indexed_array[n['name']] = n['value'];
			}
	    });

	    if (indexed_array['subRoleNames']) {
	    	indexed_array['subRoleNames'] = indexed_array['subRoleNames'].substring(0, indexed_array['subRoleNames'].length - 2);
		}

	    return indexed_array;
	}

	function setFormData(i) {
		var table = $('#dTable').DataTable();
		var row = $('#row' + i);
		var data = table.row(row).data();
		Object.keys(data).forEach(function (key) {

			var value = data[key];
			
			if (key === 'subRoleNames') {

				var values = value.split(', ');
				$("#subRolesDropdown").val(values).change(); 
				
			} else {
				$('#' + key).val(value);
			}
		});

		// Set row Id
		$('#rowId').val(i);
	}

	function setButtonAction(action) {
		if (action === 'edit') {
			$('#add').hide();
			$('#update').show();
		} else {
			$('#update').hide();
			$('#add').show();
		}
	}

	function initModalStyle() {
		modalTitle.html('<spring:message code="families.sub.create.modal.title" />')
	}

	function deleteSubFamily(i) {
		var table = $('#dTable').DataTable();
		var row = $('#row' + i);
		table.row(row).remove().draw();
	}
	
</script>
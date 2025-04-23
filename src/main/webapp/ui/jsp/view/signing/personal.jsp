<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<div class="breadcrumbs">
	<div class="breadcrumbs-inner">
		<div class="row m-0">
			<div class="col-10">
				<div class="page-header float-left">
					<div class="page-title">
						<h1 class="text-uppercase"><spring:message code="signing.personal.title" /></h1>
					</div>
				</div>
			</div>
			<div class="col-2">
				<div class="float-right calendarBtns">
					<div class="fc-button-group">
						<button type="button" class="btn btn-primary btn-sm mr-1" onclick="calendar.prev()">
							<span class="fc-icon fc-icon-chevron-left"></span>
						</button>
						<button type="button" class="btn btn-primary btn-sm" onclick="calendar.next()">
							<span class="fc-icon fc-icon-chevron-right"></span>
						</button>
					</div>
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
										<img class="user-avatar rounded-circle" src="/ui/static/images/profile.png" alt="me" height="40" />
									</div>
									
									<div style="font-size: 12px">${ user.name } ${ user.surnames }</div>
									<div style="font-size: 11px"><spring:message code="signing.personal.date" /></div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			
			<div class="row">
				<div class="col-sm-12">
					<div class="card">
						<div class="card-body">
							<a href="/signing/personal/time-control" class="btn btn-sm btn-success w-100 text-white"><i class="fa fa-shopping-bag"></i> <spring:message code="signing.hours.bag" /></a>
						</div>
					</div>
				</div>
			</div>
			
			<div class="row">
				<div class="col-sm-12">
					<div class="card">
						<div class="card-body">
							<ul class="mb-0">
								<li class="listSignings">
									<div class="box bg-primary"></div>
									<span><spring:message code="signing.today.timer" arguments="${todayTimer}" /></span>
								</li>
							</ul>
						</div>
					</div>
				</div>
			</div>
		</div>
		
		<div class="col-md-12 col-xl-9 h-100">
			<div id="calendar" class="personalCalendar" class="h-100"></div>
		</div>
	</div>
</div>

<!-- SIGNING MODAL -->
<div id="editSigningModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<div class="modal-title">
					<h5><spring:message code="signing.edit.title" /></h5>
				</div>
			</div>
			<div class="modal-body">
				<form id="editSigningForm">
					<input id="shareId" name="shareId" type="hidden" />
					<input id="shareType" name="shareType" type="hidden" />

					<div class="row">
						<div class="col-6">
							<input type="datetime-local" class="form-control input" id="startDate" name="startDate">
						</div>

						<div class="col-6">
							<input type="datetime-local" class="form-control input" id="endDate" name="endDate">
						</div>
					</div>
				</form>
			</div>
			<div class="modal-footer clearfix">
				<div class="w-100">
					<div class="float-left">
						<button type="button" class="btn btn-sm" data-dismiss="modal"><spring:message code="close" /></button>
					</div>
					<div class="float-right">
						<button id="editSigningBtn" type="submit" class="btn btn-sm btn-success"><spring:message code="edit" /></button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<script>
	let calendar;
	
	document.addEventListener('DOMContentLoaded', function() {

		const calendarEl = document.getElementById('calendar');

		calendar = new FullCalendar.Calendar(calendarEl, {
			locale: 'es',
			plugins : [ 'timeGrid' ],
			defaultView: 'timeGridWeek',
			header: false,
			allDaySlot: false,
			timeFormat: 'H:mm',
			timeZone: 'UTC',
			height: 'parent',
			events: function (fetchInfo, successCallback, failureCallback) {
				const params = {
					userId: ${user.id},
					startDate: fetchInfo.startStr,
					endDate: fetchInfo.endStr
				};

				axios.get('/v1/time-controls', { params: params }).then((response) => {
					let timeControls = response.data.data.map(timeControl => ({
						id: timeControl.id,
						title: getTitle(timeControl),
						start: timeControl.startDate,
						end: timeControl.endDate,
						color: getColor(timeControl.type),
						textColor: '#fff',
						extendedProps: {
							type: timeControl.type
						}
					}));

					successCallback(timeControls);
				}).catch(error => failureCallback(error));
			},
			eventClick: function(info) {
				const calendarSigning = info.event;
				const id = calendarSigning.id;
				const type = calendarSigning.extendedProps.type;

				if (type === 'TELEWORKING_SIGNING') {
					window.location.href = '/signings/teleworking/' + id;
				} else {
					loadModalForm(calendarSigning);
				}
			}
		});

		calendar.render();
	});

	$('#editSigningBtn').click(function() {

		showLoading();

		$.ajax({
			type: "POST",
			url: "/signing/share/request-update",
			data: $('#editSigningForm').serialize(),
			success: function(msg) {
				hideLoading();
				showNotify(msg, 'success');
			},
			error: function(e) {
				hideLoading();
				showNotify(e.responseText, 'danger');
			}
		});

		$('#editSigningModal').modal('hide');
	});

	function loadModalForm(calendarSigning) {
		const allowedTypes = ['MANUAL_SIGNINGS', 'PERSONAL_SIGNINGS'];

		const id = calendarSigning.id;
		const type = calendarSigning.extendedProps.type;

		if (!allowedTypes.some(allowedType => type.includes(allowedType))) {
			return;
		}

		const startDate = new Date(calendarSigning.start).toISOString().slice(0, 16);
		const endDate = new Date(calendarSigning.end).toISOString().slice(0, 16);

		const form = document.querySelector('#editSigningForm');

		form.querySelector('[name="shareId"]').value = id;
		form.querySelector('[name="shareType"]').value = type;
		form.querySelector('[name="startDate"]').value = startDate;
		form.querySelector('[name="endDate"]').value = endDate;

		$('#editSigningModal').modal('show');
	}

	function getTitle(timeControl) {
		return timeControl.description ? timeControl.description : getSigningText(timeControl.type);
	}

	function getColor(type) {
		if (type === 'DISPLACEMENT_SHARES') {
			return '#CC00C8';
		} else if (type === 'MANUAL_SIGNINGS') {
			return '#D8E112';
		} else if (type === 'PERSONAL_SIGNINGS') {
			return '#0062CC';
		} else {
			return '#12E1DE';
		}
	}

</script>
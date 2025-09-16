<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="row">
    <div class="col">
        <div class="float-right calendarBtns">
            <div class="fc-button-group">
                <button type="button" class="btn btn-success btn-sm mr-1" data-toggle="modal" data-target="#createModal"><spring:message code="user.detail.signing.admin.btn" /></button>
                <a href="/signings/teleworking?user=${currentUser.id}" class="btn btn-warning btn-sm mr-1 text-white"><spring:message code="user.detail.signing.teleworking" /></a>
                <a href="/signing/manual?user=${currentUser.id}" class="btn btn-warning btn-sm mr-1 text-white"><spring:message code="user.detail.signing.manual" /></a>
                <a href="/signing/personal/time-control?user=${currentUser.id}" class="btn btn-warning btn-sm mr-1 text-white"><spring:message code="signing.hours.bag" /></a>
                <button type="button" class="btn btn-primary btn-sm mr-1" data-toggle="modal" data-target="#exportModal"><spring:message code="user.detail.signing.gen" /></button>
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

<div class="row">
    <div class="col-md-12 calendar-content">
        <div id="calendar" class="personalCalendar"></div>
    </div>
</div>

<div id="createModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <div class="modal-title">
                    <h5><spring:message code="user.detail.signing.admin.btn" /></h5>
                </div>
            </div>

            <div class="modal-body">
                <form id="createForm">
                    <input type="hidden" name="userId" value="${currentUser.id}" />
                    <input id="geolocationInput" type="hidden" name="geolocation" />

                    <div class="row">
                        <div class="col">
                            <select class="form-control input select-sm" style="width: 100%" name="signingType" onchange="updateCreateForm(this.value)" required>
                                <option disabled selected="selected">
                                    <spring:message code="signing.type.selectable" />
                                </option>
                                <option value="ps"><spring:message code="signing.personal.title" /></option>
                                <option value="ums"><spring:message code="signing.manual.calendar.title" /></option>
                            </select>
                        </div>
                    </div>

                    <div id="manualTypeRow" class="row mt-2" style="display: none">
                        <div class="col">
                            <select class="form-control input select-sm" name="manualTypeId" onchange="updateForm(this.value)">
                                <option></option>
                                <c:forEach items="${manualSigningTypes}" var="manualSigningType">
                                    <option value="${manualSigningType.id}">
                                        <spring:message code="${manualSigningType.name}" />
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="row mt-2">
                        <div class="col-sm-12 col-md-6">
                            <input type="datetime-local" style="font-size: 12px" class="form-control" name="startDate">
                        </div>

                        <div class="col-sm-12 col-md-6">
                            <input type="datetime-local" style="font-size: 12px" class="form-control" name="endDate">
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
                        <button type="submit" class="btn btn-sm btn-success" onclick="createSigning()"><spring:message code="create" /></button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<div id="exportModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <form id="exportForm">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <div class="modal-title">
                        <h5 id="modalTitle">
                            <spring:message code="user.detail.signing.gen" />
                        </h5>
                    </div>
                </div>
                <div class="modal-body">
                    <div class="row">
                        <div class="col">
                            <div class="form-group mb-1">
                                <label class="col-form-label w-100"><spring:message code="start.date"/>
                                    <input name="startDate" type="datetime-local" class="form-control mt-1" />
                                </label>
                            </div>
                        </div>
                    </div>
                    <div class="row mt-2">
                        <div class="col">
                            <div class="form-group mb-1">
                                <label class="col-form-label w-100"><spring:message code="end.date"/>
                                    <input name="endDate" type="datetime-local" class="form-control mt-1" />
                                </label>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer clearfix">
                    <div class="w-100">
                        <div class="float-left">
                            <button type="button" class="btn btn-sm" data-dismiss="modal"><spring:message code="close" /></button>
                        </div>
                        <div class="float-right">
                            <button type="button" class="btn btn-sm btn-success" onclick="exportTimeControl()"><spring:message code="export" /></button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>

<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    #filterForm {
        top: 100% !important;
        left: auto !important;
        right: 0;
        min-width: 20rem;
    }
</style>

<div class="breadcrumbs">
    <div class="breadcrumbs-inner">
        <div class="row m-0">
            <div class="col">
                <div class="page-header float-left">
                    <div class="page-title">
                        <h1 class="text-uppercase"><spring:message code="summary.signing.title" /></h1>
                    </div>
                </div>
            </div>
            <div class="col">
                <div class="page-header float-right">
                    <button type="button" class="btn btn-default btn-sm" data-toggle="modal" data-target="#generateModal">
                        <spring:message code="summary.signing.generate" />
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="clearfix"></div>

<div id="generateModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <div class="modal-title">
                    <h5><spring:message code="summary.signing.generate" /></h5>
                </div>
            </div>

            <div class="modal-body">
                <form id="generateForm">
                    <div class="row">
                        <div class="col-md-6 col-sm-12">
                            <div class="form-group">
                                <label class="col-form-label w-100 bootstrap-select">
                                    <spring:message code="project"/>
                                    <select name="projectId" class="form-control select2" data-control="select2">
                                    </select>
                                </label>
                            </div>
                        </div>
                        <div class="col-md-6 col-sm-12">
                            <div class="form-group">
                                <label class="col-form-label w-100 bootstrap-select">
                                    <spring:message code="userId"/>
                                    <select name="userId" class="form-control select2" data-control="select2">
                                    </select>
                                </label>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 col-sm-12">
                            <div class="form-group">
                                <label class="col-form-label w-100">
                                    <spring:message code="start.date"/>
                                    <input type="datetime-local" name="startedAt" class="form-control mt-1" required/>
                                </label>
                            </div>
                        </div>
                        <div class="col-md-6 col-sm-12">
                            <div class="form-group">
                                <label class="col-form-label w-100">
                                    <spring:message code="end.date"/>
                                    <input type="datetime-local" name="closedAt" class="form-control mt-1" required/>
                                </label>
                            </div>
                        </div>
                    </div>
                </form>
            </div>

            <div class="modal-footer clearfix">
                <div class="w-100">
                    <div class="float-left">
                        <button type="button" class="btn btn-sm" data-dismiss="modal">
                            <spring:message code="close"/>
                        </button>
                    </div>
                    <div class="float-right">
                        <button type="button" class="btn btn-default btn-sm" onclick="generateSummary()">
                            <spring:message code="summary.signing.generate" />
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
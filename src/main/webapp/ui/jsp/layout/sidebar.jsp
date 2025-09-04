<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!-- Left Panel -->
<nav class="navbar navbar-expand-sm navbar-default">
	<div id="main-menu" class="main-menu collapse navbar-collapse">
		<ul class="nav navbar-nav">

			<spring:eval expression="@environment.getProperty('gestepm.forum.url')" var="forumUrl" />

			<sec:authorize access="hasAuthority('ROLE_OFICINA')">
				<li <c:if test="${fn:startsWith(pageName, '/signing/personal')}">class="active"</c:if>>
					<a href="/signing/personal"> <i class="menu-icon fas fa-business-time"></i><spring:message code="sidebar.signing" /></a>
				</li>
			</sec:authorize>

			<sec:authorize access="hasAuthority('ROLE_OFICINA')">
				<li class="menu-item-has-children dropdown <c:if test="${fn:startsWith(pageName, '/expenses')}">active</c:if>">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"> <i class="menu-icon fa fa-coins"></i><spring:message code="sidebar.expenses" /></a>
					<ul class="sub-menu children dropdown-menu">
						<li><i class="fa fa-user"></i><a href="/expenses/personal/sheets"><spring:message code="sidebar.expenses.personal" /></a></li>

						<sec:authorize access="hasAuthority('ROLE_ADMINISTRACION')">
							<li><i class="fa fa-check-circle"></i><a href="/expenses/corrective"><spring:message code="sidebar.expenses.correctives" /></a></li>
						</sec:authorize>
					</ul>
				</li>
			</sec:authorize>

			<sec:authorize access="hasAuthority('ROLE_JEFE_PROYECTO')">
				<li class="menu-item-has-children dropdown <c:if test="${fn:startsWith(pageName, '/holidays')}">active</c:if>">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"> <i class="menu-icon fa fa-clipboard"></i><spring:message code="sidebar.holidays" /></a>
					<ul class="sub-menu children dropdown-menu">
						<li><i class="menu-icon fa fa-suitcase-rolling"></i><a href="/holidays"><spring:message code="sidebar.holidays" /></a></li>
						<li><i class="menu-icon fa fa-suitcase-rolling"></i><a href="/holidays/admin"><spring:message code="sidebar.holidays.admin" /></a></li>
					</ul>
				</li>
			</sec:authorize>

			<sec:authorize access="!hasAuthority('ROLE_JEFE_PROYECTO') && hasAuthority('ROLE_OFICINA')">
				<li <c:if test="${fn:startsWith(pageName, '/holidays')}">class="active"</c:if>>
					<a href="/holidays"> <i class="menu-icon fa fa-suitcase-rolling"></i><spring:message code="sidebar.holidays" /></a>
				</li>
			</sec:authorize>

			<sec:authorize access="hasAuthority('ROLE_OFICINA')">
				<li class="menu-item-has-children dropdown <c:if test="${(fn:startsWith(pageName, '/signing') || fn:startsWith(pageName, '/signing/manual'))  && !fn:startsWith(pageName, '/signing/personal')}">active</c:if>">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"> <i class="menu-icon far fa-clock"></i><spring:message code="sidebar.signing.page" /></a>
					<ul class="sub-menu children dropdown-menu">
						<li><i class="fa fa-home"></i><a href="/signings/teleworking"><spring:message code="sidebar.signing.teleworking" /></a></li>
						<li><i class="fas fa-pencil-alt"></i><a href="/signing/manual"><spring:message code="sidebar.signing.manual" /></a></li>
						<li><i class="fas fa-warehouse"></i><a href="/signings/warehouse"><spring:message code="sidebar.signing.warehouse" /></a></li>
						<sec:authorize access="hasAuthority('ROLE_JEFE_PROYECTO')">
							<li><i class="fas fa-edit"></i><a href="/signing/modified-list"><spring:message code="sidebar.signing.modified.list" /></a></li>
						</sec:authorize>
					</ul>
				</li>
			</sec:authorize>

			<sec:authorize access="hasAuthority('ROLE_OPERARIO')">
				<li class="menu-item-has-children dropdown <c:if test="${fn:startsWith(pageName, '/shares')}">active</c:if>">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"> <i class="menu-icon fa fa-clipboard"></i><spring:message code="sidebar.share" /></a>
					<ul class="sub-menu children dropdown-menu">
						<li><i class="fa fa-cog"></i><a href="/shares/construction"><spring:message code="sidebar.share.construction" /></a></li>
						<li><i class="fa fa-car"></i><a href="/shares/displacement"><spring:message code="sidebar.share.displacement" /></a></li>
						<li><i class="fa fa-exclamation-triangle"></i><a href="/shares/no-programmed"><spring:message code="sidebar.share.intervention" /></a></li>
						<li><i class="fa fa-clock"></i><a href="/shares/programmed"><spring:message code="sidebar.share.programmed" /></a></li>
						<li><i class="fa fa-hand-paper"></i><a href="/shares/work"><spring:message code="sidebar.share.work" /></a></li>
					</ul>
				</li>
			</sec:authorize>

			<sec:authorize access="hasAuthority('ROLE_OFICINA')">
				<li class="menu-item-has-children dropdown <c:if test="${fn:startsWith(pageName, '/projects')}">active</c:if>">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"> <i class="menu-icon fa fa-project-diagram"></i><spring:message code="sidebar.projects" /></a>
					<ul class="sub-menu children dropdown-menu">
						<sec:authorize access="hasAuthority('ROLE_JEFE_PROYECTO')">
							<li><i class="fa fa-users-cog"></i><a href="/projects"><spring:message code="sidebar.projects.admin" /></a></li>
						</sec:authorize>
						<li><i class="fa fa-eye"></i><a href="/projects-view"><spring:message code="sidebar.projects.view" /></a></li>
					</ul>
				</li>
			</sec:authorize>

			<sec:authorize access="hasAuthority('ROLE_JEFE_PROYECTO')">
				<li <c:if test="${fn:startsWith(pageName, '/users')}">class="active"</c:if>>
					<a href="/users"> <i class="menu-icon fa fa-users"></i><spring:message code="sidebar.users" /></a>
				</li>
			</sec:authorize>

			<sec:authorize access="hasAuthority('ROLE_ADMIN')">
				<li <c:if test="${fn:startsWith(pageName, '/roles')}">class="active"</c:if>>
					<a href="/roles"> <i class="menu-icon fa fa-tags"></i><spring:message code="sidebar.roles" /></a>
				</li>
			</sec:authorize>

			<sec:authorize access="hasAuthority('ROLE_OPERARIO') and !hasAuthority('ROLE_RRHH') or hasAuthority('ROLE_ADMIN')">
				<li>
					<a href="/forum/login" target="_blank"> <i class="menu-icon fa fa-comment-alt"></i><spring:message code="sidebar.forum" /></a>
				</li>
			</sec:authorize>

			<li class="menu-item-has-children dropdown">
				<a href="#" class="dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"> <i class="menu-icon fa fa-store"></i><spring:message code="sidebar.shop" /></a>
				<ul class="sub-menu children dropdown-menu">
					<li><i class="fa fa-shopping-cart"></i><a href="https://generics-store.gashelpservices.com/index.php" target="_blank"><spring:message code="sidebar.shop.gen" /></a></li>
					<li><i class="fa fa-shopping-cart"></i><a href="https://original-store.gashelpservices.com/es/" target="_blank"><spring:message code="sidebar.shop.ori" /></a></li>
				</ul>
			</li>

			<sec:authorize access="hasAuthority('ROLE_ADMIN')">
				<li class="menu-item-has-children dropdown <c:if test="${fn:startsWith(pageName, '/admin') || fn:startsWith(pageName, '/countries') || fn:startsWith(pageName, '/activity-centers')}">active</c:if>">
					<a href="#" class="dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"> <i class="menu-icon fa fa-users-cog"></i><spring:message code="sidebar.admin" /></a>
					<ul class="sub-menu children dropdown-menu">
						<li><i class="fas fa-map-marked-alt"></i><a href="/activity-centers"><spring:message code="sidebar.admin.activity.centers" /></a></li>
						<li><i class="fa fa-people-carry"></i><a href="/admin/families"><spring:message code="sidebar.admin.families" /></a></li>
						<li><i class="fas fa-flag"></i><a href="/countries"><spring:message code="sidebar.admin.countries" /></a></li>
						<li><i class="fas fa-plane-departure"></i><a href="/admin/holidays"><spring:message code="sidebar.admin.holidays" /></a></li>
						<li><i class="fas fa-file"></i><a href="/admin/summaries"><spring:message code="sidebar.admin.summaries" /></a></li>
					</ul>
				</li>
			</sec:authorize>
		</ul>
	</div><!-- /.navbar-collapse -->
</nav>
<!-- /#left-panel -->
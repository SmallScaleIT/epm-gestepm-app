package com.epm.gestepm.rest.signings.teleworking;

import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.modelapi.common.utils.ModelUtil;
import com.epm.gestepm.modelapi.common.utils.classes.Constants;
import com.epm.gestepm.modelapi.deprecated.project.dto.Project;
import com.epm.gestepm.modelapi.project.dto.ProjectDto;
import com.epm.gestepm.modelapi.project.dto.filter.ProjectFilterDto;
import com.epm.gestepm.modelapi.project.dto.finder.ProjectByIdFinderDto;
import com.epm.gestepm.modelapi.project.service.ProjectService;
import com.epm.gestepm.modelapi.signings.teleworking.dto.TeleworkingSigningDto;
import com.epm.gestepm.modelapi.signings.teleworking.dto.finder.TeleworkingSigningByIdFinderDto;
import com.epm.gestepm.modelapi.signings.teleworking.service.TeleworkingSigningService;
import com.epm.gestepm.modelapi.deprecated.user.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.VIEW;
import static com.epm.gestepm.lib.logging.constants.LogOperations.OP_VIEW;
import static com.epm.gestepm.modelapi.project.dto.ProjectTypeDto.*;

@Controller
@RequiredArgsConstructor
@EnableExecutionLog(layerMarker = VIEW)
public class TeleworkingSigningViewController {

    private final TeleworkingSigningService teleworkingSigningService;

    private final ProjectService projectService;

    @ModelAttribute
    public User loadCommonModelView(final Locale locale, final Model model) {
        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return ModelUtil.loadConstants(locale, model, request); // FIXME: this.setCommonView(model);
    }

    @GetMapping("/signings/teleworking")
    @LogExecution(operation = OP_VIEW)
    public String viewPersonalExpensesSheets(final Locale locale, final Model model) {

        this.loadCommonModelView(locale, model);
        this.loadProjects(model);

        return "teleworking-signings";
    }

    @GetMapping("/signings/teleworking/{id}")
    @LogExecution(operation = OP_VIEW)
    public String viewPersonalExpensesSheet(@PathVariable final Integer id, final Locale locale, final Model model) {

        final User user = this.loadCommonModelView(locale, model);

        this.loadProjects(model);

        final TeleworkingSigningDto teleworkingSigning = this.teleworkingSigningService.findOrNotFound(new TeleworkingSigningByIdFinderDto(id));
        model.addAttribute("teleworkingSigning", teleworkingSigning);

        final ProjectDto project = this.projectService.findOrNotFound(new ProjectByIdFinderDto(teleworkingSigning.getProjectId()));
        model.addAttribute("projectName", project.getName());

        this.loadPermissions(user, project.getId().intValue(), model, teleworkingSigning);

        return "teleworking-signing-detail";
    }

    private void loadProjects(final Model model) {
        final ProjectFilterDto projectFilterDto = new ProjectFilterDto();
        projectFilterDto.setTypes(List.of(OFFICE, TELEWORKING));

        final List<ProjectDto> projects = this.projectService.list(projectFilterDto);
        model.addAttribute("projects", projects);
    }

    private void loadPermissions(final User user, final Integer projectId, final Model model
            , final TeleworkingSigningDto teleworkingSigning) {
        final Boolean isAdmin = Constants.ROLE_ADMIN.equals(user.getRole().getRoleName());
        final Boolean isProjectTL = Constants.ROLE_PL.equals(user.getRole().getRoleName())
                && user.getBossProjects().stream().map(Project::getId).collect(Collectors.toList()).contains(projectId.longValue());
        final Boolean isCurrentUser = teleworkingSigning.getUserId().equals(user.getId().intValue());

        model.addAttribute("canUpdate"
                , isAdmin || isProjectTL || isCurrentUser);
    }
}

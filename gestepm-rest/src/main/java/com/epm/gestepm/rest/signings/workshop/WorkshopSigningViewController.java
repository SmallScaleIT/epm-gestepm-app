package com.epm.gestepm.rest.signings.workshop;

import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.modelapi.common.utils.ModelUtil;
import com.epm.gestepm.modelapi.common.utils.classes.Constants;
import com.epm.gestepm.modelapi.deprecated.user.dto.User;
import com.epm.gestepm.modelapi.project.dto.ProjectDto;
import com.epm.gestepm.modelapi.project.dto.filter.ProjectFilterDto;
import com.epm.gestepm.modelapi.project.dto.finder.ProjectByIdFinderDto;
import com.epm.gestepm.modelapi.project.service.ProjectService;
import com.epm.gestepm.modelapi.signings.workshop.dto.WorkShopSigningDto;
import com.epm.gestepm.modelapi.signings.workshop.dto.finder.WorkshopSigningByIdFinderDto;
import com.epm.gestepm.modelapi.signings.workshop.service.WorkshopSigningService;
import com.epm.gestepm.modelapi.user.dto.UserDto;
import com.epm.gestepm.modelapi.user.dto.filter.UserFilterDto;
import com.epm.gestepm.modelapi.user.service.UserService;
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

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.VIEW;
import static com.epm.gestepm.lib.logging.constants.LogOperations.OP_VIEW;
import static com.epm.gestepm.modelapi.project.dto.ProjectTypeDto.NORMAL;
import static com.epm.gestepm.modelapi.project.dto.ProjectTypeDto.STATION;

@Controller
@RequiredArgsConstructor
@EnableExecutionLog(layerMarker = VIEW)
public class WorkshopSigningViewController {

    private final WorkshopSigningService service;
    private final ProjectService projectService;
    private final UserService userService;

    @ModelAttribute
    public User loadCommonModelView(final Locale locale, final Model model) {
        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return ModelUtil.loadConstants(locale, model, request);
    }

    @GetMapping("/signings/workshop-signings/resume")
    public String viewResume(final Locale locale, final Model model) {
        this.loadCommonModelView(locale, model);
        this.loadProjects(model);
        this.loadUsers(model);

        return "signing-resume";
    }

    @GetMapping("/signings/warehouse/{warehouseSigningId}/workshop-signings/{id}")
    @LogExecution(operation = OP_VIEW)
    public String viewWorkshopSigningDetail(@PathVariable("id") final Integer id
            , final Locale locale, final Model model) {

        final User user = this.loadCommonModelView(locale, model);

        final WorkShopSigningDto workShopSigning = service.findOrNotFound(new WorkshopSigningByIdFinderDto(id));

        model.addAttribute("workshopSigning", workShopSigning);

        final ProjectDto project = projectService.findOrNotFound(new ProjectByIdFinderDto(workShopSigning.getProjectId()));
        model.addAttribute("projectName", project.getName());
        model.addAttribute("projectId", project.getId());

        this.loadPermissions(user, workShopSigning.getProjectId(), model);

        return "workshop-signing-detail";
    }

    private void loadPermissions(final User user, final Integer projectId, final Model model) {
        boolean isAdmin = Constants.ROLE_ADMIN.equals(user.getRole().getRoleName());

        boolean isProjectTl = Constants.ROLE_PL.equals(user.getRole().getRoleName())
                && user.getBossProjects().stream().anyMatch(project -> project.getId().equals(projectId.longValue()));

        model.addAttribute("canUpdate", isAdmin || isProjectTl);
    }

    private void loadProjects(final Model model) {
        final ProjectFilterDto projectFilterDto = new ProjectFilterDto();
        projectFilterDto.setTypes(List.of(NORMAL, STATION));
        projectFilterDto.setOrder("ASC");
        projectFilterDto.setOrderBy("name");

        final List<ProjectDto> projects = this.projectService.list(projectFilterDto);
        model.addAttribute("projects", projects);
    }

    private void loadUsers(final Model model) {
        final List<UserDto> users = this.userService.list(new UserFilterDto());
        model.addAttribute("users", users);
    }
}

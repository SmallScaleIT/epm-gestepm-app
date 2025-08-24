package com.epm.gestepm.rest.signings.warehouse;

import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.modelapi.common.utils.ModelUtil;
import com.epm.gestepm.modelapi.common.utils.classes.Constants;
import com.epm.gestepm.modelapi.deprecated.user.dto.User;
import com.epm.gestepm.modelapi.project.dto.ProjectDto;
import com.epm.gestepm.modelapi.project.dto.filter.ProjectFilterDto;
import com.epm.gestepm.modelapi.project.dto.finder.ProjectByIdFinderDto;
import com.epm.gestepm.modelapi.project.service.ProjectService;
import com.epm.gestepm.modelapi.signings.warehouse.dto.WarehouseSigningDto;
import com.epm.gestepm.modelapi.signings.warehouse.dto.finder.WarehouseSigningByIdFinderDto;
import com.epm.gestepm.modelapi.signings.warehouse.service.WarehouseSigningService;
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
import static com.epm.gestepm.modelapi.project.dto.ProjectTypeDto.*;
import static com.epm.gestepm.modelapi.project.dto.ProjectTypeDto.STATION;

@Controller
@RequiredArgsConstructor
@EnableExecutionLog(layerMarker = VIEW)
public class WarehouseSigningViewController {

    private final WarehouseSigningService warehouseService;
    private final ProjectService projectService;

    @ModelAttribute
    public User loadCommonModelView(final Locale locale, final Model model) {
        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return ModelUtil.loadConstants(locale, model, request);
    }

    @GetMapping("/signings/warehouse")
    @LogExecution(operation = OP_VIEW)
    public String viewWarehouseSigning(final Locale locale, final Model model) {
        this.loadCommonModelView(locale, model);
        this.loadProjects(model);

        return "warehouse-signings";
    }

    @GetMapping("/signings/warehouse/{id}")
    @LogExecution(operation = OP_VIEW)
    public String viewWarehouseSigningDetail(@PathVariable final Integer id, final Locale locale, final Model model) {

        final User user = this.loadCommonModelView(locale, model);

        this.loadProjects(model);

        final WarehouseSigningDto warehouseSigning = warehouseService.findOrNotFound(new WarehouseSigningByIdFinderDto(id));
        model.addAttribute("warehouseSigning", warehouseSigning);

        final ProjectDto project = projectService.findOrNotFound(new ProjectByIdFinderDto(warehouseSigning.getProjectId()));
        model.addAttribute("projectName", project.getName());

        this.loadPermissions(user, project.getId(), model);

        return "warehouse-signing-detail";
    }

    private void loadProjects(final Model model) {
        final ProjectFilterDto projectFilterDto = new ProjectFilterDto();
        projectFilterDto.setTypes(List.of(NORMAL, STATION));

        final List<ProjectDto> projects = this.projectService.list(projectFilterDto);
        model.addAttribute("projects", projects);
    }

    private void loadPermissions(final User user, final Integer projectId, final Model model) {
        boolean isAdmin = Constants.ROLE_ADMIN.equals(user.getRole().getRoleName());

        boolean isProjectTl = Constants.ROLE_PL.equals(user.getRole().getRoleName())
                && user.getBossProjects().stream().anyMatch(project -> project.getId().equals(projectId.longValue()));

        model.addAttribute("canUpdate", isAdmin || isProjectTl);
    }
}

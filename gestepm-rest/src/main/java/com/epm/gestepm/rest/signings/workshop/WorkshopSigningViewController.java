package com.epm.gestepm.rest.signings.workshop;

import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.modelapi.common.utils.ModelUtil;
import com.epm.gestepm.modelapi.common.utils.classes.Constants;
import com.epm.gestepm.modelapi.deprecated.user.dto.User;
import com.epm.gestepm.modelapi.project.dto.ProjectDto;
import com.epm.gestepm.modelapi.project.dto.finder.ProjectByIdFinderDto;
import com.epm.gestepm.modelapi.project.service.ProjectService;
import com.epm.gestepm.modelapi.signings.workshop.dto.WorkShopSigningDto;
import com.epm.gestepm.modelapi.signings.workshop.dto.finder.WorkshopSigningByIdFinderDto;
import com.epm.gestepm.modelapi.signings.workshop.service.WorkshopSigningService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.VIEW;
import static com.epm.gestepm.lib.logging.constants.LogOperations.OP_VIEW;

@Controller
@RequiredArgsConstructor
@EnableExecutionLog(layerMarker = VIEW)
public class WorkshopSigningViewController {

    private final WorkshopSigningService service;

    private final ProjectService projectService;

    @ModelAttribute
    public User loadCommonModelView(final Locale locale, final Model model) {
        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return ModelUtil.loadConstants(locale, model, request);
    }

    @GetMapping("/admin/summaries")
    @LogExecution(operation = OP_VIEW)
    public String viewResume(final Locale locale, final Model model) {
        this.loadCommonModelView(locale, model);

        model.addAttribute("importPath", "admin-summaries");
        model.addAttribute("loadingPath", "admin");
        model.addAttribute("type", "summaries");

        return "admin-summaries";
    }

    @GetMapping("/signings/warehouse/{warehouseSigningId}/workshop-signings/{id}")
    @LogExecution(operation = OP_VIEW)
    public String viewWorkshopSigningDetail(@PathVariable("id") final Integer id, final Locale locale
            , final Model model) {

        final User user = this.loadCommonModelView(locale, model);

        final WorkShopSigningDto workShopSigning = service.findOrNotFound(new WorkshopSigningByIdFinderDto(id));
        model.addAttribute("workshopSigning", workShopSigning);

        final ProjectDto project = projectService.findOrNotFound(new ProjectByIdFinderDto(workShopSigning.getProjectId()));
        model.addAttribute("projectName", project.getName());

        this.loadPermissions(user, project.getId(), model, workShopSigning);

        return "workshop-signing-detail";
    }

    private void loadPermissions(final User user, final Integer projectId, final Model model
            , WorkShopSigningDto workShopSigning) {
        final Boolean isAdmin = Constants.ROLE_ADMIN.equals(user.getRole().getRoleName());

        final Boolean isProjectTl = Constants.ROLE_PL.equals(user.getRole().getRoleName())
                && user.getBossProjects().stream().anyMatch(project -> project.getId().equals(projectId.longValue()));

        final Boolean isCurrentUser = workShopSigning.getUserId().equals(user.getId().intValue());

        model.addAttribute("canUpdate", isAdmin || isProjectTl || isCurrentUser);
    }
}

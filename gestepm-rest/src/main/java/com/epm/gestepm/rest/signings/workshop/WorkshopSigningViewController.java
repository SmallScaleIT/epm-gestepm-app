package com.epm.gestepm.rest.signings.workshop;

import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.modelapi.common.utils.ModelUtil;
import com.epm.gestepm.modelapi.common.utils.classes.Constants;
import com.epm.gestepm.modelapi.deprecated.user.dto.User;
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

    @ModelAttribute
    public User loadCommonModelView(final Locale locale, final Model model) {
        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return ModelUtil.loadConstants(locale, model, request);
    }

    @GetMapping("/signings/warehouse/{warehouseSigningId}/workshop-signings/{id}")
    @LogExecution(operation = OP_VIEW)
    public String viewWorkshopSigningDetail(@PathVariable("id") final Integer id
            , final Locale locale, final Model model) {

        final User user = this.loadCommonModelView(locale, model);

        final WorkShopSigningDto workShopSigning = service.findOrNotFound(new WorkshopSigningByIdFinderDto(id));

        model.addAttribute("workshopSigning", workShopSigning);
        this.loadPermissions(user, workShopSigning.getProjectId(), model);

        return "workshop-signing-detail";
    }

    private void loadPermissions(final User user, final Integer projectId, final Model model) {
        boolean isAdmin = Constants.ROLE_ADMIN.equals(user.getRole().getRoleName());

        boolean isProjectTl = Constants.ROLE_PL.equals(user.getRole().getRoleName())
                && user.getBossProjects().stream().anyMatch(project -> project.getId().equals(projectId.longValue()));

        model.addAttribute("canUpdate", isAdmin || isProjectTl);
    }
}

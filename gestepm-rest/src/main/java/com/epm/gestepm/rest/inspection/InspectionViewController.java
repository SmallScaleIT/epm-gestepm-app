package com.epm.gestepm.rest.inspection;

import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.modelapi.common.utils.ModelUtil;
import com.epm.gestepm.modelapi.common.utils.classes.Constants;
import com.epm.gestepm.modelapi.inspection.dto.InspectionDto;
import com.epm.gestepm.modelapi.inspection.dto.finder.InspectionByIdFinderDto;
import com.epm.gestepm.modelapi.inspection.service.InspectionService;
import com.epm.gestepm.modelapi.role.dto.RoleDTO;
import com.epm.gestepm.modelapi.shares.breaks.dto.ShareBreakDto;
import com.epm.gestepm.modelapi.shares.breaks.dto.filter.ShareBreakFilterDto;
import com.epm.gestepm.modelapi.shares.breaks.service.ShareBreakService;
import com.epm.gestepm.modelapi.shares.common.dto.ShareStatusDto;
import com.epm.gestepm.modelapi.shares.noprogrammed.dto.NoProgrammedShareDto;
import com.epm.gestepm.modelapi.shares.noprogrammed.dto.finder.NoProgrammedShareByIdFinderDto;
import com.epm.gestepm.modelapi.shares.noprogrammed.service.NoProgrammedShareService;
import com.epm.gestepm.modelapi.signings.warehouse.dto.WarehouseSigningDto;
import com.epm.gestepm.modelapi.subfamily.service.SubFamilyService;
import com.epm.gestepm.modelapi.deprecated.user.dto.User;
import lombok.AllArgsConstructor;
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

@Controller
@AllArgsConstructor
@EnableExecutionLog(layerMarker = VIEW)
public class InspectionViewController {

    private final InspectionService inspectionService;

    private final NoProgrammedShareService noProgrammedShareService;

    private final ShareBreakService shareBreakService;

    private final SubFamilyService subFamilyService;

    @ModelAttribute
    public User loadCommonModelView(final Locale locale, final Model model) {

        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return ModelUtil.loadConstants(locale, model, request); // FIXME: this.setCommonView(model);
    }

    @GetMapping("/shares/no-programmed/{shareId}/inspections/{id}")
    @LogExecution(operation = OP_VIEW)
    public String viewInspection(@PathVariable final Integer shareId, @PathVariable final Integer id, final Locale locale, final Model model) {

        final User user = this.loadCommonModelView(locale, model);

        final NoProgrammedShareDto noProgrammedShare = this.noProgrammedShareService.findOrNotFound(new NoProgrammedShareByIdFinderDto(shareId));
        final List<RoleDTO> subRoles = this.subFamilyService.getSubRolsById(noProgrammedShare.getSubFamilyId().longValue());

        final String userLevel = user.getSubRole().getRol();
        final boolean hasRole = subRoles.isEmpty()
                || subRoles.stream().anyMatch(subRole -> subRole.getName().equals(userLevel));

        model.addAttribute("hasRole", hasRole);

        final InspectionDto inspection = this.inspectionService.findOrNotFound(new InspectionByIdFinderDto(id));
        model.addAttribute("inspection", inspection);

        final ShareBreakFilterDto shareBreakFilterDto = new ShareBreakFilterDto();
        shareBreakFilterDto.setInspectionIds(List.of(id));
        shareBreakFilterDto.setStatus(ShareStatusDto.NOT_FINISHED);

        final Page<ShareBreakDto> list = this.shareBreakService.list(shareBreakFilterDto, 0L, 1L);
        if (!list.isEmpty() && list.get(0).isPresent()) {
            model.addAttribute("currentShareBreak", list.get(0).get());
        }

        loadPermissions(user, inspection.getProjectId(), model, inspection);

        return "inspection-detail";
    }

    private void loadPermissions(final User user, final Integer projectId, final Model model
            , final InspectionDto inspection) {
        final Boolean isAdmin = Constants.ROLE_ADMIN.equals(user.getRole().getRoleName());

        final Boolean isProjectTl = Constants.ROLE_PL.equals(user.getRole().getRoleName())
                && user.getBossProjects().stream().anyMatch(project -> project.getId().equals(projectId.longValue()));

        final Boolean isCurrentUser = inspection.getFirstTechnicalId().equals(user.getId().intValue());

        model.addAttribute("canUpdate", isAdmin || isProjectTl || isCurrentUser);
    }
}

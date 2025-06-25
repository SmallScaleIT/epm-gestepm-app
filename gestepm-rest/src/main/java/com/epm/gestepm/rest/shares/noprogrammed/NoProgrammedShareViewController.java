package com.epm.gestepm.rest.shares.noprogrammed;

import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.modelapi.common.utils.ModelUtil;
import com.epm.gestepm.modelapi.common.utils.datatables.SortOrder;
import com.epm.gestepm.modelapi.family.dto.FamilyDTO;
import com.epm.gestepm.modelapi.family.service.FamilyService;
import com.epm.gestepm.modelapi.inspection.dto.ActionEnumDto;
import com.epm.gestepm.modelapi.inspection.dto.InspectionDto;
import com.epm.gestepm.modelapi.inspection.dto.filter.InspectionFilterDto;
import com.epm.gestepm.modelapi.inspection.service.InspectionService;
import com.epm.gestepm.modelapi.project.dto.ProjectDto;
import com.epm.gestepm.modelapi.project.dto.filter.ProjectFilterDto;
import com.epm.gestepm.modelapi.project.dto.finder.ProjectByIdFinderDto;
import com.epm.gestepm.modelapi.project.service.ProjectService;
import com.epm.gestepm.modelapi.shares.noprogrammed.dto.NoProgrammedShareDto;
import com.epm.gestepm.modelapi.shares.noprogrammed.dto.finder.NoProgrammedShareByIdFinderDto;
import com.epm.gestepm.modelapi.shares.noprogrammed.service.NoProgrammedShareService;
import com.epm.gestepm.modelapi.user.dto.UserDto;
import com.epm.gestepm.modelapi.user.dto.filter.UserFilterDto;
import com.epm.gestepm.modelapi.user.service.UserService;
import com.epm.gestepm.modelapi.deprecated.user.dto.User;
import com.epm.gestepm.modelapi.deprecated.user.dto.UserDTO;
import com.epm.gestepm.modelapi.deprecated.user.service.UserServiceOld;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
@RequiredArgsConstructor
@EnableExecutionLog(layerMarker = VIEW)
public class NoProgrammedShareViewController {

    @Value("${gestepm.forum.url}")
    private String forumUrl;

    private final FamilyService familyService;

    private final InspectionService inspectionService;

    private final NoProgrammedShareService noProgrammedShareService;

    private final ProjectService projectService;

    private final UserService userService;

    private final UserServiceOld userServiceOld;

    @ModelAttribute
    public User loadCommonModelView(final Locale locale, final Model model) {
        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return ModelUtil.loadConstants(locale, model, request); // FIXME: this.setCommonView(model);
    }

    @GetMapping("/shares/no-programmed")
    @LogExecution(operation = OP_VIEW)
    public String viewNoProgrammedSharePage(final Locale locale, final Model model) {

        this.loadCommonModelView(locale, model);

        final ProjectFilterDto projectFilterDto = new ProjectFilterDto();
        projectFilterDto.setIsTeleworking(false);

        final List<ProjectDto> projects = this.projectService.list(projectFilterDto);
        model.addAttribute("projects", projects);

        final UserFilterDto filterDto = new UserFilterDto();
        filterDto.setOrder(SortOrder.ASC.value());
        filterDto.setOrderBy("fullName");

        final List<UserDto> users = this.userService.list(filterDto);
        model.addAttribute("users", users);

        return "no-programmed-share";
    }

    @GetMapping("/shares/no-programmed/{id}")
    @LogExecution(operation = OP_VIEW)
    public String viewNoProgrammedShareDetailPage(@PathVariable final Integer id, final Locale locale, final Model model) {

        this.loadCommonModelView(locale, model);

        final NoProgrammedShareDto share = this.noProgrammedShareService.findOrNotFound(new NoProgrammedShareByIdFinderDto(id));
        final ProjectDto project = this.projectService.findOrNotFound(new ProjectByIdFinderDto(share.getProjectId()));

        final InspectionFilterDto filterDto = new InspectionFilterDto();
        filterDto.setShareId(id);
        filterDto.setOrder("DESC");
        filterDto.setOrderBy("id");

        final ActionEnumDto lastAction = this.inspectionService.list(filterDto, 0L, 1L).get(0)
                .map(InspectionDto::getAction)
                .orElse(ActionEnumDto.FOLLOWING);

        final List<FamilyDTO> families = familyService.getCommonFamilyDTOsByProjectId(share.getProjectId().longValue(), locale);
        final List<UserDTO> usersTeam = userServiceOld.getUserDTOsByProjectId(share.getProjectId().longValue());

        model.addAttribute("families", families);
        model.addAttribute("usersTeam", usersTeam);
        model.addAttribute("nextAction", ActionEnumDto.getNextAction(lastAction));

        if (project.getForumId() != null && share.getTopicId() != null) {
            model.addAttribute("forumUrl", forumUrl + "/viewtopic.php?f=" + project.getForumId() + "&t=" + share.getTopicId());
        }

        return "no-programmed-share-detail";
    }
}

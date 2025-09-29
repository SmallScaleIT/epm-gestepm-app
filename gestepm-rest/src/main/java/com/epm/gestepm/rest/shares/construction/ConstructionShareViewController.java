package com.epm.gestepm.rest.shares.construction;

import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.modelapi.common.utils.ModelUtil;
import com.epm.gestepm.modelapi.common.utils.classes.Constants;
import com.epm.gestepm.modelapi.common.utils.datatables.SortOrder;
import com.epm.gestepm.modelapi.deprecated.project.dto.Project;
import com.epm.gestepm.modelapi.project.dto.ProjectDto;
import com.epm.gestepm.modelapi.project.dto.filter.ProjectFilterDto;
import com.epm.gestepm.modelapi.project.service.ProjectService;
import com.epm.gestepm.modelapi.shares.breaks.dto.ShareBreakDto;
import com.epm.gestepm.modelapi.shares.breaks.dto.filter.ShareBreakFilterDto;
import com.epm.gestepm.modelapi.shares.breaks.service.ShareBreakService;
import com.epm.gestepm.modelapi.shares.common.dto.ShareStatusDto;
import com.epm.gestepm.modelapi.shares.construction.dto.ConstructionShareDto;
import com.epm.gestepm.modelapi.shares.construction.dto.ConstructionShareFileDto;
import com.epm.gestepm.modelapi.shares.construction.dto.filter.ConstructionShareFileFilterDto;
import com.epm.gestepm.modelapi.shares.construction.dto.finder.ConstructionShareByIdFinderDto;
import com.epm.gestepm.modelapi.shares.construction.service.ConstructionShareFileService;
import com.epm.gestepm.modelapi.shares.construction.service.ConstructionShareService;
import com.epm.gestepm.modelapi.user.dto.UserDto;
import com.epm.gestepm.modelapi.user.dto.filter.UserFilterDto;
import com.epm.gestepm.modelapi.user.service.UserService;
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
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.VIEW;
import static com.epm.gestepm.lib.logging.constants.LogOperations.OP_VIEW;
import static com.epm.gestepm.modelapi.project.dto.ProjectTypeDto.NORMAL;
import static com.epm.gestepm.modelapi.project.dto.ProjectTypeDto.STATION;

@Controller
@AllArgsConstructor
@EnableExecutionLog(layerMarker = VIEW)
public class ConstructionShareViewController {

    private final ConstructionShareService constructionShareService;

    private final ConstructionShareFileService constructionShareFileService;

    private final ProjectService projectService;

    private final ShareBreakService shareBreakService;

    private final UserService userService;

    @ModelAttribute
    public User loadCommonModelView(final Locale locale, final Model model) {
        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return ModelUtil.loadConstants(locale, model, request); // FIXME: this.setCommonView(model);
    }

    @GetMapping("/shares/construction")
    @LogExecution(operation = OP_VIEW)
    public String viewConstructionSharePage(final Locale locale, final Model model) {

        this.loadCommonModelView(locale, model);

        final ProjectFilterDto projectFilterDto = new ProjectFilterDto();
        projectFilterDto.setTypes(List.of(NORMAL, STATION));

        final List<ProjectDto> projects = this.projectService.list(projectFilterDto);
        model.addAttribute("projects", projects);

        final UserFilterDto filterDto = new UserFilterDto();
        filterDto.setOrder(SortOrder.ASC.value());
        filterDto.setOrderBy("fullName");

        final List<UserDto> users = this.userService.list(filterDto);
        model.addAttribute("users", users);

        return "construction-share";
    }

    @GetMapping("/shares/construction/{id}")
    @LogExecution(operation = OP_VIEW)
    public String viewConstructionShareDetailPage(@PathVariable final Integer id, final Locale locale, final Model model) {

        final User user = this.loadCommonModelView(locale, model);

        final ConstructionShareDto constructionShare = this.constructionShareService.findOrNotFound(new ConstructionShareByIdFinderDto(id));
        model.addAttribute("constructionShare", constructionShare);

        final ProjectFilterDto projectFilterDto = new ProjectFilterDto();
        projectFilterDto.setTypes(List.of(NORMAL, STATION));

        final List<ProjectDto> projects = this.projectService.list(projectFilterDto);
        model.addAttribute("projects", projects);

        this.loadPermissions(user, constructionShare.getProjectId(), model, constructionShare);

        final ConstructionShareFileFilterDto filterDto = new ConstructionShareFileFilterDto();
        filterDto.setShareId(id);

        final List<ConstructionShareFileDto> files = this.constructionShareFileService.list(filterDto).stream()
                .sorted(Comparator.comparing(ConstructionShareFileDto::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
        model.addAttribute("files", files);

        final ShareBreakFilterDto shareBreakFilterDto = new ShareBreakFilterDto();
        shareBreakFilterDto.setConstructionShareIds(List.of(id));
        shareBreakFilterDto.setStatus(ShareStatusDto.NOT_FINISHED);

        final Page<ShareBreakDto> list = this.shareBreakService.list(shareBreakFilterDto, 0L, 1L);
        if (!list.isEmpty() && list.get(0).isPresent()) {
            model.addAttribute("currentShareBreak", list.get(0).get());
        }

        return "construction-share-detail";
    }

    private void loadPermissions(final User user, final Integer projectId, final Model model
            , final ConstructionShareDto constructionShare) {
        final Boolean isAdmin = Constants.ROLE_ADMIN.equals(user.getRole().getRoleName());
        final Boolean isProjectTL = Constants.ROLE_PL.equals(user.getRole().getRoleName())
                && user.getBossProjects().stream().map(Project::getId).collect(Collectors.toList()).contains(projectId.longValue());
        final Boolean isCurrentUser = constructionShare.getUserId().equals(user.getId().intValue());

        model.addAttribute("canUpdate"
                , isAdmin || isProjectTL || isCurrentUser);
    }
}

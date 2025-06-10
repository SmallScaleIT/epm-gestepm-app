package com.epm.gestepm.rest.shares.work;

import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.modelapi.common.utils.ModelUtil;
import com.epm.gestepm.modelapi.common.utils.classes.Constants;
import com.epm.gestepm.modelapi.common.utils.datatables.SortOrder;
import com.epm.gestepm.modelapi.project.dto.ProjectListDTO;
import com.epm.gestepm.modelapi.project.service.ProjectService;
import com.epm.gestepm.modelapi.shares.breaks.dto.ShareBreakDto;
import com.epm.gestepm.modelapi.shares.breaks.dto.filter.ShareBreakFilterDto;
import com.epm.gestepm.modelapi.shares.breaks.service.ShareBreakService;
import com.epm.gestepm.modelapi.shares.common.dto.ShareStatusDto;
import com.epm.gestepm.modelapi.shares.work.dto.WorkShareDto;
import com.epm.gestepm.modelapi.shares.work.dto.WorkShareFileDto;
import com.epm.gestepm.modelapi.shares.work.dto.filter.WorkShareFileFilterDto;
import com.epm.gestepm.modelapi.shares.work.dto.finder.WorkShareByIdFinderDto;
import com.epm.gestepm.modelapi.shares.work.service.WorkShareFileService;
import com.epm.gestepm.modelapi.shares.work.service.WorkShareService;
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

@Controller
@AllArgsConstructor
@EnableExecutionLog(layerMarker = VIEW)
public class WorkShareViewController {

    private final WorkShareService workShareService;

    private final WorkShareFileService workShareFileService;

    private final ProjectService projectService;

    private final ShareBreakService shareBreakService;

    private final UserService userService;

    @ModelAttribute
    public User loadCommonModelView(final Locale locale, final Model model) {
        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return ModelUtil.loadConstants(locale, model, request); // FIXME: this.setCommonView(model);
    }

    @GetMapping("/shares/work")
    @LogExecution(operation = OP_VIEW)
    public String viewWorkSharePage(final Locale locale, final Model model) {

        this.loadCommonModelView(locale, model);

        final List<ProjectListDTO> projects = this.projectService.getTeleworkingProjects(false).stream()
                .sorted(Comparator.comparing(ProjectListDTO::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
        model.addAttribute("projects", projects);

        final UserFilterDto filterDto = new UserFilterDto();
        filterDto.setOrder(SortOrder.ASC.value());
        filterDto.setOrderBy("fullName");

        final List<UserDto> users = this.userService.list(filterDto);
        model.addAttribute("users", users);

        return "work-share";
    }

    @GetMapping("/shares/work/{id}")
    @LogExecution(operation = OP_VIEW)
    public String viewWorkShareDetailPage(@PathVariable final Integer id, final Locale locale, final Model model) {

        final User user = this.loadCommonModelView(locale, model);

        final WorkShareDto workShare = this.workShareService.findOrNotFound(new WorkShareByIdFinderDto(id));
        model.addAttribute("workShare", workShare);

        final List<ProjectListDTO> projects = this.projectService.getTeleworkingProjects(false).stream()
                .sorted(Comparator.comparing(ProjectListDTO::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
        model.addAttribute("projects", projects);

        boolean canUpdate = Constants.ROLE_ADMIN.equals(user.getRole().getRoleName());
        model.addAttribute("canUpdate", canUpdate);

        final WorkShareFileFilterDto filterDto = new WorkShareFileFilterDto();
        filterDto.setShareId(id);

        final List<WorkShareFileDto> files = this.workShareFileService.list(filterDto).stream()
                .sorted(Comparator.comparing(WorkShareFileDto::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
        model.addAttribute("files", files);

        final ShareBreakFilterDto shareBreakFilterDto = new ShareBreakFilterDto();
        shareBreakFilterDto.setWorkShareIds(List.of(id));
        shareBreakFilterDto.setStatus(ShareStatusDto.NOT_FINISHED);

        final Page<ShareBreakDto> list = this.shareBreakService.list(shareBreakFilterDto, 0L, 1L);
        if (!list.isEmpty() && list.get(0).isPresent()) {
            model.addAttribute("currentShareBreak", list.get(0).get());
        }

        return "work-share-detail";
    }
}

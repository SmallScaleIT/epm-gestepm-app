package com.epm.gestepm.rest.personalexpensesheet;

import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.modelapi.common.utils.ModelUtil;
import com.epm.gestepm.modelapi.common.utils.classes.Constants;
import com.epm.gestepm.modelapi.personalexpensesheet.dto.PersonalExpenseSheetDto;
import com.epm.gestepm.modelapi.personalexpensesheet.dto.PersonalExpenseSheetStatusEnumDto;
import com.epm.gestepm.modelapi.personalexpensesheet.dto.filter.PersonalExpenseSheetFilterDto;
import com.epm.gestepm.modelapi.personalexpensesheet.service.PersonalExpenseSheetService;
import com.epm.gestepm.modelapi.deprecated.user.dto.User;
import com.epm.gestepm.modelapi.project.dto.ProjectDto;
import com.epm.gestepm.modelapi.project.dto.filter.ProjectFilterDto;
import com.epm.gestepm.modelapi.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.VIEW;
import static com.epm.gestepm.lib.logging.constants.LogOperations.OP_VIEW;

@Controller
@RequiredArgsConstructor
@EnableExecutionLog(layerMarker = VIEW)
public class PersonalExpenseSheetViewController {

    private final PersonalExpenseSheetService personalExpenseSheetService;

    private final ProjectService projectService;

    @ModelAttribute
    public User loadCommonModelView(final Locale locale, final Model model) {
        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return ModelUtil.loadConstants(locale, model, request); // FIXME: this.setCommonView(model);
    }

    @GetMapping("/expenses/personal/sheets")
    @LogExecution(operation = OP_VIEW)
    public String viewPersonalExpensesSheets(final Locale locale, final Model model) {

        final User user = this.loadCommonModelView(locale, model);

        final PersonalExpenseSheetFilterDto filterDto = new PersonalExpenseSheetFilterDto();
        filterDto.setCreatedBy(user.getId().intValue());

        final List<PersonalExpenseSheetDto> personalExpenseSheets
                = this.personalExpenseSheetService.list(filterDto);

        final AtomicReference<Double> pendingAmount = new AtomicReference<>((double) 0);
        Arrays.stream(PersonalExpenseSheetStatusEnumDto.values()).forEach(status -> {
            final List<PersonalExpenseSheetDto> filteredSheets
                    = personalExpenseSheets.stream().filter(sheet -> status.equals(sheet.getStatus())).collect(Collectors.toList());
            final Double totalAmount = filteredSheets.stream().map(PersonalExpenseSheetDto::getAmount).reduce(0.0, Double::sum);
            final String amount = new DecimalFormat("###,##0.00 €").format(totalAmount);

            if (status.equals(PersonalExpenseSheetStatusEnumDto.PENDING) || status.equals(PersonalExpenseSheetStatusEnumDto.APPROVED)) {
                pendingAmount.updateAndGet(v -> v + totalAmount);
            }

            model.addAttribute("amount" + status.name(), amount);
            model.addAttribute("count" + status.name(), filteredSheets.size());
        });

        final String amount = new DecimalFormat("###,##0.00 €").format(pendingAmount.get());
        model.addAttribute("totalAmountPENDING", amount);

        this.loadProjects(user, model);

        return "personal-expense-sheets";
    }

    @GetMapping("/expenses/personal/sheets/{id}")
    @LogExecution(operation = OP_VIEW)
    public String viewPersonalExpensesSheet(@PathVariable final Integer id, final Locale locale, final Model model) {

        final User user = this.loadCommonModelView(locale, model);

        this.loadProjects(user, model);

        return "personal-expense-sheet-detail";
    }

    private void loadProjects(final User user, final Model model) {

        final ProjectFilterDto filterDto = new ProjectFilterDto();

        if (!Objects.equals(user.getRole().getId(), Constants.ROLE_ADMIN_ID) && !Objects.equals(user.getRole().getId(), Constants.ROLE_TECHNICAL_SUPERVISOR_ID)) {
            filterDto.setMemberIds(List.of(user.getId().intValue()));
        }

        final List<ProjectDto> projects = this.projectService.list(filterDto);
        model.addAttribute("projects", projects);
    }
}

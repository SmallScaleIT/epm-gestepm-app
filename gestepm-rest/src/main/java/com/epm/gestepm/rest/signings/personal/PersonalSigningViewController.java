package com.epm.gestepm.rest.signings.personal;

import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.modelapi.common.utils.ModelUtil;
import com.epm.gestepm.modelapi.common.utils.classes.Constants;
import com.epm.gestepm.modelapi.deprecated.user.dto.User;
import com.epm.gestepm.modelapi.signings.personal.service.PersonalSigningService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.VIEW;
import static com.epm.gestepm.lib.logging.constants.LogOperations.OP_VIEW;

@Controller
@RequiredArgsConstructor
@EnableExecutionLog(layerMarker = VIEW)
public class PersonalSigningViewController {

    private final PersonalSigningService service;

    @ModelAttribute
    public User loadCommonModelView(final Locale locale, final Model model) {
        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return ModelUtil.loadConstants(locale, model, request);
    }

    @GetMapping("/signings/personal")
    @LogExecution(operation = OP_VIEW)
    public String viewPersonalSigning(final Locale locale, final Model model) {
        final User user = this.loadCommonModelView(locale, model);

        final Boolean isAdmin = Constants.ROLE_ADMIN.equals(user.getRole().getRoleName());

        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("userId", user.getId());

        return "personal-signing-view";
    }
}

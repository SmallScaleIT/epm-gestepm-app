package com.epm.gestepm.model.signings.checker;

import com.epm.gestepm.lib.user.UserProvider;
import com.epm.gestepm.lib.user.data.UserData;
import com.epm.gestepm.lib.user.data.UserLogin;
import com.epm.gestepm.modelapi.common.utils.classes.Constants;
import com.epm.gestepm.modelapi.deprecated.project.dto.Project;
import com.epm.gestepm.modelapi.signings.exception.SigningForbiddenException;
import com.epm.gestepm.modelapi.deprecated.user.dto.User;
import com.epm.gestepm.modelapi.deprecated.user.service.UserServiceOld;
import com.epm.gestepm.modelapi.user.dto.UserDto;
import com.epm.gestepm.modelapi.user.dto.filter.UserFilterDto;
import com.epm.gestepm.modelapi.user.dto.finder.UserByIdFinderDto;
import com.epm.gestepm.modelapi.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class SigningUpdateChecker {

    private final UserProvider userProvider;

    private final UserService userService;

    public void checker(final Integer userId, final Integer projectId) {
        final Integer currentUserId = this.getCurrentUserId();
        final UserDto currentUser = this.userService.findOrNotFound(new UserByIdFinderDto(currentUserId));
        final Boolean isAdmin = Constants.ROLE_ADMIN_ID == currentUser.getRoleId();
        final Boolean isProjectTL = Constants.ROLE_PL_ID == currentUser.getRoleId() && this.isProjectLeader(userId, projectId);
        final Boolean isOwner = Objects.equals(userId, currentUserId);
        final boolean canUpdate = isAdmin || isProjectTL || isOwner;

        if (!canUpdate) {
            throw new SigningForbiddenException(currentUserId);
        }
    }

    private Integer getCurrentUserId() {
        final Optional<UserLogin> userLogin = this.userProvider.get(UserLogin.class);
        return userLogin.map(UserData::getValue).orElse(null);
    }

    private Boolean isProjectLeader(final Integer projectId, final Integer userId) {
        final UserFilterDto filterDto = new UserFilterDto();
        filterDto.setLeadingProjectId(projectId);

        return this.userService.list(filterDto).stream().anyMatch(userDto -> userDto.getId().equals(userId));
    }
}

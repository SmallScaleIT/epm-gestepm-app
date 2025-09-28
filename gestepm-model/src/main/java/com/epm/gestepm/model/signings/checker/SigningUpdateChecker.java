package com.epm.gestepm.model.signings.checker;

import com.epm.gestepm.model.user.utils.UserUtils;
import com.epm.gestepm.modelapi.common.utils.classes.Constants;
import com.epm.gestepm.modelapi.signings.exception.SigningForbiddenException;
import com.epm.gestepm.modelapi.user.dto.UserDto;
import com.epm.gestepm.modelapi.user.dto.filter.UserFilterDto;
import com.epm.gestepm.modelapi.user.dto.finder.UserByIdFinderDto;
import com.epm.gestepm.modelapi.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@AllArgsConstructor
public class SigningUpdateChecker {

    private final UserService userService;

    private final UserUtils userUtils;

    public void checker(final Integer userId, final Integer projectId) {
        final Integer currentUserId = this.userUtils.getCurrentUserId();
        final UserDto currentUser = this.userService.findOrNotFound(new UserByIdFinderDto(currentUserId));

        final Boolean isAdmin = Constants.ROLE_ADMIN_ID.intValue() == currentUser.getRoleId();
        final Boolean isProjectTL = projectId != null && Constants.ROLE_PL_ID.intValue() == currentUser.getRoleId() && this.isProjectLeader(userId, projectId);
        final Boolean isOwner = Objects.equals(userId, currentUserId);

        final boolean canUpdate = isAdmin || isProjectTL || isOwner;

        if (!canUpdate) {
            throw new SigningForbiddenException(currentUserId);
        }
    }

    private Boolean isProjectLeader(final Integer projectId, final Integer userId) {
        final UserFilterDto filterDto = new UserFilterDto();
        filterDto.setLeadingProjectId(projectId);

        return this.userService.list(filterDto).stream().anyMatch(userDto -> userDto.getId().equals(userId));
    }
}

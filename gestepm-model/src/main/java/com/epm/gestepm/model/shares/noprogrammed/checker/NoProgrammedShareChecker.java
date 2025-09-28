package com.epm.gestepm.model.shares.noprogrammed.checker;

import com.epm.gestepm.model.user.utils.UserUtils;
import com.epm.gestepm.modelapi.project.dto.ProjectTypeDto;
import com.epm.gestepm.modelapi.project.exception.ProjectIsNotStationException;
import com.epm.gestepm.modelapi.deprecated.user.dto.User;
import com.epm.gestepm.modelapi.user.dto.UserDto;
import com.epm.gestepm.modelapi.user.dto.finder.UserByIdFinderDto;
import com.epm.gestepm.modelapi.user.exception.UserNotFoundException;
import com.epm.gestepm.modelapi.deprecated.user.service.UserServiceOld;
import com.epm.gestepm.modelapi.project.dto.ProjectDto;
import com.epm.gestepm.modelapi.project.dto.finder.ProjectByIdFinderDto;
import com.epm.gestepm.modelapi.project.service.ProjectService;
import com.epm.gestepm.modelapi.shares.noprogrammed.dto.NoProgrammedShareDto;
import com.epm.gestepm.modelapi.shares.noprogrammed.dto.NoProgrammedShareStateEnumDto;
import com.epm.gestepm.modelapi.shares.noprogrammed.dto.creator.NoProgrammedShareCreateDto;
import com.epm.gestepm.modelapi.shares.noprogrammed.dto.updater.NoProgrammedShareUpdateDto;
import com.epm.gestepm.modelapi.shares.noprogrammed.exception.NoProgrammedShareForbiddenException;
import com.epm.gestepm.modelapi.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Supplier;

import static com.epm.gestepm.modelapi.common.utils.classes.Constants.ROLE_ADMIN_ID;
import static com.epm.gestepm.modelapi.common.utils.classes.Constants.ROLE_PL_ID;

@Component
@AllArgsConstructor
public class NoProgrammedShareChecker {

    private final ProjectService projectService;

    private final UserService userService;

    private final UserUtils userUtils;

    public void checker(final NoProgrammedShareCreateDto dto) {
        this.checker(dto.getUserId(), dto.getProjectId(), dto, null);
    }

    public void checker(final NoProgrammedShareUpdateDto updateDto, final NoProgrammedShareDto dto) {
        this.checker(updateDto.getUserId(), dto.getProjectId(), null, updateDto);
    }

    private void checker(final Integer userId, final Integer projectId, final NoProgrammedShareCreateDto createDto, final NoProgrammedShareUpdateDto updateDto) {
        final boolean closeShare = updateDto != null && NoProgrammedShareStateEnumDto.CLOSED.equals(updateDto.getState());
        final UserDto userDto = this.userService.findOrNotFound(new UserByIdFinderDto(userId));

        if (closeShare) {
            this.validateCloseSharePermission(userDto);
        } else if (createDto != null) {
            this.validateProject(projectId);
        }
    }

    private void validateCloseSharePermission(final UserDto user) {
        if (!user.getId().equals(this.userUtils.getCurrentUserId())
                && !user.getRoleId().equals(ROLE_PL_ID.intValue())
                && !user.getRoleId().equals(ROLE_ADMIN_ID.intValue())) {
            throw new NoProgrammedShareForbiddenException(user.getId(), null);
        }
    }

    private void validateProject(final Integer projectId) {
        final ProjectDto project = this.projectService.findOrNotFound(new ProjectByIdFinderDto(projectId));

        if (!ProjectTypeDto.STATION.equals(project.getType())) {
            throw new ProjectIsNotStationException(projectId);
        }
    }
}

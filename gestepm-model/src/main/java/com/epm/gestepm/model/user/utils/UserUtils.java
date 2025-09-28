package com.epm.gestepm.model.user.utils;

import com.epm.gestepm.lib.user.UserProvider;
import com.epm.gestepm.lib.user.data.UserData;
import com.epm.gestepm.lib.user.data.UserLogin;
import com.epm.gestepm.modelapi.project.dto.ProjectDto;
import com.epm.gestepm.modelapi.user.dto.UserDto;
import com.epm.gestepm.modelapi.user.dto.filter.UserFilterDto;
import com.epm.gestepm.modelapi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserUtils {

    private final UserProvider userProvider;

    private final UserService userService;

    public Integer getCurrentUserId() {
        final Optional<UserLogin> userLogin = this.userProvider.get(UserLogin.class);
        return userLogin.map(UserData::getValue).orElse(null);
    }

    public Set<String> getResponsibleEmails(final ProjectDto project) {
        if (CollectionUtils.isEmpty(project.getResponsibleIds())) {
            return new HashSet<>();
        }

        final UserFilterDto filterDto = new UserFilterDto();
        filterDto.setIds(new ArrayList<>(project.getResponsibleIds()));

        return this.userService.list(filterDto).stream().map(UserDto::getEmail).collect(Collectors.toSet());
    }
}

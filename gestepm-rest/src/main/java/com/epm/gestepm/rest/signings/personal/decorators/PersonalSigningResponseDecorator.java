package com.epm.gestepm.rest.signings.personal.decorators;

import com.epm.gestepm.lib.controller.RestRequest;
import com.epm.gestepm.lib.controller.decorator.BaseResponseDataDecorator;
import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.modelapi.deprecated.user.service.UserServiceOld;
import com.epm.gestepm.modelapi.user.dto.UserDto;
import com.epm.gestepm.modelapi.user.dto.finder.UserByIdFinderDto;
import com.epm.gestepm.modelapi.user.service.UserService;
import com.epm.gestepm.rest.signings.personal.request.PersonalSigningFindRestRequest;
import com.epm.gestepm.restapi.openapi.model.PersonalSigning;
import com.epm.gestepm.restapi.openapi.model.User;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.DELEGATOR;

@Component("personalSigningResponseDecorator")
@EnableExecutionLog(layerMarker = DELEGATOR)
public class PersonalSigningResponseDecorator extends BaseResponseDataDecorator<PersonalSigning> {

    public static final String PRS_U_EXPAND = "user";

    private final UserService userService;

    protected PersonalSigningResponseDecorator(final ApplicationContext applicationContext, final UserService userService) {
        super(applicationContext);
        this.userService = userService;
    }

    @Override
    public void decorate(final RestRequest request, final PersonalSigning data) {

        if (request.getLinks()) {
            final PersonalSigningFindRestRequest selfRequest = new PersonalSigningFindRestRequest(data.getId());
            selfRequest.commonValuesFrom(request);
        }

        if (request.hasExpand(PRS_U_EXPAND)) {

            final User user = data.getUser();
            final Integer userId = user.getId();

            final UserDto userDto = userService.findOrNotFound(new UserByIdFinderDto(userId));
            final User response = new User().id(userDto.getId()).name(userDto.getName());

            data.setUser(response);
        }
    }
}

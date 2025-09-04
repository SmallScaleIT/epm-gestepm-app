package com.epm.gestepm.rest.signings.workshop.decorators;

import com.epm.gestepm.lib.controller.RestRequest;
import com.epm.gestepm.lib.controller.decorator.BaseResponseDataDecorator;
import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.modelapi.deprecated.user.service.UserServiceOld;
import com.epm.gestepm.modelapi.project.dto.ProjectDto;
import com.epm.gestepm.modelapi.project.dto.finder.ProjectByIdFinderDto;
import com.epm.gestepm.modelapi.project.service.ProjectService;
import com.epm.gestepm.rest.project.mappers.MapPRToProjectResponse;
import com.epm.gestepm.rest.signings.warehouse.request.WarehouseSigningFindRestRequest;
import com.epm.gestepm.restapi.openapi.model.Project;
import com.epm.gestepm.restapi.openapi.model.User;
import com.epm.gestepm.restapi.openapi.model.WorkshopSigning;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.DELEGATOR;
import static com.epm.gestepm.lib.logging.constants.LogOperations.OP_PROCESS;
import static org.mapstruct.factory.Mappers.getMapper;

@Component("workshopSigningResponseDecorator")
@EnableExecutionLog(layerMarker = DELEGATOR)
public class WorkshopSigningResponseDecorator extends BaseResponseDataDecorator<WorkshopSigning> {

    private final UserServiceOld userServiceOld;

    private final ProjectService projectService;

    public static final String WSS_U_EXPAND = "user";

    public static final String WSS_P_EXPAND = "project";

    public WorkshopSigningResponseDecorator(ApplicationContext context
            , UserServiceOld userServiceOld, ProjectService projectService) {
        super(context);
        this.userServiceOld = userServiceOld;
        this.projectService = projectService;
    }

    @Override
    @LogExecution(operation = OP_PROCESS,
            msgIn = "Decorating workshop signing response",
            msgOut = "Workshop signing decorated OK",
            errorMsg = "Error decorating workshop signing response")
    public void decorate(RestRequest request, WorkshopSigning data) {

        if (request.getLinks())
        {
            final WarehouseSigningFindRestRequest selfReq = new WarehouseSigningFindRestRequest(data.getId());
            selfReq.commonValuesFrom(request);
        }

        if (request.hasExpand(WSS_U_EXPAND))
        {
            User user = data.getUser();
            Long userId = Optional.ofNullable(user.getId()).map(Integer::longValue).orElse(null);

            com.epm.gestepm.modelapi.deprecated.user.dto.User userDto = userServiceOld.getUserById(userId);

            User userResponse = new User().id(user.getId()).name(userDto.getName());

            data.setUser(userResponse);
        }

        if (request.hasExpand(WSS_P_EXPAND))
        {
            Project project = data.getProject();

            ProjectByIdFinderDto finder = new ProjectByIdFinderDto(project.getId());
            ProjectDto projectDto = projectService.findOrNotFound(finder);

            Project projectResponse = getMapper(MapPRToProjectResponse.class)
                    .from(projectDto);

            data.setProject(projectResponse);
        }
    }
}

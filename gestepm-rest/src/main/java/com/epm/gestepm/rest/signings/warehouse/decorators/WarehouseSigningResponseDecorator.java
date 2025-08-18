package com.epm.gestepm.rest.signings.warehouse.decorators;

import com.epm.gestepm.lib.controller.RestRequest;
import com.epm.gestepm.lib.controller.decorator.BaseResponseDataDecorator;
import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.modelapi.deprecated.user.service.UserServiceOld;
import com.epm.gestepm.modelapi.project.dto.ProjectDto;
import com.epm.gestepm.modelapi.project.dto.finder.ProjectByIdFinderDto;
import com.epm.gestepm.modelapi.project.service.ProjectService;
import com.epm.gestepm.rest.project.mappers.MapPRToProjectResponse;
import com.epm.gestepm.rest.signings.teleworking.request.TeleworkingSigningFindRestRequest;
import com.epm.gestepm.restapi.openapi.model.Project;
import com.epm.gestepm.restapi.openapi.model.User;
import com.epm.gestepm.restapi.openapi.model.WarehouseSigning;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.DELEGATOR;
import static com.epm.gestepm.lib.logging.constants.LogOperations.OP_PROCESS;
import static org.mapstruct.factory.Mappers.getMapper;

@Component("warehouseSigningResponseDecorator")
@EnableExecutionLog(layerMarker = DELEGATOR)
public class WarehouseSigningResponseDecorator extends BaseResponseDataDecorator<WarehouseSigning> {

    public static final String WS_U_EXPAND = "user";

    public static final String WS_P_EXPAND = "project";

    private final UserServiceOld userServiceOld;

    private final ProjectService projectService;

    protected WarehouseSigningResponseDecorator(ApplicationContext applicationContext
            , UserServiceOld userServiceOld
            , ProjectService projectService) {
        super(applicationContext);
        this.userServiceOld = userServiceOld;
        this.projectService = projectService;
    }

    @Override
    @LogExecution(operation = OP_PROCESS,
            msgIn = "Decorating warehouse signing response",
            msgOut = "Warehouse signing decorated OK",
            errorMsg = "Error decorating warehouse signing response")
    public void decorate(RestRequest request, WarehouseSigning data) {

        if (request.getLinks()) {
            final TeleworkingSigningFindRestRequest selfReq = new TeleworkingSigningFindRestRequest(data.getId());
            selfReq.commonValuesFrom(request);
        }

        if (request.hasExpand(WS_U_EXPAND))
        {
            User user = data.getUser();
            Integer userId = user.getId();

            com.epm.gestepm.modelapi.deprecated.user.dto.User userDto = userServiceOld.getUserById(Long.valueOf(userId));
            User response = new User().id(user.getId()).name(userDto.getName());

            data.setUser(response);
        }

        if (request.hasExpand(WS_P_EXPAND))
        {
            Project project = data.getProject();
            Integer projectId = project.getId();

            ProjectDto projectDto = projectService.findOrNotFound(new ProjectByIdFinderDto(projectId));
            Project response = getMapper(MapPRToProjectResponse.class).from(projectDto);

            data.setProject(response);
        }
    }
}

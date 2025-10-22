package com.epm.gestepm.rest.shares.noprogrammed.decorators;

import com.epm.gestepm.lib.controller.RestRequest;
import com.epm.gestepm.lib.controller.decorator.BaseResponseDataDecorator;
import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.modelapi.deprecated.family.service.FamilyService;
import com.epm.gestepm.modelapi.project.service.ProjectService;
import com.epm.gestepm.modelapi.project.dto.ProjectDto;
import com.epm.gestepm.modelapi.project.dto.finder.ProjectByIdFinderDto;
import com.epm.gestepm.modelapi.shares.noprogrammed.dto.finder.NoProgrammedShareFileByIdFinderDto;
import com.epm.gestepm.modelapi.shares.noprogrammed.service.NoProgrammedShareFileService;
import com.epm.gestepm.modelapi.subfamily.service.SubFamilyService;
import com.epm.gestepm.modelapi.deprecated.user.service.UserServiceOld;
import com.epm.gestepm.rest.project.mappers.MapPRToProjectResponse;
import com.epm.gestepm.rest.shares.noprogrammed.mappers.MapNPSFToFileResponse;
import com.epm.gestepm.rest.shares.noprogrammed.request.NoProgrammedShareFindRestRequest;
import com.epm.gestepm.restapi.openapi.model.*;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.DELEGATOR;
import static com.epm.gestepm.lib.logging.constants.LogOperations.OP_PROCESS;
import static org.mapstruct.factory.Mappers.getMapper;

@Component("noProgrammedShareResponseDecorator")
@EnableExecutionLog(layerMarker = DELEGATOR)
public class NoProgrammedShareResponseDecorator extends BaseResponseDataDecorator<NoProgrammedShare> {

    public static final String NPS_U_EXPAND = "user";

    public static final String NPS_P_EXPAND = "project";

    public static final String NPS_F_EXPAND = "family";

    public static final String NPS_SF_EXPAND = "subFamily";

    public static final String NPS_I_EXPAND = "inspections";

    public static final String NPS_NPSF_EXPAND = "files";

    private final FamilyService familyService;

    private final NoProgrammedShareFileService noProgrammedShareFileService;

    private final ProjectService projectService;

    private final SubFamilyService subFamilyService;

    private final UserServiceOld userServiceOld;

    public NoProgrammedShareResponseDecorator(ApplicationContext applicationContext, FamilyService familyService, NoProgrammedShareFileService noProgrammedShareFileService, ProjectService projectService, SubFamilyService subFamilyService, UserServiceOld userServiceOld) {
        super(applicationContext);
        this.familyService = familyService;
        this.noProgrammedShareFileService = noProgrammedShareFileService;
        this.projectService = projectService;
        this.subFamilyService = subFamilyService;
        this.userServiceOld = userServiceOld;
    }

    @Override
    @LogExecution(operation = OP_PROCESS,
            msgIn = "Decorating no programmed share response",
            msgOut = "No programmed share decorated OK",
            errorMsg = "Error decorating no programmed share response")
    public void decorate(RestRequest request, NoProgrammedShare data) {

        if (request.getLinks()) {

            final NoProgrammedShareFindRestRequest selfReq = new NoProgrammedShareFindRestRequest(data.getId());
            selfReq.commonValuesFrom(request);
        }

        if (request.hasExpand(NPS_U_EXPAND) && data.getUser() != null) {

            final User user = data.getUser();
            final Integer id = user.getId();

            final com.epm.gestepm.modelapi.deprecated.user.dto.User userDto = this.userServiceOld.getUserById(Long.valueOf(id));
            final User response = new User().id(id).name(userDto.getFullName());

            data.setUser(response);
        }

        if (request.hasExpand(NPS_P_EXPAND)) {

            final Project project = data.getProject();
            final Integer id = project.getId();

            final ProjectDto projectDto = this.projectService.findOrNotFound(new ProjectByIdFinderDto(id));
            final Project response = getMapper(MapPRToProjectResponse.class).from(projectDto);

            data.setProject(response);
        }

        if (request.hasExpand(NPS_F_EXPAND) && data.getFamily() != null) {

            final Family family = data.getFamily();
            final Integer id = family.getId();

            // FIXME
            final com.epm.gestepm.modelapi.deprecated.family.dto.Family familyDto = this.familyService.getById(Long.valueOf(id));
            final Family response = new Family()
                    .id(id)
                    .nameES(familyDto.getNameES())
                    .nameFR(familyDto.getNameFR());

            data.setFamily(response);
        }

        if (request.hasExpand(NPS_SF_EXPAND) && data.getSubFamily() != null) {

            final SubFamily subFamily = data.getSubFamily();
            final Integer id = subFamily.getId();

            // FIXME
            final com.epm.gestepm.modelapi.subfamily.dto.SubFamily subFamilyDto = this.subFamilyService.getById(Long.valueOf(id));
            final SubFamily response = new SubFamily()
                    .id(id)
                    .name(request.getLocale().equals("es") ? subFamilyDto.getNameES() : subFamilyDto.getNameFR());

            data.setSubFamily(response);
        }

        if (request.hasExpand(NPS_NPSF_EXPAND) && data.getFiles() != null && !data.getFiles().isEmpty()) {

            final Set<ShareFile> shareFiles = data.getFiles();

            final Set<ShareFile> response = shareFiles.stream()
                    .map(shareFile -> new NoProgrammedShareFileByIdFinderDto(shareFile.getId()))
                    .map(this.noProgrammedShareFileService::findOrNotFound)
                    .map(noProgrammedShareFile -> getMapper(MapNPSFToFileResponse.class).from(noProgrammedShareFile))
                    .collect(Collectors.toSet());

            data.setFiles(response);
        }
    }
}

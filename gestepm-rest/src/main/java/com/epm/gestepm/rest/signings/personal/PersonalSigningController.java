package com.epm.gestepm.rest.signings.personal;

import com.epm.gestepm.lib.applocale.apimodel.service.AppLocaleService;
import com.epm.gestepm.lib.controller.BaseController;
import com.epm.gestepm.lib.controller.metadata.APIMetadata;
import com.epm.gestepm.lib.controller.response.ResponseSuccessfulHelper;
import com.epm.gestepm.lib.controller.restcontext.RestContextProvider;
import com.epm.gestepm.lib.executiontrace.ExecutionRequestProvider;
import com.epm.gestepm.lib.executiontrace.ExecutionTimeProvider;
import com.epm.gestepm.lib.locale.LocaleProvider;
import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.lib.security.annotation.RequirePermits;
import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.modelapi.shares.displacement.dto.deleter.DisplacementShareDeleteDto;
import com.epm.gestepm.modelapi.signings.personal.dto.PersonalSigningDto;
import com.epm.gestepm.modelapi.signings.personal.dto.creator.PersonalSigningCreateDto;
import com.epm.gestepm.modelapi.signings.personal.dto.deleter.PersonalSigningDeleteDto;
import com.epm.gestepm.modelapi.signings.personal.dto.filter.PersonalSigningFilterDto;
import com.epm.gestepm.modelapi.signings.personal.dto.finder.PersonalSigningByIdFinderDto;
import com.epm.gestepm.modelapi.signings.personal.dto.updater.PersonalSigningUpdateDto;
import com.epm.gestepm.modelapi.signings.personal.service.PersonalSigningService;
import com.epm.gestepm.rest.common.CommonProviders;
import com.epm.gestepm.rest.common.MetadataMapper;
import com.epm.gestepm.rest.common.ResSuccessMapper;
import com.epm.gestepm.rest.signings.personal.decorators.PersonalSigningResponseDecorator;
import com.epm.gestepm.rest.signings.personal.mappers.*;
import com.epm.gestepm.rest.signings.personal.operations.FindPersonalSigningV1Operation;
import com.epm.gestepm.rest.signings.personal.operations.ListPersonalSigningV1Operation;
import com.epm.gestepm.rest.signings.personal.request.PersonalSigningFindRestRequest;
import com.epm.gestepm.rest.signings.personal.request.PersonalSigningListRestRequest;
import com.epm.gestepm.rest.signings.personal.response.ResponsesForPersonalSigning;
import com.epm.gestepm.rest.signings.personal.response.ResponsesForPersonalSigningList;
import com.epm.gestepm.restapi.openapi.api.PersonalSigningV1Api;
import com.epm.gestepm.restapi.openapi.model.*;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.REST;
import static com.epm.gestepm.lib.logging.constants.LogOperations.*;
import static com.epm.gestepm.modelapi.signings.teleworking.security.TeleworkingSigningPermission.PRMT_EDIT_TS;
import static org.mapstruct.factory.Mappers.getMapper;
import static com.epm.gestepm.modelapi.signings.personal.security.PersonalSigningPermission.*;

@RestController
@EnableExecutionLog(layerMarker = REST)
public class PersonalSigningController extends BaseController implements PersonalSigningV1Api, ResponsesForPersonalSigning, ResponsesForPersonalSigningList {

    private final PersonalSigningService personalSigningService;

    protected PersonalSigningController(final CommonProviders commonProviders, final ApplicationContext appCtx,
                                        final AppLocaleService appLocaleService, final ResponseSuccessfulHelper successHelper,
                                        final PersonalSigningService personalSigningService) {
        super(commonProviders.localeProvider(), commonProviders.executionRequestProvider(),
                commonProviders.executionTimeProvider(), commonProviders.restContextProvider(), appCtx, appLocaleService,
                successHelper);
        this.personalSigningService = personalSigningService;
    }

    @Override
    @RequirePermits(value = PRMT_READ_PRS, action = "Get personal signing list")
    @LogExecution(operation = OP_READ)
    public ResponseEntity<ListPersonalSigningsV1200Response> listPersonalSigningsV1(final List<String> meta, final Boolean links,
                                                                                    final Set<String> expand, final Long offset, final Long limit,
                                                                                    final String order, final String orderBy, final List<Integer> ids,
                                                                                    final List<Integer> userIds, final LocalDateTime startDate,
                                                                                    final LocalDateTime endDate) {
        final PersonalSigningListRestRequest request = new PersonalSigningListRestRequest(ids, userIds, startDate, endDate);

        this.setCommon(request, meta, links, expand);
        this.setDefaults(request);
        this.setPagination(request, limit, offset);
        this.setOrder(request, order, orderBy);

        final PersonalSigningFilterDto filterDto = getMapper(MapPRSToPersonalSigningFilterDto.class).from(request);
        final Page<PersonalSigningDto> page = this.personalSigningService.list(filterDto, offset, limit);

        final APIMetadata metaData = this.getMetadata(request, page, new ListPersonalSigningV1Operation());
        final List<PersonalSigning> data = getMapper(MapPRSToPersonalSigningResponse.class).from(page);

        this.decorate(request, data, PersonalSigningResponseDecorator.class);

        return toListPersonalSigningsV1200Response(metaData, data, page.hashCode());
    }

    @Override
    @RequirePermits(value = PRMT_READ_PRS, action = "Find personal signing")
    @LogExecution(operation = OP_READ)
    public ResponseEntity<CreatePersonalSigningV1200Response> findPersonalSigningByIdV1(final Integer id, final List<String> meta,
                                                                                        final Boolean links, final Set<String> expand) {
        final PersonalSigningFindRestRequest request = new PersonalSigningFindRestRequest(id);

        this.setCommon(request, meta, links, expand);

        final PersonalSigningByIdFinderDto finderDto = getMapper(MapPRSToPersonalSigningByIdFinderDto.class).from(request);
        final PersonalSigningDto dto = this.personalSigningService.findOrNotFound(finderDto);

        final APIMetadata metaData = this.getMetadata(request, new FindPersonalSigningV1Operation());
        final PersonalSigning data = getMapper(MapPRSToPersonalSigningResponse.class).from(dto);

        this.decorate(request, data, PersonalSigningResponseDecorator.class);

        return toResPersonalSigningResponse(metaData, data, dto.hashCode());
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_PRS, action = "Create personal signing")
    @LogExecution(operation = OP_CREATE)
    public ResponseEntity<CreatePersonalSigningV1200Response> createPersonalSigningV1(final CreatePersonalSigningV1Request request) {

        final PersonalSigningCreateDto createDto = getMapper(MapPRSToPersonalSigningCreateDto.class).from(request);

        final PersonalSigningDto personalSigning = this.personalSigningService.create(createDto);

        final APIMetadata metadata = this.getDefaultMetadata();
        final PersonalSigning data = getMapper(MapPRSToPersonalSigningResponse.class).from(personalSigning);

        final CreatePersonalSigningV1200Response response = new CreatePersonalSigningV1200Response();
        response.setData(data);
        response.setMetadata(getMapper(MetadataMapper.class).from(metadata));

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<CreatePersonalSigningV1200Response> updatePersonalSigningV1(final Integer id, final UpdatePersonalSigningV1Request request) {

        final PersonalSigningUpdateDto updateDto = getMapper(MapPRSToPersonalSigningUpdateDto.class).from(request);
        updateDto.setId(id);

        final PersonalSigningDto personalSigning = this.personalSigningService.update(updateDto);

        final APIMetadata metaData = this.getDefaultMetadata();
        final PersonalSigning data = getMapper(MapPRSToPersonalSigningResponse.class).from(personalSigning);

        final CreatePersonalSigningV1200Response response = new CreatePersonalSigningV1200Response();
        response.setMetadata(getMapper(MetadataMapper.class).from(metaData));
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_TS, action = "Delete personal signing")
    @LogExecution(operation = OP_DELETE)
    public ResponseEntity<ResSuccess> deletePersonalSigningV1(Integer id) {
        final PersonalSigningDeleteDto deleteDto = new PersonalSigningDeleteDto(id);

        this.personalSigningService.delete(deleteDto);

        return this.success(getMapper(ResSuccessMapper.class)::from);
    }
}

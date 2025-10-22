package com.epm.gestepm.rest.activitycenter;

import com.epm.gestepm.lib.applocale.apimodel.service.AppLocaleService;
import com.epm.gestepm.lib.controller.BaseController;
import com.epm.gestepm.lib.controller.metadata.APIMetadata;
import com.epm.gestepm.lib.controller.response.ResponseSuccessfulHelper;
import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.lib.security.annotation.RequirePermits;
import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.masterdata.api.activitycenter.dto.ActivityCenterDto;
import com.epm.gestepm.masterdata.api.activitycenter.dto.creator.ActivityCenterCreateDto;
import com.epm.gestepm.masterdata.api.activitycenter.dto.deleter.ActivityCenterDeleteDto;
import com.epm.gestepm.masterdata.api.activitycenter.dto.filter.ActivityCenterFilterDto;
import com.epm.gestepm.masterdata.api.activitycenter.dto.finder.ActivityCenterByIdFinderDto;
import com.epm.gestepm.masterdata.api.activitycenter.dto.updater.ActivityCenterUpdateDto;
import com.epm.gestepm.masterdata.api.activitycenter.service.ActivityCenterService;
import com.epm.gestepm.rest.activitycenter.decorators.ActivityCenterResponseDecorator;
import com.epm.gestepm.rest.activitycenter.mappers.*;
import com.epm.gestepm.rest.activitycenter.operations.FindActivityCenterV1Operation;
import com.epm.gestepm.rest.activitycenter.operations.ListActivityCenterV1Operation;
import com.epm.gestepm.rest.activitycenter.request.ActivityCenterFindRestRequest;
import com.epm.gestepm.rest.activitycenter.request.ActivityCenterListRestRequest;
import com.epm.gestepm.rest.activitycenter.response.ResponsesForActivityCenter;
import com.epm.gestepm.rest.activitycenter.response.ResponsesForActivityCenterList;
import com.epm.gestepm.rest.common.CommonProviders;
import com.epm.gestepm.rest.common.MetadataMapper;
import com.epm.gestepm.rest.common.ResSuccessMapper;
import com.epm.gestepm.restapi.openapi.api.ActivityCentersV1Api;
import com.epm.gestepm.restapi.openapi.model.*;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.REST;
import static com.epm.gestepm.lib.logging.constants.LogOperations.*;
import static com.epm.gestepm.masterdata.api.activitycenter.security.ActivityCenterPermission.PRMT_EDIT_AC;
import static com.epm.gestepm.masterdata.api.activitycenter.security.ActivityCenterPermission.PRMT_READ_AC;
import static org.mapstruct.factory.Mappers.getMapper;

@RestController
@EnableExecutionLog(layerMarker = REST)
public class ActivityCenterController extends BaseController implements ActivityCentersV1Api, ResponsesForActivityCenter, ResponsesForActivityCenterList {

    private final ActivityCenterService activityCenterService;

    public ActivityCenterController(final CommonProviders commonProviders, final ApplicationContext appCtx,
                                    final AppLocaleService appLocaleService, final ResponseSuccessfulHelper successHelper,
                                    final ActivityCenterService activityCenterService) {

        super(commonProviders.localeProvider(), commonProviders.executionRequestProvider(),
                commonProviders.executionTimeProvider(), commonProviders.restContextProvider(), appCtx, appLocaleService,
                successHelper);

        this.activityCenterService = activityCenterService;
    }

    @Override
    @RequirePermits(value = PRMT_READ_AC, action = "Get activity center list")
    @LogExecution(operation = OP_READ)
    public ResponseEntity<ListActivityCentersV1200Response> listActivityCentersV1(final List<String> meta, final Boolean links, Set<String> expand, final Long offset, final Long limit, final String order, final String orderBy, final List<Integer> ids, final String name, final List<Integer> countryIds) {

        final ActivityCenterListRestRequest req = new ActivityCenterListRestRequest(ids, name, countryIds);

        this.setCommon(req, meta, links, expand);
        this.setDefaults(req);
        this.setPagination(req, limit, offset);
        this.setOrder(req, order, orderBy);

        final ActivityCenterFilterDto filterDto = getMapper(MapACRToActivityCenterFilterDto.class).from(req);
        final Page<ActivityCenterDto> page = this.activityCenterService.list(filterDto, offset, limit);

        final APIMetadata metadata = this.getMetadata(req, page, new ListActivityCenterV1Operation());
        final List<ActivityCenter> data = getMapper(MapACToActivityCenterResponse.class).from(page);

        this.decorate(req, data, ActivityCenterResponseDecorator.class);

        return toResActivityCenterListResponse(metadata, data, page.hashCode());
    }

    @Override
    @RequirePermits(value = PRMT_READ_AC, action = "Find activity center")
    @LogExecution(operation = OP_READ)
    public ResponseEntity<CreateActivityCenterV1200Response> findActivityCenterByIdV1(final Integer id, final List<String> meta, final Boolean links, final Set<String> expand) {

        final ActivityCenterFindRestRequest req = new ActivityCenterFindRestRequest(id);

        this.setCommon(req, meta, links, null);

        final ActivityCenterByIdFinderDto finderDto = getMapper(MapACRToActivityCenterByIdFinderDto.class).from(req);
        final ActivityCenterDto dto = this.activityCenterService.findOrNotFound(finderDto);

        final APIMetadata metadata = this.getMetadata(req, new FindActivityCenterV1Operation());
        final ActivityCenter data = getMapper(MapACToActivityCenterResponse.class).from(dto);

        this.decorate(req, data, ActivityCenterResponseDecorator.class);

        return toResActivityCenterResponse(metadata, data, dto.hashCode());
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_AC, action = "Create activity center")
    @LogExecution(operation = OP_CREATE)
    public ResponseEntity<CreateActivityCenterV1200Response> createActivityCenterV1(final CreateActivityCenterV1Request reqCreateActivityCenter) {

        final ActivityCenterCreateDto createDto = getMapper(MapACToActivityCenterCreateDto.class).from(reqCreateActivityCenter);

        final ActivityCenterDto activityCenterDto = this.activityCenterService.create(createDto);

        final APIMetadata metadata = this.getDefaultMetadata();
        final ActivityCenter data = getMapper(MapACToActivityCenterResponse.class).from(activityCenterDto);

        final CreateActivityCenterV1200Response response = new CreateActivityCenterV1200Response();
        response.setMetadata(getMapper(MetadataMapper.class).from(metadata));
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_AC, action = "Update activity center")
    @LogExecution(operation = OP_UPDATE)
    public ResponseEntity<CreateActivityCenterV1200Response> updateActivityCenterV1(final Integer id, final UpdateActivityCenterV1Request reqUpdateActivityCenter) {

        final ActivityCenterUpdateDto updateDto = getMapper(MapACToActivityCenterUpdateDto.class).from(reqUpdateActivityCenter);
        updateDto.setId(id);

        final ActivityCenterDto activityCenterDto = this.activityCenterService.update(updateDto);

        final APIMetadata metadata = this.getDefaultMetadata();
        final ActivityCenter data = getMapper(MapACToActivityCenterResponse.class).from(activityCenterDto);

        final CreateActivityCenterV1200Response response = new CreateActivityCenterV1200Response();
        response.setMetadata(getMapper(MetadataMapper.class).from(metadata));
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_AC, action = "Delete activity center")
    @LogExecution(operation = OP_DELETE)
    public ResponseEntity<ResSuccess> deleteActivityCenterV1(final Integer id) {

        final ActivityCenterDeleteDto deleteDto = new ActivityCenterDeleteDto();
        deleteDto.setId(id);

        this.activityCenterService.delete(deleteDto);

        return this.success(getMapper(ResSuccessMapper.class)::from);
    }
}

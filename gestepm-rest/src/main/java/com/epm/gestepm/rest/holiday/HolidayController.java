package com.epm.gestepm.rest.holiday;

import com.epm.gestepm.lib.applocale.apimodel.service.AppLocaleService;
import com.epm.gestepm.lib.controller.BaseController;
import com.epm.gestepm.lib.controller.metadata.APIMetadata;
import com.epm.gestepm.lib.controller.response.ResponseSuccessfulHelper;
import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.lib.security.annotation.RequirePermits;
import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.masterdata.api.holiday.dto.HolidayDto;
import com.epm.gestepm.masterdata.api.holiday.dto.creator.HolidayCreateDto;
import com.epm.gestepm.masterdata.api.holiday.dto.deleter.HolidayDeleteDto;
import com.epm.gestepm.masterdata.api.holiday.dto.filter.HolidayFilterDto;
import com.epm.gestepm.masterdata.api.holiday.dto.finder.HolidayByIdFinderDto;
import com.epm.gestepm.masterdata.api.holiday.dto.updater.HolidayUpdateDto;
import com.epm.gestepm.masterdata.api.holiday.service.HolidayService;
import com.epm.gestepm.rest.common.CommonProviders;
import com.epm.gestepm.rest.common.MetadataMapper;
import com.epm.gestepm.rest.common.ResSuccessMapper;
import com.epm.gestepm.rest.holiday.decorators.HolidayResponseDecorator;
import com.epm.gestepm.rest.holiday.mappers.*;
import com.epm.gestepm.rest.holiday.operations.FindHolidayV1Operation;
import com.epm.gestepm.rest.holiday.operations.ListHolidayV1Operation;
import com.epm.gestepm.rest.holiday.request.HolidayFindRestRequest;
import com.epm.gestepm.rest.holiday.request.HolidayListRestRequest;
import com.epm.gestepm.rest.holiday.response.ResponsesForHoliday;
import com.epm.gestepm.rest.holiday.response.ResponsesForHolidayList;
import com.epm.gestepm.restapi.openapi.api.HolidaysV1Api;
import com.epm.gestepm.restapi.openapi.model.*;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.REST;
import static com.epm.gestepm.lib.logging.constants.LogOperations.*;
import static com.epm.gestepm.masterdata.api.holiday.security.HolidayPermission.PRMT_EDIT_H;
import static com.epm.gestepm.masterdata.api.holiday.security.HolidayPermission.PRMT_READ_H;
import static org.mapstruct.factory.Mappers.getMapper;

@RestController
@EnableExecutionLog(layerMarker = REST)
public class HolidayController extends BaseController implements HolidaysV1Api, ResponsesForHoliday, ResponsesForHolidayList {

    private final HolidayService holidayService;

    public HolidayController(final CommonProviders commonProviders, final ApplicationContext appCtx,
                             final AppLocaleService appLocaleService, final ResponseSuccessfulHelper successHelper,
                             final HolidayService holidayService) {

        super(commonProviders.localeProvider(), commonProviders.executionRequestProvider(),
                commonProviders.executionTimeProvider(), commonProviders.restContextProvider(), appCtx, appLocaleService,
                successHelper);

        this.holidayService = holidayService;
    }

    @Override
    @RequirePermits(value = PRMT_READ_H, action = "Get holiday list")
    @LogExecution(operation = OP_READ)
    public ResponseEntity<ListHolidaysV1200Response> listHolidaysV1(final List<String> meta, final Boolean links, Set<String> expand, final Long offset, final Long limit, final String order, final String orderBy, final List<Integer> ids, final String name, final Integer day, final Integer month, final List<Integer> countryIds, final List<Integer> activityCenterIds) {

        final HolidayListRestRequest req = new HolidayListRestRequest(ids, name, day, month, countryIds, activityCenterIds);

        this.setCommon(req, meta, links, expand);
        this.setDefaults(req);
        this.setPagination(req, limit, offset);
        this.setOrder(req, order, orderBy);

        final HolidayFilterDto filterDto = getMapper(MapHRToHolidayFilterDto.class).from(req);
        final Page<HolidayDto> page = this.holidayService.list(filterDto, offset, limit);

        final APIMetadata metadata = this.getMetadata(req, page, new ListHolidayV1Operation());
        final List<Holiday> data = getMapper(MapHToHolidayResponse.class).from(page);

        this.decorate(req, data, HolidayResponseDecorator.class);

        return toResHolidayListResponse(metadata, data, page.hashCode());
    }

    @Override
    @RequirePermits(value = PRMT_READ_H, action = "Find holiday")
    @LogExecution(operation = OP_READ)
    public ResponseEntity<CreateHolidayV1200Response> findHolidayByIdV1(final Integer id, final List<String> meta, final Boolean links, final Set<String> expand) {

        final HolidayFindRestRequest req = new HolidayFindRestRequest(id);

        this.setCommon(req, meta, links, null);

        final HolidayByIdFinderDto finderDto = getMapper(MapHRToHolidayByIdFinderDto.class).from(req);
        final HolidayDto dto = this.holidayService.findOrNotFound(finderDto);

        final APIMetadata metadata = this.getMetadata(req, new FindHolidayV1Operation());
        final Holiday data = getMapper(MapHToHolidayResponse.class).from(dto);

        this.decorate(req, data, HolidayResponseDecorator.class);

        return toResHolidayResponse(metadata, data, dto.hashCode());
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_H, action = "Create holiday")
    @LogExecution(operation = OP_CREATE)
    public ResponseEntity<CreateHolidayV1200Response> createHolidayV1(final CreateHolidayV1Request reqCreateHoliday) {

        final HolidayCreateDto createDto = getMapper(MapHToHolidayCreateDto.class).from(reqCreateHoliday);

        final HolidayDto activityCenterDto = this.holidayService.create(createDto);

        final APIMetadata metadata = this.getDefaultMetadata();
        final Holiday data = getMapper(MapHToHolidayResponse.class).from(activityCenterDto);

        final CreateHolidayV1200Response response = new CreateHolidayV1200Response();
        response.setMetadata(getMapper(MetadataMapper.class).from(metadata));
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_H, action = "Update holiday")
    @LogExecution(operation = OP_UPDATE)
    public ResponseEntity<CreateHolidayV1200Response> updateHolidayV1(final Integer id, final UpdateHolidayV1Request reqUpdateHoliday) {

        final HolidayUpdateDto updateDto = getMapper(MapHToHolidayUpdateDto.class).from(reqUpdateHoliday);
        updateDto.setId(id);

        final HolidayDto activityCenterDto = this.holidayService.update(updateDto);

        final APIMetadata metadata = this.getDefaultMetadata();
        final Holiday data = getMapper(MapHToHolidayResponse.class).from(activityCenterDto);

        final CreateHolidayV1200Response response = new CreateHolidayV1200Response();
        response.setMetadata(getMapper(MetadataMapper.class).from(metadata));
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_H, action = "Delete holiday")
    @LogExecution(operation = OP_DELETE)
    public ResponseEntity<ResSuccess> deleteHolidayV1(final Integer id) {

        final HolidayDeleteDto deleteDto = new HolidayDeleteDto();
        deleteDto.setId(id);

        this.holidayService.delete(deleteDto);

        return this.success(getMapper(ResSuccessMapper.class)::from);
    }
}

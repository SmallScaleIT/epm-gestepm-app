package com.epm.gestepm.rest.signings.warehouse;

import com.epm.gestepm.lib.applocale.apimodel.service.AppLocaleService;
import com.epm.gestepm.lib.controller.BaseController;
import com.epm.gestepm.lib.controller.metadata.APIMetadata;
import com.epm.gestepm.lib.controller.response.ResponseSuccessfulHelper;
import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.lib.security.annotation.RequirePermits;
import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.modelapi.signings.warehouse.dto.WarehouseSigningDto;
import com.epm.gestepm.modelapi.signings.warehouse.dto.creator.WarehouseSigningCreateDto;
import com.epm.gestepm.modelapi.signings.warehouse.dto.deleter.WarehouseSigningDeleteDto;
import com.epm.gestepm.modelapi.signings.warehouse.dto.filter.WarehouseSigningFilterDto;
import com.epm.gestepm.modelapi.signings.warehouse.dto.finder.WarehouseSigningByIdFinderDto;
import com.epm.gestepm.modelapi.signings.warehouse.dto.updater.WarehouseSigningUpdateDto;
import com.epm.gestepm.modelapi.signings.warehouse.service.WarehouseSigningService;
import com.epm.gestepm.rest.common.CommonProviders;
import com.epm.gestepm.rest.common.MetadataMapper;
import com.epm.gestepm.rest.common.ResSuccessMapper;
import com.epm.gestepm.rest.signings.warehouse.decorators.WarehouseSigningResponseDecorator;
import com.epm.gestepm.rest.signings.warehouse.mappers.*;
import com.epm.gestepm.rest.signings.warehouse.operations.FindWarehouseSigningV1Operation;
import com.epm.gestepm.rest.signings.warehouse.operations.ListWarehouseSigningV1Operation;
import com.epm.gestepm.rest.signings.warehouse.request.WarehouseSigningFindRestRequest;
import com.epm.gestepm.rest.signings.warehouse.request.WarehouseSigningListRestRequest;
import com.epm.gestepm.rest.signings.warehouse.response.ResponsesForWarehouseSigning;
import com.epm.gestepm.rest.signings.warehouse.response.ResponsesForWarehouseSigningList;
import com.epm.gestepm.restapi.openapi.api.WarehouseSigningV1Api;
import com.epm.gestepm.restapi.openapi.model.*;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.REST;
import static com.epm.gestepm.lib.logging.constants.LogOperations.*;
import static com.epm.gestepm.lib.logging.constants.LogOperations.OP_DELETE;
import static org.mapstruct.factory.Mappers.getMapper;
import static com.epm.gestepm.modelapi.signings.warehouse.security.WarehouseSigningPermission.*;

@RestController
@EnableExecutionLog(layerMarker = REST)
public class WarehouseSigningController extends BaseController implements WarehouseSigningV1Api
        , ResponsesForWarehouseSigning, ResponsesForWarehouseSigningList {

    private final WarehouseSigningService warehouseService;

    public WarehouseSigningController(final CommonProviders commonProviders, final ApplicationContext appCtx,
                                        final AppLocaleService appLocaleService, final ResponseSuccessfulHelper successHelper,
                                        final WarehouseSigningService warehouseService) {
        super(commonProviders.localeProvider(), commonProviders.executionRequestProvider(),
                commonProviders.executionTimeProvider(), commonProviders.restContextProvider(), appCtx, appLocaleService,
                successHelper);
        this.warehouseService = warehouseService;
    }

    @Override
    @RequirePermits(value = PRMT_READ_WHS, action = "Get warehouse signing list")
    @LogExecution(operation = OP_READ)
    public ResponseEntity<ListWarehouseSigningsV1200Response> listWarehouseSigningsV1(List<String> meta
            , Boolean links, Set<String> expand, Long offset, Long limit, String order, String orderBy
            , List<Integer> ids, List<Integer> userIds, List<Integer> projectIds, Boolean current
            , LocalDateTime startDate, LocalDateTime endDate) {

        final WarehouseSigningListRestRequest req = new WarehouseSigningListRestRequest(ids, userIds
                , projectIds, current, startDate, endDate);

        this.setCommon(req, meta, links, expand);
        this.setDefaults(req);
        this.setPagination(req, limit, offset);
        this.setOrder(req, order, orderBy);

        final WarehouseSigningFilterDto filterDto = getMapper(MapWHSToWarehouseSigningFilterDto.class).from(req);
        final Page<WarehouseSigningDto> page = this.warehouseService.list(filterDto, offset, limit);

        final APIMetadata metadata = this.getMetadata(req, page, new ListWarehouseSigningV1Operation());
        final List<WarehouseSigning> data = getMapper(MapWHSToWarehouseSigningResponse.class).from(page);

        this.decorate(req, data, WarehouseSigningResponseDecorator.class);

        return toListWarehouseSigningsV1200Response(metadata, data, page.hashCode());
    }

    @Override
    @RequirePermits(value = PRMT_READ_WHS, action = "Find warehouse signing")
    @LogExecution(operation = OP_READ)
    public ResponseEntity<CreateWarehouseSigningV1200Response> findWarehouseSigningByIdV1(final Integer id, final List<String> meta
            , final Boolean links, final Set<String> expand) {

        final WarehouseSigningFindRestRequest req = new WarehouseSigningFindRestRequest(id);

        this.setCommon(req, meta, links, expand);

        final WarehouseSigningByIdFinderDto finderDto = getMapper(MapWHSToWarehouseSigningByIdFinderDto.class)
                .from(req);

        final WarehouseSigningDto dto = this.warehouseService.findOrNotFound(finderDto);

        final APIMetadata metadata = this.getMetadata(req, new FindWarehouseSigningV1Operation());
        final WarehouseSigning data = getMapper(MapWHSToWarehouseSigningResponse.class).from(dto);

        this.decorate(req, data, WarehouseSigningResponseDecorator.class);

        return toResWarehouseSigningResponse(metadata, data, dto.hashCode());
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_WHS, action = "Create warehouse signing")
    @LogExecution(operation = OP_CREATE)
    public ResponseEntity<CreateWarehouseSigningV1200Response> createWarehouseSigningV1(final CreateWarehouseSigningV1Request reqCreateWarehouseSigning) {

        final WarehouseSigningCreateDto createDto = getMapper(MapWHSToWarehouseSigningCreateDto.class)
                .from(reqCreateWarehouseSigning);

        final WarehouseSigningDto warehouseSigningDto = warehouseService.create(createDto);

        final APIMetadata metadata = this.getDefaultMetadata();

        final WarehouseSigning data = getMapper(MapWHSToWarehouseSigningResponse.class)
                .from(warehouseSigningDto);

        final CreateWarehouseSigningV1200Response response = new CreateWarehouseSigningV1200Response();
        response.setMetadata(getMapper(MetadataMapper.class).from(metadata));
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_WHS, action = "Update warehouse signing")
    @LogExecution(operation = OP_UPDATE)
    public ResponseEntity<CreateWarehouseSigningV1200Response> updateWarehouseSigningV1(final Integer id, final UpdateWarehouseSigningV1Request reqUpdateWarehouseSigning) {

        final WarehouseSigningUpdateDto updateDto = getMapper(MapWHSToWarehouseSigningUpdateDto.class)
                .from(reqUpdateWarehouseSigning);

        updateDto.setId(id);

        final WarehouseSigningDto warehouseSigning = this.warehouseService.update(updateDto);

        final APIMetadata metadata = this.getDefaultMetadata();
        final WarehouseSigning data = getMapper(MapWHSToWarehouseSigningResponse.class)
                .from(warehouseSigning);

        final CreateWarehouseSigningV1200Response response = new CreateWarehouseSigningV1200Response();
        response.setMetadata(getMapper(MetadataMapper.class).from(metadata));
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_WHS, action = "Delete warehouse signing")
    @LogExecution(operation = OP_DELETE)
    public ResponseEntity<ResSuccess> deleteWarehouseSigningV1(final Integer id) {

        final WarehouseSigningDeleteDto deleteDto = new WarehouseSigningDeleteDto(id);

        this.warehouseService.delete(deleteDto);

        return this.success(getMapper(ResSuccessMapper.class)::from);
    }
}

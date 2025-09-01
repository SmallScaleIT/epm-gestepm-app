package com.epm.gestepm.rest.signings.workshop;

import com.epm.gestepm.lib.applocale.apimodel.service.AppLocaleService;
import com.epm.gestepm.lib.controller.BaseController;
import com.epm.gestepm.lib.controller.metadata.APIMetadata;
import com.epm.gestepm.lib.controller.response.ResponseSuccessfulHelper;
import com.epm.gestepm.lib.controller.restcontext.RestContextProvider;
import com.epm.gestepm.lib.executiontrace.ExecutionRequestProvider;
import com.epm.gestepm.lib.executiontrace.ExecutionTimeProvider;
import com.epm.gestepm.lib.locale.LocaleProvider;
import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.modelapi.signings.workshop.dto.WorkShopSigningDto;
import com.epm.gestepm.modelapi.signings.workshop.dto.creator.WorkshopSigningCreateDto;
import com.epm.gestepm.modelapi.signings.workshop.dto.deleter.WorkshopSigningDeleteDto;
import com.epm.gestepm.modelapi.signings.workshop.dto.filter.WorkshopSigningExportFilterDto;
import com.epm.gestepm.modelapi.signings.workshop.dto.filter.WorkshopSigningFilterDto;
import com.epm.gestepm.modelapi.signings.workshop.dto.finder.WorkshopSigningByIdFinderDto;
import com.epm.gestepm.modelapi.signings.workshop.dto.updater.WorkshopSigningUpdateDto;
import com.epm.gestepm.modelapi.signings.workshop.service.WorkshopExportService;
import com.epm.gestepm.modelapi.signings.workshop.service.WorkshopSigningService;
import com.epm.gestepm.rest.common.MetadataMapper;
import com.epm.gestepm.rest.common.ResSuccessMapper;
import com.epm.gestepm.rest.signings.workshop.decorators.WorkshopSigningResponseDecorator;
import com.epm.gestepm.rest.signings.workshop.mappers.*;
import com.epm.gestepm.rest.signings.workshop.operations.FindWorkshopSigningV1Operation;
import com.epm.gestepm.rest.signings.workshop.operations.ListWorkshopSigningV1Operation;
import com.epm.gestepm.rest.signings.workshop.request.WorkshopSigningFindRestRequest;
import com.epm.gestepm.rest.signings.workshop.request.WorkshopSigningListRestRequest;
import com.epm.gestepm.rest.signings.workshop.response.ResponsesForWorkshopSigning;
import com.epm.gestepm.rest.signings.workshop.response.ResponsesForWorkshopSigningList;
import com.epm.gestepm.restapi.openapi.api.WorkshopSigningV1Api;
import com.epm.gestepm.restapi.openapi.model.*;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.REST;
import static org.mapstruct.factory.Mappers.getMapper;

@RestController
@EnableExecutionLog(layerMarker = REST)
public class WorkshopSigningController extends BaseController implements ResponsesForWorkshopSigningList
                        , ResponsesForWorkshopSigning, WorkshopSigningV1Api {

    private final WorkshopSigningService service;

    private final WorkshopExportService exportService;

    protected WorkshopSigningController(LocaleProvider localeProvider
            , ExecutionRequestProvider executionRequestProvider
            , ExecutionTimeProvider executionTimeProvider, RestContextProvider restContextProvider
            , ApplicationContext applicationContext, AppLocaleService appLocaleService
            , ResponseSuccessfulHelper responseSuccessfulHelper
            , WorkshopSigningService service
            , WorkshopExportService exportService) {
        super(localeProvider, executionRequestProvider, executionTimeProvider, restContextProvider
                , applicationContext, appLocaleService, responseSuccessfulHelper);

        this.service = service;
        this.exportService = exportService;
    }

    @Override
    public ResponseEntity<CreateWorkshopSigningV1200Response> createWorkshopSigningV1(Integer warehouseSigningId, CreateWorkshopSigningV1Request createWorkshopSigningV1Request) {

        final WorkshopSigningCreateDto createDto = getMapper(MapWSSToWorkshopSigningCreateDto.class)
                .from(createWorkshopSigningV1Request);

        createDto.setWarehouseId(warehouseSigningId);

        final WorkShopSigningDto workShopSigningDto = service.create(createDto);

        final APIMetadata meta = this.getDefaultMetadata();
        final WorkshopSigning workshopSigning = getMapper(MapWSSToWorkshopSigningResponse.class)
                .from(workShopSigningDto);

        final CreateWorkshopSigningV1200Response response = new CreateWorkshopSigningV1200Response();
        response.setData(workshopSigning);
        response.setMetadata(getMapper(MetadataMapper.class).from(meta));

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ResSuccess> deleteWorkshopSigningV1(Integer warehouseSigningId, Integer workshopSigningId) {

        final WorkshopSigningDeleteDto deleteDto = new WorkshopSigningDeleteDto(workshopSigningId);
        service.delete(deleteDto);

        return this.success(getMapper(ResSuccessMapper.class)::from);
    }

    @Override
    public ResponseEntity<CreateWorkshopSigningV1200Response> findWorkshopSigningByIdV1(Integer warehouseSigningId
            , Integer workshopSigningId, List<String> meta, Boolean links
            , Set<String> expand) {

        final WorkshopSigningFindRestRequest req = new WorkshopSigningFindRestRequest(workshopSigningId, warehouseSigningId);

        this.setCommon(req, meta, links, expand);

        final WorkshopSigningByIdFinderDto finderDto = getMapper(MapWSSToWorkshopSigningByIdFinderDto.class)
                .from(req);

        final WorkShopSigningDto dto = service.findOrNotFound(finderDto);

        final APIMetadata metaData = this.getMetadata(req, new FindWorkshopSigningV1Operation());
        final WorkshopSigning data = getMapper(MapWSSToWorkshopSigningResponse.class)
                .from(dto);

        this.decorate(req, data, WorkshopSigningResponseDecorator.class);

        return toResWorkshopSigningResponse(metaData, data, dto.hashCode());
    }

    @Override
    public ResponseEntity<ListWorkshopSigningsV1200Response> listWorkshopSigningsV1(Integer warehouseSigningId
            , List<String> meta, Boolean links, Set<String> expand
            , Long offset, Long limit, String order, String orderBy
            , List<Integer> ids, List<Integer> userIds, List<Integer> projectIds
            , Boolean current, String startDate, String endDate) {

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        LocalDateTime localStartDate = Optional.ofNullable(startDate)
                .map(strDate -> LocalDateTime.parse(strDate, formatter))
                .orElse(null);

        LocalDateTime localEndDate = Optional.ofNullable(endDate)
                .map(strDate -> LocalDateTime.parse(strDate, formatter))
                .orElse(null);

        final WorkshopSigningListRestRequest req = new WorkshopSigningListRestRequest(ids, warehouseSigningId
                , projectIds, userIds, current, localStartDate, localEndDate);

        this.setCommon(req, meta, links, expand);
        this.setDefaults(req);
        this.setPagination(req, limit, offset);
        this.setOrder(req, order, orderBy);

        final WorkshopSigningFilterDto filterDto = getMapper(MapWSSToWorkshopSigningFilterDto.class)
                .from(req);

        final Page<WorkShopSigningDto> dtos = service.page(filterDto, offset, limit);

        final APIMetadata metaData = this.getMetadata(req, dtos, new ListWorkshopSigningV1Operation());
        final List<WorkshopSigning> data = getMapper(MapWSSToWorkshopSigningResponse.class)
                .from(dtos);

        this.decorate(req, data, WorkshopSigningResponseDecorator.class);

        return toListWorkshopSigningResponse(metaData, data, dtos.hashCode());
    }

    @Override
    public ResponseEntity<CreateWorkshopSigningV1200Response> updateWorkshopSigningV1(Integer warehouseSigningId, Integer workshopSigningId, UpdateWorkshopSigningV1Request updateWorkshopSigningV1Request) {

        final WorkshopSigningUpdateDto updateDto = getMapper(MapWSSToWorkshopSigningUpdateDto.class)
                .from(updateWorkshopSigningV1Request);

        updateDto.setId(workshopSigningId);
        updateDto.setWarehouseId(warehouseSigningId);

        final WorkShopSigningDto dto = service.update(updateDto);

        final APIMetadata metaData = this.getDefaultMetadata();
        final WorkshopSigning data = getMapper(MapWSSToWorkshopSigningResponse.class)
                .from(dto);

        final CreateWorkshopSigningV1200Response response = new CreateWorkshopSigningV1200Response();
        response.setMetadata(getMapper(MetadataMapper.class).from(metaData));
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Resource> exportWorkshopSigningV1(LocalDateTime startDate, LocalDateTime endDate, Integer userId, Integer projectId) {

        final WorkshopSigningExportFilterDto workshopFilter = new WorkshopSigningExportFilterDto();
        workshopFilter.setStartDate(startDate);
        workshopFilter.setEndDate(endDate);
        workshopFilter.setUserId(userId);
        workshopFilter.setProjectId(projectId);

        final byte[] excel = exportService.generate(workshopFilter);
        final Resource resource = new ByteArrayResource(excel);
        final String fileName = exportService.buildFileName(workshopFilter);

        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(excel.length)
                .body(resource);
    }
}

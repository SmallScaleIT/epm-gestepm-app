package com.epm.gestepm.rest.inspection;

import com.epm.gestepm.lib.applocale.apimodel.service.AppLocaleService;
import com.epm.gestepm.lib.controller.BaseController;
import com.epm.gestepm.lib.controller.metadata.APIMetadata;
import com.epm.gestepm.lib.controller.response.ResponseSuccessfulHelper;
import com.epm.gestepm.lib.locale.LocaleProvider;
import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.lib.security.annotation.RequirePermits;
import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.modelapi.common.utils.Utiles;
import com.epm.gestepm.modelapi.inspection.dto.InspectionDto;
import com.epm.gestepm.modelapi.inspection.dto.creator.InspectionCreateDto;
import com.epm.gestepm.modelapi.inspection.dto.deleter.InspectionDeleteDto;
import com.epm.gestepm.modelapi.inspection.dto.filter.InspectionFilterDto;
import com.epm.gestepm.modelapi.inspection.dto.finder.InspectionByIdFinderDto;
import com.epm.gestepm.modelapi.inspection.dto.updater.InspectionUpdateDto;
import com.epm.gestepm.modelapi.inspection.service.InspectionExportService;
import com.epm.gestepm.modelapi.inspection.service.InspectionService;
import com.epm.gestepm.modelapi.shares.noprogrammed.dto.NoProgrammedShareDto;
import com.epm.gestepm.modelapi.shares.noprogrammed.dto.finder.NoProgrammedShareByIdFinderDto;
import com.epm.gestepm.modelapi.shares.noprogrammed.service.NoProgrammedShareService;
import com.epm.gestepm.rest.common.CommonProviders;
import com.epm.gestepm.rest.common.MetadataMapper;
import com.epm.gestepm.rest.common.ResSuccessMapper;
import com.epm.gestepm.rest.inspection.decorators.InspectionResponseDecorator;
import com.epm.gestepm.rest.inspection.mappers.*;
import com.epm.gestepm.rest.inspection.operations.FindInspectionV1Operation;
import com.epm.gestepm.rest.inspection.operations.ListInspectionV1Operation;
import com.epm.gestepm.rest.inspection.request.InspectionFindRestRequest;
import com.epm.gestepm.rest.inspection.request.InspectionListRestRequest;
import com.epm.gestepm.rest.inspection.response.ResponsesForInspection;
import com.epm.gestepm.rest.inspection.response.ResponsesForInspectionList;
import com.epm.gestepm.restapi.openapi.api.InspectionV1Api;
import com.epm.gestepm.restapi.openapi.model.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.REST;
import static com.epm.gestepm.lib.logging.constants.LogOperations.*;
import static com.epm.gestepm.modelapi.inspection.security.InspectionPermission.PRMT_EDIT_I;
import static com.epm.gestepm.modelapi.inspection.security.InspectionPermission.PRMT_READ_I;
import static org.mapstruct.factory.Mappers.getMapper;

@RestController
@EnableExecutionLog(layerMarker = REST)
public class InspectionController extends BaseController implements InspectionV1Api,
        ResponsesForInspection, ResponsesForInspectionList {

    private final InspectionService inspectionService;

    private final InspectionExportService inspectionExportService;

    private final LocaleProvider localeProvider;

    private final MessageSource messageSource;

    public InspectionController(final CommonProviders commonProviders, final ApplicationContext appCtx,
                                final AppLocaleService appLocaleService, final ResponseSuccessfulHelper successHelper,
                                final InspectionService inspectionService, InspectionExportService inspectionExportService, LocaleProvider localeProvider, MessageSource messageSource) {

        super(commonProviders.localeProvider(), commonProviders.executionRequestProvider(),
                commonProviders.executionTimeProvider(), commonProviders.restContextProvider(), appCtx, appLocaleService,
                successHelper);

        this.inspectionService = inspectionService;
        this.inspectionExportService = inspectionExportService;
        this.localeProvider = localeProvider;
        this.messageSource = messageSource;
    }

    @Override
    @RequirePermits(value = PRMT_READ_I, action = "Get inspection list")
    @LogExecution(operation = OP_READ)
    public ResponseEntity<ListInspectionsV1200Response> listInspectionsV1(final Integer shareId, final List<String> meta,
                                                                          final Boolean links, final Set<String> expand,
                                                                          final Long offset, final Long limit, final String order,
                                                                          final String orderBy, final Boolean current) {
        final InspectionListRestRequest req = new InspectionListRestRequest(shareId, new ArrayList<>(), current);

        this.setCommon(req, meta, links, expand);
        this.setDefaults(req);
        this.setPagination(req, limit, offset);
        this.setOrder(req, order, orderBy);

        final InspectionFilterDto filterDto = getMapper(MapIToInspectionFilterDto.class).from(req);
        final Page<InspectionDto> page = this.inspectionService.list(filterDto, offset, limit);

        final APIMetadata metadata = this.getMetadata(req, page, new ListInspectionV1Operation());
        final List<Inspection> data = getMapper(MapIToInspectionResponse.class).from(page);

        return toListInspectionsV1200Response(metadata, data, page.hashCode());
    }

    @Override
    @RequirePermits(value = PRMT_READ_I, action = "Find inspection")
    @LogExecution(operation = OP_READ)
    public ResponseEntity<CreateInspectionV1200Response> findInspectionByIdV1(final Integer shareId, final Integer id, final List<String> meta, final Boolean links, final Set<String> expand) {

        final InspectionFindRestRequest req = new InspectionFindRestRequest(id, shareId);

        this.setCommon(req, meta, links, expand);

        final InspectionByIdFinderDto finderDto = getMapper(MapIToInspectionByIdFinderDto.class).from(req);
        final InspectionDto dto = this.inspectionService.findOrNotFound(finderDto);

        final APIMetadata metadata = this.getMetadata(req, new FindInspectionV1Operation());
        final Inspection data = getMapper(MapIToInspectionResponse.class).from(dto);

        this.decorate(req, data, InspectionResponseDecorator.class);

        return toResInspectionResponse(metadata, data, dto.hashCode());
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_I, action = "Create inspection")
    @LogExecution(operation = OP_CREATE)
    public ResponseEntity<CreateInspectionV1200Response> createInspectionV1(final Integer shareId, final CreateInspectionV1Request reqCreateInspection) {

        final InspectionCreateDto createDto = getMapper(MapIToInspectionCreateDto.class).from(reqCreateInspection);
        createDto.setShareId(shareId);

        final InspectionDto countryDto = this.inspectionService.create(createDto);

        final APIMetadata metadata = this.getDefaultMetadata();
        final Inspection data = getMapper(MapIToInspectionResponse.class).from(countryDto);

        final CreateInspectionV1200Response response = new CreateInspectionV1200Response();
        response.setMetadata(getMapper(MetadataMapper.class).from(metadata));
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_I, action = "Update inspection")
    @LogExecution(operation = OP_UPDATE)
    public ResponseEntity<CreateInspectionV1200Response> updateInspectionV1(final Integer shareId, final Integer inspectionId, final UpdateInspectionV1Request reqUpdateInspection) {

        final InspectionUpdateDto updateDto = getMapper(MapIToInspectionUpdateDto.class).from(reqUpdateInspection);
        updateDto.setId(inspectionId);
        updateDto.setShareId(shareId);

        final InspectionDto inspectionDto = this.inspectionService.update(updateDto);

        final APIMetadata metadata = this.getDefaultMetadata();
        final Inspection data = getMapper(MapIToInspectionResponse.class).from(inspectionDto);

        final CreateInspectionV1200Response response = new CreateInspectionV1200Response();
        response.setMetadata(getMapper(MetadataMapper.class).from(metadata));
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_I, action = "Delete inspection")
    @LogExecution(operation = OP_DELETE)
    public ResponseEntity<ResSuccess> deleteInspectionV1(final Integer shareId, final Integer inspectionId) {

        final InspectionDeleteDto deleteDto = new InspectionDeleteDto(inspectionId, shareId);

        this.inspectionService.delete(deleteDto);

        return this.success(getMapper(ResSuccessMapper.class)::from);
    }

    @Override
    @RequirePermits(value = PRMT_READ_I, action = "Export inspection")
    @LogExecution(operation = OP_READ)
    public ResponseEntity<Resource> exportInspectionV1(Integer shareId, final Integer inspectionId) {

        final String language = this.localeProvider.getLocale().orElse("es");
        final java.util.Locale locale = new java.util.Locale(language);

        final InspectionDto inspection = this.inspectionService.findOrNotFound(new InspectionByIdFinderDto(inspectionId));

        final byte[] pdf = this.inspectionExportService.generate(inspection);
        final Resource resource = new ByteArrayResource(pdf);
        final String fileName = messageSource.getMessage("shares.no.programmed.pdf.name",
                new Object[] {
                        inspection.getShareId().toString(),
                        inspection.getId().toString(),
                        Utiles.transform(inspection.getStartDate(), "yyyyMMdd")
                }, locale) + ".pdf";

        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    @Override
    @RequirePermits(value = PRMT_READ_I, action = "Export inspection materials")
    @LogExecution(operation = OP_READ)
    public ResponseEntity<Resource> exportInspectionMaterialsV1(Integer shareId, final Integer inspectionId) {

        final String language = this.localeProvider.getLocale().orElse("es");
        final java.util.Locale locale = new java.util.Locale(language);

        final InspectionDto inspection = this.inspectionService.findOrNotFound(new InspectionByIdFinderDto(inspectionId));

        final byte[] pdf = this.inspectionExportService.generateMaterials(inspection);
        final Resource resource = new ByteArrayResource(pdf);
        final String fileName = messageSource.getMessage("shares.no.programmed.materials.pdf.name",
                new Object[] {
                        inspection.getShareId().toString(),
                        inspection.getId().toString(),
                        Utiles.transform(inspection.getStartDate(), "yyyyMMdd")
                }, locale) + ".pdf";

        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
}


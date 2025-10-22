package com.epm.gestepm.rest.family;

import com.epm.gestepm.lib.applocale.apimodel.service.AppLocaleService;
import com.epm.gestepm.lib.controller.BaseController;
import com.epm.gestepm.lib.controller.metadata.APIMetadata;
import com.epm.gestepm.lib.controller.response.ResponseSuccessfulHelper;
import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.lib.security.annotation.RequirePermits;
import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.masterdata.api.family.dto.FamilyDto;
import com.epm.gestepm.masterdata.api.family.dto.creator.FamilyCreateDto;
import com.epm.gestepm.masterdata.api.family.dto.deleter.FamilyDeleteDto;
import com.epm.gestepm.masterdata.api.family.dto.filter.FamilyFilterDto;
import com.epm.gestepm.masterdata.api.family.dto.finder.FamilyByIdFinderDto;
import com.epm.gestepm.masterdata.api.family.dto.updater.FamilyUpdateDto;
import com.epm.gestepm.masterdata.api.family.service.FamilyService;
import com.epm.gestepm.rest.family.decorators.FamilyResponseDecorator;
import com.epm.gestepm.rest.common.CommonProviders;
import com.epm.gestepm.rest.common.MetadataMapper;
import com.epm.gestepm.rest.common.ResSuccessMapper;
import com.epm.gestepm.rest.family.mappers.*;
import com.epm.gestepm.rest.family.operations.FindFamilyV1Operation;
import com.epm.gestepm.rest.family.operations.ListFamilyV1Operation;
import com.epm.gestepm.rest.family.request.FamilyFindRestRequest;
import com.epm.gestepm.rest.family.request.FamilyListRestRequest;
import com.epm.gestepm.rest.family.response.ResponsesForFamily;
import com.epm.gestepm.rest.family.response.ResponsesForFamilyList;
import com.epm.gestepm.restapi.openapi.api.FamiliesV1Api;
import com.epm.gestepm.restapi.openapi.model.*;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.REST;
import static com.epm.gestepm.lib.logging.constants.LogOperations.*;
import static com.epm.gestepm.masterdata.api.family.security.FamilyPermission.*;
import static org.mapstruct.factory.Mappers.getMapper;

@RestController
@EnableExecutionLog(layerMarker = REST)
public class FamilyController extends BaseController implements FamiliesV1Api, ResponsesForFamily, ResponsesForFamilyList {

    private final FamilyService familyService;

    public FamilyController(final CommonProviders commonProviders, final ApplicationContext appCtx,
                            final AppLocaleService appLocaleService, final ResponseSuccessfulHelper successHelper,
                            final FamilyService familyService) {

        super(commonProviders.localeProvider(), commonProviders.executionRequestProvider(),
                commonProviders.executionTimeProvider(), commonProviders.restContextProvider(), appCtx, appLocaleService,
                successHelper);

        this.familyService = familyService;
    }

    @Override
    @RequirePermits(value = PRMT_READ_FA, action = "Get family list")
    @LogExecution(operation = OP_READ)
    public ResponseEntity<ListFamiliesV1200Response> listFamiliesV1(final List<String> meta, final Boolean links, Set<String> expand, final Long offset, final Long limit, final String order, final String orderBy, final List<Integer> ids, final String nameContains) {

        final FamilyListRestRequest req = new FamilyListRestRequest(ids, nameContains);

        this.setCommon(req, meta, links, expand);
        this.setDefaults(req);
        this.setPagination(req, limit, offset);
        this.setOrder(req, order, orderBy);

        final FamilyFilterDto filterDto = getMapper(MapFAToFamilyFilterDto.class).from(req);
        final Page<FamilyDto> page = this.familyService.list(filterDto, offset, limit);

        final APIMetadata metadata = this.getMetadata(req, page, new ListFamilyV1Operation());
        final List<Family> data = getMapper(MapFAToFamilyResponse.class).from(page);

        this.decorate(req, data, FamilyResponseDecorator.class);

        return toResFamilyListResponse(metadata, data, page.hashCode());
    }

    @Override
    @RequirePermits(value = PRMT_READ_FA, action = "Find family")
    @LogExecution(operation = OP_READ)
    public ResponseEntity<CreateFamilyV1200Response> findFamilyByIdV1(final Integer id, final List<String> meta, final Boolean links, final Set<String> expand) {

        final FamilyFindRestRequest req = new FamilyFindRestRequest(id);

        this.setCommon(req, meta, links, null);

        final FamilyByIdFinderDto finderDto = getMapper(MapFAToFamilyByIdFinderDto.class).from(req);
        final FamilyDto dto = this.familyService.findOrNotFound(finderDto);

        final APIMetadata metadata = this.getMetadata(req, new FindFamilyV1Operation());
        final Family data = getMapper(MapFAToFamilyResponse.class).from(dto);

        this.decorate(req, data, FamilyResponseDecorator.class);

        return toResFamilyResponse(metadata, data, dto.hashCode());
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_FA, action = "Create family")
    @LogExecution(operation = OP_CREATE)
    public ResponseEntity<CreateFamilyV1200Response> createFamilyV1(final CreateFamilyV1Request reqCreateFamily) {

        final FamilyCreateDto createDto = getMapper(MapFAToFamilyCreateDto.class).from(reqCreateFamily);

        final FamilyDto familyDto = this.familyService.create(createDto);

        final APIMetadata metadata = this.getDefaultMetadata();
        final Family data = getMapper(MapFAToFamilyResponse.class).from(familyDto);

        final CreateFamilyV1200Response response = new CreateFamilyV1200Response();
        response.setMetadata(getMapper(MetadataMapper.class).from(metadata));
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_FA, action = "Update family")
    @LogExecution(operation = OP_UPDATE)
    public ResponseEntity<CreateFamilyV1200Response> updateFamilyV1(final Integer id, final UpdateFamilyV1Request reqUpdateFamily) {

        final FamilyUpdateDto updateDto = getMapper(MapFAToFamilyUpdateDto.class).from(reqUpdateFamily);
        updateDto.setId(id);

        final FamilyDto familyDto = this.familyService.update(updateDto);

        final APIMetadata metadata = this.getDefaultMetadata();
        final Family data = getMapper(MapFAToFamilyResponse.class).from(familyDto);

        final CreateFamilyV1200Response response = new CreateFamilyV1200Response();
        response.setMetadata(getMapper(MetadataMapper.class).from(metadata));
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_FA, action = "Delete family")
    @LogExecution(operation = OP_DELETE)
    public ResponseEntity<ResSuccess> deleteFamilyV1(final Integer id) {

        final FamilyDeleteDto deleteDto = new FamilyDeleteDto();
        deleteDto.setId(id);

        this.familyService.delete(deleteDto);

        return this.success(getMapper(ResSuccessMapper.class)::from);
    }
}

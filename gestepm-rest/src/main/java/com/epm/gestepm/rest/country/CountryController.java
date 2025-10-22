package com.epm.gestepm.rest.country;

import com.epm.gestepm.rest.common.CommonProviders;
import com.epm.gestepm.rest.common.MetadataMapper;
import com.epm.gestepm.rest.common.ResSuccessMapper;
import com.epm.gestepm.rest.country.mappers.*;
import com.epm.gestepm.rest.country.operations.FindCountryV1Operation;
import com.epm.gestepm.rest.country.operations.ListCountryV1Operation;
import com.epm.gestepm.rest.country.request.CountryFindRestRequest;
import com.epm.gestepm.rest.country.request.CountryListRestRequest;
import com.epm.gestepm.rest.country.response.ResponsesForCountry;
import com.epm.gestepm.rest.country.response.ResponsesForCountryList;
import com.epm.gestepm.lib.applocale.apimodel.service.AppLocaleService;
import com.epm.gestepm.lib.controller.BaseController;
import com.epm.gestepm.lib.controller.metadata.APIMetadata;
import com.epm.gestepm.lib.controller.response.ResponseSuccessfulHelper;
import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.lib.security.annotation.RequirePermits;
import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.masterdata.api.country.dto.CountryDto;
import com.epm.gestepm.masterdata.api.country.dto.creator.CountryCreateDto;
import com.epm.gestepm.masterdata.api.country.dto.deleter.CountryDeleteDto;
import com.epm.gestepm.masterdata.api.country.dto.filter.CountryFilterDto;
import com.epm.gestepm.masterdata.api.country.dto.finder.CountryByIdFinderDto;
import com.epm.gestepm.masterdata.api.country.dto.updater.CountryUpdateDto;
import com.epm.gestepm.masterdata.api.country.service.CountryService;
import com.epm.gestepm.restapi.openapi.api.CountriesV1Api;
import com.epm.gestepm.restapi.openapi.model.*;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.REST;
import static com.epm.gestepm.lib.logging.constants.LogOperations.*;
import static com.epm.gestepm.masterdata.api.country.security.CountryPermission.PRMT_EDIT_C;
import static com.epm.gestepm.masterdata.api.country.security.CountryPermission.PRMT_READ_C;
import static org.mapstruct.factory.Mappers.getMapper;

@RestController
@EnableExecutionLog(layerMarker = REST)
public class CountryController extends BaseController implements CountriesV1Api, ResponsesForCountry, ResponsesForCountryList {

    private final CountryService countryService;

    public CountryController(final CommonProviders commonProviders, final ApplicationContext appCtx,
                             final AppLocaleService appLocaleService, final ResponseSuccessfulHelper successHelper,
                             final CountryService countryService) {

        super(commonProviders.localeProvider(), commonProviders.executionRequestProvider(),
                commonProviders.executionTimeProvider(), commonProviders.restContextProvider(), appCtx, appLocaleService,
                successHelper);

        this.countryService = countryService;
    }

    @Override
    @RequirePermits(value = PRMT_READ_C, action = "Get Countries List")
    @LogExecution(operation = OP_READ)
    public ResponseEntity<ListCountriesV1200Response> listCountriesV1(final List<String> meta, final Boolean links, final Long offset, final Long limit, final String order, final String orderBy, final List<Integer> ids, final String name, final List<String> tags) {

        final CountryListRestRequest req = new CountryListRestRequest(ids, name, tags);

        this.setCommon(req, meta, links, null);
        this.setDefaults(req);
        this.setPagination(req, limit, offset);
        this.setOrder(req, order, orderBy);

        final CountryFilterDto filterDto = getMapper(MapCRToCountryFilterDto.class).from(req);
        final Page<CountryDto> page = this.countryService.list(filterDto, offset, limit);

        final APIMetadata metadata = this.getMetadata(req, page, new ListCountryV1Operation());
        final List<Country> data = getMapper(MapCToCountryResponse.class).from(page);

        return toResCountryListResponse(metadata, data, page.hashCode());
    }

    @Override
    @RequirePermits(value = PRMT_READ_C, action = "Find country")
    @LogExecution(operation = OP_READ)
    public ResponseEntity<CreateCountryV1200Response> findCountryByIdV1(final Integer id, final List<String> meta, final Boolean links) {

        final CountryFindRestRequest req = new CountryFindRestRequest(id);

        this.setCommon(req, meta, links, null);

        final CountryByIdFinderDto finderDto = getMapper(MapCRToCountryByIdFinderDto.class).from(req);
        final CountryDto dto = this.countryService.findOrNotFound(finderDto);

        final APIMetadata metadata = this.getMetadata(req, new FindCountryV1Operation());
        final Country data = getMapper(MapCToCountryResponse.class).from(dto);

        return toResCountryResponse(metadata, data, dto.hashCode());
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_C, action = "Create country")
    @LogExecution(operation = OP_CREATE)
    public ResponseEntity<CreateCountryV1200Response> createCountryV1(final CreateCountryV1Request reqCreateCountry) {

        final CountryCreateDto createDto = getMapper(MapCToCountryCreateDto.class).from(reqCreateCountry);

        final CountryDto countryDto = this.countryService.create(createDto);

        final APIMetadata metadata = this.getDefaultMetadata();
        final Country data = getMapper(MapCToCountryResponse.class).from(countryDto);

        final CreateCountryV1200Response response = new CreateCountryV1200Response();
        response.setMetadata(getMapper(MetadataMapper.class).from(metadata));
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_C, action = "Update country")
    @LogExecution(operation = OP_UPDATE)
    public ResponseEntity<CreateCountryV1200Response> updateCountryV1(final Integer id, final UpdateCountryV1Request reqUpdateCountry) {

        final CountryUpdateDto updateDto = getMapper(MapCToCountryUpdateDto.class).from(reqUpdateCountry);
        updateDto.setId(id);

        final CountryDto countryDto = this.countryService.update(updateDto);

        final APIMetadata metadata = this.getDefaultMetadata();
        final Country data = getMapper(MapCToCountryResponse.class).from(countryDto);

        final CreateCountryV1200Response response = new CreateCountryV1200Response();
        response.setMetadata(getMapper(MetadataMapper.class).from(metadata));
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    @Override
    @RequirePermits(value = PRMT_EDIT_C, action = "Delete country")
    @LogExecution(operation = OP_DELETE)
    public ResponseEntity<ResSuccess> deleteCountryV1(final Integer id) {

        final CountryDeleteDto deleteDto = new CountryDeleteDto();
        deleteDto.setId(id);

        this.countryService.delete(deleteDto);

        return this.success(getMapper(ResSuccessMapper.class)::from);
    }
}

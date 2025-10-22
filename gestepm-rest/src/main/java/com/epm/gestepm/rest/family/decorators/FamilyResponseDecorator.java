
package com.epm.gestepm.rest.family.decorators;

import com.epm.gestepm.lib.controller.RestRequest;
import com.epm.gestepm.lib.controller.decorator.BaseResponseDataDecorator;
import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.masterdata.api.country.dto.CountryDto;
import com.epm.gestepm.masterdata.api.country.dto.finder.CountryByIdFinderDto;
import com.epm.gestepm.masterdata.api.country.service.CountryService;
import com.epm.gestepm.rest.family.request.FamilyFindRestRequest;
import com.epm.gestepm.rest.country.mappers.MapCToCountryResponse;
import com.epm.gestepm.restapi.openapi.model.Family;
import com.epm.gestepm.restapi.openapi.model.Country;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.DELEGATOR;
import static com.epm.gestepm.lib.logging.constants.LogOperations.OP_PROCESS;
import static org.mapstruct.factory.Mappers.getMapper;

@Component("familyResponseDecorator")
@EnableExecutionLog(layerMarker = DELEGATOR)
public class FamilyResponseDecorator extends BaseResponseDataDecorator<Family> {

    public static final String AC_C_EXPAND = "country";

    private final CountryService countryService;

    public FamilyResponseDecorator(ApplicationContext applicationContext, CountryService countryService) {
        super(applicationContext);
        this.countryService = countryService;
    }

    @Override
    @LogExecution(operation = OP_PROCESS,
            msgIn = "Decorating family response",
            msgOut = "Family decorated OK",
            errorMsg = "Error decorating family response")
    public void decorate(RestRequest request, Family data) {

        if (request.getLinks()) {

            final FamilyFindRestRequest selfReq = new FamilyFindRestRequest(data.getId());
            selfReq.commonValuesFrom(request);
        }

        /*if (request.hasExpand(AC_C_EXPAND)) {

            final Country country = data.getCountry();
            final Integer id = country.getId();

            final CountryByIdFinderDto finderDto = new CountryByIdFinderDto(id);
            final CountryDto countryDto = this.countryService.findOrNotFound(finderDto);

            final Country response = getMapper(MapCToCountryResponse.class).from(countryDto);

            data.setCountry(response);
        }*/
    }
}

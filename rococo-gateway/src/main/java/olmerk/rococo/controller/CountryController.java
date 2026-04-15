package olmerk.rococo.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import olmerk.rococo.config.RococoGatewayServiceConfig;
import olmerk.rococo.model.ArtistJson;
import olmerk.rococo.model.CountryJson;
import olmerk.rococo.service.api.GrpcArtistClient;
import olmerk.rococo.service.api.GrpcCountryClient;

import java.util.List;

@RestController
@RequestMapping("/api/country")
@SecurityRequirement(name = RococoGatewayServiceConfig.OPEN_API_AUTH_SCHEME)
public class CountryController {

    private final GrpcCountryClient countryClient;

    @Autowired
    public CountryController(GrpcCountryClient countryClient) {
        this.countryClient = countryClient;
    }

    @GetMapping(params = {"page", "size"})
    public Page<CountryJson> getAllCountries(@PageableDefault Pageable pageable) {
        List<CountryJson> countryJsonList = countryClient.getAllCountries(pageable);
        return new PageImpl<>(countryJsonList, pageable, countryJsonList.size());
    }
}


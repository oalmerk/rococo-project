package olmerk.rococo.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import olmerk.rococo.config.RococoGatewayServiceConfig;
import olmerk.rococo.model.ArtistJson;
import olmerk.rococo.model.CountryJson;
import olmerk.rococo.model.MuseumJson;
import olmerk.rococo.model.UserJson;
import olmerk.rococo.service.api.GrpcCountryClient;
import olmerk.rococo.service.api.GrpcMuseumClient;

import java.util.List;

@RestController
@RequestMapping("/api/museum")
@SecurityRequirement(name = RococoGatewayServiceConfig.OPEN_API_AUTH_SCHEME)
public class MuseumController {

    private final GrpcMuseumClient grpcMuseumClient;

    @Autowired
    public MuseumController(GrpcMuseumClient grpcMuseumClient) {
        this.grpcMuseumClient = grpcMuseumClient;
    }

    @GetMapping()
    public Page<MuseumJson> getAllMuseums(@RequestParam(required = false) String title, @PageableDefault Pageable pageable) {
        List<MuseumJson> countryJsonList = grpcMuseumClient.getAllMuseums(title == null ? "" : title, pageable);
        return new PageImpl<>(countryJsonList, pageable, countryJsonList.size());
    }

    @GetMapping("/{id}")
    public MuseumJson getById(@PathVariable("id") String id) {
        MuseumJson museumJson = grpcMuseumClient.getMuseumById(id);
        return museumJson;
    }

    @PostMapping
    public MuseumJson createMuseum(@Valid @RequestBody MuseumJson museumJson) {
        MuseumJson createdMuseumJson = grpcMuseumClient.createMuseum(museumJson);
        return createdMuseumJson;
    }

    @PatchMapping()
    public MuseumJson updateMuseum(@Valid @RequestBody MuseumJson museumJson) {
        return grpcMuseumClient.updateMuseum(museumJson);
    }
}


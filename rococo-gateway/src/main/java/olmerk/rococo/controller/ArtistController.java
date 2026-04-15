package olmerk.rococo.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import olmerk.rococo.config.RococoGatewayServiceConfig;
import olmerk.rococo.model.ArtistJson;
import olmerk.rococo.service.api.GrpcArtistClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artist")
@SecurityRequirement(name = RococoGatewayServiceConfig.OPEN_API_AUTH_SCHEME)
public class ArtistController {

    private final GrpcArtistClient artistClient;

    @Autowired
    public ArtistController(GrpcArtistClient artistClient) {
        this.artistClient = artistClient;
    }

    @GetMapping()
    public Page<ArtistJson> getAll(@RequestParam(required = false) String name, @PageableDefault Pageable pageable) {
        List<ArtistJson> artistJsonList = artistClient.getAllArtist(name == null ? "" : name, pageable);
        return new PageImpl<>(artistJsonList, pageable, artistJsonList.size());
    }


    @GetMapping("/{id}")
    public ArtistJson getById(@PathVariable("id") String id) {
        ArtistJson artistJson = artistClient.getArtistById(id);
        return artistJson;
    }

    @PostMapping
    public ArtistJson createArtist(@Valid @RequestBody ArtistJson artistJson) {
        ArtistJson createdArtistJson = artistClient.createArtist(artistJson);
        return createdArtistJson;
    }

    @PatchMapping()
    public ArtistJson updateArtist(@Valid @RequestBody ArtistJson artistJson) {
        return artistClient.updateArtist(artistJson);
    }
}
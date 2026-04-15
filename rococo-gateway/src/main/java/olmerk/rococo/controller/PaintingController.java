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
import olmerk.rococo.model.PaintingJson;
import olmerk.rococo.service.api.GrpcPaintingClient;

import java.util.List;

@RestController
@RequestMapping("/api/painting")
@SecurityRequirement(name = RococoGatewayServiceConfig.OPEN_API_AUTH_SCHEME)
public class PaintingController {

    private final GrpcPaintingClient grpcPaintingClient;

    @Autowired
    public PaintingController(GrpcPaintingClient grpcPaintingClient) {
        this.grpcPaintingClient = grpcPaintingClient;
    }

    @GetMapping()
    public Page<PaintingJson> getAllPaintings(@RequestParam(required = false) String title, @PageableDefault Pageable pageable) {
        List<PaintingJson> paintingJsons = grpcPaintingClient.getAllPainting(title == null ? "" : title, pageable);
        return new PageImpl<>(paintingJsons, pageable, paintingJsons.size());
    }

    @GetMapping("/{id}")
    public PaintingJson getById(@PathVariable("id") String id) {
        PaintingJson paintingJson = grpcPaintingClient.getPaintingById(id);
        return paintingJson;
    }

    @GetMapping("/author/{id}")
    public Page<PaintingJson> getByAuthorId(@PathVariable("id") String id, @PageableDefault Pageable pageable) {
        List<PaintingJson> paintingJsons = grpcPaintingClient.getPaintingByAuthor(id, pageable);
        return new PageImpl<>(paintingJsons, pageable, paintingJsons.size());
    }

    @PostMapping
    public PaintingJson createPainting(@Valid @RequestBody PaintingJson paintingJson) {
        PaintingJson createdpaintingJson = grpcPaintingClient.createPainting(paintingJson);
        return createdpaintingJson;
    }

    @PatchMapping()
    public PaintingJson updatePainting(@Valid @RequestBody PaintingJson paintingJson) {
        return grpcPaintingClient.updatePainting(paintingJson);
    }
}


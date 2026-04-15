package olmerk.rococo.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import olmerk.rococo.config.RococoGatewayServiceConfig;
import olmerk.rococo.model.UserJson;
import olmerk.rococo.service.api.GrpcUserdataClient;

@RestController
@RequestMapping("/api/user")
@SecurityRequirement(name = RococoGatewayServiceConfig.OPEN_API_AUTH_SCHEME)
public class UserController {

    private final GrpcUserdataClient grpcUserdataClient;

    @Autowired
    public UserController(GrpcUserdataClient grpcUserdataClient) {
        this.grpcUserdataClient = grpcUserdataClient;
    }

    @GetMapping
    public UserJson currentUser(@AuthenticationPrincipal Jwt principal) {
        final String principalUsername = principal.getClaim("sub");
        return grpcUserdataClient.getUser(principalUsername);
    }


    @PatchMapping
    public UserJson updateUserInfo(@Valid @RequestBody UserJson user) {
        return grpcUserdataClient.upDateUser(user);
    }
}
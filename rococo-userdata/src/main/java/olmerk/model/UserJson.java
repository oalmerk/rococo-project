package olmerk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import olmerk.grpc.rococo.User;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("username")
        String username,
        @JsonProperty("firstname")
        String firstname,
        @JsonProperty("lastname")
        String lastname,
        @JsonProperty("avatar")
        String avatar
) {

    public static @Nonnull UserJson fromGrpcMessage(@Nonnull User user) {
        return new UserJson(
                UUID.fromString(user.getId()),
                user.getUsername(),
                user.getFirstname(),
                user.getLastname(),
                user.getAvatar()
        );
    }
}


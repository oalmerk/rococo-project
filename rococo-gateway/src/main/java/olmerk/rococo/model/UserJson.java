package olmerk.rococo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.Size;
import olmerk.grpc.rococo.User;
import olmerk.rococo.validation.IsPhotoString;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserJson(
        @JsonProperty("id")
        UUID id,
        @Size(min = 3, max = 50, message = "Allowed username length should be from 3 to 50 characters")
        @JsonProperty("username")
        String username,
        @JsonProperty("firstname")
        @Size(max = 225, message = "First name can`t be longer than 225 characters")
        String firstname,
        @JsonProperty("lastname")
        @Size(max = 225, message = "First name can`t be longer than 225 characters")
        String lastname,
        @IsPhotoString
        @JsonProperty("avatar")
        @Size(max = 1024 * 1024, message = "Photo is should be 1Mb")
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

    public @Nonnull User toGrpcMessage() {
        User.Builder grpcUserBuilder = User.newBuilder();
        grpcUserBuilder.setId(String.valueOf(id));
        grpcUserBuilder.setUsername(username);
        if (firstname != null) {
            grpcUserBuilder.setFirstname(firstname);
        }
        if (lastname != null) {
            grpcUserBuilder.setLastname(lastname);
        }
        if (avatar != null) {
            grpcUserBuilder.setAvatar(avatar);
        }
        return grpcUserBuilder.build();
    }

}


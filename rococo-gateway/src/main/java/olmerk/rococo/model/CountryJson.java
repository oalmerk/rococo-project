package olmerk.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import olmerk.grpc.rococo.Country;

import java.util.UUID;

public record CountryJson(@JsonProperty("id")
                          UUID id,
                          @NotBlank(message = "Name can not be blank")
                          @Size(max = 225, message = "Allowed name length should be to 225 characters")
                          @JsonProperty("name")
                          String name) {

    public static @Nonnull CountryJson fromGrpcMessage(@Nonnull Country country) {
        return new CountryJson(
                UUID.fromString(country.getId()),
                country.getName());}
}




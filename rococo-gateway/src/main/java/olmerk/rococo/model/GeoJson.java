package olmerk.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import olmerk.grpc.rococo.Geo;

public record GeoJson(@JsonProperty("country")
                      CountryJson country,
                      @NotBlank(message = "city can not be blank")
                      @Size(min = 3, max = 225, message = "Allowed city length should be from 3 to 225 characters")
                      @JsonProperty("city")
                      String city) {
    public static @Nonnull GeoJson fromGrpcMessage(@Nonnull Geo geo) {
        return new GeoJson(
                CountryJson.fromGrpcMessage(geo.getCountry()),
                geo.getCity())
                ;}
}


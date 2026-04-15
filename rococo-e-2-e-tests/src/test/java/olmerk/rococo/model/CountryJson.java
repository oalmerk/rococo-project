package olmerk.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import olmerk.rococo.data.entity.museum.CountryEntity;

import java.util.UUID;

public record CountryJson(@JsonProperty("id")
                          UUID id,
                          @JsonProperty("name")
                          String name) {

    public static @Nonnull CountryJson fromJson(@Nonnull CountryEntity country) {
        return new CountryJson(
               country.getId(),
                country.getName());}
}



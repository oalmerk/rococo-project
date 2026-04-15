package olmerk.rococo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import olmerk.rococo.data.entity.museum.MuseumEntity;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public record MuseumJson(@JsonProperty("id")
                         UUID id,
                         @JsonProperty("title")
                         String title,
                         @JsonProperty("description")
                         String description,
                         @JsonProperty("photo")
                         String photo,
                         @JsonProperty("geo")
                         @JsonInclude(JsonInclude.Include.NON_NULL)
                         GeoJson geo) {

    public static @Nonnull MuseumJson fromGJson(@Nonnull MuseumEntity museum) {
        return new MuseumJson(
                museum.getId(),
                museum.getTitle(),
                museum.getDescription(),
                new String (museum.getPhoto(), StandardCharsets.UTF_8),
                new GeoJson(new CountryJson(museum.getCountry().getId(), museum.getCountry().getName()), museum.getCity()));
    }
}

package olmerk.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import olmerk.rococo.data.entity.artist.ArtistEntity;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public record ArtistJson(@JsonProperty("id")
                         UUID id,
                         @JsonProperty("name")
                         String name,
                         @JsonProperty("biography")
                         String biography,
                         @JsonProperty("photo")
                         String photo
) {
    public static @Nonnull ArtistJson fromEntity(@Nonnull ArtistEntity artist) {
        return new ArtistJson(
                artist.getId(),
                artist.getName(),
                artist.getBiography(),
                new String(artist.getPhoto(), StandardCharsets.UTF_8)
        );
    }
}

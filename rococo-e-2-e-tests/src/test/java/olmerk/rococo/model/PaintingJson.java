package olmerk.rococo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jspecify.annotations.NonNull;
import olmerk.rococo.data.entity.painting.PaintingEntity;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public record PaintingJson(
                           @JsonProperty("id")
                           UUID id,
                           @JsonProperty("title")
                           String title,
                           @JsonProperty("description")
                           String description,
                           @JsonProperty("content")
                           String content,
                           @JsonProperty("artist")
                           UUID artistId,
                           @JsonProperty("museum")
                           UUID museumId) {

    @JsonCreator
    public PaintingJson(
            @JsonProperty("id") UUID id,
            @JsonProperty("title") String title,
            @JsonProperty("description") String description,
            @JsonProperty("content") String content,
            @JsonProperty("artist") Map<String, Object> artist,
            @JsonProperty("museum") Map<String, Object> museum) {
        this(
                id,
                title,
                description,
                content,
                UUID.fromString((String) artist.get("id")),
                UUID.fromString((String) museum.get("id"))
        );
    }
    public static @NonNull PaintingJson fromJson(PaintingEntity paintingEntity) {
                return new PaintingJson(
                        paintingEntity.getId(),
                        paintingEntity.getTitle(),
                        paintingEntity.getDescription(),
                        new String(paintingEntity.getContent(), StandardCharsets.UTF_8),
                        paintingEntity.getArtistId(),
                        paintingEntity.getMuseumId());
    }
}

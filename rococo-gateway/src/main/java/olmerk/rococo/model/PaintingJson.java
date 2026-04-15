package olmerk.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import olmerk.grpc.rococo.Painting;
import olmerk.rococo.validation.IsPhotoString;

import java.util.UUID;

public record PaintingJson(@JsonProperty("id")
                           UUID id,
                           @NotBlank(message = "Title can not be blank")
                           @Size(min = 3, max = 225, message = "Allowed title length should be from 3 to 225 characters")
                           @JsonProperty("title")
                           String title,
                           @NotBlank(message = "description can not be blank")
                           @Size(min = 10, max = 225, message = "Allowed description length should be from 10 to 225 characters")
                           @JsonProperty("description")
                           String description,
                           @IsPhotoString
                           @JsonProperty("content")
                           @Size(max = 1024 * 1024, message = "Photo is should be 1Mb")
                           String content,
                           @JsonProperty("artist")
                           ArtistJson artist,
                           @JsonProperty("museum")
                           MuseumJson museum) {
    public static @Nonnull PaintingJson fromGrpcMessage(@Nonnull Painting painting) {
        return new PaintingJson(
                UUID.fromString(painting.getId()),
                painting.getTitle(),
                painting.getDescription(),
                painting.getContent(),
                ArtistJson.fromGrpcMessage(painting.getArtist()),
                MuseumJson.fromGrpcMessage(painting.getMuseum()));
    }
}


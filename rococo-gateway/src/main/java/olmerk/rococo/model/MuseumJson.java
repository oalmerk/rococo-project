package olmerk.rococo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import olmerk.grpc.rococo.Museum;
import olmerk.rococo.validation.IsPhotoString;

import java.util.UUID;

public record MuseumJson(@JsonProperty("id")
                         UUID id,
                         @NotBlank(message = "Title can not be blank")
                         @Size(min = 3, max = 225, message = "Allowed title length should be from 3 to 225 characters")
                         @JsonProperty("title")
                         String title,
                         @NotBlank(message = "description can not be blank")
                         @Size(min = 10, max = 1000, message = "Allowed description length should be from 10 to 1000 characters")
                         @JsonProperty("description")
                         String description,
                         @IsPhotoString
                         @JsonProperty("photo")
                         @Size(max = 1024 * 1024, message = "Photo is should be 1Mb")
                         String photo,
                         @JsonProperty("geo")
                         @JsonInclude(JsonInclude.Include.NON_NULL)
                         GeoJson geo) {

    public static @Nonnull MuseumJson fromGrpcMessage(@Nonnull Museum museum) {
        return new MuseumJson(
                UUID.fromString(museum.getId()),
                museum.getTitle(),
                museum.getDescription(),
                museum.getPhoto(),
                museum.getGeo().getCountry().getId().isEmpty()? null : GeoJson.fromGrpcMessage(museum.getGeo()));
    }
}


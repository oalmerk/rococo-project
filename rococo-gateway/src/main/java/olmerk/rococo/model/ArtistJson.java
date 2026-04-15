package olmerk.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import olmerk.grpc.rococo.Artist;
import olmerk.rococo.validation.IsPhotoString;

import java.util.UUID;

public record ArtistJson(@JsonProperty("id")
                         UUID id,
                         @NotBlank(message = "Name can not be blank")
                         @Size(min = 3, max = 225, message = "Allowed name length should be from 3 to 225 characters")
                         @JsonProperty("name")
                         String name,
                         @NotBlank(message = "Biography can not be blank")
                         @Size(min = 3, max = 2000, message = "Allowed biography length should be from 3 to 2000 characters")
                         @JsonProperty("biography")
                         String biography,
                         @IsPhotoString
                         @Size(max = 1024 * 1024, message = "Photo is should be 1Mb")
                         @JsonProperty("photo")
                         String photo
) {

    public static @Nonnull ArtistJson fromGrpcMessage(@Nonnull Artist artist) {
        return new ArtistJson(
                UUID.fromString(artist.getId()),
                artist.getName(),
                artist.getBiography(),
                artist.getPhoto()
        );
    }
}


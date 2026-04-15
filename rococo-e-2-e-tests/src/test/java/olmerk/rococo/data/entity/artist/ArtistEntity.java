package olmerk.rococo.data.entity.artist;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;


@Getter
@Setter
@Entity
@Table(name = "artist")
public class ArtistEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    private UUID id;

    @Column(name = "name", nullable = false, unique = true, length = 255)
    private String name;

    @Column(name = "biography", nullable = false, length = 2000)
    private String biography;


    @Column(name = "photo", columnDefinition = "bytea")
    private byte[] photo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArtistEntity that = (ArtistEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(biography, that.biography) &&
                Arrays.equals(photo, that.photo);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, name, biography);
        result = 31 * result + Arrays.hashCode(photo);
        return result;
    }
}

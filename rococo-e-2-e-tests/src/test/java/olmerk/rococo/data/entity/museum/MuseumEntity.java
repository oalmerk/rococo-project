package olmerk.rococo.data.entity.museum;

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
@Table(name = "museum")
public class MuseumEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    private UUID id;

    @Column(name = "title", nullable = false, unique = true, length = 255)
    private String title;

    @Column(name = "description", nullable = false,  length = 1000)
    private String description;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "photo", columnDefinition = "bytea")
    private byte[] photo;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private CountryEntity country;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MuseumEntity that)) return false;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getTitle(), that.getTitle()) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getCity(), that.getCity())
                && Arrays.equals(getPhoto(), that.getPhoto()) && Objects.equals(getCountry(), that.getCountry());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getId(), getTitle(), getDescription(), getCity(), getCountry());
        result = 31 * result + Arrays.hashCode(getPhoto());
        return result;
    }
}

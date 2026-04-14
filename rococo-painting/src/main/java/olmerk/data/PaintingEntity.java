package olmerk.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "painting")
public class PaintingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    private UUID id;

    @Column(name = "title", nullable = false, unique = true, length = 255)
    private String title;

    @Column(name = "description", nullable = false, unique = true)
    private String description;

    @Column(name = "content", columnDefinition = "bytea")
    private byte[] content;

    @Column(name = "artist_id", nullable = false)
    private UUID artistId;

    @Column(name = "museum_id", nullable = false)
    private UUID museumId;
}
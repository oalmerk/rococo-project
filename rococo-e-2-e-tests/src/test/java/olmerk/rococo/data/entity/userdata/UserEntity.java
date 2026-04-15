package olmerk.rococo.data.entity.userdata;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(length = 255)
    private String firstname;

    @Column(length = 255)
    private String lastname;

    @Column(name = "avatar", columnDefinition = "bytea")
    private byte[] avatar;

    public UserEntity() {}

    public UserEntity(String username, String firstname, String lastname, byte[] avatar) {
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.avatar = avatar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserEntity that)) return false;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getUsername(), that.getUsername()) && Objects.equals(getFirstname(), that.getFirstname()) && Objects.equals(getLastname(), that.getLastname()) && Arrays.equals(getAvatar(), that.getAvatar());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getId(), getUsername(), getFirstname(), getLastname());
        result = 31 * result + Arrays.hashCode(getAvatar());
        return result;
    }
}

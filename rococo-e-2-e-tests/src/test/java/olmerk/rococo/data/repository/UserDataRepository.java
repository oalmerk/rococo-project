package olmerk.rococo.data.repository;

import olmerk.rococo.data.entity.userdata.UserEntity;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

public interface UserDataRepository {
    @Nonnull
    UserEntity create(UserEntity user);
    @Nonnull
    UserEntity update(UserEntity user);

    @Nonnull
    Optional<UserEntity> findById(UUID id);
}

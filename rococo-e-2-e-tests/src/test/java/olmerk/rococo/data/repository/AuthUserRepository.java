package olmerk.rococo.data.repository;

import olmerk.rococo.data.entity.auth.AuthUserEntity;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;

public interface AuthUserRepository {

    @Nonnull
    AuthUserEntity create(AuthUserEntity user);

    @Nonnull
    Optional<AuthUserEntity> findById(UUID id);
}

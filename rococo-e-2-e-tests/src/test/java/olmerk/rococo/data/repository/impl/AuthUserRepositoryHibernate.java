package olmerk.rococo.data.repository.impl;

import jakarta.persistence.EntityManager;
import org.jspecify.annotations.NonNull;
import olmerk.rococo.config.Config;
import olmerk.rococo.data.entity.auth.AuthUserEntity;
import olmerk.rococo.data.jpa.EntityManagers;
import olmerk.rococo.data.repository.AuthUserRepository;

import java.util.Optional;
import java.util.UUID;

public class AuthUserRepositoryHibernate implements AuthUserRepository {

    private static final Config CONFIG = Config.getInstance();
    private static final EntityManager entityManager = EntityManagers.entityManager(CONFIG.authJdbcUrl());

    @Override
    public @NonNull AuthUserEntity create(AuthUserEntity user) {
        entityManager.joinTransaction();
        entityManager.persist(user);
        return user;
    }

    @Override
    public @NonNull Optional<AuthUserEntity> findById(UUID id) {
        return Optional.ofNullable(
                entityManager.find(AuthUserEntity.class, id)
        );
    }
}

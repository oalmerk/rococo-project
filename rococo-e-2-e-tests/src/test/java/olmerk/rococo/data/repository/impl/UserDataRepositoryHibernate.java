package olmerk.rococo.data.repository.impl;

import jakarta.persistence.EntityManager;
import org.jspecify.annotations.NonNull;
import olmerk.rococo.config.Config;
import olmerk.rococo.data.entity.userdata.UserEntity;
import olmerk.rococo.data.jpa.EntityManagers;
import olmerk.rococo.data.repository.UserDataRepository;

import java.util.Optional;
import java.util.UUID;

public class UserDataRepositoryHibernate implements UserDataRepository {

    private static final Config CONFIG = Config.getInstance();
    private static final EntityManager entityManager = EntityManagers.entityManager(CONFIG.userdataJdbcUrl());

    @Override
    public @NonNull UserEntity create(UserEntity user) {
        entityManager.joinTransaction();
        entityManager.persist(user);
        return user;
    }

    @Override
    public @NonNull UserEntity update(UserEntity user) {
        entityManager.joinTransaction();
        entityManager.merge(user);
        return user;
    }

    @Override
    public @NonNull Optional<UserEntity> findById(UUID id) {
        return Optional.ofNullable(
                entityManager.find(UserEntity.class, id)
        );
    }
}

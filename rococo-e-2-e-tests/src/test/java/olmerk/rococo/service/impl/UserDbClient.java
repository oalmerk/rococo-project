package olmerk.rococo.service.impl;

import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import olmerk.rococo.config.Config;
import olmerk.rococo.data.entity.auth.AuthUserEntity;
import olmerk.rococo.data.entity.auth.Authority;
import olmerk.rococo.data.entity.auth.AuthorityEntity;
import olmerk.rococo.data.entity.userdata.UserEntity;
import olmerk.rococo.data.repository.AuthUserRepository;
import olmerk.rococo.data.repository.UserDataRepository;
import olmerk.rococo.data.repository.impl.AuthUserRepositoryHibernate;
import olmerk.rococo.data.repository.impl.UserDataRepositoryHibernate;
import olmerk.rococo.data.templates.XaTransactionTemplate;
import olmerk.rococo.model.UserJson;
import olmerk.rococo.service.UsersClient;

import java.util.Arrays;

import static java.util.Objects.requireNonNull;
import static olmerk.rococo.jupiter.extension.UserExtension.DEFAULT_PASSWORD;

public class UserDbClient implements UsersClient {

    private static final Config CONFIG = Config.getInstance();
    private static final PasswordEncoder delegatingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
    private final UserDataRepository userDataRepository = new UserDataRepositoryHibernate();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CONFIG.authJdbcUrl(),
            CONFIG.userdataJdbcUrl()
    );

    @Override
    public @NonNull UserJson createUser(String username, String password) {
        return requireNonNull(xaTransactionTemplate.execute(() -> {
                    AuthUserEntity authUser = authUserEntity(username, password);
                    authUserRepository.create(authUser);
                    UserEntity userEntity = userDataRepository.create(userEntity(username));
                    return new UserJson(userEntity, DEFAULT_PASSWORD);
                }
        ));
    }

    @Override
    public @NonNull UserJson updateUser(UserJson userJson) {
         return requireNonNull(xaTransactionTemplate.execute(() -> {
                     UserEntity userEntity = new UserEntity();
                     userEntity.setFirstname(userJson.firstname());
                     userEntity.setLastname(userJson.lastname());
                     userEntity.setAvatar(userJson.avatar().getBytes());
                     UserEntity saved = userDataRepository.update(userEntity);
                     return new UserJson(saved);
                 }
         ));
    }

    @Override
    public @NonNull UserJson getUser(UserJson userJson) {
        return requireNonNull(xaTransactionTemplate.execute(() -> {
                    UserEntity userEntity = userDataRepository
                            .findById(userJson.id())
                            .orElseThrow();
                    return new UserJson(userEntity);
                }
        ));
    }

    private UserEntity userEntity(String username) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        return userEntity;
    }


    private AuthUserEntity authUserEntity(String username, String password) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(username);
        authUser.setPassword(delegatingPasswordEncoder.encode(password));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        authUser.setAuthorities(
                Arrays.stream(Authority.values()).map(
                        e -> {
                            AuthorityEntity ae = new AuthorityEntity();
                            ae.setUser(authUser);
                            ae.setAuthority(e);
                            return ae;
                        }
                ).toList()
        );
        return authUser;
    }
}

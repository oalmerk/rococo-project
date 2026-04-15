package olmerk.rococo.config;

import javax.annotation.Nonnull;

public interface Config {

    @Nonnull
    static Config getInstance() {
        return "docker".equals(System.getProperty("test.env"))
                ? DockerConfig.INSTANCE
                : LocalConfig.INSTANCE;
    }

    default String testDatabaseUsername() {
        return "postgres";
    }

    default String testDatabasePassword() {
        return "secret";
    }

    @Nonnull
    String frontUrl();

    @Nonnull
    String authUrl();

    @Nonnull
    String gatewayUrl();

    @Nonnull
    String userdataUrl();

    @Nonnull
    String authJdbcUrl();

    @Nonnull
    String userdataJdbcUrl();

    @Nonnull
    String artistJdbcUrl();

    @Nonnull
    String museumJdbcUrl();

    @Nonnull
    String paintingJdbcUrl();

    @Nonnull
    String paintingGrpcAddress();

    @Nonnull
    String userdataGrpcAddress();

    @Nonnull
    String museumGrpcAddress();

    @Nonnull
    String artistGrpcAddress();

    @Nonnull
    String screenshotBaseDir();

    default int museumGrpcPort() {
        return 8092;
    }

    default int userdataGrpcPort() {
        return 8088;
    }

    default int artistGrpcPort() {
        return 8092;
    }

    default int paintingGrpcPort() {
        return 8092;
    }

}

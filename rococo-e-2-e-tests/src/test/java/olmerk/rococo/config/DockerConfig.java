package olmerk.rococo.config;

import org.jspecify.annotations.NonNull;

import javax.annotation.Nonnull;

enum DockerConfig implements Config {
  INSTANCE;

  @Nonnull
  @Override
  public String frontUrl() {
    return "http://frontend.rococo.dc/";
  }

  @Override
  public @Nonnull String authUrl() {
    return "http://auth.rococo.dc:9000/";
  }

  @Nonnull
  @Override
  public String authJdbcUrl() {
    return "jdbc:postgresql://rococo-all-db:5432/rococo-auth";
  }

  @Nonnull
  @Override
  public String gatewayUrl() {
    return "http://gateway.rococo.dc:8080/";
  }

  @Nonnull
  @Override
  public String userdataUrl() {
    return "http://userdata.rococo.dc:8089/";
  }

  @Nonnull
  @Override
  public String userdataJdbcUrl() {
    return "jdbc:postgresql://rococo-all-db:5432/rococo-userdata";
  }

  @Override
  public @NonNull String artistJdbcUrl() {
    return "jdbc:postgresql://rococo-all-db:5432/rococo-artist";
  }

  @Override
  public @NonNull String museumJdbcUrl() {
    return "jdbc:postgresql://rococo-all-db:5432/rococo-museum";
  }

  @Override
  public @NonNull String paintingJdbcUrl() {
    return "jdbc:postgresql://rococo-all-db:5432/rococo-painting";
  }

  @Override
  public @NonNull String paintingGrpcAddress() {
    return "painting.rococo.dc";
  }

  @Nonnull
  @Override
  public String screenshotBaseDir() {
    return "screenshots/selenoid/";
  }

  @Nonnull
  @Override
  public String userdataGrpcAddress() {
    return "userdata.rococo.dc";
  }

  @Override
  public @NonNull String museumGrpcAddress() {
    return "museum.rococo.dc";
  }

  @Override
  public @NonNull String artistGrpcAddress() {
    return "artist.rococo.dc";
  }
}

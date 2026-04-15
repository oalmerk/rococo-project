package olmerk.rococo.config;

import org.jspecify.annotations.NonNull;

import javax.annotation.Nonnull;

enum LocalConfig implements Config {
  INSTANCE;

  @Nonnull
  @Override
  public String frontUrl() {
    return "http://127.0.0.1:3000/";
  }

  @Override
  public @Nonnull String authUrl() {
    return "http://127.0.0.1:9000/";
  }

  @Nonnull
  @Override
  public String gatewayUrl() {
    return "http://127.0.0.1:8080/";
  }

  @Nonnull
  @Override
  public String userdataUrl() {
    return "http://127.0.0.1:8089/";
  }

  @Nonnull
  @Override
  public String authJdbcUrl() {
    return "jdbc:postgresql://127.0.0.1:5432/rococo-auth";
  }

  @Nonnull
  @Override
  public String userdataJdbcUrl() {
    return "jdbc:postgresql://127.0.0.1:5432/rococo-userdata";
  }

  @Override
  public @NonNull String artistJdbcUrl() {
    return "jdbc:postgresql://127.0.0.1:5432/rococo-artist";
  }

  @Override
  public @NonNull String museumJdbcUrl() {
    return "jdbc:postgresql://127.0.0.1:5432/rococo-museum";
  }

  @Override
  public @NonNull String paintingJdbcUrl() {
    return "jdbc:postgresql://127.0.0.1:5432/rococo-painting";
  }

  @Nonnull
  @Override
  public String userdataGrpcAddress() {
    return "127.0.0.1";
  }

  @Override
  public @NonNull String museumGrpcAddress() {
    return "127.0.0.1";
  }

  @Override
  public @NonNull String artistGrpcAddress() {
    return "127.0.0.1";
  }

  @Override
  public @NonNull String paintingGrpcAddress() {
    return "127.0.0.1";
  }

  @Nonnull
  @Override
  public String screenshotBaseDir() {
    return "screenshots/local/";
  }
}

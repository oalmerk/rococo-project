package olmerk.rococo.jupiter.extension;

import olmerk.rococo.data.jdbc.Connections;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class DatabasesExtension implements SuiteExtension {
  @Override
  public void afterSuite() {
    Connections.closeAllConnections();
  }
}

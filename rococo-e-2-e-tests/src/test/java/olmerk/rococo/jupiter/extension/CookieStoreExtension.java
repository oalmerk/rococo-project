package olmerk.rococo.jupiter.extension;


import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import olmerk.rococo.api.core.ThreadSafeCookieStore;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CookieStoreExtension implements AfterTestExecutionCallback {

  @Override
  public void afterTestExecution(ExtensionContext context) throws Exception {
    ThreadSafeCookieStore.INSTANCE.removeAll();
  }
}

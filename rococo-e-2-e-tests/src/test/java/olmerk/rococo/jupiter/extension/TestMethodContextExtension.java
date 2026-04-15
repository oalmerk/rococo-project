package olmerk.rococo.jupiter.extension;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class TestMethodContextExtension implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void afterEach(ExtensionContext extensionContext){
        ContextHolder.INSTANCE.remove();
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext){
        ContextHolder.INSTANCE.set(extensionContext);
    }


    public static ExtensionContext context() {
        return ContextHolder.INSTANCE.get();
    }

    private enum ContextHolder{
        INSTANCE;

        private  final ThreadLocal<ExtensionContext> extensionContextThreadLocal = new ThreadLocal<>();

        public   void set(ExtensionContext extensionContext){
            extensionContextThreadLocal.set(extensionContext);
        }
        public  ExtensionContext get(){
           return extensionContextThreadLocal.get();
        }

        public  void remove(){
            extensionContextThreadLocal.remove();
        }
    }
}

package olmerk.rococo.api.core;

import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

public enum ThreadSafeCookieStore implements CookieStore {
    INSTANCE ;

    private final ThreadLocal<CookieStore> storeThreadLocal = ThreadLocal.withInitial(this::cookieStore);

    private CookieStore cookieStore(){
        return new CookieManager().getCookieStore();
    }

    @Override
    public void add(URI uri, HttpCookie cookie) {
        storeThreadLocal.get().add(uri, cookie);
    }

    @Override
    public List<HttpCookie> get(URI uri) {
        return storeThreadLocal.get().get(uri);
    }

    @Override
    public List<HttpCookie> getCookies() {
        return storeThreadLocal.get().getCookies();
    }

    @Override
    public List<URI> getURIs() {
        return  storeThreadLocal.get().getURIs();
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        return storeThreadLocal.get().remove(uri, cookie);
    }

    @Override
    public boolean removeAll() {
        return storeThreadLocal.get().removeAll();
    }

    public String cookieValue(String cookieName) {
        return getCookies().stream()
                .filter(c -> c.getName().equals(cookieName))
                .map(HttpCookie::getValue)
                .findFirst()
                .orElseThrow();
    }
}

package olmerk.rococo.jupiter.extension;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.Cookie;
import olmerk.rococo.api.core.ThreadSafeCookieStore;
import olmerk.rococo.config.Config;
import olmerk.rococo.jupiter.annotation.ApiLogin;
import olmerk.rococo.jupiter.annotation.Token;
import olmerk.rococo.model.UserJson;
import olmerk.rococo.page.common.MainPage;
import olmerk.rococo.service.impl.AuthApiClient;


public class ApiLoginExtension implements BeforeTestExecutionCallback, ParameterResolver {

    private static final Config CONFIG = Config.getInstance();
    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ApiLoginExtension.class);
    private final AuthApiClient authApiClient = new AuthApiClient();
    private final boolean setupBrowser;

    private ApiLoginExtension(boolean setupBrowser) {
        this.setupBrowser = setupBrowser;
    }

    public ApiLoginExtension() {
        this.setupBrowser = true;
    }

    public static ApiLoginExtension rest() {
        return new ApiLoginExtension(false);
    }

    @Override
    public void beforeTestExecution(ExtensionContext context){
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ApiLogin.class)
                .ifPresent(apiLogin -> {
                    final UserJson userToLogin;
                    final UserJson userFromUserExtension = UserExtension.createdUser();
                    if ("".equals(apiLogin.username()) || "".equals(apiLogin.password())) {
                        if (userFromUserExtension == null) {
                            throw new IllegalStateException("@User must be present in case that @ApiLogin is empty!");
                        }
                        userToLogin = userFromUserExtension;
                    } else {
                        UserJson fakeUser = new UserJson(
                                apiLogin.username(),
                                apiLogin.password());
                        if (userFromUserExtension != null) {
                            throw new IllegalStateException("@User must not be present in case that @ApiLogin contains username or password!");
                        }
                        UserExtension.setUser(fakeUser);
                        userToLogin = fakeUser;
                    }

                    final String token = authApiClient.login(
                            userToLogin.username(),
                            userToLogin.password()
                    );
                    setToken(token);
                    if (setupBrowser) {
                        Selenide.open(CONFIG.frontUrl());
                        Selenide.localStorage().setItem("id_token", getToken());
                        WebDriverRunner.getWebDriver().manage().addCookie(
                                getJsessionIdCookie()
                        );
                        Selenide.open(MainPage.URL, MainPage.class).checkOpen();
                    }
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(String.class)
                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), Token.class);
    }

    @Override
    public String resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return getToken();
    }

    public static void setToken(String token){
        TestMethodContextExtension.context().getStore(NAMESPACE).put("token", token);
    }

    public static String getToken(){
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("token", String.class);
    }

    public static void setCode(String code){
        TestMethodContextExtension.context().getStore(NAMESPACE).put("code", code);
    }

    public static String getTCode(){
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("code", String.class);
    }

    public static Cookie getJsessionIdCookie() {
        return new Cookie(
                "JSESSIONID",
                ThreadSafeCookieStore.INSTANCE.cookieValue("JSESSIONID")
        );
    }
}

package olmerk.rococo.page.common;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import olmerk.rococo.page.BasePage;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage> {

    public static final String URL = CONFIG.authUrl() + "login";

    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement registerButton = $("a[href='/register']");
    private final SelenideElement passwordEye = $(".form__password-button");


    @Nonnull
    public RegisterPage clickRegisterButton() {
        registerButton.click();
        return new RegisterPage();
    }

    @Step("Fill login page with credentials: username: '{0}', password: {1}")
    @Nonnull
    public LoginPage fillLoginPage(String login, String password) {
        setUsername(login);
        setPassword(password);
        return this;
    }

    @Step("Set username: '{0}'")
    @Nonnull
    public LoginPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    @Step("Set password: '{0}'")
    @Nonnull
    public LoginPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    @Step("Submit login")
    @Nonnull
    public <T extends BasePage<?>> T submit(T expectedPage) {
        submitButton.click();
        return expectedPage;
    }


    public LoginPage clickPasswordEye() {
        passwordEye.shouldBe(visible).click();
        return this;
    }

    public LoginPage checkPasswordVisible() {
        passwordInput.shouldBe(attribute("type", "text"));
        return this;
    }

    @Override
    public LoginPage checkOpen() {
        usernameInput.shouldBe(visible);
        passwordInput.shouldBe(visible);
        submitButton.shouldBe(visible);
        registerButton.shouldBe(visible);
        return this;
    }
}

package olmerk.rococo.page.common;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import olmerk.rococo.page.BasePage;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegisterPage extends BasePage<RegisterPage> {

    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement passwordSubmitInput = $("input[name='passwordSubmit']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement errorContainer = $(".form__error");

    @Nonnull
    public RegisterPage clickRegisterButton() {
        submitButton.click();
        return new RegisterPage();
    }

    @Step("Fill login page with credentials: username: '{0}', password: {1}")
    @Nonnull
    public RegisterPage fillRegisterPage(String login, String password) {
        setUsername(login);
        setPassword(password);
        setSubmitPassword(password);
        return this;
    }

    @Step("Set username: '{0}'")
    @Nonnull
    public RegisterPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    @Step("Set password: '{0}'")
    @Nonnull
    public RegisterPage setSubmitPassword(String password) {
        passwordSubmitInput.setValue(password);
        return this;
    }

    @Step("Set password: '{0}'")
    @Nonnull
    public RegisterPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    @Step("Submit login")
    @Nonnull
    public <T extends BasePage<?>> T submit(T expectedPage) {
        submitButton.click();
        return expectedPage;
    }

    @Nonnull
    public RegisterPage checkMessagePasswordSubmit() {
        String message = passwordSubmitInput.getAttribute("validationMessage");
        assertEquals("Заполните это поле.", message);
        return this;
    }

    @Nonnull
    public RegisterPage checkMessageUsername() {
        String message = usernameInput.getAttribute("validationMessage");
        assertEquals("Заполните это поле.", message);
        return this;
    }

    @Override
    public RegisterPage checkOpen() {
        usernameInput.shouldBe(visible);
        passwordInput.shouldBe(visible);
        passwordSubmitInput.shouldBe(visible);
        submitButton.shouldBe(visible);
        return this;
    }
}

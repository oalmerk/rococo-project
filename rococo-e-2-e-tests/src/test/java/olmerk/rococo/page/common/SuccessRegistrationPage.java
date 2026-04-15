package olmerk.rococo.page.common;

import com.codeborne.selenide.SelenideElement;
import olmerk.rococo.page.BasePage;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class SuccessRegistrationPage extends BasePage<SuccessRegistrationPage> {

    private final SelenideElement enterButton = $("a[href='/login']");
    private final SelenideElement subheader = $(".form__subheader");

    @Override
    public SuccessRegistrationPage checkOpen() {
        subheader.shouldBe(visible, exactText("Добро пожаловать в Rococo"));
        enterButton.shouldBe(visible);
        return this;
    }

    public LoginPage clickLoginButton() {
        enterButton.shouldBe(enabled).click();
        return new LoginPage();
    }
}

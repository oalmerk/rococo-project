package olmerk.rococo.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import olmerk.rococo.config.Config;
import olmerk.rococo.page.component.Header;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<?>> {

    protected static final Config CONFIG = Config.getInstance();
    private final SelenideElement errorContainer = $(".form__error");
    private final SelenideElement alert = $("");
    private final ElementsCollection formErrors = $$(".form__error");

    protected final Header header = new Header();

    @Nonnull
    public Header getHeader() {
        return header;
    }

    public abstract T checkOpen() ;


    @SuppressWarnings("unchecked")
    @Nonnull
    @Step("Check that alert message appears: {expectedText}")
    public T checkAlert(String text) {
        alert.shouldHave(text(text));
        return (T) this;
    }

    @Step("Check that form error message appears: {expectedText}")
    @SuppressWarnings("unchecked")
    @Nonnull
    public T checkFormErrorMessage(String... expectedText) {
        formErrors.should(CollectionCondition.textsInAnyOrder(expectedText));
        return (T) this;
    }

    @Step("Check error on page: {error}")
    @Nonnull
    public T checkError(String error) {
        errorContainer.shouldHave(exactText(error));
        return (T) this;
    }

}

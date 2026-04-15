package olmerk.rococo.page.common;

import com.codeborne.selenide.ElementsCollection;
import olmerk.rococo.page.BasePage;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage extends BasePage<MainPage> {

    private final static String BASE = "main p";
    public static final String URL = CONFIG.frontUrl();
    private final ElementsCollection selenideElements  = $$("main li");

    @Override
    public MainPage checkOpen() {
        $(BASE).shouldBe(allOf("Check Main Page", visible, exactText("Ваши любимые картины и художники всегда рядом")));
        selenideElements.shouldHave(size(3), exactTexts("Картины", "Художники", "Музеи"));
        return this;
    }


    public MainPage checkUserLogin() {
        $(BASE).shouldBe(allOf("Check Main Page", visible, exactText("Ваши любимые картины и художники всегда рядом")));
        return this;
    }
}

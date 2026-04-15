package olmerk.rococo.page.museum;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import olmerk.rococo.page.BasePage;
import olmerk.rococo.page.component.Search;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MuseumPage extends BasePage<MuseumPage> {

    private final SelenideElement addMuseumButton = $("button.variant-filled-primary");
    private final ElementsCollection museumElements  = $$("main li");
    protected final Search search = new Search();

    @Nonnull
    public Search getSearch() {
        return search;
    }

    @Override
    public MuseumPage checkOpen() {
        addMuseumButton.shouldBe(visible);
        return this;
    }

    public MuseumPage checkCountMuseums(String... museums) {
        museumElements.shouldHave(size(museums.length), exactTexts(museums));
        return this;
    }

    public MuseumCardPage clickMuseum(String museum) {
        museumElements.shouldBe(CollectionCondition.itemWithText(museum));
        museumElements.filterBy(Condition.text(museum))
                .stream()
                .findFirst()
                .orElseThrow()
                .click();
        return new MuseumCardPage();
    }

    public CreateUpdateMuseumForm clickAddMuseumButton() {
        addMuseumButton.shouldBe(enabled).click();
        return new CreateUpdateMuseumForm();
    }
}

package olmerk.rococo.page.painting;

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

public class PaintingPage extends BasePage<PaintingPage> {

    private final SelenideElement addPaintingButton = $("button.variant-filled-primary");
    private final ElementsCollection paintingElements  = $$("main li");
    protected final Search search = new Search();

    @Nonnull
    public Search getSearch() {
        return search;
    }

    @Override
    public PaintingPage checkOpen() {
        addPaintingButton.shouldBe(visible);
        return this;
    }

    public PaintingPage checkCountPaintings(String... paintings) {
        paintingElements.shouldHave(size(paintings.length), exactTexts(paintings));
        return this;
    }

    public PaintingCardPage clickPainting(String painting) {
        paintingElements.shouldBe(CollectionCondition.itemWithText(painting));
        paintingElements.filterBy(Condition.text(painting))
                .stream()
                .findFirst()
                .orElseThrow()
                .click();
        return new PaintingCardPage();
    }

    public CreateUpdatePaintingForm clickAddPaintingButton() {
        addPaintingButton.shouldBe(enabled).click();
        return new CreateUpdatePaintingForm();
    }
}

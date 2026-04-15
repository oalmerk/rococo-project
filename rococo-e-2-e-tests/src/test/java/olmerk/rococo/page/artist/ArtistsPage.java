package olmerk.rococo.page.artist;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import olmerk.rococo.page.BasePage;
import olmerk.rococo.page.component.Search;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ArtistsPage extends BasePage<ArtistsPage> {

    private final SelenideElement addArtistButton = $("button.variant-filled-primary");
    private final ElementsCollection artistsElements  = $$("main li");
    protected final Search search = new Search();

    @Nonnull
    public Search getSearch() {
        return search;
    }

    @Override
    public ArtistsPage checkOpen() {
        addArtistButton.shouldBe(visible);
        return this;
    }


    public ArtistCardPage clickArtists(String artist) {
        artistsElements.shouldBe(CollectionCondition.itemWithText(artist));
        artistsElements.filterBy(Condition.text(artist))
                .stream()
                .findFirst()
                .orElseThrow()
                .click();
        return new ArtistCardPage();
    }


    public CreateArtistModalForm clickAddArtistButton() {
        addArtistButton.shouldBe(enabled).click();
        return new CreateArtistModalForm();
    }
}

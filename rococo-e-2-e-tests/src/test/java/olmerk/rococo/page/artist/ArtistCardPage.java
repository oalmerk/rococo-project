package olmerk.rococo.page.artist;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import olmerk.rococo.model.ArtistJson;
import olmerk.rococo.page.BasePage;
import olmerk.rococo.page.painting.CreateUpdatePaintingForm;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ArtistCardPage extends BasePage<ArtistCardPage> {

    private final String BASE = "[id='page-content'] ";
    private final SelenideElement addPaintingButton = $(BASE + "button.variant-filled-primary");
    private final SelenideElement editButton = $(BASE + "button.variant-ghost");
    private final SelenideElement artistName = $(BASE + "header");
    private final SelenideElement artistBiography = $(BASE + "p");
    private final SelenideElement artistPhoto = $(BASE + "img");
    private final ElementsCollection paintingElements  = $$(BASE + "li");

    @Override
    public ArtistCardPage checkOpen() {
        $(BASE).shouldBe(visible);
        addPaintingButton.shouldBe(visible);
        editButton.shouldBe(visible);
        return this;
    }

    public ArtistCardPage checkArtist(ArtistJson artistJson) {
        artistName.shouldBe(visible, exactText(artistJson.name()));
        artistBiography.shouldBe(visible, exactText(artistJson.biography()));
        artistPhoto.shouldBe(visible, attribute("src", artistJson.photo()));
        return this;
    }

    public ArtistCardPage checkPhoto(String photo) {
        artistPhoto.shouldBe(visible, attribute("src", photo));
        return this;
    }


    public ArtistCardPage checkPainting(String... painting) {
        paintingElements.shouldHave(size(painting.length), exactTexts(painting));
        return this;
    }

    public EditArtistModalForm clickEdit() {
        editButton.click();
        return new EditArtistModalForm();
    }

    public CreateUpdatePaintingForm clickAddPainting() {
        addPaintingButton.click();
        return new CreateUpdatePaintingForm();
    }
}

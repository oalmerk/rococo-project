package olmerk.rococo.page.painting;

import com.codeborne.selenide.SelenideElement;
import olmerk.rococo.page.BasePage;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class PaintingCardPage extends BasePage<PaintingCardPage> {
    private final String BASE = "[id='page-content'] ";
    private final SelenideElement paintingTitle = $(BASE + "header");
    private final SelenideElement paintingDescription = $(BASE + "article div div.m-4");
    private final SelenideElement paintingPhoto = $(BASE + "img");
    private final SelenideElement updateButton = $(BASE + "[type='button']");


    @Override
    public PaintingCardPage checkOpen() {
        $(BASE).shouldBe(visible);
        return this;
    }

    public PaintingCardPage checkPainting(String title, String description,  String photo) {
        paintingTitle.shouldBe(visible, exactText(title));
        paintingDescription.shouldBe(visible, exactText(description));
        paintingPhoto.shouldBe(visible, attribute("src", photo));
        return this;
    }

    public CreateUpdatePaintingForm clickUpdateButton() {
        updateButton.click();
        return new CreateUpdatePaintingForm();
    }
}

package olmerk.rococo.page.museum;

import com.codeborne.selenide.SelenideElement;
import olmerk.rococo.page.BasePage;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public class MuseumCardPage extends BasePage<MuseumCardPage> {

    private final String BASE = "[id='page-content'] ";
    private final SelenideElement museumTitle = $(BASE + "header");
    private final SelenideElement museumDescription = $(BASE + "article  div div:last-child");
    private final SelenideElement museumCity = $(BASE + "article div.text-center");
    private final SelenideElement museumPhoto = $(BASE + "img");
    private final SelenideElement updateButton = $(BASE + "[type='button']");


    @Override
    public MuseumCardPage checkOpen() {
        $(BASE).shouldBe(visible);
        return this;
    }

    public MuseumCardPage checkMuseum(String title, String description, String city, String photo) {
        museumTitle.shouldBe(visible, exactText(title));
        museumDescription.shouldBe(visible, exactText(description));
        museumCity.shouldBe(visible, exactText(city));
        museumPhoto.shouldBe(visible, attribute("src", photo));
        return this;
    }

    public CreateUpdateMuseumForm clickUpdateButton() {
        updateButton.click();
        return new CreateUpdateMuseumForm();
    }
}

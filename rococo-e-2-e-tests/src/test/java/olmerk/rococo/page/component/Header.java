package olmerk.rococo.page.component;

import com.codeborne.selenide.SelenideElement;
import olmerk.rococo.page.BasePage;
import olmerk.rococo.page.artist.ArtistsPage;
import olmerk.rococo.page.common.LoginPage;
import olmerk.rococo.page.museum.MuseumPage;
import olmerk.rococo.page.painting.PaintingPage;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class Header extends BaseComponent<Header> {

    public Header() {
        super($("[data-testid='app-bar']"));
    }

    private final SelenideElement mainPageLink = self.$("h1 a[href*='/']");
    private final SelenideElement paintingLink= self.$("a[href*='/painting']");
    private final SelenideElement artistLink= self.$("a[href*='/artist']");
    private final SelenideElement museumLink= self.$("a[href*='/museum']");
    private final SelenideElement loginButton = self.$("button.variant-filled-primary");
    private final SelenideElement loginAvatar = self.$("button.variant-filled-surface");

    public LoginPage clickLoginButton(){
        loginButton.shouldBe(enabled).click();
        return new LoginPage();
    }

    public <T extends BasePage<?>> T checkUserLoginAvatar(T expectedPage){
        loginAvatar.shouldBe(visible);
        return expectedPage;
    }

    public ArtistsPage clickArtistButton(){
        artistLink.shouldBe(enabled).click();
        return new ArtistsPage();
    }

    public MuseumPage clickMuseumButton(){
        museumLink.shouldBe(enabled).click();
        return new MuseumPage();
    }

    public PaintingPage clickPaintingButton(){
        paintingLink.shouldBe(enabled).click();
        return new PaintingPage();
    }
}

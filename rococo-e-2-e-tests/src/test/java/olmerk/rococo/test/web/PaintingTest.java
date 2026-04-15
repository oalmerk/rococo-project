package olmerk.rococo.test.web;

import org.junit.jupiter.api.Test;
import olmerk.rococo.jupiter.annotation.ApiLogin;
import olmerk.rococo.jupiter.annotation.Painting;
import olmerk.rococo.jupiter.annotation.User;
import olmerk.rococo.jupiter.annotation.meta.WebTest;
import olmerk.rococo.model.PaintingJson;
import olmerk.rococo.page.common.MainPage;
import olmerk.rococo.page.painting.PaintingCardPage;
import olmerk.rococo.utils.PhotoUtils;

@WebTest
public class PaintingTest {

    @Test
    @User
    @ApiLogin
    @Painting(content = "images/сomposition8.jpg", createNewArtist = true, createNewMuseum = true)
    void successUpdatePaintingTest(PaintingJson paintingJson) {
        new MainPage()
                .getHeader()
                .clickPaintingButton()
                .checkOpen()
                .clickPainting(paintingJson.title())
                .checkPainting(paintingJson.title(), paintingJson.description(), paintingJson.content())
                .clickUpdateButton()
                .setPhoto("сomposition8_2.jpg")
                .clickSubmitButton(new PaintingCardPage())
                .checkPainting(paintingJson.title(), paintingJson.description(), PhotoUtils.fromFile("images/сomposition8_2.jpg"));
    }
}

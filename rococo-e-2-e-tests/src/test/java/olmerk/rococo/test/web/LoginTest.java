package olmerk.rococo.test.web;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;
import olmerk.rococo.jupiter.annotation.User;
import olmerk.rococo.jupiter.annotation.meta.WebTest;
import olmerk.rococo.model.UserJson;
import olmerk.rococo.page.common.LoginPage;
import olmerk.rococo.page.common.MainPage;
import olmerk.rococo.utils.RandomDataUtils;

@WebTest
public class LoginTest {

    @Test
    @User
    void successLoginTest(UserJson user) {
        Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .clickLoginButton()
                .fillLoginPage(user.username(), user.password())
                .submit(new MainPage())
                .getHeader()
                .checkUserLoginAvatar(new MainPage())
                .checkUserLogin();
    }

    @Test
    @User
    void failedLoginWithWrongUserNameTest(UserJson user) {
        Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .clickLoginButton()
                .fillLoginPage(RandomDataUtils.randomName(), user.password())
                .submit(new LoginPage())
                .checkOpen()
                .checkError("Неверные учетные данные пользователя");
    }

    @Test
    @User
    void failedLoginWithWrongPasswordTest(UserJson user) {
        Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .clickLoginButton()
                .fillLoginPage(user.username(), RandomDataUtils.randomName())
                .submit(new LoginPage())
                .checkOpen()
                .checkError("Неверные учетные данные пользователя");
    }

    @Test
    void checkEyePasswordTest() {
        String password =  RandomDataUtils.randomName();
        Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .clickLoginButton()
                .setPassword(password)
                .clickPasswordEye()
                .checkPasswordVisible();
    }
}

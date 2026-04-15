package olmerk.rococo.test.web;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;
import olmerk.rococo.jupiter.annotation.User;
import olmerk.rococo.jupiter.annotation.meta.WebTest;
import olmerk.rococo.model.UserJson;
import olmerk.rococo.page.common.MainPage;
import olmerk.rococo.page.common.RegisterPage;
import olmerk.rococo.page.common.SuccessRegistrationPage;

import static olmerk.rococo.jupiter.extension.UserExtension.DEFAULT_PASSWORD;
import static olmerk.rococo.utils.RandomDataUtils.randomUsername;

@WebTest
public class RegistrationTest {

    @Test
    void successRegistrationTest() {
        String newUsername = randomUsername();
        Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .clickLoginButton()
                .clickRegisterButton()
                .fillRegisterPage(newUsername, DEFAULT_PASSWORD)
                .submit(new SuccessRegistrationPage())
                .checkOpen()
                .clickLoginButton()
                .fillLoginPage(newUsername, DEFAULT_PASSWORD)
                .submit(new MainPage())
                .getHeader()
                .checkUserLoginAvatar(new MainPage());
    }

    @Test
    void failedRegistrationWithTooShortUserNameTest() {
        String newUsername = "UN";
        Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .clickLoginButton()
                .clickRegisterButton()
                .fillRegisterPage(newUsername, DEFAULT_PASSWORD)
                .submit(new RegisterPage())
                .checkError("Allowed username length should be from 3 to 50 characters");
    }

    @Test
    void failedRegistrationWithTooShortPasswordTest() {
        String newUsername = randomUsername();
        String password = "UN";
        Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .clickLoginButton()
                .clickRegisterButton()
                .fillRegisterPage(newUsername, password)
                .submit(new RegisterPage())
                .checkError("Allowed password length should be from 3 to 12 characters");
    }

    @Test
    void failedRegistrationWithTooLongPasswordTest() {
        String newUsername = randomUsername();
        String password = "1234567891011";
        Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .clickLoginButton()
                .clickRegisterButton()
                .fillRegisterPage(newUsername, password)
                .submit(new RegisterPage())
                .checkError("Allowed password length should be from 3 to 12 characters");
    }

    @Test
    void failedRegistrationWithDifferentPasswordTest() {
        String newUsername = randomUsername();
        Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .clickLoginButton()
                .clickRegisterButton()
                .setUsername(newUsername)
                .setPassword(DEFAULT_PASSWORD)
                .setSubmitPassword("54321")
                .submit(new RegisterPage())
                .checkError("Passwords should be equal");
    }

    @Test
    void failedRegistrationWithEmptyPasswordTest() {
        String newUsername = randomUsername();
        Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .clickLoginButton()
                .clickRegisterButton()
                .setUsername(newUsername)
                .setPassword(DEFAULT_PASSWORD)
                .setSubmitPassword("")
                .submit(new RegisterPage())
                .checkMessagePasswordSubmit();
    }

    @Test
    void failedRegistrationWithEmptyUserNameTest() {
        Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .clickLoginButton()
                .clickRegisterButton()
                .setUsername("")
                .setPassword(DEFAULT_PASSWORD)
                .setSubmitPassword(DEFAULT_PASSWORD)
                .submit(new RegisterPage())
                .checkMessageUsername();
    }

    @Test
    @User
    void failedRegistrationExistingUserNameTest(UserJson userJson) {
        Selenide.open(MainPage.URL, MainPage.class)
                .getHeader()
                .clickLoginButton()
                .clickRegisterButton()
                .fillRegisterPage(userJson.username(), DEFAULT_PASSWORD)
                .submit(new RegisterPage())
                .checkError("Username `" + userJson.username() + "` already exists");
    }
}

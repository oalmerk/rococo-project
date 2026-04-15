package olmerk.rococo.test.rest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import olmerk.rococo.jupiter.annotation.ApiLogin;
import olmerk.rococo.jupiter.annotation.Token;
import olmerk.rococo.jupiter.annotation.User;
import olmerk.rococo.jupiter.annotation.meta.RestTest;
import olmerk.rococo.jupiter.extension.ApiLoginExtension;
import olmerk.rococo.model.UserJson;
import olmerk.rococo.service.impl.GateApiClient;
import olmerk.rococo.utils.PhotoUtils;

import static io.qameta.allure.Allure.step;
import static olmerk.rococo.utils.RandomDataUtils.randomName;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RestTest
public class UserRestTest {

    @RegisterExtension
    private static final ApiLoginExtension apiLogin = ApiLoginExtension.rest();
    private final GateApiClient gateApiClient = new GateApiClient();

    @Test
    @ApiLogin
    @User
    void updateCurrentUserByApiTest(UserJson userJson, @Token String bearerToken){
        String newFirstName = randomName();
        String newLastName = randomName();
        String newPhoto = PhotoUtils.fromFile("images/pepin.jpg");

        gateApiClient.updateUser("Bearer " + bearerToken , new UserJson(userJson.id(), userJson.username(),null, newFirstName, newLastName, newPhoto));
        UserJson jsonUpdated = gateApiClient.getUser("Bearer " + bearerToken);
        step("Check user updated", () ->
                Assertions.assertAll(
                        () -> assertEquals(jsonUpdated.firstname(), newFirstName),
                        () -> assertEquals(jsonUpdated.lastname(), newLastName),
                        () -> assertEquals(jsonUpdated.avatar(), newPhoto)
                )
        );
    }
}

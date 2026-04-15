package olmerk.rococo.jupiter.annotation.meta;

import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.extension.ExtendWith;
import olmerk.rococo.jupiter.extension.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({
        AllureJunit5.class,
        BrowserExtension.class,
        UserExtension.class,
        ApiLoginExtension.class,
        ArtistExtension.class,
        MuseumExtension.class,
        PaintingExtension.class
})
public @interface WebTest {
}

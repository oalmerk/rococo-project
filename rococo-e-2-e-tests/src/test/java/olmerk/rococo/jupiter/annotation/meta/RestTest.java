package olmerk.rococo.jupiter.annotation.meta;


import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.extension.ExtendWith;
import olmerk.rococo.jupiter.extension.ArtistCountExtension;
import olmerk.rococo.jupiter.extension.MuseumCountExtension;
import olmerk.rococo.jupiter.extension.PaintingsCountExtension;
import olmerk.rococo.jupiter.extension.UserExtension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith({
    AllureJunit5.class,
        UserExtension.class,
        ArtistCountExtension.class,
        MuseumCountExtension.class,
        PaintingsCountExtension.class
})
public @interface RestTest {
}

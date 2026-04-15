package olmerk.rococo.jupiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Museum {
    String title() default "";
    String description() default "";
    String city() default "";
    String photo() default "";
    String countryName() default "Россия";
    boolean deleteAfterTest() default true;
}

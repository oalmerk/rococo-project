package olmerk.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GeoJson(@JsonProperty("country")
                      CountryJson country,
                      @JsonProperty("city")
                      String city) {
}

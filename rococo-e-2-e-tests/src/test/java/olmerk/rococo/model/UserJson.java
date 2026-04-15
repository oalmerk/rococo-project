package olmerk.rococo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import olmerk.rococo.data.entity.userdata.UserEntity;

import java.util.Arrays;
import java.util.UUID;


public record UserJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("username")
        String username,
        @JsonProperty("password")
        @JsonIgnore
        String password,
        @JsonProperty("firstname")
        String firstname,
        @JsonProperty("lastname")
        String lastname,
        @JsonProperty("avatar")
        String avatar
) {
        public UserJson( String username, String password) {
                this(null, username, password, "", "", "");
        }

        public UserJson(UserEntity saved) {
            this(saved.getId(), saved.getUsername(), null, saved.getFirstname(), saved.getLastname(), Arrays.toString(saved.getAvatar()));
    }

        public UserJson(UserEntity userEntity, String password) {
                this(userEntity.getId(), userEntity.getUsername(), password, userEntity.getFirstname(), userEntity.getLastname(), Arrays.toString(userEntity.getAvatar()));
        }
}

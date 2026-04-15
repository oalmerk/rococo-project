package olmerk.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import olmerk.rococo.controller.ArtistController;
import olmerk.rococo.model.ArtistJson;
import olmerk.rococo.service.api.GrpcArtistClient;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArtistController.class)
@ContextConfiguration(classes = olmerk.rococo.RococoGatewayService.class)
@AutoConfigureMockMvc(addFilters = false)
class ArtistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GrpcArtistClient artistClient;

    @Autowired
    private ObjectMapper objectMapper;
    private ArtistJson artist;
    private UUID artistId;

    @BeforeEach
    void setUp() {
        artistId = UUID.randomUUID();

        artist = new ArtistJson(
                artistId,
                "Василий Кандинский",
                "Русский художник-абстракционист",
                "data:image/jpeg;base64,/9j/"
        );
    }

    @Test
    @DisplayName("GET /api/artist возвращает HTTP-ответ со статусом 200 OK и cписок артистов")
    void getAllArtistsSuccess() throws Exception {
        Mockito.when(artistClient.getAllArtist(ArgumentMatchers.eq("Василий"), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(List.of(artist));

        mockMvc.perform(get("/api/artist")
                        .param("name", "Василий")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(artistId.toString()))
                .andExpect(jsonPath("$.content[0].name").value("Василий Кандинский"))
                .andExpect(jsonPath("$.content[0].biography")
                        .value("Русский художник-абстракционист"))
                .andExpect(jsonPath("$.content[0].photo")
                        .value("data:image/jpeg;base64,/9j/"));
    }

    @Test
    @DisplayName("GET /api/artist/{id} возвращает HTTP-ответ со статусом 200 OK и одного артиста по id")
    void getArtistByIdSuccess() throws Exception {
        Mockito.when(artistClient.getArtistById(artistId.toString()))
                .thenReturn(artist);

        mockMvc.perform(get("/api/artist/{id}", artistId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(artistId.toString()))
                .andExpect(jsonPath("$.name").value("Василий Кандинский"))
                .andExpect(jsonPath("$.biography").value("Русский художник-абстракционист"))
                .andExpect(jsonPath("$.photo").value("data:image/jpeg;base64,/9j/"));
    }

    @Test
    @DisplayName("POST /api/artist возвращает HTTP-ответ со статусом 200 OK и созданного художника")
    void createArtistSuccess() throws Exception {
        Mockito.when(artistClient.createArtist(ArgumentMatchers.any()))
                .thenReturn(artist);

        mockMvc.perform(post("/api/artist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(artist)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(artistId.toString()))
                .andExpect(jsonPath("$.name").value("Василий Кандинский"))
                .andExpect(jsonPath("$.biography").value("Русский художник-абстракционист"))
                .andExpect(jsonPath("$.photo").value("data:image/jpeg;base64,/9j/"));
    }

    @Test
    @DisplayName("PATH /api/artist возвращает HTTP-ответ со статусом 200 OK и отредактированного художника")
    void updateArtistSuccess() throws Exception {
        Mockito.when(artistClient.updateArtist(ArgumentMatchers.any()))
                .thenReturn(artist);

        mockMvc.perform(patch("/api/artist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(artist)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(artistId.toString()))
                .andExpect(jsonPath("$.name").value("Василий Кандинский"))
                .andExpect(jsonPath("$.biography").value("Русский художник-абстракционист"))
                .andExpect(jsonPath("$.photo").value("data:image/jpeg;base64,/9j/"));
    }


    @Test
    @DisplayName("POST /api/artist — 400 Bad Request при пустом name")
    void createArtistValidationError() throws Exception {
        ArtistJson invalidArtist = new ArtistJson(
                null,
                "",
                "Биография",
                "data:image/jpeg;base64,/9j/"
        );

        mockMvc.perform(post("/api/artist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidArtist)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @DisplayName("POST /api/artist возвращает 400 при невалидном фото")
    void createArtistInvalidPhoto() throws Exception {
        ArtistJson invalidArtist = new ArtistJson(
                artistId,
                "Василий Кандинский",
                "Русский художник-абстракционист",
                "invalid-photo-string"
        );

        mockMvc.perform(post("/api/artist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidArtist)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /api/artist возвращает 400 при невалидном фото")
    void updateArtistInvalidPhoto() throws Exception {
        ArtistJson invalidArtist = new ArtistJson(
                artistId,
                "Василий Кандинский",
                "Русский художник-абстракционист",
                "data:image/xyz,notbase64"
        );

        mockMvc.perform(patch("/api/artist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidArtist)))
                .andExpect(status().isBadRequest());
    }
}




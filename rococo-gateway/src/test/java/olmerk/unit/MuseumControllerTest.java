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
import olmerk.rococo.RococoGatewayService;
import olmerk.rococo.controller.MuseumController;
import olmerk.rococo.model.CountryJson;
import olmerk.rococo.model.GeoJson;
import olmerk.rococo.model.MuseumJson;
import olmerk.rococo.service.api.GrpcMuseumClient;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MuseumController.class)
@ContextConfiguration(classes = RococoGatewayService.class)
@AutoConfigureMockMvc(addFilters = false)
class MuseumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GrpcMuseumClient grpcMuseumClient;

    @Autowired
    private ObjectMapper objectMapper;

    private MuseumJson museum;
    private UUID museumId;
    private UUID countryId;

    @BeforeEach
    void setUp() {
        museumId = UUID.randomUUID();
        countryId = UUID.randomUUID();
        museum = new MuseumJson(
                museumId,
                "Эрмитаж",
                "Один из крупнейших музеев мира, расположен в Санкт-Петербурге",
                "data:image/jpeg;base64,/9j/",
                new GeoJson(new CountryJson(countryId, "Россия"), "Санкт-Петербург")

        );
    }

    @Test
    @DisplayName("GET /api/museum возвращает список музеев")
    void getAllMuseumsSuccess() throws Exception {
        Mockito.when(grpcMuseumClient.getAllMuseums(ArgumentMatchers.eq("Эрмитаж"), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(List.of(museum));

        mockMvc.perform(get("/api/museum")
                        .param("title", "Эрмитаж")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(museumId.toString()))
                .andExpect(jsonPath("$.content[0].title").value("Эрмитаж"))
                .andExpect(jsonPath("$.content[0].description")
                        .value("Один из крупнейших музеев мира, расположен в Санкт-Петербурге"))
                .andExpect(jsonPath("$.content[0].photo")
                        .value("data:image/jpeg;base64,/9j/"))
                .andExpect(jsonPath("$.content[0].geo.country.id").value(countryId.toString()))
                .andExpect(jsonPath("$.content[0].geo.country.name").value("Россия"))
                .andExpect(jsonPath("$.content[0].geo.city").value("Санкт-Петербург"));;
    }

    @Test
    @DisplayName("GET /api/museum/{id} возвращает музей с geo по id")
    void getMuseumByIdSuccess() throws Exception {
        Mockito.when(grpcMuseumClient.getMuseumById(museumId.toString()))
                .thenReturn(museum);

        mockMvc.perform(get("/api/museum/{id}", museumId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(museumId.toString()))
                .andExpect(jsonPath("$.title").value("Эрмитаж"))
                .andExpect(jsonPath("$.description")
                        .value("Один из крупнейших музеев мира, расположен в Санкт-Петербурге"))
                .andExpect(jsonPath("$.photo").value("data:image/jpeg;base64,/9j/"))
                .andExpect(jsonPath("$.geo.country.id").value(countryId.toString()))
                .andExpect(jsonPath("$.geo.country.name").value("Россия"))
                .andExpect(jsonPath("$.geo.city").value("Санкт-Петербург"));
    }

    @Test
    @DisplayName("POST /api/museum создаёт музей с geo")
    void createMuseumSuccess() throws Exception {
        Mockito.when(grpcMuseumClient.createMuseum(ArgumentMatchers.any(MuseumJson.class)))
                .thenReturn(museum);

        mockMvc.perform(post("/api/museum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(museum)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(museumId.toString()))
                .andExpect(jsonPath("$.title").value("Эрмитаж"))
                .andExpect(jsonPath("$.description")
                        .value("Один из крупнейших музеев мира, расположен в Санкт-Петербурге"))
                .andExpect(jsonPath("$.photo").value("data:image/jpeg;base64,/9j/"))
                .andExpect(jsonPath("$.geo.country.id").value(countryId.toString()))
                .andExpect(jsonPath("$.geo.country.name").value("Россия"))
                .andExpect(jsonPath("$.geo.city").value("Санкт-Петербург"));
    }

    @Test
    @DisplayName("PATCH /api/museum обновляет музей с geo")
    void updateMuseumSuccess() throws Exception {
        Mockito.when(grpcMuseumClient.updateMuseum(ArgumentMatchers.any(MuseumJson.class)))
                .thenReturn(museum);

        mockMvc.perform(patch("/api/museum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(museum)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(museumId.toString()))
                .andExpect(jsonPath("$.title").value("Эрмитаж"))
                .andExpect(jsonPath("$.description")
                        .value("Один из крупнейших музеев мира, расположен в Санкт-Петербурге"))
                .andExpect(jsonPath("$.photo").value("data:image/jpeg;base64,/9j/"))
                .andExpect(jsonPath("$.geo.country.id").value(countryId.toString()))
                .andExpect(jsonPath("$.geo.country.name").value("Россия"))
                .andExpect(jsonPath("$.geo.city").value("Санкт-Петербург"));
    }

    @Test
    @DisplayName("POST /api/museum с photo > 1Mb возвращает ошибку валидации")
    void createMuseumPhotoTooLarge() throws Exception {
        int size = 1024 * 1024 + 1;
        StringBuilder sb = new StringBuilder(size);
        sb.append("data:image/png;base64,");
        for (int i = 0; i < size - 22; i++) {
            sb.append('A');
        }
        String tooLargePhoto = sb.toString();

        MuseumJson invalidMuseum = new MuseumJson(
                museumId,
                "Эрмитаж",
                "Описание музея должно быть валидным и достаточно длинным",
                tooLargePhoto,
                null
        );

        mockMvc.perform(post("/api/museum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMuseum)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("Photo is should be 1Mb"));
    }

    @Test
    @DisplayName("POST /api/museum с пустым title возвращает ошибку валидации")
    void createMuseumEmptyTitle() throws Exception {
        MuseumJson invalidMuseum = new MuseumJson(
                museumId,
                "",
                "Описание музея должно быть валидным и достаточно длинным",
                museum.photo(),
                null
        );

        mockMvc.perform(post("/api/museum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMuseum)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/museum с коротким description возвращает ошибку валидации")
    void createMuseumShortDescription() throws Exception {
        MuseumJson invalidMuseum = new MuseumJson(
                museumId,
                "Эрмитаж",
                "Коротко",
                museum.photo(),
                null
        );

        mockMvc.perform(post("/api/museum")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidMuseum)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("Allowed description length should be from 10 to 1000 characters"));
    }
}

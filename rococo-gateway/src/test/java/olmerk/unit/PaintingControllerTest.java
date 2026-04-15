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
import olmerk.rococo.controller.PaintingController;
import olmerk.rococo.model.*;
import olmerk.rococo.service.api.GrpcPaintingClient;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaintingController.class)
@ContextConfiguration(classes = RococoGatewayService.class)
@AutoConfigureMockMvc(addFilters = false)
class PaintingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GrpcPaintingClient grpcPaintingClient;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID paintingId;
    private UUID artistId;
    private UUID museumId;
    private PaintingJson painting;

    @BeforeEach
    void setUp() {
        paintingId = UUID.randomUUID();
        artistId = UUID.randomUUID();
        museumId = UUID.randomUUID();

        ArtistJson artist = new ArtistJson(
                artistId,
                "Василий Кандинский",
                "Русский художник-абстракционист",
                "data:image/png;base64,image"
        );

        MuseumJson museum = new MuseumJson(
                museumId,
                "Эрмитаж",
                "Один из крупнейших музеев мира",
                "data:image/png;base64,image",
                null
        );

        painting = new PaintingJson(
                paintingId,
                "Композиция VII",
                "Знаменитая работа Кандинского, одна из ключевых в абстракционизме",
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA",
                artist,
                museum
        );
    }

    @Test
    @DisplayName("GET /api/painting возвращает список картин")
    void getAllPaintingsSuccess() throws Exception {
        Mockito.when(grpcPaintingClient.getAllPainting(
                        ArgumentMatchers.eq("Композиция"),
                        ArgumentMatchers.any(Pageable.class)))
                .thenReturn(List.of(painting));

        mockMvc.perform(get("/api/painting")
                        .param("title", "Композиция")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(paintingId.toString()))
                .andExpect(jsonPath("$.content[0].title").value("Композиция VII"))
                .andExpect(jsonPath("$.content[0].description")
                        .value("Знаменитая работа Кандинского, одна из ключевых в абстракционизме"))
                .andExpect(jsonPath("$.content[0].content").exists())
                .andExpect(jsonPath("$.content[0].artist.id").value(artistId.toString()))
                .andExpect(jsonPath("$.content[0].artist.name").value("Василий Кандинский"))
                .andExpect(jsonPath("$.content[0].museum.id").value(museumId.toString()))
                .andExpect(jsonPath("$.content[0].museum.title").value("Эрмитаж"));
    }

    @Test
    @DisplayName("GET /api/painting/{id} возвращает картину по id")
    void getPaintingByIdSuccess() throws Exception {
        Mockito.when(grpcPaintingClient.getPaintingById(paintingId.toString()))
                .thenReturn(painting);

        mockMvc.perform(get("/api/painting/{id}", paintingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(paintingId.toString()))
                .andExpect(jsonPath("$.title").value("Композиция VII"))
                .andExpect(jsonPath("$.description")
                        .value("Знаменитая работа Кандинского, одна из ключевых в абстракционизме"))
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.artist.id").value(artistId.toString()))
                .andExpect(jsonPath("$.museum.id").value(museumId.toString()));
    }

    @Test
    @DisplayName("GET /api/painting/author/{id} возвращает картины автора")
    void getPaintingByAuthorSuccess() throws Exception {
        Mockito.when(grpcPaintingClient.getPaintingByAuthor(
                        ArgumentMatchers.eq(artistId.toString()),
                        ArgumentMatchers.any(Pageable.class)))
                .thenReturn(List.of(painting));

        mockMvc.perform(get("/api/painting/author/{id}", artistId)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(paintingId.toString()))
                .andExpect(jsonPath("$.content[0].artist.id").value(artistId.toString()))
                .andExpect(jsonPath("$.content[0].title").value("Композиция VII"));
    }

    @Test
    @DisplayName("POST /api/painting создаёт картину")
    void createPaintingSuccess() throws Exception {
        Mockito.when(grpcPaintingClient.createPainting(ArgumentMatchers.any(PaintingJson.class)))
                .thenReturn(painting);

        mockMvc.perform(post("/api/painting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(painting)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(paintingId.toString()))
                .andExpect(jsonPath("$.title").value("Композиция VII"))
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    @DisplayName("PATCH /api/painting обновляет картину")
    void updatePaintingSuccess() throws Exception {
        Mockito.when(grpcPaintingClient.updatePainting(ArgumentMatchers.any(PaintingJson.class)))
                .thenReturn(painting);

        mockMvc.perform(patch("/api/painting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(painting)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(paintingId.toString()))
                .andExpect(jsonPath("$.title").value("Композиция VII"));
    }

    @Test
    @DisplayName("POST /api/painting возвращает 400 если title пустой")
    void createPaintingValidationErrorEmptyTitle() throws Exception {
        PaintingJson invalidPainting = new PaintingJson(
                null,
                "",
                "Описание картины длиной больше 10 символов",
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA",
                painting.artist(),
                painting.museum()
        );

        mockMvc.perform(post("/api/painting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPainting)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @DisplayName("POST /api/painting возвращает 400 если description слишком короткий")
    void createPaintingValidationErrorShortDescription() throws Exception {
        PaintingJson invalidPainting = new PaintingJson(
                null,
                "Композиция VII",
                "Коротко",
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA",
                painting.artist(),
                painting.museum()
        );

        mockMvc.perform(post("/api/painting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPainting)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }
}


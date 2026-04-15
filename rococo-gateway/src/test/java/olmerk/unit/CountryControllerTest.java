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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import olmerk.rococo.RococoGatewayService;
import olmerk.rococo.controller.CountryController;
import olmerk.rococo.model.CountryJson;
import olmerk.rococo.service.api.GrpcCountryClient;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CountryController.class)
@ContextConfiguration(classes = RococoGatewayService.class)
@AutoConfigureMockMvc(addFilters = false)
class CountryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GrpcCountryClient countryClient;

    @Autowired
    private ObjectMapper objectMapper;

    private CountryJson country;
    private UUID countryId;

    @BeforeEach
    void setUp() {
        countryId = UUID.randomUUID();
        country = new CountryJson(countryId, "Россия");
    }

    @Test
    @DisplayName("GET /api/country возвращает HTTP-ответ со статусом 200 OK и список стран")
    void getAllCountriesSuccess() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(countryClient.getAllCountries(ArgumentMatchers.any(Pageable.class)))
                .thenReturn(List.of(country));

        mockMvc.perform(get("/api/country")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(countryId.toString()))
                .andExpect(jsonPath("$.content[0].name").value("Россия"));
    }
}


package olmerk.unit;

import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import olmerk.data.CountryEntity;
import olmerk.data.repository.CountryRepository;
import olmerk.grpc.rococo.CountriesResponse;
import olmerk.grpc.rococo.CountryRequest;
import olmerk.service.GrpcCountryService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrpcCountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private StreamObserver<CountriesResponse> countriesResponseObserver;

    @InjectMocks
    private GrpcCountryService grpcCountryService;

    private CountryEntity country;

    @BeforeEach
    void setUp() {
        country = new CountryEntity();
        country.setId(UUID.randomUUID());
        country.setName("Россия");
    }

    @Test
    void getAllCountriesSuccess() {
        when(countryRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(country)));

        CountryRequest request = CountryRequest.newBuilder()
                .setPage(0)
                .setSize(10)
                .build();


        grpcCountryService.getAllCountries(request, countriesResponseObserver);


        ArgumentCaptor<CountriesResponse> captor =
                ArgumentCaptor.forClass(CountriesResponse.class);

        verify(countriesResponseObserver).onNext(captor.capture());
        verify(countriesResponseObserver).onCompleted();
        verify(countriesResponseObserver, never()).onError(any());

        CountriesResponse response = captor.getValue();
        assertEquals(1, response.getCountryCount());
        assertEquals(country.getId().toString(), response.getCountry(0).getId());
        assertEquals("Россия", response.getCountry(0).getName());
    }

    @Test
    void getAllCountriesEmptyResult() {
        when(countryRepository.findAll(PageRequest.of(1, 5)))
                .thenReturn(new PageImpl<>(List.of()));

        CountryRequest request = CountryRequest.newBuilder()
                .setPage(1)
                .setSize(5)
                .build();

        grpcCountryService.getAllCountries(request, countriesResponseObserver);

        ArgumentCaptor<CountriesResponse> captor =
                ArgumentCaptor.forClass(CountriesResponse.class);

        verify(countriesResponseObserver).onNext(captor.capture());
        verify(countriesResponseObserver).onCompleted();
        verify(countriesResponseObserver, never()).onError(any());

        CountriesResponse response = captor.getValue();
        assertEquals(0, response.getCountryCount());
    }
}


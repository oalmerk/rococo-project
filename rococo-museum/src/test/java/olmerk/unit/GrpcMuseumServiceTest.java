package olmerk.unit;

import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import olmerk.data.CountryEntity;
import olmerk.data.MuseumEntity;
import olmerk.data.repository.CountryRepository;
import olmerk.data.repository.MuseumRepository;
import olmerk.grpc.rococo.*;
import olmerk.service.GrpcMuseumService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrpcMuseumServiceTest {

    @Mock
    private MuseumRepository museumRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private StreamObserver<MuseumsResponse> museumsResponseObserver;

    @Mock
    private StreamObserver<MuseumResponse> museumResponseObserver;

    @InjectMocks
    private GrpcMuseumService grpcMuseumService;

    private MuseumEntity museum;
    private CountryEntity country;

    @BeforeEach
    void setUp() {
        country = new CountryEntity();
        country.setId(UUID.randomUUID());
        country.setName("Франция");

        museum = new MuseumEntity();
        museum.setId(UUID.randomUUID());
        museum.setTitle("Лувр");
        museum.setDescription("Музей искусства");
        museum.setCity("Париж");
        museum.setCountry(country);
        museum.setPhoto("data:image/png;base64,image"
                .getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void getAllMuseumsSuccess() {
        when(museumRepository.findAllByTitleContainingIgnoreCase(
                eq("Лувр"),
                eq(PageRequest.of(0, 10))
        )).thenReturn(new PageImpl<>(List.of(museum)));

        MuseumRequest request = MuseumRequest.newBuilder()
                .setName("Лувр")
                .setPage(0)
                .setSize(10)
                .build();

        grpcMuseumService.getAllMuseums(request, museumsResponseObserver);

        ArgumentCaptor<MuseumsResponse> captor =
                ArgumentCaptor.forClass(MuseumsResponse.class);

        verify(museumsResponseObserver).onNext(captor.capture());
        verify(museumsResponseObserver).onCompleted();
        verify(museumsResponseObserver, never()).onError(any());

        MuseumsResponse response = captor.getValue();
        assertEquals(1, response.getMuseumCount());
        assertEquals("Лувр", response.getMuseum(0).getTitle());
        assertEquals("Париж", response.getMuseum(0).getGeo().getCity());
    }

    @Test
    void getMuseumByIdSuccess() {
        when(museumRepository.findById(museum.getId()))
                .thenReturn(Optional.of(museum));

        GetMuseumRequest request = GetMuseumRequest.newBuilder()
                .setId(museum.getId().toString())
                .build();

        grpcMuseumService.getMuseumById(request, museumResponseObserver);

        ArgumentCaptor<MuseumResponse> captor =
                ArgumentCaptor.forClass(MuseumResponse.class);

        verify(museumResponseObserver).onNext(captor.capture());
        verify(museumResponseObserver).onCompleted();
        verify(museumResponseObserver, never()).onError(any());

        Museum grpcMuseum = captor.getValue().getMuseum();
        assertEquals("Лувр", grpcMuseum.getTitle());
        assertEquals("Франция", grpcMuseum.getGeo().getCountry().getName());
    }

    @Test
    void getMuseumByIdNotFound() {
        when(museumRepository.findById(any()))
                .thenReturn(Optional.empty());

        GetMuseumRequest request = GetMuseumRequest.newBuilder()
                .setId(UUID.randomUUID().toString())
                .build();

        grpcMuseumService.getMuseumById(request, museumResponseObserver);

        verify(museumResponseObserver).onError(any());
        verify(museumResponseObserver, never()).onNext(any());
        verify(museumResponseObserver, never()).onCompleted();
    }

    @Test
    void createMuseumSuccess() {
        when(countryRepository.findById(country.getId()))
                .thenReturn(Optional.of(country));
        when(museumRepository.saveAndFlush(any(MuseumEntity.class)))
                .thenReturn(museum);

        CreateMuseumRequest request = CreateMuseumRequest.newBuilder()
                .setTitle("Лувр")
                .setDescription("Музей искусства")
                .setGeo(
                        Geo.newBuilder()
                                .setCity("Париж")
                                .setCountry(
                                        Country.newBuilder()
                                                .setId(country.getId().toString())
                                                .build()
                                )
                )
                .setPhoto("data:image/png;base64,image")
                .build();

        grpcMuseumService.createMuseum(request, museumResponseObserver);

        ArgumentCaptor<MuseumResponse> captor =
                ArgumentCaptor.forClass(MuseumResponse.class);

        verify(museumRepository).saveAndFlush(any(MuseumEntity.class));
        verify(museumResponseObserver).onNext(captor.capture());
        verify(museumResponseObserver).onCompleted();
        verify(museumResponseObserver, never()).onError(any());

        Museum grpcMuseum = captor.getValue().getMuseum();
        assertEquals("Лувр", grpcMuseum.getTitle());
        assertEquals("Париж", grpcMuseum.getGeo().getCity());
    }

    @Test
    void createMuseumCountryNotFound() {
        when(countryRepository.findById(any()))
                .thenReturn(Optional.empty());

        CreateMuseumRequest request = CreateMuseumRequest.newBuilder()
                .setTitle("Лувр")
                .setGeo(
                        Geo.newBuilder()
                                .setCity("Париж")
                                .setCountry(
                                        Country.newBuilder()
                                                .setId(UUID.randomUUID().toString())
                                                .build()
                                )
                )
                .build();

        grpcMuseumService.createMuseum(request, museumResponseObserver);

        verify(museumResponseObserver).onError(any());
        verify(museumResponseObserver, never()).onNext(any());
        verify(museumResponseObserver, never()).onCompleted();
    }

    @Test
    void updateMuseumSuccess() {
        when(museumRepository.findByTitle("Лувр"))
                .thenReturn(Optional.of(museum));
        when(countryRepository.findById(country.getId()))
                .thenReturn(Optional.of(country));
        when(museumRepository.saveAndFlush(any()))
                .thenReturn(museum);

        UpdateMuseumRequest request = UpdateMuseumRequest.newBuilder()
                .setTitle("Лувр")
                .setDescription("Обновлённое описание")
                .setGeo(
                        Geo.newBuilder()
                                .setCity("Париж")
                                .setCountry(
                                        Country.newBuilder()
                                                .setId(country.getId().toString())
                                                .build()
                                )
                )
                .build();

        grpcMuseumService.updateMuseum(request, museumResponseObserver);

        verify(museumResponseObserver).onNext(any());
        verify(museumResponseObserver).onCompleted();
        verify(museumResponseObserver, never()).onError(any());
    }

    @Test
    void updateMuseumNotFound() {
        when(museumRepository.findByTitle(any()))
                .thenReturn(Optional.empty());

        UpdateMuseumRequest request = UpdateMuseumRequest.newBuilder()
                .setTitle("Лувр")
                .build();

        grpcMuseumService.updateMuseum(request, museumResponseObserver);

        verify(museumResponseObserver).onError(any());
        verify(museumResponseObserver, never()).onNext(any());
        verify(museumResponseObserver, never()).onCompleted();
    }

    @Test
    void isPhotoStringTest() {
        assertTrue(GrpcMuseumService.isPhotoString("data:image/png;base64,test"));
        assertFalse(GrpcMuseumService.isPhotoString("image.png"));
        assertFalse(GrpcMuseumService.isPhotoString(null));
    }
}
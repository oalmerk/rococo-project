package olmerk.unit;

import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import olmerk.data.ArtistEntity;
import olmerk.data.repository.ArtistRepository;
import olmerk.grpc.rococo.*;
import olmerk.service.GrpcArtistService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GrpcArtistServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private StreamObserver<ArtistsResponse> artistsResponseObserver;

    @Mock
    private StreamObserver<ArtistResponse> artistResponseObserver;

    @InjectMocks
    private GrpcArtistService grpcArtistService;

    private ArtistEntity artist;

    @BeforeEach
    void setUp() {
        artist = new ArtistEntity();
        artist.setId(UUID.randomUUID());
        artist.setName("Василий Васильевич Кандинский");
        artist.setBiography("Живописец и график русского зарубежья");
        artist.setPhoto("data:image/png;base64,image".getBytes(StandardCharsets.UTF_8));
    }


    @Test
    void getAllArtistsSuccess() {
        when(artistRepository.findAllByNameContainsIgnoreCase(
                eq("Василий"),
                eq(PageRequest.of(0, 10))
        )).thenReturn(new PageImpl<>(List.of(artist)));

        ArtistsRequest request = ArtistsRequest.newBuilder()
                .setName("Василий")
                .setPage(0)
                .setSize(10)
                .build();

        grpcArtistService.getAllArtists(request, artistsResponseObserver);

        ArgumentCaptor<ArtistsResponse> captor =
                ArgumentCaptor.forClass(ArtistsResponse.class);

        verify(artistsResponseObserver).onNext(captor.capture());
        verify(artistsResponseObserver).onCompleted();

        ArtistsResponse response = captor.getValue();
        assertEquals(1, response.getArtistsCount());
        assertEquals("Василий Васильевич Кандинский", response.getArtists(0).getName());
        assertEquals("Живописец и график русского зарубежья", response.getArtists(0).getBiography());
        assertEquals("data:image/png;base64,image", response.getArtists(0).getPhoto());
    }

    @Test
    void getAllArtistsEmptyResult() {
        when(artistRepository.findAllByNameContainsIgnoreCase(
                eq("Пикассо"),
                eq(PageRequest.of(0, 10))
        )).thenReturn(Page.empty());

        ArtistsRequest request = ArtistsRequest.newBuilder()
                .setName("Пикассо")
                .setPage(0)
                .setSize(10)
                .build();

        grpcArtistService.getAllArtists(request, artistsResponseObserver);

        ArgumentCaptor<ArtistsResponse> captor =
                ArgumentCaptor.forClass(ArtistsResponse.class);

        verify(artistsResponseObserver).onNext(captor.capture());
        verify(artistsResponseObserver).onCompleted();

        ArtistsResponse response = captor.getValue();
        assertEquals(0, response.getArtistsCount());
    }


    @Test
    void getAllArtistsWhenNameIsNull() {
        when(artistRepository.findAllByNameContainsIgnoreCase(
                eq(""),
                eq(PageRequest.of(0, 10))
        )).thenReturn(new PageImpl<>(List.of(artist)));


        ArtistsRequest request = ArtistsRequest.newBuilder()
                .setPage(0)
                .setSize(10)
                .build();

        grpcArtistService.getAllArtists(request, artistsResponseObserver);

        ArgumentCaptor<ArtistsResponse> captor =
                ArgumentCaptor.forClass(ArtistsResponse.class);

        verify(artistsResponseObserver).onNext(captor.capture());
        verify(artistsResponseObserver).onCompleted();

        ArtistsResponse response = captor.getValue();
        assertEquals(1, response.getArtistsCount());
        assertEquals("Василий Васильевич Кандинский",
                response.getArtists(0).getName());
    }

    @Test
    void getAllArtistsWithPagination() {
        when(artistRepository.findAllByNameContainsIgnoreCase(
                eq("Василий"),
                eq(PageRequest.of(1, 5))
        )).thenReturn(new PageImpl<>(List.of(artist)));

        ArtistsRequest request = ArtistsRequest.newBuilder()
                .setName("Василий")
                .setPage(1)
                .setSize(5)
                .build();

        grpcArtistService.getAllArtists(request, artistsResponseObserver);

        verify(artistsResponseObserver).onNext(any(ArtistsResponse.class));
        verify(artistsResponseObserver).onCompleted();
    }

    @Test
    void getAllArtistsDoesNotCallOnError() {
        when(artistRepository.findAllByNameContainsIgnoreCase(
                anyString(),
                any(PageRequest.class)
        )).thenReturn(new PageImpl<>(List.of(artist)));

        ArtistsRequest request = ArtistsRequest.newBuilder()
                .setPage(0)
                .setSize(10)
                .build();

        grpcArtistService.getAllArtists(request, artistsResponseObserver);
        verify(artistsResponseObserver, never()).onError(any());
    }

    @Test
    void createArtistSuccess() {
        when(artistRepository.saveAndFlush(any(ArtistEntity.class)))
                .thenReturn(artist);

        CreateArtistRequest request = CreateArtistRequest.newBuilder()
                .setName("Василий Васильевич Кандинский")
                .setBiography("Живописец и график русского зарубежья")
                .setPhoto("data:image/png;base64,image")
                .build();

        grpcArtistService.createArtist(request, artistResponseObserver);

        ArgumentCaptor<ArtistResponse> captor =
                ArgumentCaptor.forClass(ArtistResponse.class);

        verify(artistRepository).saveAndFlush(any(ArtistEntity.class));
        verify(artistResponseObserver).onNext(captor.capture());
        verify(artistResponseObserver).onCompleted();
        verify(artistResponseObserver, never()).onError(any());

        ArtistResponse response = captor.getValue();
        Artist grpcArtist = response.getArtist();

        assertEquals("Василий Васильевич Кандинский", grpcArtist.getName());
        assertEquals("Живописец и график русского зарубежья",
                grpcArtist.getBiography());
        assertEquals("data:image/png;base64,image",
                grpcArtist.getPhoto());
    }

    @Test
    void createArtistWithoutPhoto() {
        artist.setPhoto(null);

        when(artistRepository.saveAndFlush(any(ArtistEntity.class)))
                .thenReturn(artist);

        CreateArtistRequest request = CreateArtistRequest.newBuilder()
                .setName("Василий")
                .setBiography("Художник")
                .setPhoto("just-text")
                .build();

        grpcArtistService.createArtist(request, artistResponseObserver);

        ArgumentCaptor<ArtistResponse> captor =
                ArgumentCaptor.forClass(ArtistResponse.class);

        verify(artistResponseObserver).onNext(captor.capture());
        verify(artistResponseObserver).onCompleted();

        Artist grpcArtist = captor.getValue().getArtist();
        assertFalse(grpcArtist.hasPhoto());
    }

    @Test
    void createArtistDataIntegrityViolation() {
        when(artistRepository.saveAndFlush(any(ArtistEntity.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate"));

        CreateArtistRequest request = CreateArtistRequest.newBuilder()
                .setName("Василий")
                .setBiography("Художник")
                .build();

        grpcArtistService.createArtist(request, artistResponseObserver);

        verify(artistResponseObserver).onError(any());
        verify(artistResponseObserver, never()).onNext(any());
        verify(artistResponseObserver, never()).onCompleted();
    }
}


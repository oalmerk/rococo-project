package olmerk.service;

import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;
import olmerk.data.ArtistEntity;
import olmerk.data.repository.ArtistRepository;
import olmerk.ex.ArtistNotFoundException;
import olmerk.ex.GrpcExceptionMapper;
import olmerk.grpc.rococo.*;

import java.nio.charset.StandardCharsets;
import java.util.UUID;


@GrpcService
public class GrpcArtistService extends RococoArtistServiceGrpc.RococoArtistServiceImplBase {
    private final ArtistRepository artistRepository;

    @Autowired
    public GrpcArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void getAllArtists(ArtistsRequest request, StreamObserver<ArtistsResponse> responseObserver) {
        ArtistsResponse response = ArtistsResponse.newBuilder().addAllArtists(
                artistRepository.findAllByNameContainsIgnoreCase(request.getName() == null ? "" : request.getName(), PageRequest.of(request.getPage(), request.getSize()))
                        .stream().map(this::toGrpcArtist).toList()
        ).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void createArtist(CreateArtistRequest request, StreamObserver<ArtistResponse> responseObserver) {
        try {
            ArtistEntity artistEntity = new ArtistEntity();
            artistEntity.setName(request.getName());
            artistEntity.setBiography(request.getBiography());

            if (isPhotoString(request.getPhoto())) {
                artistEntity.setPhoto(request.getPhoto().getBytes(StandardCharsets.UTF_8));
            }

            ArtistEntity saved = artistRepository.saveAndFlush(artistEntity);

            responseObserver.onNext(
                    ArtistResponse.newBuilder()
                            .setArtist(toGrpcArtist(saved))
                            .build()
            );
            responseObserver.onCompleted();

        } catch (DataIntegrityViolationException ex) {
            responseObserver.onError(
                    GrpcExceptionMapper.map(ex, "Artist", request.getName())
            );
        }
    }

    @Override
    @Transactional
    public void updateArtist(CreateArtistRequest request, StreamObserver<ArtistResponse> responseObserver) {
        try {
            ArtistEntity artistEntity = artistRepository
                    .findByNameContainsIgnoreCase(request.getName())
                    .orElseThrow(() -> new ArtistNotFoundException(request.getName()));

            artistEntity.setName(request.getName());
            artistEntity.setBiography(request.getBiography());

            if (isPhotoString(request.getPhoto())) {
                artistEntity.setPhoto(request.getPhoto().getBytes(StandardCharsets.UTF_8));
            }

            ArtistEntity saved = artistRepository.saveAndFlush(artistEntity);

            responseObserver.onNext(
                    ArtistResponse.newBuilder()
                            .setArtist(toGrpcArtist(saved))
                            .build()
            );
            responseObserver.onCompleted();

        } catch (DataIntegrityViolationException ex) {
            responseObserver.onError(GrpcExceptionMapper.map(ex, "Artist", request.getName()));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void getArtist(GetArtistRequest request, StreamObserver<ArtistResponse> responseObserver) {
        try {
            ArtistEntity artistEntity = artistRepository
                    .findById(UUID.fromString(request.getId()))
                    .orElseThrow(() -> new ArtistNotFoundException(request.getId()));

            responseObserver.onNext(
                    ArtistResponse.newBuilder()
                            .setArtist(toGrpcArtist(artistEntity))
                            .build()
            );
            responseObserver.onCompleted();

        } catch (ArtistNotFoundException ex) {
            responseObserver.onError(GrpcExceptionMapper.map(ex)
            );
        } catch (Exception ex) {
            responseObserver.onError(GrpcExceptionMapper.map(ex)
            );
        }
    }

    public @Nonnull Artist toGrpcArtist(ArtistEntity artistEntity) {
        Artist.Builder grpcArtistBuilder = Artist.newBuilder();
        grpcArtistBuilder.setId(String.valueOf(artistEntity.getId()));
        grpcArtistBuilder.setName(artistEntity.getName());
        if (artistEntity.getBiography() != null) {
            grpcArtistBuilder.setBiography(artistEntity.getBiography());
        }
        if (artistEntity.getPhoto() != null) {
            grpcArtistBuilder.setPhoto(new String(artistEntity.getPhoto(), StandardCharsets.UTF_8));
        }
        return grpcArtistBuilder.build();
    }

    public static boolean isPhotoString(String photo) {
        return photo != null && photo.startsWith("data:image");
    }
}

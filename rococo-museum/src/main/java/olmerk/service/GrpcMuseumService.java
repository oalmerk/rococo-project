package olmerk.service;

import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;
import olmerk.data.MuseumEntity;
import olmerk.data.repository.CountryRepository;
import olmerk.data.repository.MuseumRepository;
import olmerk.exception.CountryNotFoundException;
import olmerk.exception.GrpcExceptionMapper;
import olmerk.exception.MuseumNotFoundException;
import olmerk.grpc.rococo.*;

import java.nio.charset.StandardCharsets;
import java.util.UUID;


@GrpcService
public class GrpcMuseumService extends RococoMuseumServiceGrpc.RococoMuseumServiceImplBase {

    private final MuseumRepository museumRepository;
    private final CountryRepository countryRepository;

    @Autowired
    public GrpcMuseumService(MuseumRepository museumRepository, CountryRepository countryRepository) {
        this.museumRepository = museumRepository;
        this.countryRepository = countryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void getAllMuseums(MuseumRequest request, StreamObserver<MuseumsResponse> responseObserver) {
        MuseumsResponse response = MuseumsResponse.newBuilder().addAllMuseum(
                museumRepository.findAllByTitleContainingIgnoreCase(request.getName(), PageRequest.of(request.getPage(), request.getSize()))
                        .stream().map(this::toGrpcMuseum).toList()
        ).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    @Transactional(readOnly = true)
    public void getMuseumById(GetMuseumRequest request,
                              StreamObserver<MuseumResponse> responseObserver) {
        try {
            MuseumEntity museumEntity = museumRepository
                    .findById(UUID.fromString(request.getId()))
                    .orElseThrow(() -> new MuseumNotFoundException(request.getId()));

            responseObserver.onNext(
                    MuseumResponse.newBuilder()
                            .setMuseum(toGrpcMuseum(museumEntity))
                            .build()
            );
            responseObserver.onCompleted();

        } catch (MuseumNotFoundException ex) {
            responseObserver.onError(GrpcExceptionMapper.map(ex)
            );
        } catch (Exception ex) {
            responseObserver.onError(GrpcExceptionMapper.map(ex)
            );
        }
    }

    @Transactional
    public void createMuseum(CreateMuseumRequest request, StreamObserver<MuseumResponse> responseObserver) {
        try {
            MuseumEntity museumEntity = new MuseumEntity();

            museumEntity.setTitle(request.getTitle());
            museumEntity.setDescription(request.getDescription());
            museumEntity.setCity(request.getGeo().getCity());
            museumEntity.setCountry(
                    countryRepository.findById(
                            UUID.fromString(request.getGeo().getCountry().getId())
                    ).orElseThrow(() ->
                            new CountryNotFoundException(request.getGeo().getCountry().getId()))
            );

            if (isPhotoString(request.getPhoto())) {
                museumEntity.setPhoto(request.getPhoto().getBytes(StandardCharsets.UTF_8));
            }

            MuseumEntity saved = museumRepository.saveAndFlush(museumEntity);

            responseObserver.onNext(
                    MuseumResponse.newBuilder()
                            .setMuseum(toGrpcMuseum(saved))
                            .build()
            );
            responseObserver.onCompleted();

        } catch (DataIntegrityViolationException ex) {
            responseObserver.onError(GrpcExceptionMapper.map(ex, request.getTitle()));
        } catch (CountryNotFoundException ex) {
            responseObserver.onError(GrpcExceptionMapper.map(ex));
        } catch (Exception ex) {
            responseObserver.onError(GrpcExceptionMapper.map(ex));
        }
    }

    @Override
    @Transactional
    public void updateMuseum(UpdateMuseumRequest request,
                             StreamObserver<MuseumResponse> responseObserver) {
        try {
            MuseumEntity museumEntity = museumRepository
                    .findByTitle(request.getTitle())
                    .orElseThrow(() -> new MuseumNotFoundException(request.getTitle()));

            museumEntity.setTitle(request.getTitle());
            museumEntity.setDescription(request.getDescription());
            museumEntity.setCity(request.getGeo().getCity());
            museumEntity.setCountry(
                    countryRepository.findById(
                            UUID.fromString(request.getGeo().getCountry().getId())
                    ).orElseThrow(() ->
                            new CountryNotFoundException(request.getGeo().getCountry().getId()))
            );

            if (isPhotoString(request.getPhoto())) {
                museumEntity.setPhoto(request.getPhoto().getBytes(StandardCharsets.UTF_8));
            }

            MuseumEntity saved = museumRepository.saveAndFlush(museumEntity);

            responseObserver.onNext(
                    MuseumResponse.newBuilder()
                            .setMuseum(toGrpcMuseum(saved))
                            .build()
            );
            responseObserver.onCompleted();

        } catch (DataIntegrityViolationException ex) {
            responseObserver.onError(GrpcExceptionMapper.map(ex, request.getTitle()));
        } catch (CountryNotFoundException ex) {
            responseObserver.onError(GrpcExceptionMapper.map(ex));
        } catch (Exception ex) {
            responseObserver.onError(GrpcExceptionMapper.map(ex));
        }
    }


    public @Nonnull Museum toGrpcMuseum(MuseumEntity museumEntity) {
        Museum.Builder grpcMuseumBuilder = Museum.newBuilder();
        grpcMuseumBuilder.setId(String.valueOf(museumEntity.getId()));
        grpcMuseumBuilder.setTitle(museumEntity.getTitle());
        grpcMuseumBuilder.setDescription(museumEntity.getDescription());

        Geo.Builder grpcGeoBuilder = Geo.newBuilder();
        grpcGeoBuilder.setCity(museumEntity.getCity());
        Country.Builder grpcCountryBuilder = Country.newBuilder();
        grpcCountryBuilder.setId(String.valueOf(museumEntity.getCountry().getId()));
        grpcCountryBuilder.setName(museumEntity.getCountry().getName());
        grpcGeoBuilder.setCountry(grpcCountryBuilder);

        grpcMuseumBuilder.setGeo(grpcGeoBuilder);
        if (museumEntity.getPhoto() != null) {
            grpcMuseumBuilder.setPhoto(new String(museumEntity.getPhoto(), StandardCharsets.UTF_8));
        }
        return grpcMuseumBuilder.build();
    }

    public static boolean isPhotoString(String photo) {
        return photo != null && photo.startsWith("data:image");
    }
}

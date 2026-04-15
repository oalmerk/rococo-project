package olmerk.service;

import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import olmerk.data.PaintingEntity;
import olmerk.data.repository.PaintingRepository;
import olmerk.exception.GrpcPaintingExceptionMapper;
import olmerk.exception.PaintingNotFoundException;
import olmerk.grpc.rococo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@GrpcService
public class GrpcPaintingService extends RococoPaintingServiceGrpc.RococoPaintingServiceImplBase {

    private final PaintingRepository paintingRepository;

    @Autowired
    public GrpcPaintingService(PaintingRepository paintingRepository) {
        this.paintingRepository = paintingRepository;
    }


    @Override
    @Transactional(readOnly = true)
    public void getAllPainting(PaintingRequest request, StreamObserver<PaintingsResponse> responseObserver) {
        PaintingsResponse response = PaintingsResponse.newBuilder().addAllPainting(
                paintingRepository.findAllByTitleContainingIgnoreCase(request.getTitle(), PageRequest.of(request.getPage(), request.getSize()))
                        .stream().map(this::toGrpcPainting).toList()
        ).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    @Transactional(readOnly = true)
    public void getPaintingById(GetPaintingRequest request,
                                StreamObserver<PaintingResponse> responseObserver) {
        try {
            PaintingEntity paintingEntity = paintingRepository
                    .findById(UUID.fromString(request.getId()))
                    .orElseThrow(() -> new PaintingNotFoundException(request.getId()));

            responseObserver.onNext(
                    PaintingResponse.newBuilder()
                            .setPainting(toGrpcPainting(paintingEntity))
                            .build()
            );
            responseObserver.onCompleted();

        } catch (PaintingNotFoundException ex) {
            responseObserver.onError(GrpcPaintingExceptionMapper.map(ex));
        } catch (Exception ex) {
            responseObserver.onError(GrpcPaintingExceptionMapper.map(ex));
        }
    }

    @Transactional(readOnly = true)
    public void getPaintingsByAuthorId(GetPaintingByAuthorRequest request,
                                       StreamObserver<PaintingsResponse> responseObserver) {

        PaintingsResponse response = PaintingsResponse.newBuilder()
                .addAllPainting(
                        paintingRepository
                                .findAllByArtistId(
                                        UUID.fromString(request.getId()),
                                        PageRequest.of(request.getPage(), request.getSize())
                                )
                                .stream()
                                .map(this::toGrpcPainting)
                                .toList()
                )
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void createPainting(CreatePaintingRequest request, StreamObserver<PaintingResponse> responseObserver) {
        try {
            PaintingEntity paintingEntity = new PaintingEntity();

            paintingEntity.setTitle(request.getTitle());
            paintingEntity.setDescription(request.getDescription());
            paintingEntity.setArtistId(UUID.fromString(request.getArtist().getId()));
            paintingEntity.setMuseumId(UUID.fromString(request.getMuseum().getId()));

            if (isPhotoString(request.getContent())) {
                paintingEntity.setContent(
                        request.getContent().getBytes(StandardCharsets.UTF_8)
                );
            }

            PaintingEntity saved = paintingRepository.saveAndFlush(paintingEntity);

            responseObserver.onNext(
                    PaintingResponse.newBuilder()
                            .setPainting(toGrpcPainting(saved))
                            .build()
            );
            responseObserver.onCompleted();

        } catch (DataIntegrityViolationException ex) {
            responseObserver.onError(GrpcPaintingExceptionMapper.map(ex, request.getTitle()));
        } catch (Exception ex) {
            responseObserver.onError(GrpcPaintingExceptionMapper.map(ex));
        }
    }


    @Override
    @Transactional
    public void updatePainting(UpdatePaintingRequest request, StreamObserver<PaintingResponse> responseObserver) {
        try {
            PaintingEntity paintingEntity = paintingRepository
                    .findByTitle(request.getTitle())
                    .orElseThrow(() -> new PaintingNotFoundException(request.getTitle()));

            paintingEntity.setTitle(request.getTitle());
            paintingEntity.setDescription(request.getDescription());
            paintingEntity.setArtistId(UUID.fromString(request.getArtist().getId()));
            paintingEntity.setMuseumId(UUID.fromString(request.getMuseum().getId()));

            if (isPhotoString(request.getContent())) {
                paintingEntity.setContent(
                        request.getContent().getBytes(StandardCharsets.UTF_8)
                );
            }

            PaintingEntity saved = paintingRepository.saveAndFlush(paintingEntity);

            responseObserver.onNext(
                    PaintingResponse.newBuilder()
                            .setPainting(toGrpcPainting(saved))
                            .build()
            );
            responseObserver.onCompleted();

        } catch (PaintingNotFoundException ex) {
            responseObserver.onError(GrpcPaintingExceptionMapper.map(ex));
        } catch (DataIntegrityViolationException ex) {
            responseObserver.onError(GrpcPaintingExceptionMapper.map(ex, request.getTitle()));
        } catch (Exception ex) {
            responseObserver.onError(GrpcPaintingExceptionMapper.map(ex));
        }
    }


    public @Nonnull Painting toGrpcPainting(PaintingEntity paintingEntity) {
        Painting.Builder grpcPaintingBuilder = Painting.newBuilder();
        grpcPaintingBuilder.setId(String.valueOf(paintingEntity.getId()));
        grpcPaintingBuilder.setTitle(paintingEntity.getTitle());
        grpcPaintingBuilder.setDescription(paintingEntity.getDescription());
        grpcPaintingBuilder.setContent(new String(paintingEntity.getContent(), StandardCharsets.UTF_8));

        Artist.Builder grpcArtistBuilder = Artist.newBuilder();
        grpcArtistBuilder.setId(String.valueOf(paintingEntity.getArtistId())).build();
        grpcPaintingBuilder.setArtist(grpcArtistBuilder);

        Museum.Builder grpcMuseumBuilder = Museum.newBuilder();
        grpcMuseumBuilder.setId(String.valueOf(paintingEntity.getMuseumId())).build();
        grpcPaintingBuilder.setMuseum(grpcMuseumBuilder);
        return grpcPaintingBuilder.build();
    }

    public static boolean isPhotoString(String photo) {
        return photo != null && photo.startsWith("data:image");
    }
}
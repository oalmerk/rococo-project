package olmerk.rococo.service.api;

import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Service;
import olmerk.grpc.rococo.*;
import olmerk.rococo.model.PaintingJson;

import java.util.List;

@Service
public class GrpcPaintingClient {

    private RococoPaintingServiceGrpc.RococoPaintingServiceBlockingStub blockingStub;

    public GrpcPaintingClient(GrpcChannelFactory channelFactory) {
        var channel = channelFactory.createChannel("rococo-painting");
        this.blockingStub = RococoPaintingServiceGrpc.newBlockingStub(channel);
    }

    public @Nonnull List<PaintingJson> getAllPainting(String title, Pageable pageable) {
        return blockingStub.getAllPainting(PaintingRequest.newBuilder().setTitle(title).setSize(pageable.getPageSize())
                        .setPage(pageable.getPageNumber()).build()).getPaintingList()
                .stream()
                .map(PaintingJson::fromGrpcMessage)
                .toList();
    }

    public @Nonnull PaintingJson getPaintingById(String id) {
        return PaintingJson.fromGrpcMessage(blockingStub.getPaintingById(GetPaintingRequest.newBuilder()
                .setId(id).build()).getPainting());

    }

    public @Nonnull List<PaintingJson> getPaintingByAuthor(String id, Pageable pageable) {
        return blockingStub.getPaintingsByAuthorId(GetPaintingByAuthorRequest.newBuilder().setId(id).setSize(pageable.getPageSize())
                        .setPage(pageable.getPageNumber()).build()).getPaintingList()
                .stream()
                .map(PaintingJson::fromGrpcMessage)
                .toList();
    }

    public @Nonnull PaintingJson createPainting(PaintingJson paintingJson) {
        return PaintingJson.fromGrpcMessage(blockingStub.createPainting(CreatePaintingRequest.newBuilder()
                .setTitle(paintingJson.title())
                .setDescription(paintingJson.description())
                .setContent(paintingJson.content())
                .setArtist(Artist.newBuilder().setId(String.valueOf(paintingJson.artist().id())).build())
                .setMuseum(Museum.newBuilder().setId(String.valueOf(paintingJson.museum().id())).build())
                .build()
        ).getPainting());
    }

    public @Nonnull PaintingJson updatePainting(PaintingJson paintingJson) {
        return PaintingJson.fromGrpcMessage(blockingStub.updatePainting(UpdatePaintingRequest.newBuilder()
                .setTitle(paintingJson.title())
                .setDescription(paintingJson.description())
                .setContent(paintingJson.content())
                .setArtist(Artist.newBuilder().setId(String.valueOf(paintingJson.artist().id())).build())
                .setMuseum(Museum.newBuilder().setId(String.valueOf(paintingJson.museum().id())).build())
                .build()
        ).getPainting());
    }
}

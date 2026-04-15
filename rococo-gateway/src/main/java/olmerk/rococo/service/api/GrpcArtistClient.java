package olmerk.rococo.service.api;


import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Pageable;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Service;
import olmerk.grpc.rococo.ArtistsRequest;
import olmerk.grpc.rococo.CreateArtistRequest;
import olmerk.grpc.rococo.GetArtistRequest;
import olmerk.grpc.rococo.RococoArtistServiceGrpc;
import olmerk.rococo.model.ArtistJson;

import java.util.List;

@Service
public class GrpcArtistClient {
    private RococoArtistServiceGrpc.RococoArtistServiceBlockingStub blockingStub;

    public GrpcArtistClient(GrpcChannelFactory channelFactory) {
        var channel = channelFactory.createChannel("rococo-artist");
        this.blockingStub = RococoArtistServiceGrpc.newBlockingStub(channel);
    }

    public @Nonnull List<ArtistJson> getAllArtist(String name, Pageable pageable) {
        return blockingStub.getAllArtists(ArtistsRequest.newBuilder().setName(name).setSize(pageable.getPageSize())
                        .setPage(pageable.getPageNumber()).build()).getArtistsList()
                .stream()
                .map(ArtistJson::fromGrpcMessage)
                .toList();
    }


    public @Nonnull ArtistJson getArtistById(String id) {
        return ArtistJson.fromGrpcMessage(blockingStub.getArtist(GetArtistRequest.newBuilder()
                .setId(id).build()).getArtist());
    }

    public @Nonnull ArtistJson createArtist(ArtistJson artistJson) {
        return ArtistJson.fromGrpcMessage(blockingStub.createArtist(CreateArtistRequest.newBuilder()
                        .setName(artistJson.name())
                        .setBiography(artistJson.biography())
                        .setPhoto(artistJson.photo()).build())
                .getArtist());
    }

    public @Nonnull ArtistJson updateArtist(ArtistJson artistJson) {
        return ArtistJson.fromGrpcMessage(blockingStub.updateArtist(CreateArtistRequest.newBuilder()
                .setName(artistJson.name()).setBiography(artistJson.biography())
                .setPhoto(artistJson.photo()).build()).getArtist());

    }
}


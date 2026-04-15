package olmerk.rococo.service.api;

import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Pageable;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Service;
import olmerk.grpc.rococo.*;
import olmerk.rococo.model.MuseumJson;

import java.util.List;

@Service
public class GrpcMuseumClient {
    private RococoMuseumServiceGrpc.RococoMuseumServiceBlockingStub blockingStub;

    public GrpcMuseumClient(GrpcChannelFactory channelFactory) {
        var channel = channelFactory.createChannel("rococo-museum");
        this.blockingStub = RococoMuseumServiceGrpc.newBlockingStub(channel);
    }

    public @Nonnull List<MuseumJson> getAllMuseums(String name, Pageable pageable) {
        return blockingStub.getAllMuseums(MuseumRequest.newBuilder().setSize(pageable.getPageSize()).setName(name)
                        .setPage(pageable.getPageNumber()).build()).getMuseumList()
                .stream()
                .map(MuseumJson::fromGrpcMessage)
                .toList();

    }

    public @Nonnull MuseumJson getMuseumById(String id) {
        return MuseumJson.fromGrpcMessage(blockingStub.getMuseumById(GetMuseumRequest.newBuilder()
                .setId(id).build()).getMuseum());
    }

    public @Nonnull MuseumJson createMuseum(MuseumJson museumJson) {
        return MuseumJson.fromGrpcMessage(blockingStub.createMuseum(CreateMuseumRequest.newBuilder()
                .setTitle(museumJson.title())
                .setDescription(museumJson.description())
                .setPhoto(museumJson.photo())
                .setGeo(Geo.newBuilder().setCity(museumJson.geo().city())
                        .setCountry(Country.newBuilder()
                                .setId(String.valueOf(museumJson.geo().country().id()))
                                .build())
                        .build()).build()).getMuseum());
    }

    public @Nonnull MuseumJson updateMuseum(MuseumJson museumJson) {
        return MuseumJson.fromGrpcMessage(blockingStub.updateMuseum(UpdateMuseumRequest.newBuilder()
                .setTitle(museumJson.title())
                .setDescription(museumJson.description())
                .setPhoto(museumJson.photo())
                .setGeo(Geo.newBuilder().setCity(museumJson.geo().city())
                        .setCountry(Country.newBuilder()
                                .setId(String.valueOf(museumJson.geo().country().id()))
                                .build())
                        .build()).build()).getMuseum());
    }
}


package olmerk.rococo.service.api;

import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Pageable;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Service;
import olmerk.grpc.rococo.CountryRequest;
import olmerk.grpc.rococo.RococoCountryServiceGrpc;
import olmerk.rococo.model.CountryJson;

import java.util.List;

@Service
public class GrpcCountryClient {

    private RococoCountryServiceGrpc.RococoCountryServiceBlockingStub blockingStub;
    public GrpcCountryClient(GrpcChannelFactory channelFactory) {
        var channel = channelFactory.createChannel("rococo-museum");
        this.blockingStub = RococoCountryServiceGrpc.newBlockingStub(channel);
    }

    public @Nonnull List<CountryJson> getAllCountries(Pageable pageable) {
        return blockingStub.getAllCountries(CountryRequest.newBuilder().setSize(pageable.getPageSize())
                        .setPage(pageable.getPageNumber()).build()).getCountryList()
                .stream()
                .map(CountryJson::fromGrpcMessage)
                .toList();
    }
}


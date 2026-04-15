package olmerk.service;

import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;
import olmerk.data.CountryEntity;
import olmerk.data.repository.CountryRepository;
import olmerk.grpc.rococo.CountriesResponse;
import olmerk.grpc.rococo.Country;
import olmerk.grpc.rococo.CountryRequest;
import olmerk.grpc.rococo.RococoCountryServiceGrpc;

@GrpcService
public class GrpcCountryService extends RococoCountryServiceGrpc.RococoCountryServiceImplBase {

    private final CountryRepository countryRepository;

    @Autowired
    public GrpcCountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void getAllCountries(CountryRequest request, StreamObserver<CountriesResponse> responseObserver) {
        CountriesResponse response = CountriesResponse.newBuilder().addAllCountry(
                countryRepository.findAll(PageRequest.of(request.getPage(), request.getSize()))
                        .stream().map(this::toGrpcCountry).toList()
        ).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    public @Nonnull Country toGrpcCountry(CountryEntity countryEntity) {
        Country.Builder grpcCountryBuilder = Country.newBuilder();
        grpcCountryBuilder.setId(String.valueOf(countryEntity.getId()));
        grpcCountryBuilder.setName(countryEntity.getName());
        return grpcCountryBuilder.build();
    }
}
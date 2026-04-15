package olmerk.rococo.service.api;

import jakarta.annotation.Nonnull;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Service;
import olmerk.grpc.rococo.GetUserRequest;
import olmerk.grpc.rococo.RococoUserdataServiceGrpc;
import olmerk.grpc.rococo.UpdateUserRequest;
import olmerk.rococo.model.UserJson;

@Service
public class GrpcUserdataClient {
    private RococoUserdataServiceGrpc.RococoUserdataServiceBlockingStub blockingStub;

    public GrpcUserdataClient(GrpcChannelFactory channelFactory) {
        var channel = channelFactory.createChannel("rococo-userdata");
        this.blockingStub = RococoUserdataServiceGrpc.newBlockingStub(channel);
    }

    public @Nonnull UserJson getUser(String userName) {
        return UserJson.fromGrpcMessage(blockingStub.getUser(GetUserRequest.newBuilder()
                .setUsername(userName).build()).getUser());
    }

    public @Nonnull UserJson upDateUser(UserJson userJson) {
        return UserJson.fromGrpcMessage(blockingStub.updateUser(UpdateUserRequest.newBuilder()
                .setUser(userJson.toGrpcMessage()).build()).getUser());
    }
}


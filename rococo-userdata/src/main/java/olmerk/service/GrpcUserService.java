package olmerk.service;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;
import olmerk.data.UserEntity;
import olmerk.data.repository.UserRepository;
import olmerk.exception.UserNotFoundException;
import olmerk.grpc.rococo.*;

import java.nio.charset.StandardCharsets;

import java.util.UUID;

@GrpcService
public class GrpcUserService extends RococoUserdataServiceGrpc.RococoUserdataServiceImplBase {

    private final UserRepository userRepository;

    @Autowired
    public GrpcUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public void getUser(GetUserRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            UserEntity user = userRepository
                    .findByUsername(request.getUsername()).orElseThrow(
                            () -> new UserNotFoundException(request.getUsername()));

            UserResponse response = UserResponse.newBuilder()
                    .setUser(toGrpcUser(user)).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (UserNotFoundException e) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Unexpected server error: " + e.getMessage())
                            .withCause(e)
                            .asRuntimeException()
            );
        }
    }


    @Override
    @Transactional
    public void updateUser(UpdateUserRequest request, StreamObserver<UserResponse> responseObserver) {
        UserEntity userEntity = userRepository
                .findById(UUID.fromString(request.getUser().getId())).orElseThrow(
                        () -> new UserNotFoundException(request.getUser().getId()));

        userEntity.setUsername(request.getUser().getUsername());
        userEntity.setFirstname(request.getUser().getFirstname());
        userEntity.setLastname(request.getUser().getLastname());

        if (isPhotoString(request.getUser().getAvatar())) {
            userEntity.setAvatar(request.getUser().getAvatar().getBytes(StandardCharsets.UTF_8));
        }
        UserEntity saved = userRepository.save(userEntity);

        responseObserver.onNext(
                UserResponse.newBuilder()
                        .setUser(toGrpcUser(saved))
                        .build()
        );
        responseObserver.onCompleted();
    }

    public static boolean isPhotoString(String photo) {
        return photo != null && photo.startsWith("data:image");
    }

    public @Nonnull User toGrpcUser(UserEntity userEntity) {
        User.Builder grpcUserBuilder = User.newBuilder();
        grpcUserBuilder.setId(String.valueOf(userEntity.getId()));
        grpcUserBuilder.setUsername(userEntity.getUsername());
        if (userEntity.getFirstname() != null) {
            grpcUserBuilder.setFirstname(userEntity.getFirstname());
        }
        if (userEntity.getLastname() != null) {
            grpcUserBuilder.setLastname(userEntity.getLastname());
        }
        if (userEntity.getAvatar() != null) {
            grpcUserBuilder.setAvatar(new String(userEntity.getAvatar(), StandardCharsets.UTF_8));
        }
        return grpcUserBuilder.build();
    }
}


package olmerk.unit;

import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import olmerk.data.UserEntity;
import olmerk.data.repository.UserRepository;
import olmerk.exception.UserNotFoundException;
import olmerk.grpc.rococo.GetUserRequest;
import olmerk.grpc.rococo.UpdateUserRequest;
import olmerk.grpc.rococo.User;
import olmerk.grpc.rococo.UserResponse;
import olmerk.service.GrpcUserService;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrpcUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StreamObserver<UserResponse> userResponseObserver;

    @InjectMocks
    private GrpcUserService grpcUserService;

    private UserEntity user;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setUsername("vasya");
        user.setFirstname("Василий");
        user.setLastname("Иванов");
        user.setAvatar(
                "data:image/png;base64,image"
                        .getBytes(StandardCharsets.UTF_8)
        );
    }

    @Test
    void getUserSuccess() {
        when(userRepository.findByUsername("vasya"))
                .thenReturn(Optional.of(user));

        GetUserRequest request = GetUserRequest.newBuilder()
                .setUsername("vasya")
                .build();

        grpcUserService.getUser(request, userResponseObserver);

        ArgumentCaptor<UserResponse> captor =
                ArgumentCaptor.forClass(UserResponse.class);

        verify(userResponseObserver).onNext(captor.capture());
        verify(userResponseObserver).onCompleted();
        verify(userResponseObserver, never()).onError(any());

        User grpcUser = captor.getValue().getUser();

        assertEquals(user.getId().toString(), grpcUser.getId());
        assertEquals("vasya", grpcUser.getUsername());
        assertEquals("Василий", grpcUser.getFirstname());
        assertEquals("Иванов", grpcUser.getLastname());
        assertEquals("data:image/png;base64,image", grpcUser.getAvatar());
    }

    @Test
    void getUserNotFound() {
        when(userRepository.findByUsername("vasya"))
                .thenReturn(Optional.empty());

        GetUserRequest request = GetUserRequest.newBuilder()
                .setUsername("vasya")
                .build();

        grpcUserService.getUser(request, userResponseObserver);

        verify(userResponseObserver).onError(any());
        verify(userResponseObserver, never()).onNext(any());
        verify(userResponseObserver, never()).onCompleted();
    }

    @Test
    void getUserUnexpectedError() {
        when(userRepository.findByUsername(any()))
                .thenThrow(new RuntimeException("DB is down"));

        GetUserRequest request = GetUserRequest.newBuilder()
                .setUsername("vasya")
                .build();

        grpcUserService.getUser(request, userResponseObserver);

        verify(userResponseObserver).onError(any());
        verify(userResponseObserver, never()).onNext(any());
        verify(userResponseObserver, never()).onCompleted();
    }

    @Test
    void updateUserSuccess() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any(UserEntity.class)))
                .thenReturn(user);

        User grpcUser = User.newBuilder()
                .setId(user.getId().toString())
                .setUsername("vasya_updated")
                .setFirstname("Василий")
                .setLastname("Петров")
                .setAvatar("data:image/png;base64,image")
                .build();

        UpdateUserRequest request = UpdateUserRequest.newBuilder()
                .setUser(grpcUser)
                .build();

        grpcUserService.updateUser(request, userResponseObserver);

        ArgumentCaptor<UserResponse> captor =
                ArgumentCaptor.forClass(UserResponse.class);

        verify(userRepository).save(any(UserEntity.class));
        verify(userResponseObserver).onNext(captor.capture());
        verify(userResponseObserver).onCompleted();
        verify(userResponseObserver, never()).onError(any());

        User responseUser = captor.getValue().getUser();

        assertEquals("vasya_updated", responseUser.getUsername());
        assertEquals("Василий", responseUser.getFirstname());
        assertEquals("Петров", responseUser.getLastname());
    }

    @Test
    void updateUserNotFound() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.empty());

        User grpcUser = User.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setUsername("vasya")
                .build();

        UpdateUserRequest request = UpdateUserRequest.newBuilder()
                .setUser(grpcUser)
                .build();

        assertThrows(UserNotFoundException.class, () ->
                grpcUserService.updateUser(request, userResponseObserver)
        );

        verify(userResponseObserver, never()).onNext(any());
        verify(userResponseObserver, never()).onCompleted();
    }

    @Test
    void isPhotoStringTest() {
        assertTrue(GrpcUserService.isPhotoString("data:image/png;base64,test"));
        assertFalse(GrpcUserService.isPhotoString("http://image.png"));
        assertFalse(GrpcUserService.isPhotoString(null));
    }
}


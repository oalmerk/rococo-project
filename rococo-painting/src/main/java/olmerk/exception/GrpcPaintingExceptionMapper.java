package olmerk.exception;

import io.grpc.Status;
import org.springframework.dao.DataIntegrityViolationException;

import java.sql.SQLException;

public final class GrpcPaintingExceptionMapper {

    private GrpcPaintingExceptionMapper() {
    }

    public static RuntimeException map(PaintingNotFoundException ex) {
        return Status.NOT_FOUND
                .withDescription(ex.getMessage())
                .asRuntimeException();
    }

    public static RuntimeException map(DataIntegrityViolationException ex, String paintingTitle) {

        if (isUniqueViolation(ex)) {
            return Status.ALREADY_EXISTS
                    .withDescription("Painting with title '" + paintingTitle + "' already exists")
                    .asRuntimeException();
        }

        return Status.INTERNAL
                .withDescription("Database error")
                .withCause(ex)
                .asRuntimeException();
    }


    public static RuntimeException map(Throwable ex) {
        return Status.INTERNAL
                .withDescription("Unexpected server error")
                .withCause(ex)
                .asRuntimeException();
    }

    private static boolean isUniqueViolation(DataIntegrityViolationException ex) {
        Throwable cause = ex.getMostSpecificCause();
        if (cause instanceof SQLException sqlEx) {
            return "23505".equals(sqlEx.getSQLState());
        }
        return false;
    }
}

package olmerk.rococo.data.jdbc;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Connections {

    private static final Map<String, JdbcConnectionHolder> JDBC_CONNECTION_HOLDER = new ConcurrentHashMap<>();

    private Connections() {
    }

    public static JdbcConnectionHolder getJdbcConnectionsHolder(String jdbcUrl){
        return JDBC_CONNECTION_HOLDER.computeIfAbsent(
                jdbcUrl,
                key -> new JdbcConnectionHolder(
                        DataSources.dataSource(jdbcUrl)
                )
        );
    }

    @Nonnull
    public static JdbcConnectionHolders getJdbcConnectionsHolders(String... jdbcUrl) {
        List<JdbcConnectionHolder> result = new ArrayList<>();
        for (String url : jdbcUrl) {
            result.add(getJdbcConnectionsHolder(url));
        }
        return new JdbcConnectionHolders(result);
    }

    public static void closeAllConnections() {
        JDBC_CONNECTION_HOLDER.values().forEach(JdbcConnectionHolder::closeAllConnections);
    }
}

package olmerk.rococo.data.jdbc;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.p6spy.engine.spy.P6DataSource;
import org.apache.commons.lang3.StringUtils;
import olmerk.rococo.config.Config;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DataSources {

    private static final Config CFG = Config.getInstance();
    private static final Map<String, DataSource> DATA_SOURCE_MAP = new ConcurrentHashMap<>();

    private DataSources() {
    }

    public static  DataSource dataSource(String jdbcUrl){
       return DATA_SOURCE_MAP.computeIfAbsent(jdbcUrl, key -> {
            AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();
            final String uniqId = StringUtils.substringAfter(jdbcUrl, "5432/");
            atomikosDataSourceBean.setUniqueResourceName(uniqId);
            atomikosDataSourceBean.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource");
            Properties properties = new Properties();
            properties.put("URL", jdbcUrl);
            properties.put("user", CFG.testDatabaseUsername());
            properties.put("password", CFG.testDatabasePassword());
            atomikosDataSourceBean.setXaProperties(properties);
            atomikosDataSourceBean.setPoolSize(3);
            atomikosDataSourceBean.setMaxPoolSize(10);
            P6DataSource p6DataSource = new P6DataSource(
                    atomikosDataSourceBean
            );
            try {
                InitialContext initialContext = new InitialContext();
                initialContext.bind("java:comp/env/jdbc/" + uniqId, p6DataSource);
            } catch (NamingException e) {
                throw new RuntimeException(e);
            }
            return p6DataSource;
        }
        );
    }
}


package sagan;

import javax.sql.DataSource;


import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.Assert;

public abstract class DatabaseConfig {

    @Bean
    public abstract DataSource dataSource();

    protected void configureDataSource(org.apache.tomcat.jdbc.pool.DataSource dataSource) {
        dataSource.setMaxActive(20);
        dataSource.setMaxIdle(8);
        dataSource.setMinIdle(8);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
    }
}

@Configuration
@Profile(SaganProfiles.STANDALONE)
class StandaloneDatabaseConfig extends DatabaseConfig {

    @Bean
    public DataSource dataSource() {
        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
		
		dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://ec2-107-22-248-166.compute-1.amazonaws.com:5432/dvgigtp2t3pki?sslmode=require&user=dtiivwohxfhuov&password=EI3JI4EtaF89GOC-b3U4FqcU_I");

        //dataSource.setUsername("edajunppnshgkg");
        //dataSource.setPassword("GnOCuBy8dmtUipKoHsDXRbqgSk");
		dataSource.setTestOnBorrow(true);
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnReturn(true);
        dataSource.setValidationQuery("SELECT 1");

        //configureDataSource(dataSource);

        return dataSource;
    }
}

@Configuration
@Profile(SaganProfiles.CLOUDFOUNDRY)
class CloudFoundryDatabaseConfig extends DatabaseConfig {

    @Bean
    public Cloud cloud() {
        return new CloudFactory().getCloud();
    }

    @Bean
    public DataSource dataSource() {
        DataSource dataSource = cloud().getServiceConnector("sagan-db", DataSource.class, null);
        Assert.isInstanceOf(org.apache.tomcat.jdbc.pool.DataSource.class, dataSource);
        configureDataSource((org.apache.tomcat.jdbc.pool.DataSource) dataSource);
        return dataSource;
    }
}

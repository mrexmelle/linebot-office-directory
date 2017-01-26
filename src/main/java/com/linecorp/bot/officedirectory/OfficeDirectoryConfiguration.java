
package com.linecorp.bot.officedirectory;

import java.net.URISyntaxException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import com.linecorp.bot.client.LineMessagingService;
import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.client.LineSignatureValidator;

@Configuration
@PropertySource("classpath:application.properties")
public class OfficeDirectoryConfiguration
{
    @Autowired
    Environment mEnv;
    
	@Bean
    public DataSource getDataSource()
    {
        String dbUrl=System.getenv("JDBC_DATABASE_URL");
        String username=System.getenv("JDBC_DATABASE_USERNAME");
        String password=System.getenv("JDBC_DATABASE_PASSWORD");
        
        DriverManagerDataSource ds=new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl(dbUrl);
        ds.setUsername(username);
        ds.setPassword(password);
        
        return ds;
    }
    
    @Bean
    public EmployeeDao getEmployeeDao()
    {
        return new EmployeeDaoImpl(getDataSource());
    }
    
    @Bean(name="com.linecorp.channelSecret")
    public String getChannelSecret()
    {
        return mEnv.getProperty("com.linecorp.channelSecret");
    }
    
    @Bean(name="com.linecorp.channelToken")
    public String getChannelToken()
    {
        return mEnv.getProperty("com.linecorp.channelToken");
    }
    
    @Bean
    public LineMessagingService getLineMessagingService()
    {
        return LineMessagingServiceBuilder.create(getChannelToken()).build();
    }
    
    @Bean
    public LineSignatureValidator getLineSignatureValidator()
    {
        return new LineSignatureValidator(getChannelSecret().getBytes());
    }
};

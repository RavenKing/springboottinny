package tinyworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

/**
 * Hello world!
 *
 */
@EnableConfigurationProperties
@SpringBootApplication
//@EnableJpaRepositories({ "com.sap.cp.core" })
@EntityScan({ "tinyworld" })
@ComponentScan({ "tinyworld" })
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class SimpleApplication extends SpringBootServletInitializer
{
	private static Class<SimpleApplication> applicationClass = SimpleApplication.class;

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(applicationClass);
	}

	public static void main(String[] args) {
		SpringApplication.run(SimpleApplication.class, args);
	}}
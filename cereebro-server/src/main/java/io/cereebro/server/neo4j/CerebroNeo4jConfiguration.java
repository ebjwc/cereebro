package io.cereebro.server.neo4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.cereebro.core.SystemResolver;
import io.cereebro.server.CereebroServerConfiguration;
import io.cereebro.server.neo4j.repository.MicroserviceDependencyRepository;
import io.cereebro.server.neo4j.repository.MicroserviceRepository;

@Configuration
@AutoConfigureBefore(CereebroServerConfiguration.class)
@ConditionalOnProperty("spring.data.neo4j.uri")
public class CerebroNeo4jConfiguration {
	
	@Value("${daivb.admin.dependency.component.filter:}")
	private String componentFilter;
	
	@Bean
	public MicroserviceDependencyService microserviceDependencyService(MicroserviceRepository microserviceRepository,MicroserviceDependencyRepository microserviceDependencyRepository ) {
		return new MicroserviceDependencyService(microserviceRepository,microserviceDependencyRepository);
	}
	
	@Bean("systemResolver")
    public SystemResolver systemResolver(MicroserviceDependencyService microserviceDependencyService) {
        return new Neo4jCerebroSystemResolver(microserviceDependencyService,componentFilter);
    }

}

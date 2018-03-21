/*
 * Copyright © 2017 the original authors (http://cereebro.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package io.cereebro.server.neo4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

import io.cereebro.core.SystemResolver;
import io.cereebro.server.CereebroServerConfiguration;
import io.cereebro.server.CereebroServerProperties;
import io.cereebro.server.neo4j.repository.MicroserviceDependencyRepository;
import io.cereebro.server.neo4j.repository.MicroserviceRepository;

@Configuration
@AutoConfigureBefore(CereebroServerConfiguration.class)
@ConditionalOnProperty("spring.data.neo4j.uri")
@EnableNeo4jRepositories(basePackages = "io.cereebro.server.neo4j.repository")
@EntityScan(basePackages = "io.cereebro.server.neo4j.model")
public class CerebroNeo4jConfiguration {
	
	
	@Bean
	public MicroserviceDependencyService microserviceDependencyService(MicroserviceRepository microserviceRepository,MicroserviceDependencyRepository microserviceDependencyRepository ) {
		return new MicroserviceDependencyService(microserviceRepository,microserviceDependencyRepository);
	}
	
	@Bean("systemResolver")
    public SystemResolver systemResolver(MicroserviceDependencyService microserviceDependencyService) {
        return new Neo4jCerebroSystemResolver(microserviceDependencyService);
    }

}

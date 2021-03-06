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
package io.cereebro.snitch.detect.neo4j;

import java.util.List;

import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.cereebro.snitch.detect.ConditionalOnEnabledDetector;
import io.cereebro.snitch.detect.Detectors;

@Configuration
@ConditionalOnClass(value = { Session.class })
@ConditionalOnEnabledDetector(Neo4jRelationshipDetectorAutoConfiguration.PROP)
public class Neo4jRelationshipDetectorAutoConfiguration {

    static final String PROP = "neo4j";

    @Autowired(required = false)
    private List<Session> neo4jSessions;

    @Bean
    @ConfigurationProperties(prefix = Detectors.PREFIX + "." + PROP)
    public Neo4jRelationshipDetector neo4jDetector() {
        return new Neo4jRelationshipDetector(neo4jSessions);
    }

}

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
package io.cereebro.snitch.detect.jms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import io.cereebro.core.BaseRelationshipDetector;
import io.cereebro.core.Component;
import io.cereebro.core.ComponentType;
import io.cereebro.core.Dependency;
import io.cereebro.core.Relationship;
import io.cereebro.core.RelationshipDetector;

/**
 * Jms relationship detector based on Spring's {@link ConnectionFactory}
 * abstraction.
 * To import topic, queue or connection settings you can configure environment settings which will 
 * be taken over into the relation by defining settings below in application.yml
 * It's a map that export a key to cereebro snwitch > server with the value from the following SPRING SPEL expression extracted from environment 
 * cereebro.application.snitch.detect.jms.exportkeys:
 *           topic: ${daivb.core.event.publisher.jms.destination.prefix:''}${daivb.core.event.publisher.jms.destination.topic}
 *           queue: ${daivb.core.event.publisher.jms.destination.queue} * 
 * 
 * @author fwallwit
 */
public class JmsRelationshipDetector extends BaseRelationshipDetector  implements RelationshipDetector {

    private final List<JmsTemplate> jmsTemplates;

    /**
     * * RabbitMQ relationship detector based on Spring's
     * {@link ConnectionFactory} abstraction. Note that any component connected
     * to RabbitMQ will have a dependency on the broker : Both the "producer"
     * and "consumer" (MQ) applications have a dependency on RabbitMQ.
     * 
     * @param jmsTemplates
     *            All RabbitMQ connections available.
     */
    public JmsRelationshipDetector(List<JmsTemplate> jmsTemplates) {
        this.jmsTemplates = new ArrayList<>();
        if (jmsTemplates != null) {
        	jmsTemplates.addAll(jmsTemplates);
        }
    }

    @Override
    public Set<Relationship> detect() {
        if (jmsTemplates.isEmpty()) {
            return Collections.emptySet();
        }
        return jmsTemplates.stream().map(factory -> createJmsDependency(factory.getDefaultDestinationName()))
                .collect(Collectors.toSet());
    }

    /**
     * Create a dependency on a RabbitMQ component with a given name.
     * 
     * @param name
     *            Component name.
     * @return Dependency on a component with RabbitMQ type.
     */
    private Dependency createJmsDependency(String name) {
        return Dependency.on(Component.of(name, ComponentType.JMS));
    }

}

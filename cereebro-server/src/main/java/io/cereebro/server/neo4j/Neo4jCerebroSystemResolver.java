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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Value;

import io.cereebro.core.Component;
import io.cereebro.core.ComponentRelationships;
import io.cereebro.core.ComponentRelationships.ComponentRelationshipsBuilder;
import io.cereebro.core.Consumer;
import io.cereebro.core.Dependency;
import io.cereebro.core.ResolutionError;
import io.cereebro.core.SimpleSystemResolver;
import io.cereebro.core.Snitch;
import io.cereebro.core.SnitchRegistry;
import io.cereebro.core.SnitchingException;
import io.cereebro.core.System;
import io.cereebro.core.System.SystemBuilder;
import io.cereebro.core.SystemFragment;
import io.cereebro.server.neo4j.model.CerebroComponent;
import io.cereebro.server.neo4j.model.CerebroDependency;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Neo4jCerebroSystemResolver extends SimpleSystemResolver {

	private static final String DEFAULT_ERROR_MESSAGE = "Could not access or process Snitch";

	private final MicroserviceDependencyService microserviceDependencyService;

	private final Pattern pattern;

	@Value("cereebro.server.system.component.filter")
	private String componentFilter;

	public Neo4jCerebroSystemResolver(MicroserviceDependencyService microserviceDependencyService) {
		this.microserviceDependencyService = microserviceDependencyService;
		if (componentFilter!=null)
			pattern = Pattern.compile(componentFilter);
		else pattern = null;

	}

	private boolean isNeo4jActive() {
		return microserviceDependencyService != null;
	}

	@Override
	public System resolve(String systemName, SnitchRegistry snitchRegistry) {
		Set<SystemFragment> frags = new HashSet<>();
		Set<ResolutionError> errors = new HashSet<>();
		for (Snitch snitch : snitchRegistry.getAll()) {
			try {
				frags.add(snitch.snitch());
			} catch (SnitchingException e) {
				LOGGER.error("Snitch error caught while resolving system at URI : " + e.getSnitchUri(), e);
				errors.add(ResolutionError.translate(e));
			} catch (RuntimeException e) {
				LOGGER.error("Technical error caught while resolving system at URI : " + snitch.getUri(), e);
				errors.add(ResolutionError.of(snitch.getUri(), DEFAULT_ERROR_MESSAGE));
			}
		}
		return resolveBuilder(systemName, frags).errors(errors).build();
	}

	@Override
	public System resolve(String systemName, Collection<SystemFragment> fragments) {
		return resolveBuilder(systemName, fragments).build();
	}

	/**
	 * Resolves the system and returns a mutable {@link SystemBuilder} instance.
	 * 
	 * @param systemName
	 *            System name.
	 * @param fragments
	 *            System fragments.
	 * @return mutable SystemBuilder ready to be built into the real System.
	 */
	private SystemBuilder resolveBuilder(String systemName, Collection<SystemFragment> fragments) {

		// we can't accept remaining (orphaned) nodes
		if (isNeo4jActive())
			microserviceDependencyService.emptyDb();

		// Flatten all fragments into a single relationship set
		Set<ComponentRelationships> relationships = fragments.stream().map(SystemFragment::getComponentRelationships)
				.flatMap(Collection::stream).collect(Collectors.toSet());

		Map<Component, ComponentRelationshipsBuilder> map = new HashMap<>();

		// Browse all relationships to complete them with one another
		for (ComponentRelationships rel : relationships) {
			if (pattern!=null && pattern.matcher(rel.getComponent().getName()).matches())
				continue;

			ComponentRelationshipsBuilder cerebroCompRelBuilder = getOrCreate(map, rel.getComponent());

			// Add the current component as a producer of its consumers
			cerebroCompRelBuilder.addDependencies(rel.getDependencies());
			for (Dependency cerebProducer : rel.getDependencies()) {
				ComponentRelationshipsBuilder dependencyBuilder = getOrCreate(map, cerebProducer.getComponent());
				dependencyBuilder.addConsumer(Consumer.by(rel.getComponent()));
			}

			// Add the current component as a consumer of its producers
			cerebroCompRelBuilder.addConsumers(rel.getConsumers());
			for (Consumer consumer : rel.getConsumers()) {
				ComponentRelationshipsBuilder consumerBuilder = getOrCreate(map, consumer.getComponent());
				consumerBuilder.addDependency(Dependency.on(rel.getComponent()));
			}

			if (isNeo4jActive()) {
				CerebroComponent consumer = microserviceDependencyService.findComponent(rel.getComponent().getName());

				// link dependencies
				rel.getDependencies().forEach(dependencyNode -> {
					if (dependencyNode.getComponent().getName() == null) {
						LOGGER.error("cerebro component name is not set -" + dependencyNode.getComponent().toString());
					} else {
						CerebroComponent producer = microserviceDependencyService
								.findComponent(dependencyNode.getComponent().getName());
						if (producer == null) {
							CerebroComponent cerebroComponent = microserviceDependencyService.createOrSaveComponent(
									new CerebroComponent(dependencyNode.getComponent().getName(), splitTypesIntoSet(dependencyNode.getComponent().getType())));
														
							LOGGER.debug("Created node " + cerebroComponent.getName() + " of type "
									+ cerebroComponent.getType());
						}

						CerebroDependency dep = new CerebroDependency(consumer, producer,
								splitTypesIntoSet(dependencyNode.getComponent().getType()));
						microserviceDependencyService.createOrSaveDependency(dep);
						LOGGER.debug("Created neo4j dependency " + producer.getName() + " ->" + consumer.getName()
								+ " of type " + dep.getType());
					}

				});
			}
		}

		// @formatter:off
		Set<ComponentRelationships> bigPicture = map.values().stream().map(ComponentRelationshipsBuilder::build)
				.collect(Collectors.toSet());
		// @formatter:on

		return io.cereebro.core.System.builder().name(systemName).componentRelationships(bigPicture);
	}

	private Set<String> splitTypesIntoSet(@NotNull String str) {
		HashSet<String> myHashSet = new HashSet<String>(3); // Or a more realistic size
		StringTokenizer st = new StringTokenizer(str, "/");
		while (st.hasMoreTokens())
			myHashSet.add(st.nextToken());
		return myHashSet;
	}

	/**
	 * Create a ComponentRelationshipsBuilder if one didn't already exist in the
	 * map.
	 * 
	 * @param map
	 * @param component
	 * @return ComponentRelationshipsBuilder
	 */
	private ComponentRelationshipsBuilder getOrCreate(Map<Component, ComponentRelationshipsBuilder> map,
			Component component) {
		ComponentRelationshipsBuilder builder = map.get(component);
		if (builder == null) {
			builder = ComponentRelationships.builder().component(component);
			map.put(component, builder);

			if (isNeo4jActive()) {
				CerebroComponent cerebroComponent = microserviceDependencyService
						.createOrSaveComponent(new CerebroComponent(component.getName(), splitTypesIntoSet(component.getType())));
				LOGGER.debug("Created node " + cerebroComponent.getName() + " of type " + cerebroComponent.getType());
			}
		}
		return builder;
	}

}

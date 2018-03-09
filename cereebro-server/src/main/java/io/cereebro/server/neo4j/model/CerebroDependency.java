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
package io.cereebro.server.neo4j.model;

import java.util.Set;

import javax.annotation.Nonnull;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;


@RelationshipEntity(type = "dependency")
public class CerebroDependency {
	@GraphId
	private Long id;

	@StartNode
	CerebroComponent consumer;
	@EndNode
	CerebroComponent producer;
	
	@Index(unique = false, primary= false)
	private Set<String> type;

	public CerebroDependency() {
		super();
	}

	public CerebroDependency(@Nonnull CerebroComponent consumer, @Nonnull CerebroComponent producer, @Nonnull Set<String> type) {
		super();
		this.consumer = consumer;
		this.producer = producer;
		this.type = type;
	}

	public Set<String> getType() {
		return type;
	}

	public void addType(String type) {
		this.type.add(type);
	}
}

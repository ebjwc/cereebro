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

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;


@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@NodeEntity
public class MicroserviceNode {

	public MicroserviceNode() {
		// default
	}
	
	public MicroserviceNode(String springApplicationId, String springApplicationName, Set<MicroserviceNode> consumes) {
		super();
		this.springApplicationId = springApplicationId;
		this.springApplicationName = springApplicationName;
		this.consumes = consumes;
	}

	@GraphId
	private Long id;

	/**
	 * Application short name read from ${spring.application.id}.
	 */
	private String springApplicationId;

	/**
	 * Application name read from ${spring.application.name}.
	 */
	private String springApplicationName;

    /**
     * List of APIs current microservice consumes.
     */
	@Relationship(type = "consumes", direction = Relationship.OUTGOING)
    private Set<MicroserviceNode> consumes;
	
	
	public String getSpringApplicationId() {
		return springApplicationId;
	}


	public void setSpringApplicationId(String springApplicationId) {
		this.springApplicationId = springApplicationId;
	}


	public String getSpringApplicationName() {
		return springApplicationName;
	}


	public void setSpringApplicationName(String springApplicationName) {
		this.springApplicationName = springApplicationName;
	}


	public Set<MicroserviceNode> getConsumes() {
		return consumes;
	}

	public void dependsOn(MicroserviceNode microserviceNode) {
		if (consumes == null) {
			consumes = new HashSet<>();
		}
		consumes.add(microserviceNode);
	}	

	public void setConsumes(Set<MicroserviceNode> consumes) {
		this.consumes = consumes;
	}


	public MicroserviceNode(String springApplicationId, String springApplicationName) {

		this.springApplicationId = springApplicationId;
		this.springApplicationName = springApplicationName;
	}
}
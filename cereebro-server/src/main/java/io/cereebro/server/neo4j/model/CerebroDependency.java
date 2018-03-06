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

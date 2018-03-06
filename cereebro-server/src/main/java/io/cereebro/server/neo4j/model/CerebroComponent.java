package io.cereebro.server.neo4j.model;

import java.util.Objects;
import java.util.Set;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity(label="component")
public class CerebroComponent {

	@GraphId
	private Long id;

	@Index(unique = true, primary= true)
	private String name;

	@Index(unique = false, primary= false)
	private Set<String> type;

	public CerebroComponent() {
		super();
	}

	public CerebroComponent(String name, Set<String> type) {
		this.name = Objects.requireNonNull(name, "Component name required");
		this.type = Objects.requireNonNull(type, "Component type required");
	}

	/**
	 * Component name.
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Component type.
	 * 
	 * @return type
	 */
	public Set<String> getType() {
		return type;
	}
	
	public void addType(String type) {
		this.type.add(type);
	}

}

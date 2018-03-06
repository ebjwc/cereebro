package io.cereebro.server.neo4j.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import io.cereebro.server.neo4j.model.CerebroDependency;


//@Repository
public interface MicroserviceDependencyRepository extends Neo4jRepository<CerebroDependency, Long> {
	
	CerebroDependency findByType(@Param("type") String name);
	CerebroDependency findById(@Param("id") Long id);

}
package io.cereebro.server.neo4j.repository;



import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import io.cereebro.server.neo4j.model.CerebroComponent;


//@Repository
public interface MicroserviceRepository extends Neo4jRepository<CerebroComponent,Long> {
	CerebroComponent findById(@Param("id") Long id);
	CerebroComponent findByName(@Param("name") String name);
	CerebroComponent findByType(@Param("type") String name);
}
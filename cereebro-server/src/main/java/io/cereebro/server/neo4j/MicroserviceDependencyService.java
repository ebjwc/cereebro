package io.cereebro.server.neo4j;

import org.springframework.transaction.annotation.Transactional;

import io.cereebro.server.neo4j.model.CerebroComponent;
import io.cereebro.server.neo4j.model.CerebroDependency;
import io.cereebro.server.neo4j.repository.MicroserviceDependencyRepository;
import io.cereebro.server.neo4j.repository.MicroserviceRepository;


public class MicroserviceDependencyService {

	private MicroserviceRepository microserviceRepository;
	private MicroserviceDependencyRepository microserviceDependencyRepository;

	// @Autowired
	// private Neo4jTransactionManager neo4jTransactionManager;
	//
	public MicroserviceDependencyService(MicroserviceRepository microserviceRepository, MicroserviceDependencyRepository microserviceDependencyRepository) {
		this.microserviceRepository = microserviceRepository;
		this.microserviceDependencyRepository = microserviceDependencyRepository;
	}

	@Transactional
	public CerebroComponent createOrSaveComponent(CerebroComponent component) {
		return microserviceRepository.save(component);
	}
	
	public CerebroComponent findComponent(String name) {
		return microserviceRepository.findByName(name);
	}
	
	public CerebroComponent findComponent(Long id) {
		return microserviceRepository.findById(id);
	}
	
	
	@Transactional
	public CerebroDependency createOrSaveDependency(CerebroDependency dep) {
		return microserviceDependencyRepository.save(dep);
	}
	
	public CerebroDependency findDependencyByType(String type) {
		return microserviceDependencyRepository.findByType(type);
	}
	
	public void emptyDb() {
		microserviceRepository.deleteAll();
		microserviceDependencyRepository.deleteAll();
	}


}
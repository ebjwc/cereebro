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

import javax.validation.constraints.NotNull;

import org.springframework.transaction.annotation.Transactional;

import io.cereebro.server.neo4j.model.CerebroComponent;
import io.cereebro.server.neo4j.model.CerebroDependency;
import io.cereebro.server.neo4j.repository.MicroserviceDependencyRepository;
import io.cereebro.server.neo4j.repository.MicroserviceRepository;


public class MicroserviceDependencyService {

	private MicroserviceRepository microserviceRepository;
	private MicroserviceDependencyRepository microserviceDependencyRepository;

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
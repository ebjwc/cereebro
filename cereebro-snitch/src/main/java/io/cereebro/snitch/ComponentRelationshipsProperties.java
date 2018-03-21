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
package io.cereebro.snitch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.cereebro.core.Consumer;
import io.cereebro.core.Dependency;
import io.cereebro.core.Relationship;
import lombok.Data;

@Data
public final class ComponentRelationshipsProperties {

    private ComponentProperties component = new ComponentProperties();
    private Map<String,String> exportkeys = new HashMap<String,String>();
    private List<DependencyProperties> dependencies = new ArrayList<>();
    private List<ConsumerProperties> consumers = new ArrayList<>();

    public Set<Dependency> dependencies() {
        return dependencies.stream().map(DependencyProperties::toDependency).collect(Collectors.toSet());
    }

    public Set<Consumer> consumers() {
        return consumers.stream().map(ConsumerProperties::toConsumer).collect(Collectors.toSet());
    }

    public Set<Relationship> getRelationships() {
        return Stream.concat(dependencies().stream(), consumers().stream()).collect(Collectors.toSet());
    }
    
    public Map<String,String> getExportkeys() {
    	return exportkeys;
    }
    

}

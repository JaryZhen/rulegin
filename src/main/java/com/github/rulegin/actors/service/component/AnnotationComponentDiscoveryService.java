/**
 * Copyright © 2016-2017 The Thingsboard Authors
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
package com.github.rulegin.actors.service.component;

import com.github.rulegin.common.data.component.ComponentType;
import com.github.rulegin.common.data.component.DefineComponent;
import com.github.rulegin.dao.components.DefineComponentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;
import com.github.rulegin.common.component.Action;
import com.github.rulegin.common.component.Rule;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.util.*;

@Service
@Slf4j
public class AnnotationComponentDiscoveryService implements ComponentDiscoveryService {

    @Value("${plugins.scan_packages}")
    private String[] scanPackages;

    @Autowired
    private Environment environment;


    @Autowired
    private DefineComponentService defineComponentService;

    private Map<String, DefineComponent> defineComponents = new HashMap<>();

    private Map<ComponentType, List<DefineComponent>> deComponentsMap = new HashMap<>();

    private ObjectMapper mapper = new ObjectMapper();

    private boolean isInstall() {
        return environment.acceptsProfiles("install");
    }

    @PostConstruct
    public void init() {
        log.info("java Initializing..."+this.getClass().getName());
        log.info("scanPackages = {}",scanPackages.toString());
        boolean isinstall =isInstall();
        log.info("is install = : {}",isinstall);
        if (true) {
            discoverComponents();
        }
    }

    private void registerComponents(ComponentType type, Class<? extends Annotation> annotation) {
        //List<ComponentDescriptor> components = persist(getBeanDefinitions(annotation), type);
        List<DefineComponent> components = persistD(getBeanDefinitions(annotation),type);
        deComponentsMap.put(type, components);
        registerComponents(components);
    }

    private void registerComponents(Collection<DefineComponent> comps) {
        comps.forEach(c -> defineComponents.put(c.getClazz(), c));
    }

    private List<DefineComponent> persistD(Set<BeanDefinition> filterDefs, ComponentType type) {
            List<DefineComponent> result = new ArrayList<>();
            for (BeanDefinition def : filterDefs) {
                DefineComponent component = new DefineComponent();
                String clazzName = def.getBeanClassName();
                try {
                    component.setType(type);
                    Class<?> clazz = Class.forName(clazzName);
                    switch (type) {
                        case RULE:
                            Rule rule = clazz.getAnnotation(Rule.class);
                            component.setName(rule.name());
                            component.setScope(rule.scope());
                            component.setDataSourceType(rule.dataSourceType());
                            component.setFiltersType(rule.filtersType());
                            component.setActionsType(rule.actionsType());
                            break;
                        case ACTION:
                            Action actionAnnotation = clazz.getAnnotation(Action.class);
                            component.setName(actionAnnotation.name());
                            component.setScope(actionAnnotation.scope());
                            break;
                        default:
                            throw new RuntimeException(type + " is not supported yet!");
                    }
                    //component.setConfigurationDescriptor(mapper.readTree(Resources.toString(Resources.getResource(descriptorResourceName), Charsets.UTF_8)));
                    component.setClazz(clazzName);
                    log.info("Processing scanned component: {}", component);
                } catch (Exception e) {
                    log.error("Can't initialize component {}, due to {}", def.getBeanClassName(), e.getMessage(), e);
                    throw new RuntimeException(e);
                }
                DefineComponent persistedComponent = defineComponentService.findByClazz(clazzName);

                if (persistedComponent == null) {
                    log.info("Persisting new component: {}", component);
                    component = defineComponentService.saveComponent(component);
                } else if (component.equals(persistedComponent)) {
                    log.info("Component is already persisted: {}", persistedComponent);
                    log.info("");
                    component = persistedComponent;
                } else {
                    log.info("Component {} will be updated to {}", persistedComponent, component);
                    defineComponentService.deleteByClazz(persistedComponent.getClazz());
                    component.setId(persistedComponent.getId());
                    component = defineComponentService.saveComponent(component);
                }
                result.add(component);
            }
            return result;
    }

    private Set<BeanDefinition> getBeanDefinitions(Class<? extends Annotation> componentType) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(componentType));
        Set<BeanDefinition> defs = new HashSet<>();
        for (String scanPackage : scanPackages) {
            defs.addAll(scanner.findCandidateComponents(scanPackage));
        }
        return defs;
    }

    @Override
    public void discoverComponents() {
        registerComponents(ComponentType.RULE, Rule.class);

        registerComponents(ComponentType.ACTION, Action.class);

        log.info("Found following definitions: {}", defineComponents.values());
    }

    @Override
    public List<DefineComponent> getComponents(ComponentType type) {
        return Collections.unmodifiableList(deComponentsMap.get(type));
    }

    @Override
    public Optional<DefineComponent> getComponent(String clazz) {
        return Optional.ofNullable(defineComponents.get(clazz));
    }

    @Override
    public Optional<DefineComponent> getDefineComponent(String clazz) {
        return Optional.of(defineComponents.get(clazz));
    }

    @Override
    public List<DefineComponent> getPluginActions(String pluginClazz) {
        Optional<DefineComponent> pluginOpt = getComponent(pluginClazz);
        if (pluginOpt.isPresent()) {
            DefineComponent plugin = pluginOpt.get();
            if (ComponentType.PLUGIN != plugin.getType()) {
                throw new IllegalArgumentException(pluginClazz + " is not a plugin!");
            }
            List<DefineComponent> result = new ArrayList<>();
            for (String action : plugin.getActionsType().split(",")) {
                getComponent(action).ifPresent(v -> result.add(v));
            }
            return result;
        } else {
            throw new IllegalArgumentException(pluginClazz + " is not a component!");
        }
    }
}

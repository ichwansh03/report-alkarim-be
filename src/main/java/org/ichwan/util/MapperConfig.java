package org.ichwan.util;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class MapperConfig {

    private ModelMapper modelMapper;

    @PostConstruct
    public void init() {
        this.modelMapper = new ModelMapper();

        // Configuration
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true)
                .setAmbiguityIgnored(false);

        // Custom mappings
        configureCustomMappings();
    }

    private void configureCustomMappings() {
        // Add custom mappings here jika diperlukan
        // Example:
        // modelMapper.typeMap(User.class, UserDTO.class)
        //     .addMapping(User::getFullName, UserDTO::setName);
    }

    /**
     * Map single entity to DTO
     */
    public <D, E> D map(E entity, Class<D> dtoClass) {
        if (entity == null) {
            return null;
        }
        return modelMapper.map(entity, dtoClass);
    }

    /**
     * Map list of entities to list of DTOs
     */
    public <D, E> List<D> mapList(List<E> entities, Class<D> dtoClass) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(entity -> map(entity, dtoClass))
                .collect(Collectors.toList());
    }

    /**
     * Map DTO back to entity
     */
    public <D, E> E mapToEntity(D dto, Class<E> entityClass) {
        if (dto == null) {
            return null;
        }
        return modelMapper.map(dto, entityClass);
    }

    /**
     * Update existing entity with DTO data (for PATCH operations)
     */
    public <D, E> void mapToExistingEntity(D dto, E entity) {
        if (dto != null && entity != null) {
            modelMapper.map(dto, entity);
        }
    }

    /**
     * Get ModelMapper instance untuk advanced usage
     */
    public ModelMapper getModelMapper() {
        return modelMapper;
    }

}

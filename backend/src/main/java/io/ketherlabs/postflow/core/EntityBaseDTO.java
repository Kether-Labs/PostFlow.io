package io.ketherlabs.postflow.core;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class EntityBaseDTO {
    protected UUID id;
    protected LocalDateTime createdOn;
    protected LocalDateTime lastUpdateOn;
    protected short status;

    public EntityBaseDTO() {
        super();
        this.createdOn = LocalDateTime.now();
    }

    public EntityBaseDTO(EntityBase entityBase) {
        this.id = entityBase.getId();
        this.createdOn = entityBase.getCreatedOn();
        this.lastUpdateOn = entityBase.getLastUpdateOn();
    }

    public EntityBaseDTO(UUID id, LocalDateTime lastUpdateOn) {
        this.id = id;
        this.lastUpdateOn = lastUpdateOn;
    }

    public EntityBaseDTO(UUID id) {
        this.id = id;
    }

}

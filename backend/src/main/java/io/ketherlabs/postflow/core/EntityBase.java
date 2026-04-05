package io.ketherlabs.postflow.core;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;


@MappedSuperclass
@Data
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class EntityBase {


    @Id
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    protected UUID id;

    @Column(name = "created_on")
    @Temporal(TemporalType.TIMESTAMP)
    protected LocalDateTime createdOn;

    @Column(name = "last_update_on")
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    protected LocalDateTime lastUpdateOn;

    @Column(name = "deleted", columnDefinition = "boolean default false")
    protected boolean deleted;

    @Column(name = "deleted_at")
    @LastModifiedDate
    protected LocalDateTime deletedAt;

    @Basic(optional = false)
    @Column(name = "status", nullable = false)
    protected short status;

    public EntityBase(UUID id) {
        this.id = id;
    }

    public EntityBase(UUID id, LocalDateTime createdOn) {
        this.id = id;
        this.createdOn = createdOn;
    }

    public EntityBase(EntityBaseDTO entityBaseDTO) {
        this.id = entityBaseDTO.getId();
        this.createdOn = entityBaseDTO.getCreatedOn();
        this.lastUpdateOn = entityBaseDTO.getLastUpdateOn();
        this.status = entityBaseDTO.getStatus();
    }

    public static EntityBaseDTO fromEntityBase(EntityBase entity) {
        return new EntityBaseDTO(entity);
    }

}

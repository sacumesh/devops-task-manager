package com.dsti.devops_task_manager.repositories;

import com.dsti.devops_task_manager.entities.TaskEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<@NonNull TaskEntity, @NonNull Long> {

    @NonNull
    @Override
    Optional<@NonNull TaskEntity> findById(@NonNull Long aLong);


}

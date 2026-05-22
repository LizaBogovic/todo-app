package com.example.demo.repository;

import com.example.demo.mapper.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {
    //@Query("select ")

    @Query("select h from TaskEntity h") //HQL JPQL
 //@Query(nativeQuery = true, value = "select * from task")
    List<TaskEntity> getAllTasks();
}

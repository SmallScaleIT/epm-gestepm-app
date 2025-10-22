package com.epm.gestepm.model.family.dao;

import org.springframework.data.repository.CrudRepository;

import com.epm.gestepm.modelapi.deprecated.family.dto.Family;

import java.util.List;

public interface FamilyRepository extends CrudRepository<Family, Long>, FamilyRepositoryCustom {
    List<Family> findByProjectId(Long id);
}

package com.epm.gestepm.model.family.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.epm.gestepm.modelapi.common.utils.datatables.PaginationCriteria;
import com.epm.gestepm.modelapi.deprecated.family.dto.FamilyDTO;
import com.epm.gestepm.modelapi.deprecated.family.dto.FamilyTableDTO;
import org.springframework.stereotype.Repository;

import com.epm.gestepm.modelapi.deprecated.family.dto.Family;
import com.epm.gestepm.modelapi.deprecated.project.dto.Project;
import com.epm.gestepm.modelapi.common.utils.datatables.util.DataTableUtil;

@Repository
public class FamilyRepositoryImpl implements FamilyRepositoryCustom {

	@PersistenceContext	
	private EntityManager entityManager;
	
	public Family findFamilyByName(String name) {
		
		try {

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Family> cQuery = cb.createQuery(Family.class);

			Root<Family> root = cQuery.from(Family.class);

			cQuery.select(root)
				  .where(cb.and(cb.or(cb.equal(root.get("common"), 1), cb.equal(root.get("common"), 2)), cb.or(cb.equal(root.get("nameES"), name), cb.equal(root.get("nameFR"), name))));
			
			return entityManager.createQuery(cQuery).getSingleResult();

		} catch (Exception e) {
			return null;
		}
	}
	
	public List<FamilyTableDTO> findFamilyTableDTOs(PaginationCriteria pagination) {
		
		try {

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<FamilyTableDTO> cq = cb.createQuery(FamilyTableDTO.class);

			/* #BASE_QUERY */
			
			Root<Family> root = cq.from(Family.class);

			List<Predicate> predicates = new ArrayList<>();

			cq.multiselect(root.get("id"), root.get("nameES"), root.get("nameFR"), root.get("brand"), root.get("model"), root.get("enrollment"));

			Predicate whereCondition = cb.or(cb.equal(root.get("common"), 1), cb.equal(root.get("common"), 2));
			predicates.add(whereCondition);
			
			/* END #BASE_QUERY */
			
			/* #WHERE_CLAUSE */
			Predicate whereFilter = DataTableUtil.generateWhereCondition(pagination, cb, root);
				
			if (whereFilter != null) {
				predicates.add(whereFilter);
			}
			/* END #WHERE_CLAUSE */
			
			/* #ORDER_CLAUSE */
			List<Order> orderList = DataTableUtil.generateOrderByCondition(pagination, cb, root);
			
			if(!orderList.isEmpty()) {
				cq.orderBy(orderList);
			}
			/* END #ORDER_CLAUSE */
			
			// Appending all Predicates
			cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		
			TypedQuery<FamilyTableDTO> criteriaQuery = entityManager.createQuery(cq);

			/* #PAGE_NUMBER */
			criteriaQuery.setFirstResult(pagination.getPageNumber());
			/* END #PAGE_NUMBER */
			
			/* #PAGE_SIZE */
			criteriaQuery.setMaxResults(pagination.getPageSize());
			/* END #PAGE_SIZE */
			
			return criteriaQuery.getResultList();

		} catch (Exception e) {
			return Collections.emptyList();
		}
	}
	
	public Long findFamiliesCount() {
		
		try {
			
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			
			Root<Family> root = cq.from(Family.class);
			
			cq.select(cb.count(root));
			cq.where(cb.or(cb.equal(root.get("common"), 1), cb.equal(root.get("common"), 2)));
			
			return entityManager.createQuery(cq).getSingleResult();
			
		} catch (Exception e) {
			return 0L;
		}
	}
	
	@Override
	public List<FamilyTableDTO> findFamiliesDataTablesByProjectId(Long projectId, PaginationCriteria pagination) {
		
		try {

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<FamilyTableDTO> cq = cb.createQuery(FamilyTableDTO.class);

			/* #BASE_QUERY */
			Root<Project> prRoot = cq.from(Project.class);
			Join<Family, Project> diRoot = prRoot.join("families", JoinType.INNER);
			
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(cb.equal(prRoot.get("id"), projectId));
			
			cq.multiselect(diRoot.get("id"), diRoot.get("nameES"), diRoot.get("nameFR"), diRoot.get("brand"), diRoot.get("model"), diRoot.get("enrollment"));
			
			/* END #BASE_QUERY */
			
			/* #WHERE_CLAUSE */
			Predicate whereFilter = DataTableUtil.generateWhereCondition(pagination, cb, diRoot);
				
			if (whereFilter != null) {
				predicates.add(whereFilter);
			}
			/* END #WHERE_CLAUSE */
			
			/* #ORDER_CLAUSE */
			List<Order> orderList = DataTableUtil.generateOrderByCondition(pagination, cb, diRoot);
			
			if(!orderList.isEmpty()) {
				cq.orderBy(orderList);
			}
			/* END #ORDER_CLAUSE */
			
			// Appending all Predicates
			cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
		
			TypedQuery<FamilyTableDTO> criteriaQuery = entityManager.createQuery(cq);

			/* #PAGE_NUMBER */
			criteriaQuery.setFirstResult(pagination.getPageNumber());
			/* END #PAGE_NUMBER */
			
			/* #PAGE_SIZE */
			criteriaQuery.setMaxResults(pagination.getPageSize());
			/* END #PAGE_SIZE */
			
			return criteriaQuery.getResultList();

		} catch (Exception e) {
			return Collections.emptyList();
		}
	}
	
	@Override
	public Long findFamiliesCountByProjectId(Long projectId) {
		
		try {

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);

			Root<Project> prRoot = cq.from(Project.class);
			Join<Family, Project> diRoot = prRoot.join("families", JoinType.INNER);

			cq.select(cb.count(diRoot));
			cq.where(cb.equal(prRoot.get("id"), projectId));

			return entityManager.createQuery(cq).getSingleResult();

		} catch (Exception e) {
			return 0L;
		}
	}
	
	@Override
	public List<FamilyDTO> findCommonFamilyDTOsByProjectId(Long projectId, Locale locale) {

		try {

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<FamilyDTO> cQuery = cb.createQuery(FamilyDTO.class);

			Root<Family> root = cQuery.from(Family.class);

			cQuery.multiselect(root.get("id"), root.get("nameES"), root.get("nameFR"), root.get("brand"), root.get("model"), root.get("enrollment"))
				  .where(cb.or(cb.equal(root.get("common"), 1), cb.equal(root.get("project"), projectId)));
			
			if ("es".equals(locale.getLanguage())) {
				cQuery.orderBy(cb.asc(root.get("nameES")));
			} else {
				cQuery.orderBy(cb.asc(root.get("nameFR")));
			}
			
			return entityManager.createQuery(cQuery).getResultList();

		} catch (Exception e) {
			return Collections.emptyList();
		}
	}
	
	@Override
	public List<FamilyDTO> findCustomFamilyDTOsByProjectId(Long projectId) {
		
		try {

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<FamilyDTO> cQuery = cb.createQuery(FamilyDTO.class);

			Root<Family> root = cQuery.from(Family.class);

			cQuery.multiselect(root.get("id"), root.get("family").get("id"), root.get("nameES"), root.get("nameFR"), root.get("brand"), root.get("model"), root.get("enrollment"))
				  .where(cb.and(cb.equal(root.get("common"), 3), cb.equal(root.get("project"), projectId)));
			
			return entityManager.createQuery(cQuery).getResultList();

		} catch (Exception e) {
			return Collections.emptyList();
		}
	}
	
	@Override
	public List<FamilyDTO> findClonableFamilyDTOs(Locale locale) {
		
		try {

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<FamilyDTO> cQuery = cb.createQuery(FamilyDTO.class);

			Root<Family> root = cQuery.from(Family.class);

			cQuery.multiselect(root.get("id"), root.get("nameES"), root.get("nameFR"))
				  .where(cb.or(cb.equal(root.get("common"), 1), cb.equal(root.get("common"), 2)));
			
			if ("es".equals(locale.getLanguage())) {
				cQuery.orderBy(cb.asc(root.get("nameES")));
			} else {
				cQuery.orderBy(cb.asc(root.get("nameFR")));
			}

			return entityManager.createQuery(cQuery).getResultList();

		} catch (Exception e) {
			return Collections.emptyList();
		}
	}
}

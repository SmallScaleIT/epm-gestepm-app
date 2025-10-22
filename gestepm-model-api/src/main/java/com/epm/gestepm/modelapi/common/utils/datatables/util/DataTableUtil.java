package com.epm.gestepm.modelapi.common.utils.datatables.util;

import com.epm.gestepm.modelapi.deprecated.activitycenter.dto.ActivityCenter;
import com.epm.gestepm.modelapi.common.utils.datatables.PaginationCriteria;
import com.epm.gestepm.modelapi.common.utils.datatables.SortOrder;
import com.epm.gestepm.modelapi.deprecated.country.dto.Country;
import com.epm.gestepm.modelapi.deprecated.displacementshare.dto.DisplacementShare;
import com.epm.gestepm.modelapi.deprecated.expense.dto.Expense;
import com.epm.gestepm.modelapi.expensecorrective.dto.ExpenseCorrective;
import com.epm.gestepm.modelapi.deprecated.expensesheet.dto.ExpenseSheet;
import com.epm.gestepm.modelapi.deprecated.family.dto.Family;
import com.epm.gestepm.modelapi.holiday.dto.Holiday;
import com.epm.gestepm.modelapi.deprecated.interventionshare.dto.InterventionShare;
import com.epm.gestepm.modelapi.deprecated.interventionsubshare.dto.InterventionSubShare;
import com.epm.gestepm.modelapi.deprecated.materialrequired.dto.MaterialRequired;
import com.epm.gestepm.modelapi.deprecated.project.dto.Project;
import com.epm.gestepm.modelapi.subrole.dto.SubRole;
import com.epm.gestepm.modelapi.deprecated.user.dto.User;
import com.epm.gestepm.modelapi.userholiday.dto.UserHoliday;
import com.epm.gestepm.modelapi.usermanualsigning.dto.UserManualSigning;
import com.epm.gestepm.modelapi.deprecated.workshare.WorkShare;
import org.hibernate.query.criteria.internal.path.ListAttributeJoin;
import org.hibernate.query.criteria.internal.path.RootImpl;
import org.hibernate.query.criteria.internal.path.SingularAttributeJoin;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.Map.Entry;

/**
 * The Class AppUtil.
 *
 * @author pavan.solapure
 */
public class DataTableUtil {

	private DataTableUtil() {
		
	}
	
	/**
	 * Checks if is collection empty.
	 *
	 * @param collection the collection
	 * @return true, if is collection empty
	 */
	private static boolean isCollectionEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	/**
	 * Checks if is object empty.
	 *
	 * @param object the object
	 * @return true, if is object empty
	 */
	public static boolean isObjectEmpty(Object object) {
		if (object == null) {
			return true;
		} else if (object instanceof String) {
			if (((String) object).trim().length() == 0) {
				return true;
			}
		} else if (object instanceof Collection) {
			return isCollectionEmpty((Collection<?>) object);
		}
		return false;
	}
	
	/**
	 * Generate DataTable Filters in Criteria
	 * @param pagination
	 * @param cb
	 * @param roots
	 * @return Predicate
	 */
	public static Predicate generateWhereCondition(PaginationCriteria pagination, CriteriaBuilder cb, Object... roots) {

		Predicate whereFilter = null;

		if (!pagination.getFilterBy().getMapOfFilters().isEmpty()) {

			Iterator<Entry<String, String>> fbit = pagination.getFilterBy().getMapOfFilters().entrySet().iterator();

			while (fbit.hasNext()) {

				Map.Entry<String, String> pair = fbit.next();

				String prefixRoot = pair.getKey().split("_")[0];
				String columnName = pair.getKey().split("_")[1];

				Predicate filterPred = null;

				for (Object root : roots) {

					if (root instanceof RootImpl) {
						RootImpl<?> objRoot = (RootImpl<?>) root;
						if (objRoot.getJavaType().equals(getClassType(prefixRoot))) {
							filterPred = cb.like(objRoot.get(columnName), "%" + pair.getValue() + "%");
						}

					} else if (root instanceof SingularAttributeJoin) {
						SingularAttributeJoin<?, ?> objRoot = (SingularAttributeJoin<?, ?>) root;

						if (objRoot.getJavaType().equals(getClassType(prefixRoot))) {
							filterPred = cb.like(objRoot.get(columnName), "%" + pair.getValue() + "%");
						}
					} else if (root instanceof ListAttributeJoin) {
						ListAttributeJoin<?, ?> objRoot = (ListAttributeJoin<?, ?>) root;

						if (objRoot.getJavaType().equals(getClassType(prefixRoot))) {
							filterPred = cb.like(objRoot.get(columnName), "%" + pair.getValue() + "%");
						}
					}
				}

				if (whereFilter == null) {
					whereFilter = filterPred;
				} else {
					whereFilter = cb.or(whereFilter, filterPred);
				}
			}
		}

		return whereFilter;
	}
	
	/**
	 * Generate DataTable OrderBy in Criteria
	 * @param pagination
	 * @param cb
	 * @param roots
	 * @return
	 */
	public static List<Order> generateOrderByCondition(PaginationCriteria pagination, CriteriaBuilder cb, Object... roots) {
		
		List<Order> orderByList = new ArrayList<>();
		
		if (!pagination.getSortBy().getSortBys().isEmpty()) {

			Iterator<Entry<String, SortOrder>> sbit = pagination.getSortBy().getSortBys().entrySet().iterator();

			while (sbit.hasNext()) {
				
				Map.Entry<String, SortOrder> pair = sbit.next();
				
				String prefixRoot = pair.getKey().split("_")[0];
				String columnName = pair.getKey().split("_")[1];

				for (Object root : roots) {

					if (root instanceof RootImpl) {
						RootImpl<?> objRoot = (RootImpl<?>) root;
						
						if (objRoot.getJavaType().equals(getClassType(prefixRoot))) {
							if ("ASC".equalsIgnoreCase(pair.getValue().toString())) {
								
								if ("fullName".equals(columnName) && objRoot.getJavaType().equals(User.class)) {
									orderByList.add(cb.asc(cb.concat(cb.concat(objRoot.get("name"), " "), objRoot.get("surnames"))));
								} else {
									orderByList.add(cb.asc(objRoot.get(columnName)));
								}
								
							} else if ("DESC".equalsIgnoreCase(pair.getValue().toString())) {
								
								if ("fullName".equals(columnName) && objRoot.getJavaType().equals(User.class)) {
									orderByList.add(cb.desc(cb.concat(cb.concat(objRoot.get("name"), " "), objRoot.get("surnames"))));
								} else {
									orderByList.add(cb.desc(objRoot.get(columnName)));
								}
							}
						}
					} else if (root instanceof SingularAttributeJoin) {
						SingularAttributeJoin<?, ?> objRoot = (SingularAttributeJoin<?, ?>) root;
						
						if (objRoot.getJavaType().equals(getClassType(prefixRoot))) {
							if ("ASC".equalsIgnoreCase(pair.getValue().toString())) {
								
								if ("fullName".equals(columnName) && objRoot.getJavaType().equals(User.class)) {
									orderByList.add(cb.asc(cb.concat(cb.concat(objRoot.get("name"), " "), objRoot.get("surnames"))));
								} else {
									orderByList.add(cb.asc(objRoot.get(columnName)));
								}
								
							} else if ("DESC".equalsIgnoreCase(pair.getValue().toString())) {
								
								if ("fullName".equals(columnName) && objRoot.getJavaType().equals(User.class)) {
									orderByList.add(cb.desc(cb.concat(cb.concat(objRoot.get("name"), " "), objRoot.get("surnames"))));
								} else {
									orderByList.add(cb.desc(objRoot.get(columnName)));
								}
							}
						}
					} else if (root instanceof ListAttributeJoin) {
						ListAttributeJoin<?, ?> objRoot = (ListAttributeJoin<?, ?>) root;
						
						if (objRoot.getJavaType().equals(getClassType(prefixRoot))) {
							if ("ASC".equalsIgnoreCase(pair.getValue().toString())) {
								
								if ("fullName".equals(columnName) && objRoot.getJavaType().equals(User.class)) {
									orderByList.add(cb.asc(cb.concat(cb.concat(objRoot.get("name"), " "), objRoot.get("surnames"))));
								} else {
									orderByList.add(cb.asc(objRoot.get(columnName)));
								}
								
							} else if ("DESC".equalsIgnoreCase(pair.getValue().toString())) {
								
								if ("fullName".equals(columnName) && objRoot.getJavaType().equals(User.class)) {
									orderByList.add(cb.desc(cb.concat(cb.concat(objRoot.get("name"), " "), objRoot.get("surnames"))));
								} else {
									orderByList.add(cb.desc(objRoot.get(columnName)));
								}
							}
						}
					}
				}
			}
		}

		return orderByList;
	}
	
	/**
	 * Maintenance Method
	 * @param table
	 * @return
	 */
	private static Class<?> getClassType(String table) {
		
		switch(table) {
			case "co":
				return Country.class;
			case "ds":
				return DisplacementShare.class;
			case "ec":
				return ExpenseCorrective.class;
			case "es":
				return ExpenseSheet.class;
			case "ex":
				return Expense.class;
			case "fa":
				return Family.class;
			case "ho":
				return Holiday.class;
			case "is":
				return InterventionShare.class;
			case "iss":
				return InterventionSubShare.class;
			case "mr":
				return MaterialRequired.class;
			case "pr":
				return Project.class;
			case "pv":
				return ActivityCenter.class;
			case "sr":
				return SubRole.class;
			case "uh":
				return UserHoliday.class;
			case "ums":
				return UserManualSigning.class;
			case "us":
				return User.class;
			case "ws":
				return WorkShare.class;
			default:
				return null;
		}
	}

	/**
	 * Builds the paginated query.
	 *
	 * @param baseQuery          the base query
	 * @param paginationCriteria the pagination criteria
	 * @return the string
	 */
	@Deprecated
	public static String buildPaginatedQuery(String baseQuery, PaginationCriteria paginationCriteria) {
		StringBuilder sb = new StringBuilder(
				"SELECT FILTERED_ORDERD_RESULTS.* FROM (SELECT BASEINFO.* FROM ( #BASE_QUERY# ) BASEINFO #WHERE_CLAUSE#  #ORDER_CLASUE# ) FILTERED_ORDERD_RESULTS LIMIT #PAGE_NUMBER#, #PAGE_SIZE#");
		String finalQuery = null;
		if (!DataTableUtil.isObjectEmpty(paginationCriteria)) {
			finalQuery = sb.toString().replace("#BASE_QUERY#", baseQuery)
					.replace("#WHERE_CLAUSE#",
							((DataTableUtil.isObjectEmpty(paginationCriteria.getFilterByClause())) ? "" : " WHERE ")
									+ paginationCriteria.getFilterByClause())
					.replace("#ORDER_CLASUE#", paginationCriteria.getOrderByClause())
					.replace("#PAGE_NUMBER#", paginationCriteria.getPageNumber().toString())
					.replace("#PAGE_SIZE#", paginationCriteria.getPageSize().toString());
		}
		return (null == finalQuery) ? baseQuery : finalQuery;
	}
}

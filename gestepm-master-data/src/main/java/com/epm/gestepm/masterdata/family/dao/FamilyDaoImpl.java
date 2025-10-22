package com.epm.gestepm.masterdata.family.dao;

import com.epm.gestepm.lib.entity.AttributeMap;
import com.epm.gestepm.lib.jdbc.api.datasource.SQLDatasource;
import com.epm.gestepm.lib.jdbc.api.orderby.SQLOrderByType;
import com.epm.gestepm.lib.jdbc.api.query.SQLInsert;
import com.epm.gestepm.lib.jdbc.api.query.SQLQuery;
import com.epm.gestepm.lib.jdbc.api.query.fetch.SQLQueryFetchMany;
import com.epm.gestepm.lib.jdbc.api.query.fetch.SQLQueryFetchOne;
import com.epm.gestepm.lib.jdbc.api.query.fetch.SQLQueryFetchPage;
import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.lib.types.Page;
import com.epm.gestepm.masterdata.family.dao.entity.Family;
import com.epm.gestepm.masterdata.family.dao.entity.creator.FamilyCreate;
import com.epm.gestepm.masterdata.family.dao.entity.deleter.FamilyDelete;
import com.epm.gestepm.masterdata.family.dao.entity.filter.FamilyFilter;
import com.epm.gestepm.masterdata.family.dao.entity.finder.FamilyByIdFinder;
import com.epm.gestepm.masterdata.family.dao.entity.updater.FamilyUpdate;
import com.epm.gestepm.masterdata.family.dao.mappers.FamilyRowMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static com.epm.gestepm.lib.jdbc.api.orderby.SQLOrderByType.ASC;
import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.DAO;
import static com.epm.gestepm.lib.logging.constants.LogOperations.*;
import static com.epm.gestepm.masterdata.family.dao.constants.FamilyQueries.*;
import static com.epm.gestepm.masterdata.family.dao.mappers.FamilyRowMapper.COL_FA_ID;

@Component("familyDao")
@EnableExecutionLog(layerMarker = DAO)
public class FamilyDaoImpl implements FamilyDao {

    private final SQLDatasource sqlDatasource;

    public FamilyDaoImpl(SQLDatasource sqlDatasource) {
        this.sqlDatasource = sqlDatasource;
    }

    @Override
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Querying list of families",
            msgOut = "Querying list of families OK",
            errorMsg = "Failed to query list of families")
    public List<Family> list(FamilyFilter filter) {

        final SQLQueryFetchMany<Family> sqlQuery = new SQLQueryFetchMany<Family>()
                .useRowMapper(new FamilyRowMapper())
                .useQuery(QRY_LIST_OF_FA)
                .useFilter(FILTER_FA_BY_PARAMS)
                .withParams(filter.collectAttributes());

        this.setOrder(filter.getOrder(), filter.getOrderBy(), sqlQuery);

        return this.sqlDatasource.fetch(sqlQuery);
    }

    @Override
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Querying page of families",
            msgOut = "Querying page of families OK",
            errorMsg = "Failed to query page of families")
    public Page<Family> list(FamilyFilter filter, Long offset, Long limit) {

        final SQLQueryFetchPage<Family> sqlQuery = new SQLQueryFetchPage<Family>()
                .useRowMapper(new FamilyRowMapper())
                .useQuery(QRY_PAGE_OF_FA)
                .useCountQuery(QRY_COUNT_OF_FA)
                .useFilter(FILTER_FA_BY_PARAMS)
                .offset(offset)
                .limit(limit)
                .withParams(filter.collectAttributes());

        this.setOrder(filter.getOrder(), filter.getOrderBy(), sqlQuery);

        return this.sqlDatasource.fetch(sqlQuery);
    }

    @Override
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Querying to find family by ID",
            msgOut = "Querying to find family by ID OK",
            errorMsg = "Failed query to find family by ID")
    public Optional<Family> find(FamilyByIdFinder finder) {

        final SQLQueryFetchOne<Family> sqlQuery = new SQLQueryFetchOne<Family>()
                .useRowMapper(new FamilyRowMapper())
                .useQuery(QRY_LIST_OF_FA)
                .useFilter(FILTER_FA_BY_ID)
                .withParams(finder.collectAttributes());

        return this.sqlDatasource.fetch(sqlQuery);
    }

    @Override
    @LogExecution(operation = OP_CREATE,
            debugOut = true,
            msgIn = "Persisting new family",
            msgOut = "New family persisted OK",
            errorMsg = "Failed to persist new family")
    public Family create(FamilyCreate create) {

        final AttributeMap params = create.collectAttributes();

        final FamilyByIdFinder finder = new FamilyByIdFinder();

        final SQLInsert<BigInteger> sqlInsert = new SQLInsert<BigInteger>()
                .useQuery(QRY_CREATE_FA)
                .withParams(params)
                .onGeneratedKey(f -> finder.setId(f.intValue()));

        this.sqlDatasource.insert(sqlInsert);

        return this.find(finder).orElse(null);
    }

    @Override
    @LogExecution(operation = OP_UPDATE,
            debugOut = true,
            msgIn = "Persisting update for family",
            msgOut = "Update for family persisted OK",
            errorMsg = "Failed to persist update for family")
    public Family update(FamilyUpdate update) {

        final Integer id = update.getId();
        final AttributeMap params = update.collectAttributes();

        final FamilyByIdFinder finder = new FamilyByIdFinder();
        finder.setId(id);

        final SQLQuery sqlQuery = new SQLQuery()
                .useQuery(QRY_UPDATE_FA)
                .withParams(params);

        this.sqlDatasource.execute(sqlQuery);

        return this.find(finder).orElse(null);
    }

    @Override
    @LogExecution(operation = OP_DELETE,
            debugOut = true,
            msgIn = "Persisting delete for family",
            msgOut = "Delete for family persisted OK",
            errorMsg = "Failed to persist delete for family")
    public void delete(FamilyDelete delete) {

        final AttributeMap params = delete.collectAttributes();

        final SQLQuery sqlQuery = new SQLQuery()
                .useQuery(QRY_DELETE_FA)
                .withParams(params);

        this.sqlDatasource.execute(sqlQuery);
    }

    private void setOrder(SQLOrderByType order, String orderBy, SQLQueryFetchMany<Family> sqlQuery) {

        final String orderByStatement = StringUtils.isNoneBlank(orderBy) && !orderBy.equals("id") ? orderBy : COL_FA_ID;
        final SQLOrderByType orderStatement = order != null ? order : ASC;

        sqlQuery.addOrderBy(orderByStatement, orderStatement);
    }
}

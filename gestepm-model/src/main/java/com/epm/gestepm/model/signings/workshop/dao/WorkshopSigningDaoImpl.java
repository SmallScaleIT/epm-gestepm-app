package com.epm.gestepm.model.signings.workshop.dao;

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
import com.epm.gestepm.model.signings.workshop.dao.entity.WorkshopSigning;
import com.epm.gestepm.model.signings.workshop.dao.entity.creator.WorkshopSigningCreate;
import com.epm.gestepm.model.signings.workshop.dao.entity.deleter.WorkshopSigningDelete;
import com.epm.gestepm.model.signings.workshop.dao.entity.filter.WorkshopSigningFilter;
import com.epm.gestepm.model.signings.workshop.dao.entity.finder.WorkshopSigningByIdFinder;
import com.epm.gestepm.model.signings.workshop.dao.entity.updater.WorkshopSigningUpdate;
import com.epm.gestepm.model.signings.workshop.dao.mappers.WorkshopSigningRowMapper;
import com.epm.gestepm.model.signings.workshop.dao.mappers.WorkshopSigningUpdateRowMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.DAO;
import static com.epm.gestepm.lib.logging.constants.LogOperations.*;
import static com.epm.gestepm.model.signings.workshop.dao.constants.WorkshopSigningQueries.*;
import static com.epm.gestepm.model.signings.workshop.dao.mappers.WorkshopSigningRowMapper.*;

@Component("workshopSigningDao")
@EnableExecutionLog(layerMarker = DAO)
public class WorkshopSigningDaoImpl implements WorkshopSigningDao {

    private final SQLDatasource datasource;

    public WorkshopSigningDaoImpl(SQLDatasource datasource) {
        this.datasource = datasource;
    }

    @Override
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Querying list of workshop signings",
            msgOut = "Querying list of workshop signings OK",
            errorMsg = "Failed to query list of workshop signings")
    public List<@Valid WorkshopSigning> list(WorkshopSigningFilter signingFilter) {

        SQLQueryFetchMany<WorkshopSigning> sqlQuery = new SQLQueryFetchMany<WorkshopSigning>()
                .useRowMapper(new WorkshopSigningRowMapper())
                .useQuery(QRY_LIST_OF_WSS)
                .useFilter(FILTER_WSS_BY_PARAMS)
                .withParams(signingFilter.collectAttributes());

        this.setOrder(signingFilter.getOrder(), signingFilter.getOrderBy(), sqlQuery);

        return datasource.fetch(sqlQuery);
    }

    @Override
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Querying page of workshop signings",
            msgOut = "Querying page of workshop signings OK",
            errorMsg = "Failed to query page of workshop signings")
    public Page<@Valid WorkshopSigning> list(WorkshopSigningFilter signingFilter, Long offset, Long limit) {

        SQLQueryFetchPage<WorkshopSigning> sqlQuery = new SQLQueryFetchPage<WorkshopSigning>()
                .useRowMapper(new WorkshopSigningRowMapper())
                .useQuery(QRY_PAGE_OF_WSS)
                .useCountQuery(QRY_COUNT_OF_WSS)
                .useFilter(FILTER_WSS_BY_PARAMS)
                .offset(offset)
                .limit(limit)
                .withParams(signingFilter.collectAttributes());

        this.setOrder(signingFilter.getOrder(), signingFilter.getOrderBy(), sqlQuery);

        return datasource.fetch(sqlQuery);
    }

    @Override
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Querying to find workshop signing by ID",
            msgOut = "Querying to find workshop signing by ID OK",
            errorMsg = "Failed query to find workshop signing by ID")
    public Optional<@Valid WorkshopSigning> find(WorkshopSigningByIdFinder finder) {

        SQLQueryFetchOne<WorkshopSigning> sqlQuery = new SQLQueryFetchOne<WorkshopSigning>()
                .useRowMapper(new WorkshopSigningRowMapper())
                .useQuery(QRY_LIST_OF_WSS)
                .useFilter(FILTER_WSS_BY_ID)
                .withParams(finder.collectAttributes());

        return datasource.fetch(sqlQuery);
    }

    @Override
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Querying to find workshop signing by ID",
            msgOut = "Querying to find workshop signing by ID OK",
            errorMsg = "Failed query to find workshop signing by ID")
    public Optional<WorkshopSigningUpdate> findUpdateSigning(WorkshopSigningByIdFinder finder) {
        SQLQueryFetchOne<WorkshopSigningUpdate> sqlQuery = new SQLQueryFetchOne<WorkshopSigningUpdate>()
                .useRowMapper(new WorkshopSigningUpdateRowMapper())
                .useQuery(QRY_LIST_OF_WSS)
                .useFilter(FILTER_WSS_BY_ID)
                .withParams(finder.collectAttributes());

        return datasource.fetch(sqlQuery);
    }

    @Override
    @LogExecution(operation = OP_CREATE,
            debugOut = true,
            msgIn = "Persisting new workshop signing",
            msgOut = "New workshop signing persisted OK",
            errorMsg = "Failed to persist new workshop signing")
    public WorkshopSigning create(WorkshopSigningCreate create) {

        WorkshopSigningByIdFinder finder = new WorkshopSigningByIdFinder();

        SQLInsert<BigInteger> sqlInsert = new SQLInsert<BigInteger>()
                .useQuery(QRY_CREATE_WSS)
                .withParams(create.collectAttributes())
                .onGeneratedKey(id -> finder.setId(id.intValue()));

        datasource.insert(sqlInsert);

        return find(finder).orElse(null);
    }

    @Override
    @LogExecution(operation = OP_UPDATE,
            debugOut = true,
            msgIn = "Persisting update for workshop signing",
            msgOut = "Update for workshop signing persisted OK",
            errorMsg = "Failed to persist update for workshop signing")
    public WorkshopSigning update(WorkshopSigningUpdate update) {

        WorkshopSigningByIdFinder finder = new WorkshopSigningByIdFinder();
        finder.setId(update.getId());

        SQLQuery updateSQL = new SQLQuery()
                .useQuery(QRY_UPDATE_WSS)
                .withParams(update.collectAttributes());

        datasource.execute(updateSQL);

        return find(finder).orElse(null);
    }

    @Override
    @LogExecution(operation = OP_DELETE,
            debugOut = true,
            msgIn = "Persisting delete for workshop signing",
            msgOut = "Delete for workshop signing persisted OK",
            errorMsg = "Failed to persist delete for workshop signing")
    public void delete(WorkshopSigningDelete delete) {

        SQLQuery deleteSQL = new SQLQuery()
                .useQuery(QRY_DELETE_WSS)
                .withParams(delete.collectAttributes());

        datasource.execute(deleteSQL);
    }

    private void setOrder(final SQLOrderByType order, final String orderBy, final SQLQueryFetchMany<WorkshopSigning> sqlQuery) {
        final String orderByStatement = StringUtils.isNoneBlank(orderBy) && !orderBy.equals("id")
                ? this.getOrderColumn(orderBy)
                : COL_WSS_ID;
        final SQLOrderByType orderStatement = order != null
                ? order
                : SQLOrderByType.DESC;
        sqlQuery.addOrderBy(orderByStatement, orderStatement);
    }

    private String getOrderColumn(final String orderBy) {
        if ("startedAt".equals(orderBy)) {
            return COL_WSS_STARTED_AT;
        } else if ("closedAt".equals(orderBy)) {
            return COL_WSS_CLOSED_AT;
        } else if ("project.name".equals(orderBy)) {
            return COL_WSS_PROJECT_NAME;
        } else if ("warehouse_signing_id".equals(orderBy)) {
            return COL_WSS_WAREHOUSE_ID;
        }
        return orderBy;
    }
}

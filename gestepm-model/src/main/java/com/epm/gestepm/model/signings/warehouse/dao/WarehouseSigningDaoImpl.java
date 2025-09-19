package com.epm.gestepm.model.signings.warehouse.dao;

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
import com.epm.gestepm.model.signings.warehouse.dao.entity.WarehouseSigning;
import com.epm.gestepm.model.signings.warehouse.dao.entity.creator.WarehouseSigningCreate;
import com.epm.gestepm.model.signings.warehouse.dao.entity.deleter.WarehouseSigningDelete;
import com.epm.gestepm.model.signings.warehouse.dao.entity.filter.WarehouseSigningFilter;
import com.epm.gestepm.model.signings.warehouse.dao.entity.finder.WarehouseSigningByIdFinder;
import com.epm.gestepm.model.signings.warehouse.dao.entity.updater.WarehouseSigningUpdate;
import com.epm.gestepm.model.signings.warehouse.dao.mappers.WarehouseSigningRowMapper;
import com.epm.gestepm.model.signings.warehouse.dao.mappers.WarehouseSigningUpdateRowMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.DAO;

import static com.epm.gestepm.lib.logging.constants.LogOperations.*;
import static com.epm.gestepm.model.signings.warehouse.dao.constants.WarehouseSigningQueries.*;
import static com.epm.gestepm.model.signings.warehouse.dao.mappers.WarehouseSigningRowMapper.*;

@Component("warehouseSigningDao")
@EnableExecutionLog(layerMarker = DAO)
public class WarehouseSigningDaoImpl implements WarehouseSigningDao {

    private final SQLDatasource datasource;

    public WarehouseSigningDaoImpl(SQLDatasource datasource) {
        this.datasource = datasource;
    }

    @Override
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Querying list of warehouse signings",
            msgOut = "Querying list of warehouse signings OK",
            errorMsg = "Failed to query list of warehouse signings")
    public List<WarehouseSigning> list(WarehouseSigningFilter filter) {

        final SQLQueryFetchMany<WarehouseSigning> sqlQuery = new SQLQueryFetchMany<WarehouseSigning>()
                .useRowMapper(new WarehouseSigningRowMapper())
                .useQuery(QRY_LIST_OF_WHS)
                .useFilter(FILTER_WHS_BY_PARAMS)
                .withParams(filter.collectAttributes());

        this.setOrder(filter.getOrder(), filter.getOrderBy(), sqlQuery);

        return datasource.fetch(sqlQuery);
    }

    @Override
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Querying page of warehouse signings",
            msgOut = "Querying page of warehouse signings OK",
            errorMsg = "Failed to query page of warehouse signings")
    public Page<WarehouseSigning> list(WarehouseSigningFilter filter, Long offset, Long limit) {

        final SQLQueryFetchPage<WarehouseSigning> sqlQuery = new SQLQueryFetchPage<WarehouseSigning>()
                .useRowMapper(new WarehouseSigningRowMapper())
                .useQuery(QRY_PAGE_OF_WHS)
                .useCountQuery(QRY_COUNT_OF_WHS)
                .useFilter(FILTER_WHS_BY_PARAMS)
                .offset(offset)
                .limit(limit)
                .withParams(filter.collectAttributes());

        this.setOrder(filter.getOrder(), filter.getOrderBy(), sqlQuery);

        return datasource.fetch(sqlQuery);
    }

    @Override
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Querying to find warehouse signing by ID",
            msgOut = "Querying to find warehouse signing by ID OK",
            errorMsg = "Failed query to find warehouse signing by ID")
    public Optional<WarehouseSigning> find(WarehouseSigningByIdFinder finder) {

        final SQLQueryFetchOne<WarehouseSigning> sqlQuery = new SQLQueryFetchOne<WarehouseSigning>()
                .useRowMapper(new WarehouseSigningRowMapper())
                .useQuery(QRY_LIST_OF_WHS)
                .useFilter(FILTER_WHS_BY_ID)
                .withParams(finder.collectAttributes());

        return datasource.fetch(sqlQuery);
    }

    @Override
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Querying to find warehouse signing by ID",
            msgOut = "Querying to find warehouse signing by ID OK",
            errorMsg = "Failed query to find warehouse signing by ID")
    public Optional<WarehouseSigningUpdate> findUpdateSigning(WarehouseSigningByIdFinder finder) {

        SQLQueryFetchOne<WarehouseSigningUpdate> sqlQuery = new SQLQueryFetchOne<WarehouseSigningUpdate>()
                .useRowMapper(new WarehouseSigningUpdateRowMapper())
                .useQuery(QRY_LIST_OF_WHS)
                .useFilter(FILTER_WHS_BY_ID)
                .withParams(finder.collectAttributes());

        return datasource.fetch(sqlQuery);
    }

    @Override
    @LogExecution(operation = OP_CREATE,
            debugOut = true,
            msgIn = "Persisting new warehouse signing",
            msgOut = "New warehouse signing persisted OK",
            errorMsg = "Failed to persist new warehouse signing")
    public WarehouseSigning create(WarehouseSigningCreate create) {

        WarehouseSigningByIdFinder finder = new WarehouseSigningByIdFinder();

        SQLInsert<BigInteger> insert = new SQLInsert<BigInteger>()
                .useQuery(QRY_CREATE_WHS)
                .withParams(create.collectAttributes())
                .onGeneratedKey(id -> finder.setId(id.intValue()));

        datasource.insert(insert);

        return find(finder).orElse(null);
    }

    @Override
    @LogExecution(operation = OP_UPDATE,
            debugOut = true,
            msgIn = "Persisting update for warehouse signing",
            msgOut = "Update for warehouse signing persisted OK",
            errorMsg = "Failed to persist update for warehouse signing")
    public WarehouseSigning update(WarehouseSigningUpdate update) {

        WarehouseSigningByIdFinder finder = new WarehouseSigningByIdFinder();
        finder.setId(update.getId());

        SQLQuery sqlUpdate = new SQLQuery()
                .useQuery(QRY_UPDATE_WHS)
                .withParams(update.collectAttributes());

        datasource.execute(sqlUpdate);

        return find(finder).orElse(null);
    }

    @Override
    @LogExecution(operation = OP_DELETE,
            debugOut = true,
            msgIn = "Persisting delete for warehouse signing",
            msgOut = "Delete for warehouse signing persisted OK",
            errorMsg = "Failed to persist delete for warehouse signing")
    public void delete(WarehouseSigningDelete delete) {

        SQLQuery sqlDelete = new SQLQuery()
                .useQuery(QRY_DELETE_WHS)
                .withParams(delete.collectAttributes());

        datasource.execute(sqlDelete);
    }

    private void setOrder(final SQLOrderByType order, final String orderBy, final SQLQueryFetchMany<WarehouseSigning> sqlQuery) {
        final String orderByStatement = StringUtils.isNoneBlank(orderBy) && !orderBy.equals("id")
                ? this.getOrderColumn(orderBy)
                : COL_WHS_ID;
        final SQLOrderByType orderStatement = order != null
                ? order
                : SQLOrderByType.DESC;
        sqlQuery.addOrderBy(orderByStatement, orderStatement);
    }

    private String getOrderColumn(final String orderBy) {
        if ("startedAt".equals(orderBy)) {
            return COL_WHS_STARTED_AT;
        } else if ("closedAt".equals(orderBy)) {
            return COL_WHS_CLOSED_AT;
        } else if ("project.name".equals(orderBy)) {
            return COL_WHS_PROJECT_NAME;
        }
        return orderBy;
    }
}

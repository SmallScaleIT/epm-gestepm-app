package com.epm.gestepm.model.signings.personal.dao;

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
import com.epm.gestepm.model.signings.personal.dao.entity.PersonalSigning;
import com.epm.gestepm.model.signings.personal.dao.entity.creator.PersonalSigningCreate;
import com.epm.gestepm.model.signings.personal.dao.entity.deleter.PersonalSigningDelete;
import com.epm.gestepm.model.signings.personal.dao.entity.filter.PersonalSigningFilter;
import com.epm.gestepm.model.signings.personal.dao.entity.finder.PersonalSigningByIdFinder;
import com.epm.gestepm.model.signings.personal.dao.entity.updater.PersonalSigningUpdate;
import com.epm.gestepm.model.signings.personal.dao.mappers.PersonalSigningRowMapper;
import com.epm.gestepm.model.signings.personal.dao.mappers.PersonalSigningUpdateRowMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.DAO;

import static com.epm.gestepm.lib.logging.constants.LogOperations.*;
import static com.epm.gestepm.model.signings.personal.dao.constants.PersonalSigningQueries.*;
import static com.epm.gestepm.model.signings.personal.dao.mappers.PersonalSigningRowMapper.*;

@Component("personalSigningDao")
@EnableExecutionLog(layerMarker = DAO)
@RequiredArgsConstructor
public class PersonalSigningDaoImpl implements PersonalSigningDao {

    private final SQLDatasource datasource;

    @Override
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Querying list of personal signings",
            msgOut = "Querying list of personal signings OK",
            errorMsg = "Failed to query list of personal signings")
    public List<@Valid PersonalSigning> list(PersonalSigningFilter filter) {

        SQLQueryFetchMany<PersonalSigning> sqlQuery = new SQLQueryFetchMany<PersonalSigning>()
                .useQuery(QRY_LIST_OF_PRS)
                .useRowMapper(new PersonalSigningRowMapper())
                .useFilter(FILTER_PRS_BY_PARAMS)
                .withParams(filter.collectAttributes());

        this.setOrder(filter.getOrder(),filter.getOrderBy(), sqlQuery);

        return datasource.fetch(sqlQuery);
    }

    @Override
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Querying page of personal signings",
            msgOut = "Querying page of personal signings OK",
            errorMsg = "Failed to query page of personal signings")
    public Page<@Valid PersonalSigning> list(PersonalSigningFilter filter, Long offset, Long limit) {

        SQLQueryFetchPage<PersonalSigning> sqlQuery = new SQLQueryFetchPage<PersonalSigning>()
                .useRowMapper(new PersonalSigningRowMapper())
                .useQuery(QRY_LIST_OF_PRS)
                .useCountQuery(QRY_COUNT_OF_PRS)
                .useFilter(FILTER_PRS_BY_PARAMS)
                .limit(limit)
                .offset(offset)
                .withParams(filter.collectAttributes());

        this.setOrder(filter.getOrder(), filter.getOrderBy(), sqlQuery);

        return datasource.fetch(sqlQuery);
    }

    @Override
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Querying to find personal signing by ID",
            msgOut = "Querying to find personal signing by ID OK",
            errorMsg = "Failed query to find personal signing by ID")
    public Optional<@Valid PersonalSigning> find(PersonalSigningByIdFinder finder) {

        SQLQueryFetchOne<PersonalSigning> sqlQuery = new SQLQueryFetchOne<PersonalSigning>()
                .useRowMapper(new PersonalSigningRowMapper())
                .useQuery(QRY_LIST_OF_PRS)
                .useFilter(FILTER_PRS_BY_ID)
                .withParams(finder.collectAttributes());

        return datasource.fetch(sqlQuery);
    }

    @Override
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Querying to find personal signing by ID",
            msgOut = "Querying to find personal signing by ID OK",
            errorMsg = "Failed query to find personal signing by ID")
    public Optional<@Valid PersonalSigningUpdate> findUpdate(PersonalSigningByIdFinder finder) {

        SQLQueryFetchOne<PersonalSigningUpdate> sqlQuery = new SQLQueryFetchOne<PersonalSigningUpdate>()
                .useRowMapper(new PersonalSigningUpdateRowMapper())
                .useQuery(QRY_LIST_OF_PRS)
                .useFilter(FILTER_PRS_BY_ID)
                .withParams(finder.collectAttributes());

        return datasource.fetch(sqlQuery);
    }

    @Override
    @LogExecution(operation = OP_CREATE,
            debugOut = true,
            msgIn = "Persisting new personal signing",
            msgOut = "New personal signing persisted OK",
            errorMsg = "Failed to persist new personal signing")
    public PersonalSigning create(PersonalSigningCreate create) {

        PersonalSigningByIdFinder finder = new PersonalSigningByIdFinder();

        SQLInsert<BigInteger> sqlInsert = new SQLInsert<BigInteger>()
                .useQuery(QRY_CREATE_PRS)
                .withParams(create.collectAttributes())
                .onGeneratedKey(id -> finder.setId(id.intValue()));

        datasource.insert(sqlInsert);

        return find(finder).orElse(null);
    }

    @Override
    @LogExecution(operation = OP_UPDATE,
            debugOut = true,
            msgIn = "Persisting update for personal signing",
            msgOut = "Update for personal signing persisted OK",
            errorMsg = "Failed to persist update for personal signing")
    public PersonalSigning update(PersonalSigningUpdate update) {

        SQLQuery sqlUpdate = new SQLQuery()
                .useQuery(QRY_UPDATE_PRS)
                .withParams(update.collectAttributes());

        datasource.execute(sqlUpdate);

        return find(new PersonalSigningByIdFinder(update.getId())).orElse(null);
    }

    @Override
    @LogExecution(operation = OP_DELETE,
            debugOut = true,
            msgIn = "Persisting delete for personal signing",
            msgOut = "Delete for personal signing persisted OK",
            errorMsg = "Failed to persist delete for personal signing")
    public void delete(PersonalSigningDelete delete) {

        SQLQuery query = new SQLQuery()
                .useQuery(QRY_DELETE_PRS)
                .withParams(delete.collectAttributes());

        datasource.execute(query);
    }

    private void setOrder(final SQLOrderByType order, final String orderBy, final SQLQueryFetchMany<PersonalSigning> sqlQuery) {
        final String orderByStatement = StringUtils.isNoneBlank(orderBy) && !orderBy.equals("id")
                ? this.getOrderColumn(orderBy)
                : COL_PRS_ID;
        final SQLOrderByType orderStatement = order != null
                ? order
                : SQLOrderByType.DESC;
        sqlQuery.addOrderBy(orderByStatement, orderStatement);
    }

    private String getOrderColumn(final String orderBy) {
        if ("startDate".equals(orderBy)) {
            return COL_PRS_START_DATE;
        } else if ("endDate".equals(orderBy)) {
            return COL_PRS_END_DATE;
        }
        return orderBy;
    }
}

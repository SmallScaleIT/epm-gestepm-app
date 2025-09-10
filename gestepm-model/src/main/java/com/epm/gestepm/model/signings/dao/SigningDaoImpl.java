package com.epm.gestepm.model.signings.dao;

import com.epm.gestepm.lib.jdbc.api.datasource.SQLDatasource;
import com.epm.gestepm.lib.jdbc.api.orderby.SQLOrderByType;
import com.epm.gestepm.lib.jdbc.api.query.fetch.SQLQueryFetchMany;
import com.epm.gestepm.lib.logging.annotation.EnableExecutionLog;
import com.epm.gestepm.lib.logging.annotation.LogExecution;
import com.epm.gestepm.model.signings.dao.entity.Signing;
import com.epm.gestepm.model.signings.dao.entity.filter.SigningFilter;
import com.epm.gestepm.model.signings.dao.mappers.SigningRowMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.List;

import static com.epm.gestepm.lib.logging.constants.LogLayerMarkers.DAO;

import static com.epm.gestepm.lib.logging.constants.LogOperations.OP_READ;
import static com.epm.gestepm.model.signings.dao.constants.SigningQueries.*;

import static com.epm.gestepm.model.signings.dao.mappers.SigningRowMapper.*;

@Component("signingDao")
@EnableExecutionLog(layerMarker = DAO)
@RequiredArgsConstructor
public class SigningDaoImpl implements SigningDao {

    private final SQLDatasource dataSource;

    @Override
    @LogExecution(operation = OP_READ,
            debugOut = true,
            msgIn = "Querying list of active signings",
            msgOut = "Querying list of active signings",
            errorMsg = "Failed to query list of active signings")
    public List<@Valid Signing> list(SigningFilter filter) {

        SQLQueryFetchMany<Signing> sqlQuery = new SQLQueryFetchMany<Signing>()
                .useRowMapper(new SigningRowMapper())
                .useQuery(QRY_LIST_OF_SGN)
                .useFilter(FILTER_CURRENT_SGN)
                .withParams(filter.collectAttributes());

        this.setOrder(filter.getOrder(), filter.getOrderBy(), sqlQuery);

        return dataSource.fetch(sqlQuery);
    }

    private void setOrder(final SQLOrderByType order, final String orderBy, final SQLQueryFetchMany<Signing> sqlQuery) {
        final String orderByStatement = StringUtils.isNoneBlank(orderBy) && !orderBy.equals("id")
                ? this.getOrderColumn(orderBy)
                : COL_SGN_ID;
        final SQLOrderByType orderStatement = order != null
                ? order
                : SQLOrderByType.DESC;
        sqlQuery.addOrderBy(orderByStatement, orderStatement);
    }

    private String getOrderColumn(final String orderBy) {
        return orderBy;
    }
}

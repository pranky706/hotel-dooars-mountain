package com.dooars.mountain.repository.operation;

import com.dooars.mountain.constants.AllGolbalConstants;
import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.model.operation.OperationTime;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Prantik Guha on 09-06-2021
 **/
@Repository
public class OparetionTimeRepositoryImpl implements OparetionTimeRepository{
    private static final Logger LOGGER = LoggerFactory.getLogger(OparetionTimeRepositoryImpl.class);

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final String UPDATE = "UPDATE operationtime set openhr = :openhr, openmin = :openmin, closehr = :closehr, closemin = :closemin where id = :id";

    private final String GET_ALL = "SELECT * FROM operationtime";

    private final RowMapper<OperationTime> mapper = new RowMapper<OperationTime>() {

        @Override
        public OperationTime mapRow(ResultSet rs, int rowNum) throws SQLException {
            LOGGER.trace("Entering into OperationTime mapper");
            OperationTime operationTime = new OperationTime();
            operationTime.setOpenHr(rs.getInt("openhr"));
            operationTime.setOpenMin(rs.getInt("openmin"));
            operationTime.setCloseHr(rs.getInt("closehr"));
            operationTime.setCloseMin(rs.getInt("closemin"));
            operationTime.setId(rs.getInt("id"));
            return operationTime;
        }
    };

    @Autowired
    OparetionTimeRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public void updateOperationTime(OperationTime operationTime) throws BaseException {
        LOGGER.trace("Entering into updateOperationTime method in OparetionTimeRepositoryImpl with {}", operationTime.toString());
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("openhr", operationTime.getOpenHr());
        namedParameters.addValue("openmin", operationTime.getOpenMin());
        namedParameters.addValue("closehr", operationTime.getCloseHr());
        namedParameters.addValue("closemin", operationTime.getCloseMin());
        namedParameters.addValue("id", operationTime.getId());
        try {
            jdbcTemplate.update(UPDATE, namedParameters);
        } catch(Exception e) {
            throw new BaseException(e.getMessage(), AllGolbalConstants.REPO_LAYER, null);
        }
    }

    @Override
    public List<OperationTime> getOperationTimes() throws BaseException {
        LOGGER.trace("Entering into getOperationTimes method in OparetionTimeRepositoryImpl with");
        return Try.ofSupplier(() -> jdbcTemplate.query(GET_ALL, mapper))
                .getOrElseThrow(throwable -> new BaseException(throwable.getMessage(), AllGolbalConstants.REPO_LAYER, null));
    }
}

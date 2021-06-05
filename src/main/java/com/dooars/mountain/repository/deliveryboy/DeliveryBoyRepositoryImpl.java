package com.dooars.mountain.repository.deliveryboy;

import com.dooars.mountain.constants.AllGolbalConstants;
import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.model.deliveryboy.DeliveryBoy;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Prantik Guha on 05-06-2021
 **/
@Repository
public class DeliveryBoyRepositoryImpl implements DeliveryBoyRepository{
    private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryBoyRepositoryImpl.class);

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    DeliveryBoyRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final String ADD_URL = "INSERT into deliveryboy(name, mobile, isdelete) values(:name, :mobile, :isdelete)";

    private final String GET_URL = "SELECT * from deliveryboy where mobile = :mobile and isdelete = :isdelete";

    private final String CHECK_URL = "SELECT * from deliveryboy where mobile = :mobile";

    private final String GET_ALL_URL = "SELECT * from deliveryboy where isdelete = :isdelete";

    private final String UPDATE_URL = "UPDATE deliveryboy set name = :name where mobile = :mobile";

    private final String DELETE_URL = "UPDATE deliveryboy set isdelete = :isdelete where mobile = :mobile";

    private final RowMapper<DeliveryBoy> mapper = new RowMapper<DeliveryBoy>() {
        @Override
        public DeliveryBoy mapRow(ResultSet rs, int rowNum) throws SQLException {
            DeliveryBoy deliveryBoy = new DeliveryBoy();
            deliveryBoy.setMobile(rs.getLong("mobile"));
            deliveryBoy.setName(rs.getString("name"));
            return deliveryBoy;
        }
    };

    @Override
    public void addDeliveryBoy(DeliveryBoy deliveryBoy) throws BaseException {
        LOGGER.trace("Entering into addDeliveryBoy method in DeliveryBoyRepositoryImpl with {}", deliveryBoy.toString());
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("name", deliveryBoy.getName());
        namedParameters.addValue("mobile", deliveryBoy.getMobile());
        namedParameters.addValue("isdelete", AllGolbalConstants.FALSE);
        try {
            jdbcTemplate.update(ADD_URL, namedParameters);
        } catch(Exception e) {
            throw new BaseException(e.getMessage(), AllGolbalConstants.REPO_LAYER, null);
        }
    }

    @Override
    public DeliveryBoy getBoyByNumber(long mobile) throws BaseException {
        LOGGER.trace("Entering into getBoyByNumber method in DeliveryBoyRepositoryImpl with {}", mobile);
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("isdelete", AllGolbalConstants.FALSE);
        namedParameters.addValue("mobile", mobile);
        return Try.ofSupplier(() -> DataAccessUtils.singleResult(jdbcTemplate.query(GET_URL, namedParameters, mapper)))
                .getOrElseThrow(throwable -> new BaseException(throwable.getMessage(), AllGolbalConstants.REPO_LAYER, null));
    }

    @Override
    public DeliveryBoy checkBoyByNumber(long mobile) throws BaseException {
        LOGGER.trace("Entering into checkBoyByNumber method in DeliveryBoyRepositoryImpl with {}", mobile);
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("mobile", mobile);
        return Try.ofSupplier(() -> DataAccessUtils.singleResult(jdbcTemplate.query(CHECK_URL, namedParameters, mapper)))
                .getOrElseThrow(throwable -> new BaseException(throwable.getMessage(), AllGolbalConstants.REPO_LAYER, null));
    }

    @Override
    public List<DeliveryBoy> getAllDeliveryBoy() throws BaseException {
        LOGGER.trace("Entering into getAllDeliveryBoy method in DeliveryBoyRepositoryImpl with");
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("isdelete", AllGolbalConstants.FALSE);
        return Try.ofSupplier(() -> jdbcTemplate.query(GET_ALL_URL, namedParameters, mapper))
                .getOrElseThrow(throwable -> new BaseException(throwable.getMessage(), AllGolbalConstants.REPO_LAYER, null));
    }

    @Override
    public void updateDeliveryBoy(DeliveryBoy deliveryBoy) throws BaseException {
        LOGGER.trace("Entering into updateDeliveryBoy method in DeliveryBoyRepositoryImpl with {}", deliveryBoy.toString());
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("name", deliveryBoy.getName());
        namedParameters.addValue("mobile", deliveryBoy.getMobile());
        try {
            jdbcTemplate.update(UPDATE_URL, namedParameters);
        } catch(Exception e) {
            throw new BaseException(e.getMessage(), AllGolbalConstants.REPO_LAYER, null);
        }
    }

    @Override
    public void deleteDeliveryBoy(long mobile) throws BaseException {
        LOGGER.trace("Entering into deleteDeliveryBoy method in DeliveryBoyRepositoryImpl with {}", mobile);
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("mobile", mobile);
        namedParameters.addValue("isdelete", AllGolbalConstants.TRUE);
        try {
            jdbcTemplate.update(DELETE_URL, namedParameters);
        } catch(Exception e) {
            throw new BaseException(e.getMessage(), AllGolbalConstants.REPO_LAYER, null);
        }
    }

    @Override
    public void undoDeleteDeliveryBoy(long mobile) throws BaseException {
        LOGGER.trace("Entering into undoDeleteDeliveryBoy method in DeliveryBoyRepositoryImpl with {}", mobile);
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("mobile", mobile);
        namedParameters.addValue("isdelete", AllGolbalConstants.FALSE);
        try {
            jdbcTemplate.update(DELETE_URL, namedParameters);
        } catch(Exception e) {
            throw new BaseException(e.getMessage(), AllGolbalConstants.REPO_LAYER, null);
        }
    }
}

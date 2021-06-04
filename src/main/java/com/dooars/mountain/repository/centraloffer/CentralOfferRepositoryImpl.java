package com.dooars.mountain.repository.centraloffer;

import com.dooars.mountain.constants.AllGolbalConstants;
import com.dooars.mountain.model.centraloffer.CentralOffer;
import com.dooars.mountain.model.common.BaseException;
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
import java.time.LocalDate;
import java.util.List;

/**
 * @author Prantik Guha on 23-05-2021
 **/

@Repository
public class CentralOfferRepositoryImpl implements CentralOfferRepository{

    private static final Logger LOGGER = LoggerFactory.getLogger(CentralOfferRepositoryImpl.class);

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final String ADD_OFFER = "INSERT INTO offers(offerName, offerDescription, offerImageName, offer, offerFrom, offerUpto, isdelete)   \r\n" +
            "VALUES (:offerName, :offerDescription, :offerImageName, :offer, :offerFrom, :offerUpto, :isdelete);";

    private final String LAST_ID = "SELECT lastval();";

    private final String GET_OFFER_BY_DATE = "SELECT * \n" +
            "  FROM offers \n" +
            " WHERE offerFrom <= to_date(:date,'YYYY-MM-DD')\n" +
            "   AND offerUpto >= to_date(:date,'YYYY-MM-DD') and isdelete = :isdelete";

    private final String GET_ALL_OFFER= "SELECT * FROM offers WHERE isdelete = :isdelete";

    private final String UPDATE_OFFER = "UPDATE offers set offerName = :offerName, offerDescription = :offerDescription, offerImageName = :offerImageName , offer = :offer, offerFrom = :offerFrom, offerUpto = :offerUpto where offerId = :offerId";

    private final String DELETE_OFFER = "UPDATE offers set isdelete = :isdelete where offerId = :offerId";

    private final RowMapper<CentralOffer> mapper = new RowMapper<CentralOffer>() {

        @Override
        public CentralOffer mapRow(ResultSet rs, int rowNum) throws SQLException {
            LOGGER.trace("Entering into CentralOffer mapper");
            CentralOffer centralOffer = new CentralOffer();
            centralOffer.setOfferId(rs.getLong("offerId"));
            centralOffer.setOfferDescription(rs.getString("offerDescription"));
            centralOffer.setOfferName(rs.getString("offerName"));
            centralOffer.setOfferImageName(rs.getString("offerImageName"));
            centralOffer.setOffer(rs.getFloat("offer"));
            if ( null == rs.getString("offerFrom")) {
                centralOffer.setOfferFrom(null);
            } else {
                centralOffer.setOfferFrom(LocalDate.parse(rs.getString("offerFrom")));
            }
            if ( null == rs.getString("offerUpto")) {
                centralOffer.setOfferUpto(null);
            } else {
                centralOffer.setOfferUpto(LocalDate.parse(rs.getString("offerUpto")));
            }
            return centralOffer;
        }
    };

    @Autowired
    CentralOfferRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }



    @Override
    public CentralOffer addOffer(CentralOffer centralOffer) throws BaseException {
        LOGGER.trace("Entering into addOffer method in OfferRepositoryImpl with {}", centralOffer.toString());
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("offerName", centralOffer.getOfferName());
        namedParameters.addValue("offerDescription", centralOffer.getOfferDescription());
        namedParameters.addValue("offerImageName", centralOffer.getOfferImageName());
        namedParameters.addValue("offer", centralOffer.getOffer());
        namedParameters.addValue("offerFrom", centralOffer.getOfferFrom());
        namedParameters.addValue("offerUpto", centralOffer.getOfferUpto());
        namedParameters.addValue("isdelete", AllGolbalConstants.FALSE);
        try {
            jdbcTemplate.update(ADD_OFFER, namedParameters);
            long id = DataAccessUtils.singleResult(jdbcTemplate.query(LAST_ID,(rs, rowNum) -> rs.getLong(1)));
            centralOffer.setOfferId(id);
            return centralOffer;
        } catch(Exception e) {
            throw new BaseException(e.getMessage(), AllGolbalConstants.REPO_LAYER, null);
        }
    }

    @Override
    public List<CentralOffer> getOfferByDate(String date) throws BaseException {
        LOGGER.trace("Entering into getOfferByDate method in OfferRepositoryImpl with {}", date);
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("isdelete", AllGolbalConstants.FALSE);
        namedParameters.addValue("date", date);
        return Try.ofSupplier(() -> jdbcTemplate.query(GET_OFFER_BY_DATE, namedParameters, mapper))
                .getOrElseThrow(throwable -> new BaseException(throwable.getMessage(), AllGolbalConstants.REPO_LAYER, null));
    }

    @Override
    public List<CentralOffer> getAllOffer() throws BaseException {
        LOGGER.trace("Entering into getAllOffer method in OfferRepositoryImpl with {}");
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("isdelete", AllGolbalConstants.FALSE);
        return Try.ofSupplier(() -> jdbcTemplate.query(GET_ALL_OFFER, namedParameters, mapper))
                .getOrElseThrow(throwable -> new BaseException(throwable.getMessage(), AllGolbalConstants.REPO_LAYER, null));
    }

    @Override
    public CentralOffer updateOffer(CentralOffer centralOffer) throws BaseException {
        LOGGER.trace("Entering into updateOffer method in OfferRepositoryImpl with {}", centralOffer.toString());
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("offerName", centralOffer.getOfferName());
        namedParameters.addValue("offerDescription", centralOffer.getOfferDescription());
        namedParameters.addValue("offerImageName", centralOffer.getOfferImageName());
        namedParameters.addValue("offer", centralOffer.getOffer());
        namedParameters.addValue("offerFrom", centralOffer.getOfferFrom());
        namedParameters.addValue("offerUpto", centralOffer.getOfferUpto());
        namedParameters.addValue("offerId", centralOffer.getOfferId());
        try {
            jdbcTemplate.update(UPDATE_OFFER, namedParameters);
            return centralOffer;
        } catch(Exception e) {
            throw new BaseException(e.getMessage(), AllGolbalConstants.REPO_LAYER, null);
        }
    }

    @Override
    public void deleteOffer(long offerId) throws BaseException {
        LOGGER.trace("Entering into deleteOffer method in OfferRepositoryImpl with {}", offerId);
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("isdelete", AllGolbalConstants.TRUE);
        namedParameters.addValue("offerId", offerId);
        try {
            jdbcTemplate.update(DELETE_OFFER, namedParameters);
        } catch(Exception e) {
            throw new BaseException(e.getMessage(), AllGolbalConstants.REPO_LAYER, null);
        }
    }
}

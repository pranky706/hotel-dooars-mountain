/**
 * 
 */
package com.dooars.mountain.repository.item;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.dooars.mountain.constants.AllGolbalConstants;
import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.model.item.Item;
import com.dooars.mountain.model.item.MenuPair;
import com.dooars.mountain.model.item.Offer;

import io.vavr.control.Try;

/**
 * @author Prantik Guha
 * 21-May-2021 
 * ItemRepositoryImpl.java
 */

@Repository
public class ItemRepositoryImpl implements ItemRepository {
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemRepositoryImpl.class);
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	private final String ADD_ITEM = "INSERT INTO items(itemName, description, imageName, offer, offerFrom, offerUpto, price, groupId, isAvailable, isdelete)   \r\n" + 
			"VALUES (:itemName, :description, :imageName, :offer, :offerFrom, :offerUpto, :price, :groupId, :isAvailable, :isdelete);";
	
	private final String LAST_ID = "SELECT lastval();";
	
	private final String GET_ITEM = "select * from items where itemId = :itemId and isdelete = :isdelete";
	
	private final String GET_ITEMS_BYGROUPID = "select * from items where groupId = :groupId and isdelete = :isdelete";
	
	private final String UPDATE_AVAIL = "UPDATE items set isAvailable = :isAvailable where itemId = :itemId";
	
	private final String DELETE_ITEM = "UPDATE items set isdelete = :isdelete where itemId = :itemId";
	
	private final String DELETE_ITEM_BY_GROUPID = "UPDATE items set isdelete = :isdelete where groupId = :groupId";
	
	private final String UPDATE_ITEM = "UPDATE items set itemName = :itemName, price = :price, description = :description, offer = :offer, offerFrom = :offerFrom, offerUpto = :offerUpto, imageName=:imageName, groupId = :groupId, isAvailable = :isAvailable  where itemId = :itemId";
	
	private final String GET_ALL_MENU = "select items.*, menu_group.groupName  from items, menu_group"
			+ " where items.groupId = menu_group.groupId and menu_group.isDelete = :isdelete and items.isDelete = :isdelete;";
	
	private final RowMapper<Item> mapper = new RowMapper<Item>() {
		
		@Override
		public Item mapRow(ResultSet rs, int rowNum) throws SQLException {
			LOGGER.trace("Entering into Item mapper");
			Item item = new Item();
			item.setItemId(rs.getInt("itemId"));
			item.setDescription(rs.getString("description"));
			item.setGroupId(rs.getInt("groupId"));
			item.setImageName(rs.getString("imageName"));
			item.setItemName(rs.getString("itemName"));
			item.setOffer(rs.getFloat("offer"));
			item.setIsAvailable(rs.getString("isAvailable"));
			if ( null == rs.getString("offerFrom")) {
				item.setOfferFrom(null);
			} else {
				item.setOfferFrom(LocalDate.parse(rs.getString("offerFrom")));
			}
			if ( null == rs.getString("offerUpto")) {
				item.setOfferUpto(null);
			} else {
				item.setOfferUpto(LocalDate.parse(rs.getString("offerUpto")));
			}
			item.setPrice(rs.getFloat("price"));
			return item;
		}
	};
	
	private final RowMapper<MenuPair> menuMapper = new RowMapper<MenuPair>() {
		
		@Override
		public MenuPair mapRow(ResultSet rs, int rowNum) throws SQLException {
			LOGGER.trace("Entering into MenuPair mapper");
			MenuPair menuPair = new MenuPair();
			Item item = new Item();
			item.setItemId(rs.getInt("itemId"));
			item.setDescription(rs.getString("description"));
			item.setGroupId(rs.getInt("groupId"));
			item.setImageName(rs.getString("imageName"));
			item.setItemName(rs.getString("itemName"));
			item.setOffer(rs.getFloat("offer"));
			item.setIsAvailable(rs.getString("isAvailable"));
			if ( null == rs.getString("offerFrom")) {
				item.setOfferFrom(null);
			} else {
				item.setOfferFrom(LocalDate.parse(rs.getString("offerFrom")));
			}
			if ( null == rs.getString("offerUpto")) {
				item.setOfferUpto(null);
			} else {
				item.setOfferUpto(LocalDate.parse(rs.getString("offerUpto")));
			}
			item.setPrice(rs.getFloat("price"));
			menuPair.setItem(item);
			menuPair.setGroupName(rs.getString("groupName"));
			return menuPair;
		}
	};
	
	@Autowired
	ItemRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public Item addItem(Item item) throws BaseException {
		LOGGER.trace("Entering into addItem method in ItemRepositoryImpl with {}", item.toString());
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("itemName", item.getItemName());
		namedParameters.addValue("description", item.getDescription());
		namedParameters.addValue("imageName", item.getImageName());
		namedParameters.addValue("offer", item.getOffer());
		namedParameters.addValue("offerFrom", item.getOfferFrom());
		namedParameters.addValue("offerUpto", item.getOfferUpto());
		namedParameters.addValue("price", item.getPrice());
		namedParameters.addValue("groupId", item.getGroupId());
		namedParameters.addValue("isdelete", AllGolbalConstants.FALSE);
		namedParameters.addValue("isAvailable", AllGolbalConstants.TRUE);
		try {
			jdbcTemplate.update(ADD_ITEM, namedParameters);
			int id = DataAccessUtils.singleResult(jdbcTemplate.query(LAST_ID,(rs, rowNum) -> rs.getInt(1)));
			item.setItemId(id);
			return item;
		} catch(Exception e) {
			throw new BaseException(e.getMessage(), AllGolbalConstants.REPO_LAYER, null);
		}
	}

	@Override
	public Item getItemById(int itemId) throws BaseException {
		LOGGER.trace("Entering into getItemById method in ItemRepositoryImpl with {}", itemId);
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("itemId", itemId);
		namedParameters.addValue("isdelete", AllGolbalConstants.FALSE);
		return Try.ofSupplier(() -> DataAccessUtils.singleResult(jdbcTemplate.query(GET_ITEM, namedParameters, mapper)))
			.getOrElseThrow(throwable -> new BaseException(throwable.getMessage(), AllGolbalConstants.REPO_LAYER, null));
	}

	@Override
	public List<Item> getItemByGroupId(int groupId) throws BaseException {
		LOGGER.trace("Entering into getItemByGroupId method in ItemRepositoryImpl with {}", groupId);
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("isdelete", AllGolbalConstants.FALSE);
		namedParameters.addValue("groupId", groupId);
		return Try.ofSupplier(() -> jdbcTemplate.query(GET_ITEMS_BYGROUPID, namedParameters, mapper))
			.getOrElseThrow(throwable -> new BaseException(throwable.getMessage(), AllGolbalConstants.REPO_LAYER, null));
	}

	@Override
	public void changeItemAvailability(int itemId, String status) throws BaseException {
		LOGGER.trace("Entering into changeItemAvailability method in ItemRepositoryImpl with {} {}", itemId, status);
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("itemId", itemId);
		namedParameters.addValue("isAvailable", status);
		try {
			jdbcTemplate.update(UPDATE_AVAIL, namedParameters);
		} catch(Exception e) {
			throw new BaseException(e.getMessage(), AllGolbalConstants.REPO_LAYER, null);
		}

	}

	@Override
	public void deleteItem(int itemId) throws BaseException {
		LOGGER.trace("Entering into deleteItem method in ItemRepositoryImpl with {}", itemId);
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("isdelete", AllGolbalConstants.TRUE);
		namedParameters.addValue("itemId", itemId);
		try {
			jdbcTemplate.update(DELETE_ITEM, namedParameters);
		} catch(Exception e) {
			throw new BaseException(e.getMessage(), AllGolbalConstants.REPO_LAYER, null);
		}

	}
	
	@Override
	public void deleteItemByGroupId(int groupId) throws BaseException {
		LOGGER.trace("Entering into deleteItemByGroupId method in ItemRepositoryImpl with {}", groupId);
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("isdelete", AllGolbalConstants.TRUE);
		namedParameters.addValue("groupId", groupId);
		try {
			jdbcTemplate.update(DELETE_ITEM_BY_GROUPID, namedParameters);
		} catch(Exception e) {
			throw new BaseException(e.getMessage(), AllGolbalConstants.REPO_LAYER, null);
		}

	}

	@Override
	public Item updateItem(Item item) throws BaseException {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("itemName", item.getItemName());
		namedParameters.addValue("price", item.getPrice());
		namedParameters.addValue("description", item.getDescription());
		namedParameters.addValue("itemId", item.getItemId());
		namedParameters.addValue("offer", item.getOffer());
		namedParameters.addValue("offerFrom", item.getOfferFrom());
		namedParameters.addValue("offerUpto", item.getOfferUpto());
		namedParameters.addValue("groupId", item.getGroupId());
		namedParameters.addValue("imageName", item.getImageName());
		namedParameters.addValue("isAvailable", item.getIsAvailable());
		try {
			jdbcTemplate.update(UPDATE_ITEM, namedParameters);
			return getItemById(item.getItemId());
		} catch(Exception e) {
			throw new BaseException(e.getMessage(), AllGolbalConstants.REPO_LAYER, null);
		}
	}
	
	private String getStacktrace( Throwable th) {
		StringWriter sw = new StringWriter();
        th.printStackTrace(new PrintWriter(sw));
        String exceptionAsString = sw.toString();
        return exceptionAsString;
	}

	@Override
	public List<MenuPair> getAllMenu() throws BaseException {
		LOGGER.trace("Entering into getAllMenu method in ItemRepositoryImpl with {}");
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("isdelete", AllGolbalConstants.FALSE);
		return Try.ofSupplier(() -> jdbcTemplate.query(GET_ALL_MENU, namedParameters, menuMapper))
				.getOrElseThrow(throwable -> new BaseException(throwable.getMessage(), AllGolbalConstants.REPO_LAYER, null));
	}

}

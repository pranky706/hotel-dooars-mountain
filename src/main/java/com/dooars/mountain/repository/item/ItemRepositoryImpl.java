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

import com.dooars.mountain.model.item.Category;
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
	
	private final String ADD_ITEM = "INSERT INTO items(itemName, description, imageName, offer, offerFrom, offerUpto, price, groupId, categoryId, isAvailable, isdelete)   \r\n" +
			"VALUES (:itemName, :description, :imageName, :offer, :offerFrom, :offerUpto, :price, :groupId, :categoryId, :isAvailable, :isdelete);";
	
	private final String LAST_ID = "SELECT lastval();";
	
	private final String GET_ITEM = "select * from items where itemId = :itemId and isdelete = :isdelete";
	
	private final String GET_ITEMS_BYGROUPID = "select * from items where groupId = :groupId and isdelete = :isdelete";
	
	private final String UPDATE_AVAIL = "UPDATE items set isAvailable = :isAvailable where itemId = :itemId";
	
	private final String DELETE_ITEM = "UPDATE items set isdelete = :isdelete where itemId = :itemId";
	
	private final String DELETE_ITEM_BY_GROUPID = "UPDATE items set isdelete = :isdelete where groupId = :groupId";
	
	private final String UPDATE_ITEM = "UPDATE items set categoryId = :categoryId, itemName = :itemName, price = :price, description = :description, offer = :offer, offerFrom = :offerFrom, offerUpto = :offerUpto, imageName=:imageName, groupId = :groupId, isAvailable = :isAvailable  where itemId = :itemId";
	
	private final String GET_ALL_MENU = "select items.*, menu_group.groupName  from items, menu_group"
			+ " where items.groupId = menu_group.groupId and menu_group.isDelete = :isdelete and items.isDelete = :isdelete;";

	private final String ADD_CATEGORY = "INSERT into category(categoryName, categoryImageName, isdelete) values(:categoryName, :categoryImageName, :isdelete)";

	private final String GET_ITEMS_BY_CATEGORY_ID = "select * from items where categoryId = :categoryId and isdelete = :isdelete";

	private final String GET_ITEMS = "select * from items where isdelete = :isdelete";

	private final String DELETE_CATEGORY = "UPDATE category set isdelete = :isdelete where categoryId = :categoryId";

	private final String DELETE_ITEM_BY_CATEGORY_ID = "UPDATE items set isdelete = :isdelete where categoryId = :categoryId";

	private final String UPDATE_CATEGORY = "UPDATE category set categoryName = :categoryName, categoryImageName = :categoryImageName where categoryId = :categoryId";

	private final String GET_ALL_CATEGORY = "select * from category where isdelete = :isdelete";


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
			item.setCategoryId(rs.getInt("categoryId"));
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
			item.setCategoryId(rs.getInt("categoryId"));
			menuPair.setItem(item);
			menuPair.setGroupName(rs.getString("groupName"));
			return menuPair;
		}
	};

	private final RowMapper<Category> categoryMapper = new RowMapper<Category>() {

		@Override
		public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
			LOGGER.trace("Entering into Category mapper");
			Category category = new Category();
			category.setCategoryId(rs.getInt("categoryId"));
			category.setCategoryName(rs.getString("categoryName"));
			category.setCategoryImageName(rs.getString("categoryImageName"));
			return category;
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
		namedParameters.addValue("categoryId", item.getCategoryId());
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
		namedParameters.addValue("categoryId", item.getCategoryId());
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

	@Override
	public Category addCategory(Category category) throws BaseException {
		LOGGER.trace("Entering into addCategory method in ItemRepositoryImpl with {}", category.toString());
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("categoryName", category.getCategoryName());
		namedParameters.addValue("categoryImageName", category.getCategoryImageName());
		namedParameters.addValue("isdelete", AllGolbalConstants.FALSE);
		try {
			jdbcTemplate.update(ADD_CATEGORY, namedParameters);
			int id = DataAccessUtils.singleResult(jdbcTemplate.query(LAST_ID,(rs, rowNum) -> rs.getInt(1)));
			category.setCategoryId(id);
			return category;
		} catch(Exception e) {
			throw new BaseException(e.getMessage(), AllGolbalConstants.REPO_LAYER, null);
		}
	}

	@Override
	public List<Item> getItemByCategoryId(int categoryId) throws BaseException {
		LOGGER.trace("Entering into getItemByCategoryId method in ItemRepositoryImpl with {}", categoryId);
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("isdelete", AllGolbalConstants.FALSE);
		namedParameters.addValue("categoryId", categoryId);
		return Try.ofSupplier(() -> jdbcTemplate.query(GET_ITEMS_BY_CATEGORY_ID, namedParameters, mapper))
				.getOrElseThrow(throwable -> new BaseException(throwable.getMessage(), AllGolbalConstants.REPO_LAYER, null));
	}

	@Override
	public List<Item> getAllItem() throws BaseException {
		LOGGER.trace("Entering into getAllItem method in ItemRepositoryImpl with");
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("isdelete", AllGolbalConstants.FALSE);
		return Try.ofSupplier(() -> jdbcTemplate.query(GET_ITEMS, namedParameters, mapper))
				.getOrElseThrow(throwable -> new BaseException(throwable.getMessage(), AllGolbalConstants.REPO_LAYER, null));
	}

	@Override
	public void deleteCategory(int categoryId) throws BaseException {
		LOGGER.trace("Entering into deleteCategory method in ItemRepositoryImpl with {}", categoryId);
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("isdelete", AllGolbalConstants.TRUE);
		namedParameters.addValue("categoryId", categoryId);
		try {
			jdbcTemplate.update(DELETE_CATEGORY, namedParameters);
		} catch(Exception e) {
			throw new BaseException(e.getMessage(), AllGolbalConstants.REPO_LAYER, null);
		}
	}

	@Override
	public void deleteItemByCategory(int categoryId) throws BaseException {
		LOGGER.trace("Entering into deleteItemByCategory method in ItemRepositoryImpl with {}", categoryId);
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("isdelete", AllGolbalConstants.TRUE);
		namedParameters.addValue("categoryId", categoryId);
		try {
			jdbcTemplate.update(DELETE_ITEM_BY_CATEGORY_ID, namedParameters);
		} catch(Exception e) {
			throw new BaseException(e.getMessage(), AllGolbalConstants.REPO_LAYER, null);
		}
	}

	@Override
	public void updateCategory(Category category) throws BaseException {
		LOGGER.trace("Entering into updateCategory method in ItemRepositoryImpl with {}", category.toString());
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("categoryName", category.getCategoryName());
		namedParameters.addValue("categoryImageName", category.getCategoryImageName());
		namedParameters.addValue("categoryId", category.getCategoryId());
		try {
			jdbcTemplate.update(UPDATE_CATEGORY, namedParameters);
		} catch(Exception e) {
			throw new BaseException(e.getMessage(), AllGolbalConstants.REPO_LAYER, null);
		}
	}

	@Override
	public List<Category> getAllCategory() throws BaseException {
		LOGGER.trace("Entering into getAllCategory method in ItemRepositoryImpl with");
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("isdelete", AllGolbalConstants.FALSE);
		return Try.ofSupplier(() -> jdbcTemplate.query(GET_ALL_CATEGORY, namedParameters, categoryMapper))
				.getOrElseThrow(throwable -> new BaseException(throwable.getMessage(), AllGolbalConstants.REPO_LAYER, null));
	}

}

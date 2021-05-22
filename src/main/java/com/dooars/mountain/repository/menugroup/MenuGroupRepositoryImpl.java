/**
 * 
 */
package com.dooars.mountain.repository.menugroup;

import java.sql.ResultSet;
import java.sql.SQLException;
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
import com.dooars.mountain.model.menugroup.MenuGroup;

import io.vavr.control.Try;

/**
 * @author Prantik Guha
 * 21-May-2021 
 * MenuGroupRepositoryImpl.java
 */

@Repository
public class MenuGroupRepositoryImpl implements MenuGroupRepository {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MenuGroupRepositoryImpl.class);
	
	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	private final String ADD_MENU_GROUP = "INSERT INTO menu_group(groupName, isdelete)   \r\n" + 
			"VALUES (:groupName, :isdelete);";
	
	private final String LAST_ID = "SELECT lastval();";
	
	private final String GET_MENU_GROUP = "select * from menu_group where groupId = :groupId and isdelete = :isdelete";
	
	private final String GET_ALL_MENU_GROUP = "select * from menu_group where isdelete = :isdelete";
	
	private final String DELETE_MENU_GROUP = "UPDATE menu_group set isdelete = :isdelete where groupId = :groupId";
	
	private final String UPDATE_MENU_GROUP = "UPDATE menu_group set groupName = :groupName where groupId = :groupId";
	
	private final RowMapper<MenuGroup> mapper = new RowMapper<MenuGroup>() {
		
		@Override
		public MenuGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
			LOGGER.trace("Entering into MenuGroup mapper");
			MenuGroup menuGroup = new MenuGroup();
			menuGroup.setGroupId(rs.getInt("groupId"));
			menuGroup.setGroupName(rs.getString("groupName"));
			return menuGroup;
		}
	};
	
	@Autowired
	MenuGroupRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public MenuGroup addMenuGroup(String name) throws BaseException {
		LOGGER.trace("Entering into addMenuGroup method in MenuGroupRepositoryImpl with {}", name);
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("groupName", name);
		namedParameters.addValue("isdelete", AllGolbalConstants.FALSE);
		try {
			jdbcTemplate.update(ADD_MENU_GROUP, namedParameters);
			int id = DataAccessUtils.singleResult(jdbcTemplate.query(LAST_ID,(rs, rowNum) -> rs.getInt(1)));
			MenuGroup group = new MenuGroup();
			group.setGroupId(id);
			group.setGroupName(name);
			return group;
		} catch(Exception e) {
			throw new BaseException(e.getMessage(), AllGolbalConstants.REPO_LAYER, null);
		}

	}

	@Override
	public MenuGroup getMenuGroupById(int id) throws BaseException {
		LOGGER.trace("Entering into getMenuGroupById method in MenuGroupRepositoryImpl with {}", id);
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("groupId", id);
		namedParameters.addValue("isdelete", AllGolbalConstants.FALSE);
		return Try.ofSupplier(() -> DataAccessUtils.singleResult(jdbcTemplate.query(GET_MENU_GROUP, namedParameters, mapper)))
			.getOrElseThrow(throwable -> new BaseException(throwable.getMessage(), AllGolbalConstants.REPO_LAYER, null));
	}

	@Override
	public List<MenuGroup> getAllMenuGroups() throws BaseException {
		LOGGER.trace("Entering into getAllMenuGroups method in MenuGroupRepositoryImpl with {}");
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("isdelete", AllGolbalConstants.FALSE);
		return Try.ofSupplier(() -> jdbcTemplate.query(GET_ALL_MENU_GROUP, namedParameters, mapper))
			.getOrElseThrow(throwable -> new BaseException(throwable.getMessage(), AllGolbalConstants.REPO_LAYER, null));
	}

	@Override
	public MenuGroup updateMenuGroup(MenuGroup menuGroup) throws BaseException {
		LOGGER.trace("Entering into updateMenuGroup method in MenuGroupRepositoryImpl with {}", menuGroup.toString());
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("groupName", menuGroup.getGroupName());
		namedParameters.addValue("groupId", menuGroup.getGroupId());
		try {
			jdbcTemplate.update(UPDATE_MENU_GROUP, namedParameters);
			return menuGroup;
		} catch(Exception e) {
			throw new BaseException(e.getMessage(), AllGolbalConstants.REPO_LAYER, null);
		}

	}

	@Override
	public void deleteMenuGroup(int id) throws BaseException {
		LOGGER.trace("Entering into updateMenuGroup method in MenuGroupRepositoryImpl with {}", id);
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("isdelete", AllGolbalConstants.TRUE);
		namedParameters.addValue("groupId", id);
		try {
			jdbcTemplate.update(DELETE_MENU_GROUP, namedParameters);
		} catch(Exception e) {
			throw new BaseException(e.getMessage(), AllGolbalConstants.REPO_LAYER, null);
		}

	}

}

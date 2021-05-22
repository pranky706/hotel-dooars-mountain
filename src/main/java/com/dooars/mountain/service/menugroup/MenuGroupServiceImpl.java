/**
 * 
 */
package com.dooars.mountain.service.menugroup;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.model.menugroup.MenuGroup;
import com.dooars.mountain.repository.item.ItemRepository;
import com.dooars.mountain.repository.menugroup.MenuGroupRepository;

/**
 * @author Prantik Guha
 * 21-May-2021 
 * MenuGroupServiceImpl.java
 */

@Service
public class MenuGroupServiceImpl implements MenuGroupService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MenuGroupServiceImpl.class);
	
	private final MenuGroupRepository menuGroupRepository;
	private final ItemRepository itemRepository;
	
	@Autowired
	MenuGroupServiceImpl(MenuGroupRepository menuGroupRepository, ItemRepository itemRepository) {
		this.menuGroupRepository = menuGroupRepository;
		this.itemRepository = itemRepository;
	}

	@Override
	public MenuGroup addMenuGroup(String name) throws BaseException {
		LOGGER.trace("Entering into addMenuGroup method in MenuGroupServiceImpl with{}", name);
		return menuGroupRepository.addMenuGroup(name);
	}

	@Override
	public MenuGroup getMenuGroupById(int id) throws BaseException {
		LOGGER.trace("Entering into getMenuGroupById method in MenuGroupServiceImpl with{}", id);
		return menuGroupRepository.getMenuGroupById(id);
	}

	@Override
	public List<MenuGroup> getAllMenuGroups() throws BaseException {
		LOGGER.trace("Entering into getAllMenuGroups method in MenuGroupServiceImpl with{}");
		return menuGroupRepository.getAllMenuGroups();
	}

	@Override
	public MenuGroup updateMenuGroup(MenuGroup menuGroup) throws BaseException {
		LOGGER.trace("Entering into updateMenuGroup method in MenuGroupServiceImpl with{}", menuGroup.toString());
		return menuGroupRepository.updateMenuGroup(menuGroup);

	}

	@Override
	public void deleteMenuGroup(int id) throws BaseException {
		LOGGER.trace("Entering into deleteMenuGroup method in MenuGroupServiceImpl with{}", id);
		menuGroupRepository.deleteMenuGroup(id);
		itemRepository.deleteItemByGroupId(id);
	}

}

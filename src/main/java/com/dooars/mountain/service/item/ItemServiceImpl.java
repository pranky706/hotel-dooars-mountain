/**
 * 
 */
package com.dooars.mountain.service.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.model.item.GroupValue;
import com.dooars.mountain.model.item.Item;
import com.dooars.mountain.model.item.Menu;
import com.dooars.mountain.model.item.MenuPair;
import com.dooars.mountain.model.item.Offer;
import com.dooars.mountain.model.menugroup.MenuGroup;
import com.dooars.mountain.repository.item.ItemRepository;
import com.dooars.mountain.repository.menugroup.MenuGroupRepository;

/**
 * @author Prantik Guha
 * 21-May-2021 
 * ItemServiceImpl.java
 */

@Service
public class ItemServiceImpl implements ItemService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemServiceImpl.class);
	
	private final ItemRepository itemRepo;
	private final MenuGroupRepository menuRepo;
	
	@Autowired
	ItemServiceImpl(ItemRepository itemRepo, MenuGroupRepository menuRepo) {
		this.itemRepo = itemRepo;
		this.menuRepo = menuRepo;
	}

	@Override
	public Item addItem(Item item) throws BaseException {
		LOGGER.trace("Entering into addItem method in ItemServiceImpl with {}", item.toString());
		return itemRepo.addItem(item);
	}

	@Override
	public Item getItemById(int itemId) throws BaseException {
		LOGGER.trace("Entering into getItemById method in ItemServiceImpl with {}", itemId);
		return itemRepo.getItemById(itemId);
	}

	@Override
	public List<Item> getItemByGroupId(int groupId) throws BaseException {
		LOGGER.trace("Entering into getItemByGroupId method in ItemServiceImpl with {}", groupId);
		return itemRepo.getItemByGroupId(groupId);
	}

	@Override
	public void changeItemAvailability(int itemId, String status) throws BaseException {
		LOGGER.trace("Entering into changeItemAvailability method in ItemServiceImpl with {}", itemId);
		itemRepo.changeItemAvailability(itemId, status);
	}

	@Override
	public Item updateOffer(Offer offer, int itemId) throws BaseException {
		LOGGER.trace("Entering into updateOffer method in ItemServiceImpl with {} {}", offer.toString(), itemId);
		return itemRepo.updateOffer(offer, itemId);
	}

	@Override
	public void deleteItem(int itemId) throws BaseException {
		LOGGER.trace("Entering into deleteItem method in ItemServiceImpl with {}", itemId);
		itemRepo.deleteItem(itemId);
	}

	@Override
	public Item updateItem(Item item) throws BaseException {
		LOGGER.trace("Entering into updateItem method in ItemServiceImpl with {}", item.toString());
		return itemRepo.updateItem(item);
	}

//	@Override
//	public Menu getMenu() throws BaseException {
//		LOGGER.trace("Entering into getMenu method in ItemServiceImpl with {}");
//		List<MenuGroup> menuGroups = menuRepo.getAllMenuGroups();
//		List<GroupValue> groupValues = new ArrayList<GroupValue>();
//		for (MenuGroup mg : menuGroups) {
//			GroupValue groupValue = new GroupValue();
//			groupValue.setGroupName(mg.getGroupName());
//			groupValue.setItems(itemRepo.getItemByGroupId(mg.getGroupId()));
//			groupValues.add(groupValue);
//		}
//		Menu menu = new Menu();
//		menu.setGroupValues(groupValues);
//		return menu;
//	}
	
	@Override
	public Menu getMenu() throws BaseException {
		LOGGER.trace("Entering into getMenu method in ItemServiceImpl with {}");
		List<MenuPair> menuPairs = itemRepo.getAllMenu();
		Map<String, List<Item>> map = new HashMap<String, List<Item>>();
		for (MenuPair mp : menuPairs) {
			if ( !map.containsKey(mp.getGroupName())) {
				map.put(mp.getGroupName(), new ArrayList<Item>());
			}
			List<Item> items = map.get(mp.getGroupName());
			items.add(mp.getItem());
			map.put(mp.getGroupName(), items);
		}
		Menu menu = new Menu();
		List<GroupValue> groupValues = new ArrayList<GroupValue>();
		for (Map.Entry<String,List<Item>> entry : map.entrySet()) {
			GroupValue groupValue = new GroupValue();
			groupValue.setGroupName(entry.getKey());
			groupValue.setItems(entry.getValue());
			groupValues.add(groupValue);
		}
		menu.setGroupValues(groupValues);
		return menu;
	}
	
	

}

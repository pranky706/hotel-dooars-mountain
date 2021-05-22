/**
 * 
 */
package com.dooars.mountain.repository.item;

import java.util.List;

import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.model.item.Item;
import com.dooars.mountain.model.item.MenuPair;
import com.dooars.mountain.model.item.Offer;

/**
 * @author Prantik Guha
 * 21-May-2021 
 * ItemRepository.java
 */
public interface ItemRepository {
	
	Item addItem(Item item) throws BaseException;
	Item getItemById(int itemId) throws BaseException;
	List<Item> getItemByGroupId(int groupId) throws BaseException;
	void changeItemAvailability(int itemId, String status) throws BaseException;
	Item updateOffer(Offer offer, int itemId) throws BaseException;
	void deleteItem(int itemId) throws BaseException;
	void deleteItemByGroupId(int groupId) throws BaseException;
	Item updateItem( Item item) throws BaseException;
	List<MenuPair> getAllMenu() throws BaseException;
}

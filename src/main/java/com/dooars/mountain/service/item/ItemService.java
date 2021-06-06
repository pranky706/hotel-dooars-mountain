/**
 * 
 */
package com.dooars.mountain.service.item;

import java.util.List;

import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.model.item.Category;
import com.dooars.mountain.model.item.Item;
import com.dooars.mountain.model.item.Menu;
import com.dooars.mountain.model.item.Offer;

/**
 * @author Prantik Guha
 * 21-May-2021 
 * ItemService.java
 */
public interface ItemService {
	
	Item addItem(Item item) throws BaseException;
	List<Item> addItems(List<Item> item) throws BaseException;
	Item getItemById(int itemId) throws BaseException;
	List<Item> getItemByGroupId(int groupId) throws BaseException;
	void changeItemAvailability(int itemId, String status) throws BaseException;
	void deleteItem(int itemId) throws BaseException;
	Item updateItem( Item item) throws BaseException;
	Menu getMenu() throws BaseException;
	Category addCategory(Category category) throws BaseException;
	List<Item> getItemByCategoryId(int categoryId) throws BaseException;
	void deleteCategory(int categoryId) throws BaseException;
	void updateCategory(Category category) throws BaseException;
	List<Category> getAllCategory() throws BaseException;
}

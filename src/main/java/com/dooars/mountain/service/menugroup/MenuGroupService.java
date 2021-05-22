/**
 * 
 */
package com.dooars.mountain.service.menugroup;

import java.util.List;

import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.model.menugroup.MenuGroup;

/**
 * @author Prantik Guha
 * 21-May-2021 
 * MenuGroupService.java
 */
public interface MenuGroupService {
	
	MenuGroup addMenuGroup(String name) throws BaseException;
	MenuGroup getMenuGroupById(int id) throws BaseException;
	List<MenuGroup> getAllMenuGroups() throws BaseException;
	MenuGroup updateMenuGroup(MenuGroup menuGroup) throws BaseException;
	void deleteMenuGroup(int id) throws BaseException;

}

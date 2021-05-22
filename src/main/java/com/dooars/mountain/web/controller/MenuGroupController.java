/**
 * 
 */
package com.dooars.mountain.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.model.menugroup.MenuGroup;
import com.dooars.mountain.service.menugroup.MenuGroupService;


/**
 * @author Prantik Guha
 * 21-May-2021 
 * MenuGroupController.java
 */

@CrossOrigin
@RestController
@RequestMapping("/api/menu-group-service")
public class MenuGroupController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MenuGroupController.class);
	
	private final MenuGroupService service;
	private ControllerHelper helper;
	
	public MenuGroupController(MenuGroupService service, ControllerHelper helper) {
		this.service = service;
		this.helper = helper;
	}
	
	
	@SuppressWarnings("unchecked")
	@PostMapping("/addMenuGroup")
	public <T> ResponseEntity<T> addMenuGroup(@RequestParam("groupName") String groupName) {
		LOGGER.trace("Entering into addMenuGroup method in MenuGroupController with{}", groupName);
		try {
			if ( null == groupName || "".equals(groupName)) {
				return new ResponseEntity<T>(HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<T>((T) service.addMenuGroup(groupName), HttpStatus.OK);
		} catch (BaseException e) {
			return helper.constructErrorResponse(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping("/getAllMenuGroup")
	public <T> ResponseEntity<T> getAllMenuGroup() {
		LOGGER.trace("Entering into getAllMenuGroup method in MenuGroupController with{}");
		try {
			return new ResponseEntity<T>((T) service.getAllMenuGroups(), HttpStatus.OK);
		} catch (BaseException e) {
			return helper.constructErrorResponse(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping("/getMenuGroup")
	public <T> ResponseEntity<T> getMenuGroup(@RequestParam("groupId") int groupId) {
		LOGGER.trace("Entering into getAllMenuGroup method in MenuGroupController with{}", groupId);
		try {
			MenuGroup menuGroup = service.getMenuGroupById(groupId);
			if ( null != menuGroup) {
				return new ResponseEntity<T>((T) menuGroup, HttpStatus.OK);
			} else {
				return new ResponseEntity<T>(HttpStatus.NOT_FOUND);
			}
			
		} catch (BaseException e) {
			return helper.constructErrorResponse(e);
		}
	}
	
	@PostMapping("/deleteMenuGroup")
	public <T> ResponseEntity<T> deleteMenuGroup(@RequestParam("groupId") int groupId) {
		LOGGER.trace("Entering into deleteMenuGroup method in MenuGroupController with{}", groupId);
		try {
			service.deleteMenuGroup(groupId);
			return new ResponseEntity<T>(HttpStatus.OK);
			
		} catch (BaseException e) {
			return helper.constructErrorResponse(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping("/updateMenuGroup")
	public <T> ResponseEntity<T> updateMenuGroup(@RequestBody MenuGroup menuGroup) {
		LOGGER.trace("Entering into updateMenuGroup method in MenuGroupController with{}", menuGroup.toString());
		try {
			if ( null == menuGroup || "".equals(menuGroup.getGroupName())) {
				return new ResponseEntity<T>(HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<T>((T) service.updateMenuGroup(menuGroup), HttpStatus.OK);
		} catch (BaseException e) {
			return helper.constructErrorResponse(e);
		}
	}

}

/**
 * 
 */
package com.dooars.mountain.web.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.model.item.Item;
import com.dooars.mountain.model.item.Menu;
import com.dooars.mountain.model.item.Offer;
import com.dooars.mountain.service.item.ItemService;



/**
 * @author Prantik Guha
 * 21-May-2021 
 * ItemController.java
 */

@CrossOrigin
@RestController
@RequestMapping("/api/item-service")
public class ItemController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemController.class);
	
	private final ItemService service;
	private ControllerHelper helper;
	private final Validator validator;
	
	public ItemController(ItemService service, ControllerHelper helper, 
			@Qualifier("addItemValidator") Validator validator) {
		this.service = service;
		this.helper = helper;
		this.validator = validator;
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping("/addItem")
	public <T> ResponseEntity<T> addItem(@RequestBody Item item, BindingResult bindingResult) {
		LOGGER.trace("Entering into addItem method in ItemController with{}", item.toString());
		return (ResponseEntity<T>) helper.validateAndExecute(validator, bindingResult, item, () -> service.addItem(item));		
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping("/getItemsByGroupId")
	public <T> ResponseEntity<T> getItemsByGroupId(@RequestParam("groupId") int groupId) {
		LOGGER.trace("Entering into getItemsByGroupId method in ItemController with{}", groupId);
		try {
			List<Item> items = service.getItemByGroupId(groupId);
			if ( null != items && items.size() > 0) {
				return new ResponseEntity<T>((T) items, HttpStatus.OK);
			} else {
				return new ResponseEntity<T>(HttpStatus.NOT_FOUND);
			}
			
		} catch (BaseException e) {
			return helper.constructErrorResponse(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping("/getItemByItemId")
	public <T> ResponseEntity<T> getItemByItemId(@RequestParam("itemId") int itemId) {
		LOGGER.trace("Entering into getItemByItemId method in ItemController with{}", itemId);
		try {
			Item item = service.getItemById(itemId);
			if ( null != item) {
				return new ResponseEntity<T>((T) item, HttpStatus.OK);
			} else {
				return new ResponseEntity<T>(HttpStatus.NOT_FOUND);
			}
			
		} catch (BaseException e) {
			return helper.constructErrorResponse(e);
		}
	}
	

	@PostMapping("/changeItemAvailability")
	public <T> ResponseEntity<T> changeItemAvailability(@RequestParam("itemId") int itemId, @RequestParam("status") String status) {
		LOGGER.trace("Entering into changeItemAvailability method in ItemController with{}", itemId);
		try {
			service.changeItemAvailability(itemId, status);
			return new ResponseEntity<T>( HttpStatus.OK);			
		} catch (BaseException e) {
			return helper.constructErrorResponse(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping("/updateOffer")
	public <T> ResponseEntity<T> updateOffer(@RequestParam("itemId") int itemId, @RequestBody Offer offer) {
		LOGGER.trace("Entering into updateOffer method in ItemController with{}", itemId);
		try {
			Item item = service.updateOffer(offer, itemId);
			if ( null != item) {
				return new ResponseEntity<T>((T) item, HttpStatus.OK);
			} else {
				return new ResponseEntity<T>(HttpStatus.NOT_FOUND);
			}
			
		} catch (BaseException e) {
			return helper.constructErrorResponse(e);
		}
	}
	
	@PostMapping("/deleteItem")
	public <T> ResponseEntity<T> deleteItem(@RequestParam("itemId") int itemId) {
		LOGGER.trace("Entering into deleteItem method in ItemController with{}", itemId);
		try {
			service.deleteItem(itemId);
			return new ResponseEntity<T>(HttpStatus.OK);
			
		} catch (BaseException e) {
			return helper.constructErrorResponse(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping("/updateItem")
	public <T> ResponseEntity<T> updateItem(@RequestBody Item item, BindingResult bindingResult) {
		LOGGER.trace("Entering into updateItem method in ItemController with{}", item.toString());
		return (ResponseEntity<T>) helper.validateAndExecute(validator, bindingResult, item, () -> service.updateItem(item));		
	}
	
	
	@SuppressWarnings("unchecked")
	@PostMapping("/getMenu")
	public <T> ResponseEntity<T> getMenu() {
		LOGGER.trace("Entering into getMenu method in ItemController with{}");
		try {
			Menu menu = service.getMenu();
			if ( null != menu && null != menu.getGroupValues() && menu.getGroupValues().size() > 0) {
				return new ResponseEntity<T>((T) menu, HttpStatus.OK);
			} else {
				return new ResponseEntity<T>(HttpStatus.NOT_FOUND);
			}
			
		} catch (BaseException e) {
			return helper.constructErrorResponse(e);
		}
	}
	

}

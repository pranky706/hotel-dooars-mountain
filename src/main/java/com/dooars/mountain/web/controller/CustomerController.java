/**
 * 
 */
package com.dooars.mountain.web.controller;

import com.dooars.mountain.constants.AllEndPoints;
import com.dooars.mountain.constants.AllGolbalConstants;
import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.model.customer.CustomerToken;
import com.dooars.mountain.model.customer.Location;
import com.dooars.mountain.model.customer.Platform;
import com.dooars.mountain.model.deliveryboy.DeliveryBoy;
import com.dooars.mountain.model.order.CurrentStatus;
import com.dooars.mountain.model.order.Order;
import com.dooars.mountain.web.commands.order.UpdateOrderStatus;
import com.dooars.mountain.web.commands.token.AddPushTokenCommand;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import com.dooars.mountain.model.common.ApiResult;
import com.dooars.mountain.model.customer.Customer;
import com.dooars.mountain.service.customer.CustomerService;
import com.dooars.mountain.constants.CustomerConstants;

import java.security.SignatureException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Prantik Guha
 * 19-May-2021 
 * CustomerController.java
 */

@CrossOrigin
@RestController
public class CustomerController {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerController.class);
	
	private final CustomerService service;
	private final Validator validator, addLocationValidator, updateLocationValidator,
			updateOrderStatusValidator, placeOrderValidator, addPushTokenValidator;
	private ControllerHelper helper;
	
	public CustomerController(CustomerService service, @Qualifier("addCustomerValidator") Validator validator,
							  @Qualifier("addLocationValidator") Validator addLocationValidator,
							  @Qualifier("updateLocationValidator") Validator updateLocationValidator,
							  @Qualifier("updateOrderStatusValidator") Validator updateOrderStatusValidator,
							  @Qualifier("placeOrderValidator") Validator placeOrderValidator,
							  @Qualifier("addPushTokenValidator") Validator addPushTokenValidator,
							  ControllerHelper helper) {
		this.service = service;
		this.validator = validator;
		this.addLocationValidator = addLocationValidator;
		this.updateLocationValidator = updateLocationValidator;
		this.updateOrderStatusValidator = updateOrderStatusValidator;
		this.placeOrderValidator = placeOrderValidator;
		this.addPushTokenValidator = addPushTokenValidator;
		this.helper = helper;
	}
	
	
	@SuppressWarnings("unchecked")
	@PostMapping(CustomerConstants.ADD_CUSTOMER_URL)
	public <T> ResponseEntity<T> addCustomer(@RequestBody Customer customer, BindingResult bindingResult) {
		LOGGER.trace("Entering into addCustomer method in CustomerController with{}", customer.toString());
		return (ResponseEntity<T>) helper.validateAndExecute(validator, bindingResult, customer, () -> service.addCustomer(customer));		
	}

	@PostMapping(CustomerConstants.GET_CUSTOMER_URL)
	public <T> ResponseEntity<T> getCustomer(@RequestParam("mobileNumber") long mobileNumber) {
		LOGGER.trace("Entering into addCustomer method in CustomerController with {}", mobileNumber);
		try {
			Customer customer = service.getCustomer(mobileNumber);
			if ( null != customer) {
				return new ResponseEntity<T>((T) customer, HttpStatus.OK);
			} else {
				return new ResponseEntity<T>(HttpStatus.NOT_FOUND);
			}

		} catch (BaseException e) {
			return helper.constructErrorResponse(e);
		}
	}

	@PostMapping(CustomerConstants.ADD_LOCATION_URL)
	public <T> ResponseEntity<T> addLocation(@RequestBody Location location, @RequestParam("mobileNumber") long mobileNumber, BindingResult bindingResult) {
		LOGGER.trace("Entering into addLocation method in CustomerController with {}", location);
		try {
			addLocationValidator.validate(location, bindingResult);
			if (bindingResult.hasErrors())
				return helper.constructFieldErrorResponse(bindingResult);
			Location addedLocation = service.addLocation(location, mobileNumber);
			if ( null != addedLocation) {
				return new ResponseEntity<T>((T) addedLocation, HttpStatus.OK);
			} else {
				return new ResponseEntity<T>(HttpStatus.BAD_REQUEST);
			}

		} catch (BaseException | JsonProcessingException e) {
			return helper.constructErrorResponse(e);
		}
	}

	@PostMapping(CustomerConstants.PLACE_ORDER_URL)
	public <T> ResponseEntity<T> placeOrder(@RequestBody Order order, BindingResult bindingResult) {
		LOGGER.trace("Entering into placeOrder method in CustomerController with {}", order);
		try {
			placeOrderValidator.validate(order, bindingResult);
			if (bindingResult.hasErrors())
				return helper.constructFieldErrorResponse(bindingResult);
			if (!service.verifySign(order, order.getMobileNumber()))
				return new ResponseEntity<T>(HttpStatus.CONFLICT);
			Order orderAdded = service.addOrder(order, order.getMobileNumber());
			if ( null != orderAdded) {
				return new ResponseEntity<T>((T) orderAdded, HttpStatus.OK);
			} else {
				Map<String, Object> map = new HashMap<>();
				map.put("message", "Location Not found or we currently do not deliver to this location.");
				return new ResponseEntity<T>((T) map, HttpStatus.BAD_REQUEST);
			}

		} catch (BaseException | JsonProcessingException | SignatureException e) {
			return helper.constructErrorResponse(e);
		}
	}

	@PostMapping(CustomerConstants.ADD_PUSH_TOKEN_URL)
	public <T> ResponseEntity<T> addPushToken(@RequestBody AddPushTokenCommand command, BindingResult bindingResult) {
		LOGGER.trace("Entering into addPushToken method in CustomerController with {}", command.toString());
		try {
			addPushTokenValidator.validate(command, bindingResult);
			if (bindingResult.hasErrors())
				return helper.constructFieldErrorResponse(bindingResult);
			service.addPushToken(command);
			return new ResponseEntity<T>(HttpStatus.OK);
		} catch (BaseException | JsonProcessingException e) {
			return helper.constructErrorResponse(e);
		}
	}

	@PostMapping(CustomerConstants.GET_PUSH_TOKEN_URL)
	public <T> ResponseEntity<T> getPushTokens(@RequestParam("mobileNumber") long mobileNumber) {
		LOGGER.trace("Entering into getPushTokens method in CustomerController with {}", mobileNumber);
		try {
			List<CustomerToken> customerTokens = service.getTokens(mobileNumber);
			if ( null != customerTokens && customerTokens.size() > 0) {
				return new ResponseEntity<T>((T) customerTokens, HttpStatus.OK);
			} else {
				return new ResponseEntity<T>((T) Collections.emptyList(), HttpStatus.NOT_FOUND);
			}

		} catch (BaseException e) {
			return helper.constructErrorResponse(e);
		}
	}

	@PostMapping(CustomerConstants.GET_LOCATION_URL)
	public <T> ResponseEntity<T> getLocation(@RequestParam("mobileNumber") long mobileNumber) {
		LOGGER.trace("Entering into getLocation method in CustomerController with {}", mobileNumber);
		try {
			List<Location> locations = service.getLocations(mobileNumber);
			if ( null != locations && locations.size() > 0) {
				return new ResponseEntity<T>((T) locations, HttpStatus.OK);
			} else {
				return new ResponseEntity<T>((T) Collections.emptyList(), HttpStatus.NOT_FOUND);
			}

		} catch (BaseException e) {
			return helper.constructErrorResponse(e);
		}
	}

	@PostMapping(CustomerConstants.GET_ORDER_URL)
	public <T> ResponseEntity<T> getOrders(@RequestParam("mobileNumber") long mobileNumber) {
		LOGGER.trace("Entering into getOrders method in CustomerController with {}", mobileNumber);
		try {
			List<Order> orders = service.getOrders(mobileNumber);
			if ( null != orders && orders.size() > 0) {
				return new ResponseEntity<T>((T) orders, HttpStatus.OK);
			} else {
				return new ResponseEntity<T>((T) Collections.emptyList(), HttpStatus.NOT_FOUND);
			}

		} catch (BaseException e) {
			return helper.constructErrorResponse(e);
		}
	}

	@PostMapping(AllEndPoints.GET_ALL_ORDER_URL)
	public <T> ResponseEntity<T> getAllOrders(@RequestParam("noOfObjects") int noOfObjects,@RequestParam("currentPage") int currentPage) {
		LOGGER.trace("Entering into getAllOrders method in CustomerController with");
		try {
			List<Order> orders = service.getAllOrders(noOfObjects, currentPage);
			if ( null != orders && orders.size() > 0) {
				Map<String, Object> map = new HashMap<>();
				map.put("totalOrderCount", service.getOrderCount());
				map.put("orders", orders);
				map.put("currentPage", currentPage);
				map.put("bucketUrl", AllGolbalConstants.BUCKET_URL);
				return new ResponseEntity<T>((T) map, HttpStatus.OK);
			} else {
				return new ResponseEntity<T>((T) Collections.emptyList(), HttpStatus.NOT_FOUND);
			}

		} catch (BaseException e) {
			return helper.constructErrorResponse(e);
		}
	}

	@PostMapping(AllEndPoints.GET_ALL_ORDER_BY_STATUS_URL)
	public <T> ResponseEntity<T> getOrdersByStatus(@RequestParam("currentStatus") CurrentStatus currentStatus, @RequestParam("noOfObjects") int noOfObjects,@RequestParam("currentPage") int currentPage) {
		LOGGER.trace("Entering into getOrdersByStatus method in CustomerController with {}", currentStatus);
		try {
			List<Order> orders = service.getAllOrdersByStatus(currentStatus, noOfObjects, currentPage);
			if ( null != orders && orders.size() > 0) {
				Map<String, Object> map = new HashMap<>();
				map.put("totalOrderCount", service.getOrderCount(currentStatus));
				map.put("orders", orders);
				map.put("currentPage", currentPage);
				map.put("bucketUrl", AllGolbalConstants.BUCKET_URL);
				return new ResponseEntity<T>((T) map, HttpStatus.OK);
			} else {
				return new ResponseEntity<T>((T) Collections.emptyList(), HttpStatus.NOT_FOUND);
			}

		} catch (BaseException e) {
			return helper.constructErrorResponse(e);
		}
	}

	@PostMapping(AllEndPoints.GET_ALL_ORDER_NOT_DELIVERED_URL)
	public <T> ResponseEntity<T> getOrdersNotDelivered(@RequestParam("noOfObjects") int noOfObjects,@RequestParam("currentPage") int currentPage) {
		LOGGER.trace("Entering into getOrdersNotDelivered method in CustomerController with {} {}", noOfObjects, currentPage);
		try {
			List<Order> orders = service.getAllOrdersNotCompleted(noOfObjects, currentPage);
			if ( null != orders && orders.size() > 0) {
				Map<String, Object> map = new HashMap<>();
				map.put("totalOrderCount", service.getOrderCountNotCompleted());
				map.put("orders", orders);
				map.put("currentPage", currentPage);
				map.put("bucketUrl", AllGolbalConstants.BUCKET_URL);
				return new ResponseEntity<T>((T) map, HttpStatus.OK);
			} else {
				return new ResponseEntity<T>((T) Collections.emptyList(), HttpStatus.NOT_FOUND);
			}

		} catch (BaseException e) {
			return helper.constructErrorResponse(e);
		}
	}

	@PostMapping(CustomerConstants.UPDATE_LOCATION_URL)
	public <T> ResponseEntity<T> updateLocation(@RequestBody Location location, @RequestParam("mobileNumber") long mobileNumber, BindingResult bindingResult) {
		LOGGER.trace("Entering into updateLocation method in CustomerController with {}", mobileNumber);
		try {
			updateLocationValidator.validate(location, bindingResult);
			if (bindingResult.hasErrors())
				return helper.constructFieldErrorResponse(bindingResult);
			Location updatedLocation = service.updateLocation(location, mobileNumber);
			if ( null != updatedLocation) {
				return new ResponseEntity<T>((T) updatedLocation, HttpStatus.OK);
			} else {
				return new ResponseEntity<T>(HttpStatus.NOT_FOUND);
			}

		} catch (BaseException | JsonProcessingException e) {
			return helper.constructErrorResponse(e);
		}
	}

	@PostMapping(CustomerConstants.UPDATE_ORDER_STATUS)
	public <T> ResponseEntity<T> updateOrderStatus(@RequestBody UpdateOrderStatus updateOrderStatus, BindingResult bindingResult) {
		LOGGER.trace("Entering into updateOrderStatus method in CustomerController with {}", updateOrderStatus.toString());
		try {
			updateOrderStatusValidator.validate(updateOrderStatus, bindingResult);
			if (bindingResult.hasErrors())
				return helper.constructFieldErrorResponse(bindingResult);
			Order order = service.updateOrderStatus(updateOrderStatus.getOrderId(), updateOrderStatus.getMobileNumber(), updateOrderStatus.getCurrentStatus());
			if ( null != order) {
				return new ResponseEntity<T>((T) order, HttpStatus.OK);
			} else {
				return new ResponseEntity<T>(HttpStatus.NOT_FOUND);
			}

		} catch (BaseException | JsonProcessingException e) {
			return helper.constructErrorResponse(e);
		}
	}

	@PostMapping(CustomerConstants.DELETE_LOCATION_URL)
	public <T> ResponseEntity<T> deleteLocation(@RequestParam("locationId") long locationId, @RequestParam("mobileNumber") long mobileNumber) {
		LOGGER.trace("Entering into deleteLocation method in CustomerController with {} {}", mobileNumber, locationId);
		try {
			if ( String.valueOf(mobileNumber).length() != 10)
				return new ResponseEntity<T>(HttpStatus.BAD_REQUEST);
			Location deletedLocation = service.deleteLocation(locationId, mobileNumber);
			if ( null != deletedLocation) {
				return new ResponseEntity<T>((T) deletedLocation, HttpStatus.OK);
			} else {
				return new ResponseEntity<T>(HttpStatus.NOT_FOUND);
			}

		} catch (BaseException | JsonProcessingException e) {
			return helper.constructErrorResponse(e);
		}
	}

	@PostMapping(CustomerConstants.ADD_DELIVERY_BOY_TO_ORDER_URL)
	public <T> ResponseEntity<T> addDeliveryBoy(@RequestParam("orderId") long orderId, @RequestBody DeliveryBoy deliveryBoy) {
		LOGGER.trace("Entering into addDeliveryBoy method in CustomerController with {} {}", orderId, deliveryBoy.toString());
		try {
			if ( null == deliveryBoy) {
				return new ResponseEntity<T>(HttpStatus.BAD_REQUEST);
			}
			if ( null == deliveryBoy.getName() || "".equals(deliveryBoy.getName())) {
				return new ResponseEntity<T>(HttpStatus.BAD_REQUEST);
			}
			if ( 0 == deliveryBoy.getMobile() || String.valueOf(deliveryBoy.getMobile()).length() != 10) {
				return new ResponseEntity<T>(HttpStatus.BAD_REQUEST);
			}
			Order order = service.addDeliveryBoyToOrder(deliveryBoy, orderId);
			if ( null != order) {
				return new ResponseEntity<T>((T) order, HttpStatus.OK);
			} else {
				return new ResponseEntity<T>(HttpStatus.NOT_FOUND);
			}

		} catch (BaseException | JsonProcessingException e) {
			return helper.constructErrorResponse(e);
		}
	}


	@PostMapping(CustomerConstants.SIGN_OUT_URL)
	public <T> ResponseEntity<T> signOut(@RequestParam("mobileNumber") long mobileNumber, @RequestParam("platform") Platform platform) {
		LOGGER.trace("Entering into addDeliveryBoy method in CustomerController with {} {}", mobileNumber, platform);
		try {
			if ( 0 == mobileNumber || String.valueOf(mobileNumber).length() != 10) {
				return new ResponseEntity<T>(HttpStatus.BAD_REQUEST);
			}
			service.removePushToken(mobileNumber, platform);
			return new ResponseEntity<T>(HttpStatus.OK);
		} catch (BaseException | JsonProcessingException e) {
			return helper.constructErrorResponse(e);
		}
	}

	@PostMapping(CustomerConstants.GET_DELIVERY_CHARGE_URL)
	public <T> ResponseEntity<T> getDeliveryCharge(@RequestParam("latitude") double latitude, @RequestParam("longitude") double longitude) {
		LOGGER.trace("Entering into getDeliveryCharge method in CustomerController with {} {}", latitude, longitude);
		try {
			double charge = service.getDeliveryCharge(latitude, longitude);
			Map<String, Object> map = new HashMap<>();
			map.put("deliveryCharge", charge);
			return new ResponseEntity<T>((T) map, HttpStatus.OK);
		} catch (Exception e) {
			return helper.constructErrorResponse(e);
		}
	}
}

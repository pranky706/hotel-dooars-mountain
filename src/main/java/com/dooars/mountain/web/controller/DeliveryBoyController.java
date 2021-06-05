package com.dooars.mountain.web.controller;

import com.dooars.mountain.constants.AllEndPoints;
import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.model.deliveryboy.DeliveryBoy;
import com.dooars.mountain.service.deliveryboy.DeliveryBoyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Prantik Guha on 06-06-2021
 **/
@CrossOrigin
@RestController
public class DeliveryBoyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryBoyController.class);

    private final DeliveryBoyService service;
    private ControllerHelper helper;

    public DeliveryBoyController(DeliveryBoyService service, ControllerHelper helper) {
        this.service = service;
        this.helper = helper;
    }

    @PostMapping(AllEndPoints.ADD_DELIVERY_BOY)
    public <T> ResponseEntity<T> addDeliveryBoy(@RequestBody DeliveryBoy deliveryBoy) {
        LOGGER.trace("Entering into addDeliveryBoy method in DeliveryBoyController with {}", deliveryBoy.toString());
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
            service.addDeliveryBoy(deliveryBoy);
            return new ResponseEntity<T>(HttpStatus.OK);
        } catch (BaseException e) {
            return helper.constructErrorResponse(e);
        }
    }

    @PostMapping(AllEndPoints.GET_DELIVERY_BOY)
    public <T> ResponseEntity<T> getBoy(@RequestParam("mobile") long mobile) {
        LOGGER.trace("Entering into getBoy method in DeliveryBoyController with{}", mobile);
        try {
            if ( 0 == mobile || String.valueOf(mobile).length() != 10) {
                return new ResponseEntity<T>(HttpStatus.BAD_REQUEST);
            }
            DeliveryBoy deliveryBoy = service.getBoyByNumber(mobile);
            if ( null != deliveryBoy) {
                return new ResponseEntity<T>((T) deliveryBoy, HttpStatus.OK);
            } else {
                return new ResponseEntity<T>(HttpStatus.NOT_FOUND);
            }

        } catch (BaseException e) {
            return helper.constructErrorResponse(e);
        }
    }

    @PostMapping(AllEndPoints.GET_ALL_DELIVERY_BOY)
    public <T> ResponseEntity<T> getAllBoy() {
        LOGGER.trace("Entering into getAllBoy method in DeliveryBoyController with");
        try {
            List<DeliveryBoy> deliveryBoys = service.getAllDeliveryBoy();
            if ( null != deliveryBoys && deliveryBoys.size() > 0) {
                return new ResponseEntity<T>((T) deliveryBoys, HttpStatus.OK);
            } else {
                return new ResponseEntity<T>(HttpStatus.NOT_FOUND);
            }

        } catch (BaseException e) {
            return helper.constructErrorResponse(e);
        }
    }

    @PostMapping(AllEndPoints.UPDATE_DELIVERY_BOY)
    public <T> ResponseEntity<T> updateDeliveryBoy(@RequestBody DeliveryBoy deliveryBoy) {
        LOGGER.trace("Entering into updateDeliveryBoy method in DeliveryBoyController with {}", deliveryBoy.toString());
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
            service.updateDeliveryBoy(deliveryBoy);
            return new ResponseEntity<T>(HttpStatus.OK);
        } catch (BaseException e) {
            return helper.constructErrorResponse(e);
        }
    }

    @PostMapping(AllEndPoints.DELETE_DELIVERY_BOY)
    public <T> ResponseEntity<T> deleteBoy(@RequestParam("mobile") long mobile) {
        LOGGER.trace("Entering into deleteBoy method in DeliveryBoyController with{}", mobile);
        try {
            if ( 0 == mobile || String.valueOf(mobile).length() != 10) {
                return new ResponseEntity<T>(HttpStatus.BAD_REQUEST);
            }
            service.deleteDeliveryBoy(mobile);
            return new ResponseEntity<T>(HttpStatus.OK);
        } catch (BaseException e) {
            return helper.constructErrorResponse(e);
        }
    }
}

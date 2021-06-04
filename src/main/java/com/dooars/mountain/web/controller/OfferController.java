package com.dooars.mountain.web.controller;



import com.dooars.mountain.constants.AllEndPoints;
import com.dooars.mountain.constants.AllGolbalConstants;
import com.dooars.mountain.model.centraloffer.CentralOffer;
import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.service.centraloffer.CentralOfferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Prantik Guha on 23-05-2021
 **/

@CrossOrigin
@RestController
public class OfferController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OfferController.class);
    private final CentralOfferService service;
    private final ControllerHelper helper;
    private final Validator addValidator, updateValidator;

    public OfferController(CentralOfferService service, ControllerHelper helper,
                          @Qualifier("addOfferValidator") Validator addValidator,
                           @Qualifier("updateOfferValidator") Validator updateValidator) {
        this.service = service;
        this.helper = helper;
        this.addValidator = addValidator;
        this.updateValidator = updateValidator;
    }

    @SuppressWarnings("unchecked")
    @PostMapping(AllEndPoints.ADD_OFFER)
    public <T> ResponseEntity<T> addOffer(@RequestBody CentralOffer centralOffer, BindingResult bindingResult) {
        LOGGER.trace("Entering into addItem method in OfferController with{}", centralOffer.toString());
        return (ResponseEntity<T>) helper.validateAndExecute(addValidator, bindingResult, centralOffer, () -> service.addOffer(centralOffer));
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/api/offer-service/getOffer")
    public <T> ResponseEntity<T> getOffer() {
        LOGGER.trace("Entering into getOffer method in OfferController with{}");
        try {
            List<CentralOffer> centralOffers = service.getOfferByDate();
            if ( null != centralOffers && centralOffers.size() > 0) {
                return new ResponseEntity<T>((T) centralOffers, HttpStatus.OK);
            } else {
                return new ResponseEntity<T>((T) Collections.emptyList(), HttpStatus.NOT_FOUND);
            }

        } catch (BaseException e) {
            return helper.constructErrorResponse(e);
        }
    }

    @SuppressWarnings("unchecked")
    @PostMapping(AllEndPoints.GET_OFFER)
    public <T> ResponseEntity<T> getOfferAdmin() {
        LOGGER.trace("Entering into getOfferAdmin method in OfferController with{}");
        try {
            List<CentralOffer> centralOffers = service.getAllOffer();
            if ( null != centralOffers && centralOffers.size() > 0) {
                Map<String, Object> map = new HashMap<>();
                map.put("bucketUrl", AllGolbalConstants.BUCKET_URL);
                map.put("offers", centralOffers);
                return new ResponseEntity<T>((T) map, HttpStatus.OK);
            } else {
                return new ResponseEntity<T>((T) Collections.emptyList(), HttpStatus.NOT_FOUND);
            }

        } catch (BaseException e) {
            return helper.constructErrorResponse(e);
        }
    }

    @SuppressWarnings("unchecked")
    @PostMapping(AllEndPoints.UPDATE_OFFER)
    public <T> ResponseEntity<T> updateOffer(@RequestBody CentralOffer centralOffer, BindingResult bindingResult) {
        LOGGER.trace("Entering into updateOffer method in OfferController with{}", centralOffer.toString());
        return (ResponseEntity<T>) helper.validateAndExecute(updateValidator, bindingResult, centralOffer, () -> service.updateOffer(centralOffer));
    }

    @PostMapping(AllEndPoints.DELETE_OFFER)
    public <T> ResponseEntity<T> deleteOffer(@RequestParam("offerId") int offerId) {
        LOGGER.trace("Entering into deleteOffer method in OfferController with{}", offerId);
        try {
            service.deleteOffer(offerId);
            return new ResponseEntity<T>(HttpStatus.OK);

        } catch (BaseException e) {
            return helper.constructErrorResponse(e);
        }
    }
}

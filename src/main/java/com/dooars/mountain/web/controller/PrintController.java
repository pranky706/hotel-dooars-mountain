package com.dooars.mountain.web.controller;

import com.dooars.mountain.constants.AllGolbalConstants;
import com.dooars.mountain.service.print.PrintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import java.util.HashMap;
import java.util.Map;

/**
 * @author Prantik Guha on 08-06-2021
 **/
@CrossOrigin
@RestController
public class PrintController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrintController.class);

    private final ControllerHelper helper;
    private final PrintService printService;

    @Autowired
    PrintController(ControllerHelper helper, PrintService printService) {
        this.helper = helper;
        this.printService = printService;
    }

    @PostMapping("/api/admin/print-service/printKot")
    public <T> ResponseEntity<T> printKot(@RequestParam("orderId") long orderId) {
        LOGGER.trace("Entering into printKot method in PrintController with {}", orderId);
        try {
            if (0 == orderId)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            Map<String, String> map = new HashMap<>();
            map.put("fileUrl", AllGolbalConstants.BUCKET_URL + printService.createKOT(orderId));
            return new ResponseEntity<T>((T) map, HttpStatus.OK);
        } catch (Exception e) {
            return helper.constructErrorResponse(e);
        }
    }

    @PostMapping("/api/admin/print-service/printBill")
    public <T> ResponseEntity<T> printBill(@RequestParam("orderId") long orderId) {
        LOGGER.trace("Entering into printBill method in PrintController with {}", orderId);
        try {
            if (0 == orderId)
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            Map<String, String> map = new HashMap<>();
            map.put("fileUrl", AllGolbalConstants.BUCKET_URL + printService.createBill(orderId));
            return new ResponseEntity<T>((T) map, HttpStatus.OK);
        } catch (Exception e) {
            return helper.constructErrorResponse(e);
        }
    }

    @PostMapping("/api/admin/print-service/deleteBill")
    public <T> ResponseEntity<T> deleteBill(@RequestParam("fileName") String fileName) {
        LOGGER.trace("Entering into deleteBill method in PrintController with {}", fileName);
        try {
            if ( null == fileName || "".equals(fileName) || !"_bill".contains(fileName))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            printService.deleteBill(fileName);
            return new ResponseEntity<T>(HttpStatus.OK);
        } catch (Exception e) {
            return helper.constructErrorResponse(e);
        }
    }

    @PostMapping("/api/admin/print-service/deleteKot")
    public <T> ResponseEntity<T> deleteKot(@RequestParam("fileName") String fileName) {
        LOGGER.trace("Entering into deleteKot method in PrintController with {}", fileName);
        try {
            if ( null == fileName || "".equals(fileName) || !"_kot".contains(fileName))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            printService.deleteKot(fileName);
            return new ResponseEntity<T>(HttpStatus.OK);
        } catch (Exception e) {
            return helper.constructErrorResponse(e);
        }
    }
}

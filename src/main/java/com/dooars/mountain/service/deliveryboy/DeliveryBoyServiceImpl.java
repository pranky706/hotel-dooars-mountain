package com.dooars.mountain.service.deliveryboy;

import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.model.deliveryboy.DeliveryBoy;
import com.dooars.mountain.repository.deliveryboy.DeliveryBoyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Prantik Guha on 05-06-2021
 **/
@Service
public class DeliveryBoyServiceImpl implements DeliveryBoyService{

    private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryBoyServiceImpl.class);

    private final DeliveryBoyRepository repository;

    @Autowired
    DeliveryBoyServiceImpl(DeliveryBoyRepository repository) {
        this.repository = repository;
    }

    @Override
    public void addDeliveryBoy(DeliveryBoy deliveryBoy) throws BaseException {
        LOGGER.trace("Entering into addDeliveryBoy method in DeliveryBoyServiceImpl with{}", deliveryBoy.toString());
        DeliveryBoy gotDeliveryBoy = repository.checkBoyByNumber(deliveryBoy.getMobile());
        if ( null != gotDeliveryBoy)
            repository.undoDeleteDeliveryBoy(deliveryBoy.getMobile());
        else
            repository.addDeliveryBoy(deliveryBoy);
    }

    @Override
    public DeliveryBoy getBoyByNumber(long mobile) throws BaseException {
        LOGGER.trace("Entering into getBoyByNumber method in DeliveryBoyServiceImpl with{}", mobile);
        return repository.getBoyByNumber(mobile);
    }

    @Override
    public List<DeliveryBoy> getAllDeliveryBoy() throws BaseException {
        LOGGER.trace("Entering into getAllDeliveryBoy method in DeliveryBoyServiceImpl with");
        return repository.getAllDeliveryBoy();
    }

    @Override
    public void updateDeliveryBoy(DeliveryBoy deliveryBoy) throws BaseException {
        LOGGER.trace("Entering into updateDeliveryBoy method in DeliveryBoyServiceImpl with {}", deliveryBoy.toString());
        repository.updateDeliveryBoy(deliveryBoy);
    }

    @Override
    public void deleteDeliveryBoy(long mobile) throws BaseException {
        LOGGER.trace("Entering into deleteDeliveryBoy method in DeliveryBoyServiceImpl with {}", mobile);
        repository.deleteDeliveryBoy(mobile);
    }
}

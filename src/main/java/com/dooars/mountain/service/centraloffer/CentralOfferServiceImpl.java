package com.dooars.mountain.service.centraloffer;

import com.dooars.mountain.model.centraloffer.CentralOffer;
import com.dooars.mountain.model.common.BaseException;

import com.dooars.mountain.repository.centraloffer.CentralOfferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Prantik Guha on 23-05-2021
 **/

@Service
public class CentralOfferServiceImpl implements CentralOfferService{

    private static final Logger LOGGER = LoggerFactory.getLogger(CentralOfferServiceImpl.class);

    private final CentralOfferRepository repository;

    @Autowired
    CentralOfferServiceImpl(CentralOfferRepository repository) {
        this.repository = repository;
    }
    @Override
    public CentralOffer addOffer(CentralOffer centralOffer) throws BaseException {
        LOGGER.trace("Entering into addOffer method in CentralOfferServiceImpl with {}", centralOffer.toString());
        return repository.addOffer(centralOffer);
    }

    @Override
    public List<CentralOffer> getOfferByDate() throws BaseException {
        LOGGER.trace("Entering into getOfferByDate method in CentralOfferServiceImpl with {}");
        return repository.getOfferByDate(LocalDate.now().toString());
    }

    @Override
    public CentralOffer updateOffer(CentralOffer centralOffer) throws BaseException {
        LOGGER.trace("Entering into updateOffer method in CentralOfferServiceImpl with {}", centralOffer.toString());
        return repository.updateOffer(centralOffer);
    }

    @Override
    public void deleteOffer(long offerId) throws BaseException {
        LOGGER.trace("Entering into deleteOffer method in CentralOfferServiceImpl with {}", offerId);
        repository.deleteOffer(offerId);
    }
}

package com.dooars.mountain.repository.centraloffer;

import com.dooars.mountain.model.centraloffer.CentralOffer;
import com.dooars.mountain.model.common.BaseException;

import java.util.List;

/**
 * @author Prantik Guha on 23-05-2021
 **/
public interface CentralOfferRepository {

    CentralOffer addOffer(CentralOffer centralOffer) throws BaseException;
    List<CentralOffer> getOfferByDate(String data) throws BaseException;
    List<CentralOffer> getAllOffer() throws BaseException;
    CentralOffer updateOffer(CentralOffer centralOffer) throws BaseException;
    void deleteOffer(long offerId) throws BaseException;
}

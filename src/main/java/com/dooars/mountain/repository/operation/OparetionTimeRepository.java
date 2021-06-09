package com.dooars.mountain.repository.operation;

import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.model.operation.OperationTime;

import java.util.List;

/**
 * @author Prantik Guha on 09-06-2021
 **/
public interface OparetionTimeRepository {

    void updateOperationTime(OperationTime operationTime) throws BaseException;
    List<OperationTime> getOperationTimes() throws BaseException;
}

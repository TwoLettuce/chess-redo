package service;

import dataaccess.DataAccess;
import dataaccess.InternalServerErrorException;

public class DataService {

    private final DataAccess dataAccess;

    public DataService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public void clear() throws InternalServerErrorException {
        dataAccess.clear();
    }
}

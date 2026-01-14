package service;

import dataaccess.DataAccess;

public class DataService {

    private final DataAccess dataAccess;

    public DataService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public void clear() {
        dataAccess.clear();
    }
}

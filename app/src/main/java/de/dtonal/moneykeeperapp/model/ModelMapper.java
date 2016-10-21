package de.dtonal.moneykeeperapp.model;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Wraps the Jackson ObjectMapper to share it application wide as a
 * singleton.
 * Created by dtonal on 21.10.16.
 */
public class ModelMapper extends ObjectMapper{

    private static ModelMapper instance = null;

    private ModelMapper() {
        super();
    }

    public static ModelMapper getInstance()
    {
        if(instance == null)
        {
            instance = new ModelMapper();
        }
        return instance;
    }
}

package com.swozo.webservice.exceptions;

public class ServiceModuleNotFoundException extends RuntimeException{

    public ServiceModuleNotFoundException(Long id) { super("Could not find service module " + id); }
}

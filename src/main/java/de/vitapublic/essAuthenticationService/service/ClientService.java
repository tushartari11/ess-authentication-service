package de.vitapublic.essAuthenticationService.service;

import de.vitapublic.essAuthenticationService.controller.exception.LogicalException;

public interface ClientService {
    String authenticateClient(String clientKey, String clientSecret) throws LogicalException;
}

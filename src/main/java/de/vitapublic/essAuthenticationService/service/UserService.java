package de.vitapublic.essAuthenticationService.service;

import de.vitapublic.essAuthenticationService.controller.exception.LogicalException;
import de.vitapublic.essAuthenticationService.model.User;

public interface UserService {
    String registerUser(User user) throws LogicalException;

    User findByUsername(String username);
}

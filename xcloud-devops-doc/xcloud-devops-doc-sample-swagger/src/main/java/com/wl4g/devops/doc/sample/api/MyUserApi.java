package com.wl4g.devops.doc.sample.api;

import com.wl4g.devops.doc.sample.model.User;
import javax.servlet.ServletException;

public interface MyUserApi {
    User getUserById(Integer id) throws ServletException;

    void deleteUserById(Integer id) throws ServletException;

    void createUser(User user) throws ServletException;

    void modifyUser(User user) throws ServletException;
}
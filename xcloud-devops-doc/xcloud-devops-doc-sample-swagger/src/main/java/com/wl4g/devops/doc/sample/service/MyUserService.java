package com.wl4g.devops.doc.sample.service;

import com.wl4g.devops.doc.sample.api.MyUserApi;
import com.wl4g.devops.doc.sample.model.User;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;

import javax.servlet.ServletException;

@Service("com.wl4g.devops.doc.sample.service.MyUserService")
@Path("/test/v1/user")
public class MyUserService {

@Autowired
private MyUserApi delegateApi;
    @GET
    @Path("/{id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public User getUserById(@NotNull @QueryParam("id") Integer id) throws ServletException {
        return delegateApi.getUserById(id);
    }

    @DELETE
    @Path("/{id}")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public void deleteUserById(@NotNull @PathParam("id") Integer id) throws ServletException {
        delegateApi.deleteUserById(id);
    }

    @POST
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public void createUser(User user) throws ServletException {
        delegateApi.createUser(user);
    }

    @PUT
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public void modifyUser(User user) throws ServletException {
        delegateApi.modifyUser(user);
    }
}
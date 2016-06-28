/**
 * Copyright (C) 2016 UniKnow (info.uniknow@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uniknow.agiledev.docMockRest.examples.swagger.annotated;

import io.swagger.annotations.*;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/user")
@Api(value="/user", description = "Operations about user")
@Produces({"application/json", "application/xml"})
public interface UserResource {

    @POST
    @ApiOperation(value = "Create user",
            notes = "This can only be done by the logged in user.",
            position = 1)
    Response createUser(
            @ApiParam(value = "Created user object", required = true) User user);

    @POST
    @Path("/createWithArray")
    @ApiOperation(value = "Creates list of users with given input array",
            position = 2)
    Response createUsersWithArrayInput(
            @ApiParam(value = "List of user object", required = true) User[] users);

    @POST
    @Path("/createWithList")
    @ApiOperation(value = "Creates list of users with given input array",
            position = 3)
    Response createUsersWithListInput(
            @ApiParam(value = "List of user object", required = true) java.util.List<User> users);

    @PUT
    @Path("/{username}")
    @ApiOperation(value = "Updated user",
            notes = "This can only be done by the logged in user.",
            position = 4)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid user supplied"),
            @ApiResponse(code = 404, message = "User not found") })
    Response updateUser(
            @ApiParam(value = "name that need to be deleted", required = true) @PathParam("username") String username,
            @ApiParam(value = "Updated user object", required = true) User user);

    @DELETE
    @Path("/{username}")
    @ApiOperation(value = "Delete user",
            notes = "This can only be done by the logged in user.",
            position = 5)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid username supplied"),
            @ApiResponse(code = 404, message = "User not found") })
    Response deleteUser(
            @ApiParam(value = "The name that needs to be deleted", required = true) @PathParam("username") String username);

    @GET
    @Path("/{username}")
    @ApiOperation(value = "Get user by user name",
            response = User.class,
            position = 0)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", responseContainer = "TEST"),
            @ApiResponse(code = 400, message = "Invalid username supplied"),
            @ApiResponse(code = 404, message = "User not found") }) Response getUserByName(
            @ApiParam(value = "The name that needs to be fetched. Use user1 for testing. ", required = true) @PathParam("username") String username);

    @GET
    @Path("/login")
    @ApiOperation(value = "Logs user into the system",
            response = String.class,
            position = 6)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid username/password supplied") })
    Response loginUser(
            @ApiParam(value = "The user name for login", required = true) @QueryParam("username") String username,
            @ApiParam(value = "The password for login in clear text", required = true) @QueryParam("password") String password);

    @GET
    @Path("/logout")
    @ApiOperation(value = "Logs out current logged in user session",
            position = 7)
    Response logoutUser();
}

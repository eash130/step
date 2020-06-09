// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.JsonObject;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();

    // Assume user is not logged in and respond with a login link that redirects to "/" after login
    // link is either a login URL or a logout URL depending on the current status of the user
    String userEmail = "";
    String link = userService.createLoginURL("/");
    if (userService.isUserLoggedIn()) {
      userEmail = userService.getCurrentUser().getEmail();
      link = userService.createLogoutURL("/");
    }
    JsonObject loginInfo = new JsonObject();
    loginInfo.addProperty("email", userEmail);
    loginInfo.addProperty("authenticationURL", link);
    response.setContentType("application/json");
    response.getWriter().println(loginInfo);
  }
}

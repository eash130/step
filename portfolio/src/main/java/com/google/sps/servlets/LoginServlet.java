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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  private static final String USER_INFO = "User";
  private static final String NICKNAME = "nickname";
  private static final String USER_ID = "userId";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("text/html");
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      // Prompt user to login to their Google account.
      String loginLink = userService.createLoginURL("/login");
      response.getWriter().println("<h3>Welcome!</h3>");
      response.getWriter().println("<p>Click <a href=\"" + loginLink + "\">here</a> to login.</p>");
      response.getWriter().println("<p>Click <a href=\"/\">here</a> to return to the home page</p>");
    } else if (getUserNickname(userService.getCurrentUser().getUserId()) == "") {
      // Prompt user to select a nickname to go by.
      String userEmail = userService.getCurrentUser().getEmail();
      response.getWriter().println("<h3>Welcome " + userEmail + "!</h3>");
      response.getWriter().println("<p>Choose a nickname for everyone to call you by.</p>");
      response.getWriter().println("<form action=\"/login\" method=\"POST\">");
      response.getWriter().println("<input type=\"text\" name=\"nickname\" />");
      response.getWriter().println("<input type=\"submit\" value=\"Confirm\" />");
      response.getWriter().println("</form>");
    } else {
      // user is good to go
      String nickname = getUserNickname(userService.getCurrentUser().getUserId());
      String logoutLink = userService.createLogoutURL("/login");
      response.getWriter().println("<h3>Welcome " + nickname + "!</h3>");
      response.getWriter().println("<p>Your account is registered and you can post comments!</p>");
      response.getWriter().println("<p>Click <a href=\"" + logoutLink + "\">here</a> to logout.</p>");
      response.getWriter().println("<p>Click <a href=\"/\">here</a> to return to the home page</p>");
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String nickname = request.getParameter(NICKNAME);
    if (!nickname.isEmpty()) {
      Entity userEntity = new Entity(USER_INFO);
      userEntity.setProperty(NICKNAME, nickname);
      UserService userService = UserServiceFactory.getUserService();
      userEntity.setProperty(USER_ID, userService.getCurrentUser().getUserId());
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(userEntity);
    }
    response.sendRedirect("/login");
  }

  /**
   * Returns the nickname of the user with id, or empty String if the user has not set a nickname.
   */
  private String getUserNickname(String userId) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query =
        new Query(USER_INFO)
            .setFilter(new Query.FilterPredicate(USER_ID, Query.FilterOperator.EQUAL, userId));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();
    if (entity == null) {
      return "";
    }
    String nickname = (String) entity.getProperty(NICKNAME);
    return nickname;
  }
}

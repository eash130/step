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
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles the poll on the web page. */
@WebServlet("/poll")
public class PollServlet extends HttpServlet {

  private static final String TASK_NAME = "Polls";
  private static final String POLL_NAME = "pollName";
  private static final String POLL_RESPONSE = "pollResponse";
  private static final String VOTE_COUNT = "voteCount";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String pollName = request.getParameter(POLL_NAME);
    Query pollQuery = new Query(TASK_NAME)
        .setFilter(new FilterPredicate(POLL_NAME, FilterOperator.EQUAL, pollName));
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery pollResults = datastore.prepare(pollQuery);
    Map<String, Long> pollData = new HashMap<>();
    for (Entity pollEntity: pollResults.asIterable()) {
      String vote = (String) pollEntity.getProperty(POLL_RESPONSE);
      long count = (long) pollEntity.getProperty(VOTE_COUNT);
      pollData.put(vote, count);
    }
    Gson gson = new Gson();
    String json = gson.toJson(pollData);
    response.setContentType("application/json");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String pollName = request.getParameter(POLL_NAME);
    String pollResponse = request.getParameter(POLL_RESPONSE).toLowerCase();

    // Attempt to retrieve the (poll name + poll response) combination from datastore and increment
    // the vote count.
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query(TASK_NAME)
        .setFilter(new FilterPredicate(POLL_NAME, FilterOperator.EQUAL, pollName))
        .setFilter(new FilterPredicate(POLL_RESPONSE, FilterOperator.EQUAL, pollResponse));
    PreparedQuery pollRow = datastore.prepare(query);
    Entity datastoreEntity = pollRow.asSingleEntity();
    if (datastoreEntity == null) {
      Entity pollEntity = new Entity(TASK_NAME);
      pollEntity.setProperty(POLL_NAME, pollName);
      pollEntity.setProperty(POLL_RESPONSE, pollResponse);
      pollEntity.setProperty(VOTE_COUNT, 1);
      datastore.put(pollEntity);
    } else {
      long currentCount = (long) datastoreEntity.getProperty(VOTE_COUNT);
      datastoreEntity.setProperty(VOTE_COUNT, currentCount + 1);
      datastore.put(datastoreEntity);
    }
    
    response.sendRedirect("/");
  }
}

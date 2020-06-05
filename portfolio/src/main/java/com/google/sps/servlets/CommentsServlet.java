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
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that displays and update comments on the web page. */
@WebServlet("/comments")
public class CommentsServlet extends HttpServlet {

  private static final String TASK_NAME = "Comments";
  private static final String TIMESTAMP = "timestamp";
  private static final String COMMENT_PROPERTY = "comment";
  private static final String PAGE_SIZE = "filterCount";
  private static final String COMMENT_ID = "commentId";

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String pageSize = request.getParameter(PAGE_SIZE);
    int filterQuantity;
    if (pageSize == null) {
      // Default filter is 25 comments per page.
      filterQuantity = 25;
    } else {
      filterQuantity = Integer.parseInt(pageSize);
    }
    Query commentsQuery = new Query(TASK_NAME).addSort(TIMESTAMP, SortDirection.ASCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery commentResults = datastore.prepare(commentsQuery);
    List<Entity> requestedComments = commentResults.asList(
        FetchOptions.Builder.withLimit(filterQuantity));
    ArrayList<Comment> messages = new ArrayList();
    for (Entity commentEntity : requestedComments) {
      long commentId = commentEntity.getKey().getId();
      String message = (String) commentEntity.getProperty(COMMENT_PROPERTY);
      long timestamp = (long) commentEntity.getProperty(TIMESTAMP);
      Comment newComment = new Comment(commentId, message, timestamp);
      messages.add(newComment);
    }
    Gson gson = new Gson();
    String jsonMessages = gson.toJson(messages);
    response.setContentType("application/json");
    response.getWriter().println(jsonMessages);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String newComment = request.getParameter(COMMENT_PROPERTY);
    long timestamp = System.currentTimeMillis();
    if (!newComment.isEmpty()) {
      Entity commentEntity = new Entity(TASK_NAME);
      commentEntity.setProperty(COMMENT_PROPERTY, newComment);
      commentEntity.setProperty(TIMESTAMP, timestamp);
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(commentEntity);
    }
    response.sendRedirect("/");
  }

  @Override
  public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
    long commentId = Long.parseLong(request.getParameter(COMMENT_ID));
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key commentKey = KeyFactory.createKey(TASK_NAME, commentId);
    datastore.delete(commentKey);
    response.sendRedirect("/");
  }
}

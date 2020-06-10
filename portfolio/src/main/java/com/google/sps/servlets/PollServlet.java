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

  private static final String POLL_NAME = "pollName";
  private static final String POLL_RESPONSE = "pollResponse";
  private Map<String, Map<String, Integer>> polls = new HashMap<>();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Map<String, Integer> poll = polls.get(request.getParameter(POLL_NAME));
    Gson gson = new Gson();
    String json = gson.toJson(poll);
    response.setContentType("application/json");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Map<String, Integer> poll = polls.get(request.getParameter(POLL_NAME));
    if (poll == null) {
      poll = new HashMap<>();
      polls.put(request.getParameter(POLL_NAME), poll);
    }
    String pollResponse = request.getParameter(POLL_RESPONSE);
    if (!pollResponse.isEmpty()) {
      int currentVotes = poll.containsKey(pollResponse) ? poll.get(pollResponse) : 0;
      poll.put(pollResponse, currentVotes + 1);
    }
    response.sendRedirect("/");
  }
}

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

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private ArrayList<String> jsonValues;

  @Override
  public void init() {
    jsonValues = new ArrayList();
    jsonValues.add("This is the first message!");
    jsonValues.add("Let's add another one!");
    jsonValues.add("Well three's a crowd");
    jsonValues.add("They said three was fine, but what's wrong with four.");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String convertedJson = String.format("{\"firstMessage\": \"%s\", \"secondMessage\": \"%s\", " +
                                         "\"thirdMessage\": \"%s\", \"fourthMessage\": \"%s\"}",
                                         jsonValues.get(0), jsonValues.get(1), jsonValues.get(2),
                                         jsonValues.get(3));
    response.setContentType("application/json;");
    response.getWriter().println(convertedJson);
  }
}

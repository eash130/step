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

/**
 * Adds a random greeting to the page.
 */
function addRandomQuote() {
  const quotes =
      ['I\'m ready!',
       'The best time to wear a striped sweater is all the time.',
       'Krusty Krab Pizza, it\'s the pizza for you and me!',
       'Is mayonnaise an instrument?',
       'The inner machinations of my mind are an enigma.',
       'My leg!',
       'Do you smell it? That smell. A kind of smelly smell. The smelly smell that smells... smelly',
       'Once there was an ugly barnacle. He was so ugly that everyone died. The end!',
       'I knew I shouldn\'t have gotten out of bed today.',
       'The pioneers used to ride these babies for miles!'];

  // Pick a random greeting.
  const quote = quotes[Math.floor(Math.random() * quotes.length)];

  // Add it to the page.
  const spongebobContainer = document.getElementById('quote-container');

  let bubblesImg = document.getElementById('bubbles');
  let currentFrame = 1;

  // Creates a bubble effect that transitions between quote displays.
  // There is a delay of 10ms between iterations to make the animation smooth.
  function bubbleLoop() {
    const numFrames = 40;
    const delay = 10;
    setTimeout(function() {
      // Calculates a percentage based on the cu
      let percentage = Math.abs(
          (100 / (numFrames / 2)) * (currentFrame - numFrames / 2));
      if (bubblesImg.style.top === '0%' || bubblesImg.style.top === 'auto') {
        bubblesImg.style.bottom = '' + percentage + '%';
        bubblesImg.style.top = 'auto';
      } else {
        bubblesImg.style.top = '' + percentage + '%';
      }
      if (currentFrame === numFrames / 2) {
        spongebobContainer.innerText = quote;
      }
      currentFrame++;
      if (currentFrame < numFrames + 1) {
        bubbleLoop();
      }
    }, delay);
  }
  bubbleLoop();
  bubblesImg.style.top = '100%';
  bubblesImg.style.bottom = '';
}

function fetchComments() {
  fetch('/comments').then(response => response.json()).then(messages => {
    const commentSection = document.getElementById('comment-list');
    messages.forEach(message => {
      commentSection.appendChild(
          createComment(message.email, message.message, message.commentId));
    });
    getLoginStatus();
  });
}

function filterComments() {
  const filterCount = document.getElementById('filter').value;
  let fetchLink = '/comments?filterCount=' + filterCount;
  fetch(fetchLink).then(response => response.json()).then(messages => {
    const commentSection = document.getElementById('comment-list');
    // Clear current comments and re-add with appropriate filter.
    commentSection.innerHTML = '';
    messages.forEach(message => commentSection.appendChild(
        createComment(message.email, message.message, message.commentId)));
  });
}

function deleteComment(id) {
  fetch('/comment/' + id, {method: 'DELETE'});
  location.reload();
}

/** Creates a comment. */
function createComment(email, text, id) {
  const message = createListElement(email, text);
  const deleteButton = createTrashIcon(id);
  message.appendChild(deleteButton);
  return message;
}

/** Creates an <li> element containing text. */
function createListElement(email, text) {
  const liElement = document.createElement('li');
  liElement.innerText = email + ' says ' + text;
  liElement.style.display = 'flex';
  return liElement;
}

/** Creates an <img> element for deleting comments. */
function createTrashIcon(id) {
  const imgElement = document.createElement('img');
  imgElement.src = '/images/trash.png';
  imgElement.alt = 'Delete';
  imgElement.id = id;
  imgElement.onclick = function() { deleteComment(id); };
  imgElement.style.width = '25px';
  imgElement.style.height = '25px';
  return imgElement;
}

function getLoginStatus() {
  fetch("/login").then(response => response.json()).then(authInfo => {
    const commentForm = document.getElementById('comment-form');
    const loginMessage = document.getElementById('login-message');
    const loginURL = document.getElementById('login-URL');
    const logoutURL = document.getElementById('logout-URL');
    if (authInfo.email === "") {
      // If the user is not logged in, then prevent the user from posting a
      // comment until they are logged in.
      commentForm.style.display = 'none';
      loginMessage.style.display = 'block';
      loginURL.href = authInfo.authenticationURL;
      logoutURL.style.display = 'none';
    } else {
      // If the user is logged in, display the comment submission form and
      // present a link to log out.
      commentForm.style.display = 'block';
      loginMessage.style.display = 'none';
      logoutURL.style.display = 'block';
      logoutURL.href = authInfo.authenticationURL;
    }
  });
}

google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawPetChart);

function drawPetChart() {
  fetch('/poll?pollName=pets').then(results => results.json()).then(petVotes => {
    const data = new google.visualization.DataTable();
    data.addColumn('string', 'Pet');
    data.addColumn('number', 'Votes');
    Object.keys(petVotes).forEach(pet => {
      data.addRow([pet, petVotes[pet]]);
    });
    
    const options = {
      'title': 'Favorite Pets',
      'width': 500,
      'height': 500
    };

    const chart = new google.visualization.PieChart(
        document.getElementById('pet-poll'));
    chart.draw(data, options);
  })
}

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
  let currentIteration = 1;

  // Creates a bubble effect that transitions between quote displays.
  // There is a delay between iterations to make the animation smooth.
  function bubbleLoop() {
    const numIterations = 40;
    const delay = 13;
    setTimeout(function() {
      // Calculates a percentage based on the cu
      let percentage = Math.abs((100 / (numIterations / 2)) * (currentIteration - numIterations / 2));
      if (bubblesImg.style.top === '0%' || bubblesImg.style.top === 'auto') {
        bubblesImg.style.bottom = '' + percentage + '%';
        bubblesImg.style.top = 'auto';
      } else {
        bubblesImg.style.top = '' + percentage + '%';
      }
      if (currentIteration === numIterations / 2) {
        spongebobContainer.innerText = quote;
      }
      currentIteration++;
      if (currentIteration < numIterations + 1) {
        bubbleLoop();
      }
    }, delay);
  }
  bubbleLoop();
  bubblesImg.style.top = '100%';
  bubblesImg.style.bottom = '';
}

function greetBack() {
  fetch('/data').then(response => response.text()).then(response => document.getElementById('greeting-response').innerHTML = response);
}

function fetchComments() {
  fetch('/comments').then(response => response.json()).then(messages => {
    const commentSection = document.getElementById('comment-list');
    messages.forEach(message => commentSection.appendChild(createListElement(message)));
  })
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

function filterComments() {
  const filterCount = document.getElementById('filter').value;
  let fetchLink = '/comments?filterCount=' + filterCount;
  fetch(fetchLink).then(response => response.json()).then(messages => {
    const commentSection = document.getElementById('comment-list');
    // Clear current comments and re-add with appropriate filter.
    commentSection.innerHTML = '';
    messages.forEach(message => commentSection.appendChild(createListElement(message)));
  })
}

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
      ['I\'m ready!', 'The best time to wear a striped sweater is all the time.',
       'Krusty Krab Pizza, it\'s the pizza for you and me!', 'Is mayonnaise an instrument?',
       'The inner machinations of my mind are an enigma.', 'My leg!',
       'Do you smell it? That smell. A kind of smelly smell. The smelly smell that smells... smelly',
       'Once there was an ugly barnacle. He was so ugly that everyone died. The end!',
       'I knew I shouldn\'t have gotten out of bed today.',
       'The pioneers used to ride these babies for miles!'];

  // Pick a random greeting.
  const quote = quotes[Math.floor(Math.random() * quotes.length)];

  // Add it to the page.
  const spongebobContainer = document.getElementById('spongebob-container');
  spongebobContainer.innerText = quote;
}

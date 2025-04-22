# Project 4 - *Outta Mind Memory Game*

Submitted by: **Jamul MacNear**

**Outta Mind Memory Game** is an app that ['features a grid of playing cards with different pictures on them to test a player's memory. The cards begin face down and a player is able to flip over two cards at a time. If the cards match, they will disappear from the screen. If the cards do not match, the cards will flip back over for the player to try another pair.'] 

Time spent: **15* hours spent in total

## Required Features

The following **required** functionality is completed:

- [X] App loads to display a grid of cards initially placed face-down:
  - Upon launching the app, a grid of cards should be visible.
  - Cards are facedown to indicate the start of the game.
- [X] Users can tap cards to toggle their display between the back and the face: 
  - Tapping on a facedown card should flip it to reveal the front.
  - Tapping a second card that is not identical should flip both back down
- [X] When two matching cards are found, they both disappear from view:
  - Implement logic to check if two tapped cards match.
  - If they match, both cards should either disappear.
  - If they don't match, they should return to the facedown position.
- [X] User can reset the game and start a new game via a button:
  - Include a button that allows users to reset the game.
  - This button should shuffle the cards and reset any game-related state.
 
The following **optional** features are implemented:

- [X] User can select number of pairs to play with (at least 2 unique values like 2 and 4).
  * (Hint: user Picker)
- [X] App allows for user to scroll to see pairs out of view.
  * (Hint: Use a Scrollview)
- [X] Add any flavor you’d like to your UI with colored buttons or backgrounds, unique cards, etc. 
  * Enhance the visual appeal of the app with colored buttons, backgrounds, or unique card designs.
  * Consider using animations or transitions to make the user experience more engaging.

The following **additional** features are implemented:

- [ ] List anything else that you can get done to improve the app functionality!

## Video Walkthrough

Here's a walkthrough of implemented user stories:

Here is a reminder on how to embed Loom videos on GitHub. Feel free to remove this reminder once you upload your README. 

[Memory Game Youtube Walkthrough] - (https://youtu.be/oAbTT7ob6Yc) .

## Notes

I had a challenge with figuring out how to display the grid of cards using SwiftUI. I also took time to figure out how to make the cards interact with each other for matching pairs.

## License

    Copyright [2025] [Jamul MacNear]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

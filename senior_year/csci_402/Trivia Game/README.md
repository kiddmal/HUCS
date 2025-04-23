# Project 5 - *Trivia Game*

Submitted by: **Jamul MacNear**

**Trivia Game** is an app that [pulls data from the Open Trivia Database using REST API calls to create an interactive Trivia Game features many different topics. The Game is customizeable from difficulty, multiple choice, true/false, and even features a timer.] 

Time spent: **15** hours spent in total

## Required Features

The following **required** functionality is completed:

- [X] App launches to an Options screen where user can modify the types of questions presented when the game starts. Users should be able to choose:
  - [X] Number of questions
  - [X] Category of questions
  - [X] Difficulty of questions
  - [X] Type of questions (Multiple Choice or True False)
- [ ] User can tap a button to start trivia game, this presents questions and answers in a List or Card view.
  - Hint: For Card view visit your FlashCard app. List view is an equivalent to UITableView in UIKit. Much easier to use!
- [X] Selected choices are marked as user taps their choice (but answered is not presented yet!)
- [X] User can submit choices and is presented with a score on trivia game
 
The following **optional** features are implemented:

- [X] User has answer marked as correct or incorrect after submitting choices (alongside their score).
- [X] Implement a timer that puts pressure on the user! Choose any time that works and auto submit choices after the timer expires. 

The following **additional** features are implemented:

- [ ] List anything else that you can get done to improve the app functionality!

## Video Walkthrough

Here's a walkthrough of implemented user stories:

[Trivia Game Demo] - (https://youtu.be/zh_2LNRK4Is) .

## Notes

Describe any challenges encountered while building the app.

I had a challenge with making the game function smoothly. A lot of data loading at once has caused lag with possible runtime issues. I also came across the bug of the timer continuing to run after the user has completed their trivia quiz.

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

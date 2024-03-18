# Class Timer

## Description
In the teaching profession, effective time management is crucial for a smooth class flow. This project addresses the fundamental need for teachers to efficiently allocate time across various activities within a class. Whether it's allowing students time to complete exercises, providing instructions for tasks, or ensuring adequate time for end-of-class tasks like exit tickets and classroom tidying, this tool aims to streamline the time management process for educators.

## Problem
Ensuring effective time management in the classroom is crucial, yet it often presents challenges for teachers that can lead to negative outcomes:
1. Neglect: Ineffective time management in classrooms can lead to negative outcomes, such as neglecting important tasks due to activities overrunning and contributing to teachers' cognitive load.
2. Complexity of Time Tracking: While keeping track of time seems straightforward, the process becomes more challenging as the number of tasks and variables increases. Teachers often have numerous elements to manage simultaneously, making manual time tracking cumbersome and prone to errors.
3. Increased Cognitive Load: Keeping track of time adds to the cognitive load of teachers, diverting their attention and mental resources away from other essential tasks. As teachers juggle multiple responsibilities, such as class content delivery, student engagement, and behaviour management, the additional burden of time monitoring can strain their cognitive capacity. The cognitive load associated with tracking time can impede a teacher's performance, resulting in subpar explanations and overall teaching quality.

By addressing these challenges, this application aims to provide a streamlined solution for teachers to manage time effectively, minimising cognitive load and optimising instructional efficiency.

## Approach
The Class Timer application aims to reduce the cognitive load of tracking time during a class. 

The application can be accessed here:
https://nomacca.github.io/classtimertool/

### Basic Workflow
1. Set up classes on the Classes page, inputting class names and start and end times.
2. Before or during a class, select the appropriate class on the Classes page.
3. During the class, dynamically choose quick timers from the Timers page to start immediately.

### Classes Page
Before entering a classroom, teachers can set up their classes and their respective times on the Classes page, eliminating the need for setup during the class. Teachers can add classes with their names and start and end times on this page. A recommended setup includes creating classes corresponding to school periods, such as Period 1, Period 2, etc. If teaching double periods, combined classes like Period 1&2 can also be included. The class data is saved to the browser's local storage, ensuring that the information persists even after exiting the application.

### Timers Page
The Timers page allows teachers to manage multiple timers simultaneously. This will often include a class timer and dynamically selected quick timers.

#### Progress Bar
Each timer, including the class timer and quick timers, features a progress bar for visual tracking of time. Various states of the timers provide additional information to aid decision-making during the class. The use of progress bars allows teachers to quickly gain information about the elapsed time during a class. This visual aid can also be presented to students, helping them self-monitor their progress.

#### Quick Timer
A quick timer provides a rapid method to set a timer during a class, enhancing flexibility and adaptability to changing instructional needs.

## License
This is a free software project distributed under the terms of the GNU General Public License (GPL) version 2. You are welcome to reuse and modify the code in accordance with the provisions of this license.

## Contact 
If you use this timer, I would be interested in any feedback.

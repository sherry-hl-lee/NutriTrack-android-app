# NutriTrack

A context-aware Android nutrition application that helps users track
meals, monitor dietary progress, receive reminders, and get personalised
nutrition feedback.

## Overview

NutriTrack was developed as a team project for **FIT5046 -- Mobile and
Distributed Computing Systems** at Monash University.

The application focuses on helping young adults and university students
manage irregular eating habits through meal tracking, nutrition
visualisation, personalised targets, reminders, and intelligent dietary
feedback.

Rather than functioning only as a calorie logger, NutriTrack combines
users' dietary history, goals, time-based context, and current food
intake to provide more adaptive nutrition guidance.

## Key Features

-   User registration and authentication
-   User profile and health-data management
-   Meal logging with create, read, update, and delete (CRUD) operations
-   Food search using external nutrition data
-   Daily calorie and nutrition visualisation
-   Personalised nutrition targets and dietary insights
-   Meal reminder system using Android alarms and notifications
-   Multiple reminder timers with per-user reminder handling
-   AI-assisted meal suggestions and nutrition feedback
-   Daily nutrition scoring based on dietary intake and protein
    consumption
-   Meal history and progress tracking

## Tech Stack

-   **Language:** Kotlin
-   **Platform:** Android
-   **UI:** Jetpack Compose
-   **Local persistence:** Room Database
-   **Networking:** Retrofit
-   **Authentication:** Firebase Authentication
-   **AI integration:** Gemini API
-   **Background scheduling:** Android AlarmManager
-   **Architecture components:** Repository / ViewModel pattern
-   **Version control:** Git & GitHub

## Application Flow

``` text
User
  |
  +--> Authentication
  |
  +--> Dashboard
        |
        +--> Log / Edit Meals
        +--> Search Food
        +--> View Meal History
        +--> Nutrition Targets
        +--> Nutrition Score
        +--> AI Meal Suggestions
        +--> Reminder Settings
        +--> Profile
```

## Engineering Highlights

### Personalised Nutrition Feedback

NutriTrack evaluates dietary information such as calorie and protein
intake against user goals. The application combines rule-based scoring
with AI-assisted feedback to provide actionable suggestions rather than
only displaying raw nutrition data.

### Meal Reminder System

The reminder feature uses Android scheduling and notification mechanisms
to support multiple meal reminders. Reminder behaviour is associated
with individual users so that schedules remain relevant to the signed-in
account.

### Local Data Persistence

Room is used to persist application data such as meal records and
nutrition targets, supporting structured local storage and retrieval
across application screens.

### External API Integration

Retrofit is used to retrieve food and nutritional information for the
food-search workflow, reducing manual data entry and connecting the
Android client to external data sources.

## My Contributions

This was a collaborative team project. My primary contributions
included:

-   Co-developed the **meal reminder system**, including
    notifications/alarms, multiple timers, per-user reminders, and
    reminder UI improvements
-   Co-developed the **AI meal suggestion and nutrition feedback**
    feature
-   Integrated and refined the **Gemini API response workflow**
-   Contributed to the **AI Meal screen** and per-user AI score reset
    behaviour
-   Contributed to application-shell updates including
    navigation/ViewModel integration, alert toasts, and edge-to-edge
    safe-area handling
-   Co-developed the **nutrition scoring system**, including scoring
    logic, protein intake calculations, feedback updates, and related
    fixes

## Running the Project

### Prerequisites

-   Android Studio
-   Android SDK
-   JDK compatible with the project Gradle configuration
-   Required Firebase configuration
-   Required API credentials for external services

### Setup

1.  Clone the repository.
2.  Open the project in Android Studio.
3.  Add the required local API/Firebase configuration without committing
    secrets to Git.
4.  Sync Gradle dependencies.
5.  Run the application using an Android emulator or compatible Android
    device.

> API keys, Firebase credentials, and other secrets should not be
> committed to the repository.

## Project Context

NutriTrack was developed as an academic team project for **FIT5046 --
Mobile and Distributed Computing Systems** at Monash University.

The repository is presented as a software engineering portfolio project.
The **My Contributions** section distinguishes my individual work from
the broader team implementation.

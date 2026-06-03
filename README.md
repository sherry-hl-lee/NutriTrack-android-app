# NutriTrack

## Overview

NutriTrack is an Android mobile application designed to help young adults and university students develop healthier eating habits. The app allows users to record meals, monitor nutritional intake, receive personalised dietary recommendations, and track their health progress through visual reports and scoring systems.

This project was developed as part of the FIT5046 Mobile and Distributed Computing Systems assessment. The app aims to support **SDG 3: Good Health and Well-being** by encouraging healthier food choices and increasing awareness of daily nutrition.

## Features

* User Registration and Login
* Personal Profile and Dietary Preferences
* Meal and Nutrition Tracking
* Food Search and Nutrition Lookup
* Personalised Health Scoring System
* Nutrition Data Visualisation (Charts and Graphs)
* Meal and Water Intake Reminders
* Context-Aware Dietary Recommendations
* AI-Powered Nutrition Feedback

## Technologies Used

* Kotlin
* Jetpack Compose
* Room Database
* Retrofit
* Navigation Component
* WorkManager
* AlarmManager
* Material Design 3
* Google Firebase / Firestore

## External APIs

### USDA FoodData Central API

The app uses the USDA FoodData Central API to retrieve nutritional information for food items, including:

* Calories
* Protein
* Carbohydrates
* Fat
* Vitamins and minerals

API documentation:
https://fdc.nal.usda.gov/api-guide.html

### Google Gemini API

The app uses Google Gemini API to provide intelligent dietary analysis and personalised recommendations based on:

* User dietary goals
* Nutrition intake history
* Meal patterns
* Health scoring results

Examples include:

* Healthy food suggestions
* Nutrition feedback
* Goal-based dietary advice
* Meal improvement recommendations

API documentation:
https://ai.google.dev/

## Setup Instructions

### Prerequisites

* Android Studio (latest version)
* Android SDK 24+
* Internet connection
* Google Firebase project

### API Keys Required

Create a `local.properties` file and add:

```properties
USDA_API_KEY=YOUR_USDA_API_KEY
GEMINI_API_KEY=YOUR_GEMINI_API_KEY
```

### Installation

1. Clone the repository:

```bash
git clone <repository-url>
```

2. Open the project in Android Studio.

3. Add your API keys to `local.properties`.

4. Sync Gradle files.

5. Run the application on an Android emulator or physical device.

## Future Improvements

* Barcode scanning for food products
* Image-based food recognition
* Google Fit integration
* Advanced AI nutrition coaching
* Social features and progress sharing

## Authors

FIT5046 Assessment Project Team

## License

This project is developed for educational purposes only.

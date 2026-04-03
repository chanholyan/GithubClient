# GitHub Android Client

An Android application built with Kotlin and Jetpack Compose to browse and interact with GitHub repositories. This project was developed as a technical assessment.

## Overview

The application supports both guest browsing and authenticated interactions. The UI is built entirely with Jetpack Compose, following a modern, flat design aesthetic. 

## Features

* Guest Browsing: Users can view popular repositories and search repositories by programming language without authentication.
* Authentication: Secure login using a GitHub Personal Access Token (PAT). The token is stored securely using AndroidX EncryptedSharedPreferences.
* Profile: Authenticated users can view a list of their own repositories.
* Native Issue Creation: Users can submit new issues to repositories via a native Android Compose dialog, bypassing web view limitations.
* Error Handling: Built-in network error handling with visual feedback and retry mechanisms across all screens.

## Tech Stack

* Language: Kotlin
* UI: Jetpack Compose, Material 3
* Architecture: MVVM (Model-View-ViewModel)
* Networking: Retrofit, OkHttp, Moshi
* Dependency Injection: Dagger Hilt
* Image Loading: Coil
* Local Storage: AndroidX Security Crypto

## Setup and Installation

1. Clone the repository:
git clone https://github.com/chanholyan/GithubClient.git

2. Open the project in Android Studio.
3. Sync the project with Gradle files.
4. Run the app on an emulator or physical device.

Note: A pre-built release APK is included in the repository for quick evaluation.

## Testing Authentication

To test the authenticated features (viewing personal repositories and raising issues), you will need a GitHub Personal Access Token (classic) with the `repo` scope. 

You can use your own token, or use the temporary token provided below for this assessment:

Test Token: Test Token: <Provided securely via email submission>

Navigate to the "Profile" tab in the app to sign in with the token.
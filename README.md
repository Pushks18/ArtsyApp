# ArtsyApp

ArtsyApp is an Android application that lets users explore artists and their artworks through the Artsy API. Authenticated users can favorite artists, view similar artists, and manage their account seamlessly.

## Features

* **Search Artists**
  Quickly search for artists using the built-in search bar.

* **Artist Details & Artworks**
  View artist descriptions and a curated collection of their artworks.

* **Favorite Artists**
  Logged-in users can favorite artists and view their favorites on the homepage.

* **Authentication**

  * Register and log in securely
  * Persistent login using cookies
  * Logout and delete account functionality

* **Similar Artists**
  Discover similar artists based on your interests once logged in.

## Tech Stack

### Frontend (Android App)

* Kotlin
* Jetpack Compose
* Android Studio
* Retrofit for network calls
* Coil for image loading

### Backend (API Server)

* Node.js
* Express.js
* MongoDB with Mongoose
* JWT authentication with cookies

### API

* [Artsy API](https://developers.artsy.net/)

<img width="352" alt="Screenshot 2025-07-03 at 12 36 36 PM" src="https://github.com/user-attachments/assets/99031371-740d-409d-97bc-dc57bb0153ca" />

<img width="361" alt="Screenshot 2025-07-03 at 12 37 04 PM" src="https://github.com/user-attachments/assets/a9d279e8-e0a8-463d-b4ae-e99068fc8bea" />

<img width="349" alt="Screenshot 2025-07-03 at 12 37 12 PM" src="https://github.com/user-attachments/assets/1f53c872-1087-47bd-9b6a-9014098947ca" />

<img width="341" alt="Screenshot 2025-07-03 at 12 37 24 PM" src="https://github.com/user-attachments/assets/1bdc5dac-bd4d-492b-9413-00d3acbed647" />

---

## Getting Started

### Clone the Repo

```bash
git clone https://github.com/Pushks18/ArtsyApp.git
cd ArtsyApp
```

### Android App Setup

1. Open the project in Android Studio.
2. Sync Gradle and run on an emulator or device.
3. Make sure your backend server is running locally or deployed.

### Backend Setup (Node.js)

```bash
cd backend
npm install
npm run dev
```

> Configure a `.env` file in the backend root with the following:

```
MONGODB_URI=your_mongodb_connection_string
JWT_SECRET=your_jwt_secret
```

---

## Folder Structure

```
ArtsyApp/
│
├── app/                      # Android app code (Kotlin)
├── backend/                  # Node.js backend API
└── README.md
```

---

## Contributing

Pull requests are welcome! If you find any bugs or want to suggest features, feel free to open an issue.


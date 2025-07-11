# Login Signup Page (Android App)

This is a simple Android application implementing a **Login/Signup** flow. It includes multiple user roles, form validation, UI components, and backend integration points.

## 🚀 Features

- 📱 Login & Signup UI
- 👤 Multiple user roles (Normal User, Ministry User)
- 🔒 Password Reset functionality
- 🌄 Image Upload & Display using Firebase
- 📡 Backend-ready structure for Flask API integration
- 🎨 Custom UI components with drawable and layout resources

## 📂 Project Structure

```
Login-signup-page-main/
├── app/
│   ├── build/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/loginsignup/   # Kotlin/Java Source
│   │   │   ├── res/                             # Layouts, Drawables
│   │   │   └── AndroidManifest.xml
├── build.gradle
└── settings.gradle
```

## 🛠️ Getting Started

### Prerequisites

- Android Studio (Giraffe/Koala or above recommended)
- Android SDK 33+
- Firebase Project (optional for image upload feature)

### Installation

1. Clone the repository:
   ```bash
   git clone git@github.com:Chetanpyasi/-eGatividhi.git
   ```
2. Open in Android Studio.
3. Let Gradle sync and build the project.
4. Connect an emulator or physical device.
5. Click **Run ▶️**.

### Firebase Setup (Optional)

1. Create a Firebase project.
2. Add Android App in Firebase Console.
3. Download `google-services.json` and place it in:
   ```
   app/
   └── google-services.json
   ```
4. Enable Firestore and Cloud Storage if needed.

## 📸 Screenshots

(Add screenshots of login, signup, image upload pages)

## 🧑‍💻 Author

- Chetan Pyasi


## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

📱 **App Name**:

_Music Player App_

🚀 How to Run the App

Clone or extract the project in Android Studio.

Make sure your system has:

Android Studio 
Kotlin 1.9+

Gradle sync with Ktor, Coil, and AndroidX libraries

Connect an Android device or start an emulator.

Click Run ▶ to install and launch the app.

The app will automatically fetch songs from the API (iTunes) and display them.

Tap any track to play its preview, and use the play/pause button and seek bar to control playback.


🌐 **Which API You Chose and Why**

_Chosen API_:
 Apple iTunes Search API → https://itunes.apple.com/search?term=pop&media=music&entity=song

Reason for Choosing iTunes API:

Free and public: No authentication or API key required.

Reliable and stable: Apple’s iTunes API is widely used and consistent.

Provides preview audio links (previewUrl) that can be directly streamed using MediaPlayer.

Structured JSON format: Works smoothly with Kotlinx Serialization and Ktor Client.

Fallback-ready: Deezer API was included as an alternative source, but iTunes was chosen as the primary API due to fewer blocking/CORS issues.


** Any Assumptions Made**

The app assumes the user has an active internet connection to fetch and play tracks.

Each song fetched includes a valid preview URL (30-second sample).

Playback is handled using Android’s built-in MediaPlayer, and long streaming or background playback (beyond previews) is not implemented.

Sorting is limited to track name and duration for simplicity.

If the app is launched for the first time, the MusicService may take a moment to initialize — playback starts automatically once it’s bound.

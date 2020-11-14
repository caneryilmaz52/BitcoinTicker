# BitcoinTicker
A Simple Cryptocurrency Price Tracker App with MVVM, Retrofit, LiveData, Lifecycle, Navigation, Hilt, Kotlin Coroutines, Room, Firebase Authentication, Firebase Firestore.

## Project Architecture
- MVVM

## Project Features
1. Coin Searching by name or symbol
2. Realtime coin price tracking
3. Firebase authentication via email and password
4. Firebase Cloud Firestore read, write, delete coin data for favorities

## Dependencies

- [Firebase Authentication](https://firebase.google.com/docs/auth)
- [Firebase Firestore](https://firebase.google.com/docs/firestore)
- [Retrofit](https://square.github.io/retrofit/)
- [Lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle)
- [Kotlin Coroutines](https://developer.android.com/kotlin/coroutines)
- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- [Room Persistence Library](https://developer.android.com/topic/libraries/architecture/room)
- [Navigation](https://developer.android.com/guide/navigation)
- [Glide](https://github.com/bumptech/glide)
- [SpinKit](https://github.com/ybq/Android-SpinKit)


## API
[Cryptocurrency API](https://www.coingecko.com/en/api)

### API End Points
- ping - for check api status
- coins/list - for get all coins
- coins/{id} - for a coin detail(name, current price, hash algoritm etc.)

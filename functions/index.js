const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

// Upon new user Auth, add to DB
exports.addUserToDB = functions.auth.user().onCreate(event => {
  admin.database().ref('/users/' + event.data.uid).set({
    name: event.data.displayName,
    email: event.data.email,
    photo: "null"
  });
  admin.database().ref('/scores/users/' + event.data.uid).set({
  });
});

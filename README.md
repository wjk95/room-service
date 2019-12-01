# RoomService 
This is a mobile (Android) application designed to provide a basic communication between workers of the hotel and its guests.
#



# Features

RoomService uses Firebase Authentication and Cloud Firestore modules. First one is used by hotel workers to log in. Guests log into the application by scanning a QR Code (Google Vision API) containing an ID, which is assigned to a hotel room and can be found there. A guest can request i. e. a room cleaning, or a taxi for chosen time from dedicated guest panel. There is also a tab for hotel information, which shows its name, address and location (using Google Cloud Maps API). Requests are stored in a Firestore database.

There are 4 different worker account types: Manager, Maid, Consierge and Receptionist. A worker type determines which requests he should receive in his panel. Requests are shown with FirebaseUI RecyclerView Adapter, which updates data in real time.

Receptionist will only receive requests of ordering a taxi.
Maid will only receive requests involving room cleaning services.
Consierge will only receive special requests (coming to the room).

Manager receives all requests so he can monitor them, or to help other workers in fulfilling them. He also has a panel dedicated to room management, adding and deleting rooms and changing their IDs.

# Test login

To login to the TestHotel accounts and rooms, use one of the following accounts:

Login: admin@testhotel.pl
Password: 123456

Login: helena@hotel.pl
Password: 123456

Or generate a QR code with

weddirk5

//package com.rs.roomservice;
//
//import android.app.Application;
//import android.content.Intent;
//
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import androidx.annotation.NonNull;
//import androidx.multidex.MultiDexApplication;
//
//import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
//
///* Home.java
// * Created by Wojciech Krajewski
// * 2019
// */public class Home extends MultiDexApplication {
//
//
//
//    @Override
//    public void onCreate() {
//
//        super.onCreate();
//
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        List<String> hotelList = new ArrayList<>();
//
//
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        FirebaseUser mUser = mAuth.getCurrentUser();
//
//
//        //if UID doesn't match any of the registered workers or clients, it will start LoginActivity
//
//
//        if(mUser != null) {
//            //First checking if there is a registered worker in any (hotels/$hotel$/users/userID) path.
//            String id = mUser.getUid();
//            db.collection("hotels").get().addOnCompleteListener((@NonNull Task<QuerySnapshot> task) -> {
//
//                if (task.isSuccessful()) {
//                    //Starting with creating the list of all hotels, which then is going to be iterated to search for its users (workers).
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        hotelList.add(document.getId());
//                    }
//                    //Iteration
//                    for (String s : hotelList) {
//
//                        DocumentReference workerRef = db.collection("hotels").document(s).collection("users").document(mUser.getUid());
//
//                        workerRef.get().addOnCompleteListener((@NonNull Task<DocumentSnapshot> documentSnapshotTask) -> {
//
//                            if (documentSnapshotTask.isSuccessful()) {
//                                DocumentSnapshot document = documentSnapshotTask.getResult();
//
//                                if (document.exists()) {
//                                    String workerType = document.getString("type");
//
//                                    if (workerType.equals("manager")) {
//                                        Intent intent = new Intent(this, ManagerActivity.class);
//                                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
//                                        startActivity(intent);
//
//                                    } else if (workerType.equals("maid")) {
//                                        Intent intent = new Intent(this, MaidActivity.class);
//                                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
//                                        startActivity(intent);
//
//                                    } else if (workerType.equals("consierge")) {
//                                        Intent intent = new Intent(this, ConsiergeActivity.class);
//                                        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
//                                        startActivity(intent);
//                                    }   else if (workerType.equals("receptionist")) {
//                                            Intent intent = new Intent(this, ReceptionistActivity.class);
//                                            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
//                                            startActivity(intent);
//                                    }
//                                }
//
//                            }
//
//                        });
//
//
//                    }
//
//                    /* Every room has its subcollection of clients logged into it. Query looks for document containing clientId
//                       matching current user UID. If it finds it, it proceeds to get a reference to hotel that client has logged
//                       into, in order to get the name of this hotel, which is then passed to the Client Activity. */
//
//                    db.collectionGroup("clients").whereEqualTo("clientId", id).get()
//                            .addOnSuccessListener((QuerySnapshot queryDocumentSnapshots) -> {
//                                int size = queryDocumentSnapshots.getDocuments().size();
//
//                                if(size > 0) {
//
//                                    DocumentReference hotel = queryDocumentSnapshots.getDocuments().get(0).getReference().getParent().getParent().getParent().getParent();
//                                    DocumentReference room = queryDocumentSnapshots.getDocuments().get(0).getReference().getParent().getParent();
//                                    DocumentReference client = queryDocumentSnapshots.getDocuments().get(0).getReference();
//
//                                    /*
//                                    Check if the room ID is the same as when the client first logged in (authentication purposes -
//                                    - Room's ID is often changed by manager to make sure there isn't any abuse.
//                                     */
//                                    client.get().addOnCompleteListener((@NonNull Task<DocumentSnapshot> t) -> {
//
//                                        DocumentSnapshot clientDocumentSnapshot = t.getResult();
//
//                                        String initialId = clientDocumentSnapshot.getString("roomId");
//
//                                        room.get().addOnCompleteListener((@NonNull Task<DocumentSnapshot> t1) -> {
//
//                                            DocumentSnapshot roomDocumentSnapshot = t1.getResult();
//                                            String roomNumber = roomDocumentSnapshot.getString("roomNumber");
//
//                                            String roomId = roomDocumentSnapshot.getId();
//
//                                            if(initialId.equals(roomId)) {
//
//                                                Intent intent = new Intent(Home.this, ClientActivity.class);
//                                                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
//                                                intent.putExtra("roomNumber", roomNumber);
//                                                intent.putExtra("roomId", roomId);
//
//                                                hotel.get().addOnCompleteListener((@NonNull Task<DocumentSnapshot> t2) -> {
//
//                                                    DocumentSnapshot hotelDocumentSnapshot = t2.getResult();
//
//                                                    String hotelName = hotelDocumentSnapshot.getId();
//                                                    intent.putExtra("hotelName", hotelName);
//                                                    startActivity(intent);
//                                                });
//                                            }
//                                        });
//
//                                    });
//
//
//                                }
//
//                            });
//                }
//            });
//
//        } else {
//            Intent intent = new Intent(this, LoginActivity.class);
//            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//
//        }
//
//    }
//}

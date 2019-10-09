package com.rs.roomservice.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.rs.roomservice.R;
import com.rs.roomservice.classes.Task;
import com.rs.roomservice.enums.Worker;
import com.rs.roomservice.ui.main.TaskAdapter;


public class MaidActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference tasksRef = db.collection("hotels").document("TestHotel").collection("tasks");

    private Button logout;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();

    private TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maid);

        String userId = mUser.getUid();
        logout = findViewById(R.id.btn_maid_logout);

        Query query = tasksRef.orderBy("date", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Task> options = new FirestoreRecyclerOptions.Builder<Task>()
                .setQuery(query, Task.class)
                .build();


        adapter = new TaskAdapter(options, Worker.MAID);

        RecyclerView recyclerView = findViewById(R.id.rv_maid);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener((DocumentSnapshot documentSnapshot, int position) -> {

            Task task = documentSnapshot.toObject(Task.class);

            String workerId = task.getWorkerId();
            String taskStatus = task.getTaskStatus();

            DocumentReference taskDocument = documentSnapshot.getReference();



            if(task.getWorkerId()==null) {

                new AlertDialog.Builder(this)
                        .setTitle("Request options")
                        .setMessage("Do you want to fulfill this request?")
                        .setNegativeButton("NO", null)
                        .setPositiveButton("YES", (DialogInterface arg0, int arg1) -> {

                            taskDocument.update("taskStatus", "pending");
                            taskDocument.update("workerId", userId);

                        }).create()
                        .show();

            } else if(task.getWorkerId().equals(userId)) {

                new AlertDialog.Builder(this)
                        .setTitle("What's next?")
                        .setMessage("If you have fulfilled this request and want to delete it, select \"DELETE\". If you want someone else to do it, select \"CANCEL\". ")
                        .setNegativeButton("CANCEL", (DialogInterface arg0, int arg1) -> {
                            taskDocument.update("taskStatus", "todo");
                            taskDocument.update("workerId", null);
                        })
                        .setPositiveButton("DELETE", (DialogInterface arg0, int arg1) -> {

                            //adapter.deleteItem(position);
                            taskDocument.delete();

                        }).create()
                        .show();
            } else if(task.getWorkerId()!=userId && !task.getWorkerId().isEmpty()) {

                DocumentReference docRef = db.collection("hotels").document("TestHotel").collection("users").document(task.getWorkerId());
                docRef.get().addOnCompleteListener((@NonNull com.google.android.gms.tasks.Task<DocumentSnapshot> t) -> {
                    if (t.isSuccessful()) {
                        DocumentSnapshot document = t.getResult();
                        if (document != null) {
                            String workerName = document.getString("name");

                            new AlertDialog.Builder(this)
                                    .setMessage("Request is being done by " + workerName +".")
                                    .setPositiveButton("GOT IT", null)
                                    .create()
                                    .show();

                        }
                    } else {

                        new AlertDialog.Builder(this)
                                .setMessage("Request is being done by " + task.getWorkerId() + ".")
                                .setPositiveButton("GOT IT", null)
                                .create()
                                .show();

                    }


                });

            }


        });

        logout.setOnClickListener((View v) -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MaidActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


}

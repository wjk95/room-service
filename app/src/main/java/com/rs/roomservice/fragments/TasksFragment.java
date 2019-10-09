package com.rs.roomservice.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.rs.roomservice.activities.LoginActivity;
import com.rs.roomservice.ui.main.TaskAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TasksFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TasksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TasksFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button logout;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference tasksRef = db.collection("hotels").document("TestHotel").collection("tasks");

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();

    private TaskAdapter adapter;

    private Context context;

    private OnFragmentInteractionListener mListener;

    public TasksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param //param1 Parameter 1.
     * @param //param2 Parameter 2.
     * @return A new instance of fragment TasksFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TasksFragment newInstance() {
        TasksFragment fragment = new TasksFragment();
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        context = getActivity().getApplicationContext();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);


        String userId = mUser.getUid();
        Query query = tasksRef.orderBy("date", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Task> options = new FirestoreRecyclerOptions.Builder<Task>()
                .setQuery(query, Task.class)
                .build();



        adapter = new TaskAdapter(options, null);

        logout = view.findViewById(R.id.btn_manager_logout);

        RecyclerView recyclerView = view.findViewById(R.id.rv_tasks);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener((DocumentSnapshot documentSnapshot, int position) -> {

            Task task = documentSnapshot.toObject(Task.class);

            String workerId = task.getWorkerId();
            String taskStatus = task.getTaskStatus();

            DocumentReference taskDocument = documentSnapshot.getReference();



            if(task.getWorkerId()==null) {

                new AlertDialog.Builder(getActivity())
                        .setTitle("Request options")
                        .setMessage("Do you want to fulfill this request?")
                        .setNegativeButton("NO", null)
                        .setPositiveButton("YES", (DialogInterface arg0, int arg1) -> {

                            taskDocument.update("taskStatus", "pending");
                            taskDocument.update("workerId", userId);

                        }).create()
                        .show();

            } else if(task.getWorkerId().equals(userId)) {

                new AlertDialog.Builder(getActivity())
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

                            new AlertDialog.Builder(getActivity())
                                    .setMessage("Request is being done by " + workerName +".")
                                    .setPositiveButton("GOT IT", null)
                                    .create()
                                    .show();

                        }
                    } else {

                        new AlertDialog.Builder(context)
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
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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

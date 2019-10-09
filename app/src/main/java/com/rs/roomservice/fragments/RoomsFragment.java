package com.rs.roomservice.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.rs.roomservice.R;
import com.rs.roomservice.classes.Room;
import com.rs.roomservice.ui.main.RoomAdapter;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RoomsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RoomsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference roomsRef = db.collection("hotels").document("TestHotel").collection("rooms");

    private RoomAdapter adapter;

    private Context context;


    private OnFragmentInteractionListener mListener;

    public RoomsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param //param1 Parameter 1.
     * @param //param2 Parameter 2.
     * @return A new instance of fragment RoomsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RoomsFragment newInstance() {
        RoomsFragment fragment = new RoomsFragment();
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
        View view = inflater.inflate(R.layout.fragment_rooms, container, false);

        Query query = roomsRef.orderBy("roomNumber", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Room> options = new FirestoreRecyclerOptions.Builder<Room>()
                .setQuery(query, Room.class)
                .build();

        adapter = new RoomAdapter(options);

        FloatingActionButton mAddButton = (FloatingActionButton) view.findViewById(R.id.fab_add_room);

        mAddButton.setOnClickListener((View v) -> {

            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());


            View mView = getLayoutInflater().inflate(R.layout.dialog_room_add, null);

            EditText mNumberEditText = (EditText) mView.findViewById(R.id.et_room_number);
            EditText mIdEditText = (EditText) mView.findViewById(R.id.et_room_id);
            Button mGenerateButton = (Button) mView.findViewById(R.id.btn_generate_id);
            Button mSaveButton = (Button) mView.findViewById(R.id.btn_save);
            Button mCancelButton = (Button) mView.findViewById(R.id.btn_cancel);


            mBuilder.setView(mView);
            AlertDialog dialog = mBuilder.create();
            dialog.show();

            //Generating Firestore Unique ID
            mGenerateButton.setOnClickListener((View view1) -> {
                mIdEditText.setText(roomsRef.document().getId());
            });

            mCancelButton.setOnClickListener((View view1) -> {
                dialog.dismiss();
            });

            //Save button functionality
            mSaveButton.setOnClickListener((View view1) -> {

                String roomNumber = mNumberEditText.getText().toString();
                String roomId = mIdEditText.getText().toString();


                if(!roomNumber.trim().isEmpty() && !roomId.trim().isEmpty()) {

                    Map<String, Object> room = new HashMap<>();
                    room.put("roomID", roomId);
                    room.put("roomNumber", roomNumber);
                    room.put("roomOccupied", false);

                    db.collection("hotels").document("TestHotel").collection("rooms").document(roomId)
                            .set(room);

                    Toast.makeText(context,
                            "Room added successfully",
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                } else {
                    Toast.makeText(context,
                            "Please fill all the data",
                            Toast.LENGTH_SHORT).show();
                }

            });
        });

        RecyclerView recyclerView = view.findViewById(R.id.rv_rooms);
        // Removes blinks
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);





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

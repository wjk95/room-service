package com.rs.roomservice.ui.main;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rs.roomservice.R;
import com.rs.roomservice.classes.Room;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

/* RoomAdapter.java
 * Created by Wojciech Krajewski
 * 2019
 */
public class RoomAdapter extends FirestoreRecyclerAdapter<Room, RoomAdapter.RoomHolder> {


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    private int mExpandedPosition = -1;
    int previousExpandedPosition = -1;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public RoomAdapter(@NonNull FirestoreRecyclerOptions<Room> options) {
        super(options);
    }


    @Override
    protected void onBindViewHolder(@NonNull RoomHolder roomHolder, int i, @NonNull Room room) {


        int colorGreen = roomHolder.itemView.getResources().getColor(R.color.colorEmptyRoom);
        int colorRed = roomHolder.itemView.getResources().getColor(R.color.colorOccupiedRoom);

        roomHolder.roomNumber.setText("Room " + room.getRoomNumber());



        final boolean isExpanded = i == mExpandedPosition;
        roomHolder.expandedItem.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        roomHolder.itemView.setActivated(isExpanded);

        if (isExpanded)
            previousExpandedPosition = i;

        roomHolder.item.setOnClickListener((View v) -> {
                mExpandedPosition = isExpanded ? -1: i;
                notifyItemChanged(previousExpandedPosition);
                notifyItemChanged(i);
        });


        roomHolder.roomDetails.setOnClickListener((View v) -> {
            new AlertDialog.Builder(roomHolder.itemView.getContext())
                    .setTitle("Room Details")
                    .setMessage("Room ID is: " + room.getRoomID() + ".")
                    .setNegativeButton("CANCEL", null)
                    .setPositiveButton("COPY ID TO CLIPBOARD", (DialogInterface arg0, int arg1) -> {
                        ClipboardManager clipboard = (ClipboardManager) roomHolder.itemView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText(
                                "Room ID",
                                room.getRoomID());
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(roomHolder.itemView.getContext(), "Saved to clipboard.", Toast.LENGTH_SHORT).show();
                    }).create().show();


        });

        roomHolder.roomDelete.setOnClickListener((View v) -> {
            new AlertDialog.Builder(roomHolder.itemView.getContext())
                    .setTitle("Deleting room")
                    .setMessage("Do you want to delete this room? Any guest will also get logged out.")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, (DialogInterface arg0, int arg1) -> {
                        deleteClients(i);
                        deleteItem(i);
                    }).create().show();

        });


        roomHolder.roomLogout.setOnClickListener((View v) -> {
            new AlertDialog.Builder(roomHolder.itemView.getContext())
                    .setTitle("Logging guests out")
                    .setMessage("Any guest will get logged out. Room ID will stay the same. Proceed?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, (DialogInterface arg0, int arg1) -> {
                        deleteClients(i);
                    }).create().show();

        });

        if (!room.isRoomOccupied()) {
            roomHolder.roomStatus.setText("Empty");
            roomHolder.roomStatus.setTextColor(colorGreen);
        } else {
            roomHolder.roomStatus.setText("Occupied");
            roomHolder.roomStatus.setTextColor(colorRed);
        }

    }

    @NonNull
    @Override
    public RoomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_item,
                parent, false);


        return new RoomHolder(v);

    }

    class RoomHolder extends RecyclerView.ViewHolder {

         LinearLayout item;
         TextView roomNumber;
         TextView roomStatus;
         LinearLayout expandedItem;
         TextView roomDetails;
         TextView roomLogout;
         TextView roomDelete;

         public RoomHolder(@NonNull View itemView) {
             super(itemView);
             item = itemView.findViewById(R.id.room_item_layout);
             roomNumber = itemView.findViewById(R.id.tv_room_number);
             roomStatus = itemView.findViewById(R.id.tv_room_status);
             expandedItem = itemView.findViewById(R.id.room_item_layout_expanded);
             roomDetails = itemView.findViewById(R.id.tv_room_details);
             roomLogout = itemView.findViewById(R.id.tv_room_logout);
             roomDelete = itemView.findViewById(R.id.tv_room_delete);
         }
     }


    public void deleteItem(int position) {

        getSnapshots().getSnapshot(position).getReference().delete();

    }


    public void deleteClients(int position) {

        getSnapshots().getSnapshot(position).getReference().collection("clients")
                .get().addOnCompleteListener((@NonNull Task<QuerySnapshot> task) -> {

            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {

                    document.getReference().delete();
                }
            }
        });
        getSnapshots().getSnapshot(position).getReference().update("isOccupied", false);
    }


}

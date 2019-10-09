package com.rs.roomservice.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.rs.roomservice.R;
import com.rs.roomservice.classes.Task;
import com.rs.roomservice.enums.Worker;

import java.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/* TaskAdapter.java
 * Created by Wojciech Krajewski
 * 2019
 */public class TaskAdapter extends FirestoreRecyclerAdapter<Task, TaskAdapter.TaskHolder> {


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    private OnItemClickListener listener;

    Worker worker;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersRef = db.collection("hotels").document("TestHotel").collection("users");

    public TaskAdapter(@NonNull FirestoreRecyclerOptions<Task> options, Worker worker) {
        super(options);
        this.worker = worker;
    }

    @Override
    protected void onBindViewHolder(@NonNull TaskHolder taskHolder, int i, @NonNull Task task) {

        if (worker == Worker.MAID && !task.getTaskType().equals("New Towel")
                && !task.getTaskType().equals("Rubbish Takeout") && !task.getTaskType().equals("Bathroom Replenishment")
                && !task.getTaskType().equals("Room Cleaning")){
            taskHolder.itemLayout.setVisibility(View.GONE);
        }

        if (worker == Worker.RECEPTIONIST && (task.getTaskType().equals("New Towel")
                || task.getTaskType().equals("Rubbish Takeout") || task.getTaskType().equals("Bathroom Replenishment")
                || task.getTaskType().equals("Room Cleaning") || task.getTaskType().equals("Assistance needed"))){
            taskHolder.itemLayout.setVisibility(View.GONE);
        }

        if (worker == Worker.CONSIERGE && !task.getTaskType().equals("Assistance needed")) {
            taskHolder.itemLayout.setVisibility(View.GONE);
        }

        taskHolder.roomNumber.setText("Room " + task.getRoomNumber());
        taskHolder.taskType.setText(task.getTaskType());
        SimpleDateFormat format = new SimpleDateFormat("dd-MM HH:mm");
        String date = format.format(task.getDate().toDate());
        taskHolder.issueDate.setText(date);

        int colorGreen = taskHolder.itemView.getResources().getColor(R.color.colorBackgroundGreen);
        int colorYellow = taskHolder.itemView.getResources().getColor(R.color.colorBackgroundYellow);

        if(task.getTaskStatus().equals("todo")) {
            taskHolder.itemLayout.setBackgroundColor(colorYellow);
        } else if( task.getTaskStatus().equals("pending")) {
            taskHolder.itemLayout.setBackgroundColor(colorGreen);
        }


    }

    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item,
                parent, false);


        return new TaskHolder(v);

    }


    class TaskHolder extends RecyclerView.ViewHolder {

        TextView taskType;
        TextView roomNumber;
        TextView issueDate;
        LinearLayout itemLayout;

        public TaskHolder(@NonNull View itemView) {
            super(itemView);
            taskType = itemView.findViewById(R.id.tv_task_type);
            roomNumber = itemView.findViewById(R.id.tv_task_room_number);
            itemLayout = itemView.findViewById(R.id.layout_task_item);
            issueDate = itemView.findViewById(R.id.tv_task_date_created);

            itemView.setOnClickListener((View v) -> {

                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(getSnapshots().getSnapshot(position), position);
                }


            });


        }

    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void deleteItem(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }


}

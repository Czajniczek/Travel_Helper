package com.example.travelhelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class NeedRideFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private Activity myActivity;
    private Context myContext;
    private FloatingActionButton addFloatingButton;
    private RecyclerView recyclerView;
    private List<NeedRide> needRides;
    private NeedRideAdapter needRideAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        myActivity = getActivity();
        myContext = myActivity.getApplicationContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_need_ride, container, false);

        needRides = new ArrayList<>();

        addFloatingButton = view.findViewById(R.id.need_ride_floating_button);
        addFloatingButton.setOnClickListener(v -> {
            myActivity.startActivity(new Intent(myContext, AddNeedRideActivity.class));
        });

        recyclerView = view.findViewById(R.id.need_ride_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(myContext));

        needRideAdapter = new NeedRideAdapter();
        recyclerView.setAdapter(needRideAdapter);

        CollectionReference collection = FirebaseFirestore.getInstance().collection("Need ride");
        collection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {

                    NeedRide needRide = new NeedRide();
                    needRide.setId(doc.getId());
                    needRide.setsFromCity(doc.getString("From city"));
                    needRide.setsFromStreet(doc.getString("From street"));
                    needRide.setsToCity(doc.getString("To city"));
                    needRide.setsToStreet(doc.getString("To street"));
                    needRide.setsDay(doc.getString("Day"));
                    needRide.setsMonth(doc.getString("Month"));
                    needRide.setsYear(doc.getString("Year"));
                    needRide.setsHour(doc.getString("Hour"));
                    needRide.setsMinute(doc.getString("Minute"));
                    needRide.setUserId(doc.getString("User ID"));
                    needRides.add(needRide);
                }
                needRideAdapter.notifyDataSetChanged();
            } else Toast.makeText(myContext, "Error: " + task.getException(), Toast.LENGTH_LONG).show();
        });

        return view;
    }

    private class NeedRideHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView avatar;
        private TextView cityFrom, streetFrom, cityTo, streetTo, date, time;
        private NeedRide needRide;

        public NeedRideHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_need_ride, parent, false));

            itemView.setOnClickListener(this);


            cityFrom = itemView.findViewById(R.id.item_need_ride_city_from);
            streetFrom = itemView.findViewById(R.id.item_need_ride_street_from);
            cityTo = itemView.findViewById(R.id.item_need_ride_city_to);
            streetTo = itemView.findViewById(R.id.item_need_ride_street_to);
            date = itemView.findViewById(R.id.item_need_ride_date2);
            time = itemView.findViewById(R.id.item_need_ride_time2);
            avatar = itemView.findViewById(R.id.need_ride_image);
        }

        public void bind(NeedRide needRide) {
            this.needRide = needRide;

            cityFrom.setText(needRide.getsFromCity());
            streetFrom.setText(needRide.getsFromStreet());
            cityTo.setText(needRide.getsToCity());
            streetTo.setText(needRide.getsToStreet());
            date.setText(needRide.getsDay() + "/" + needRide.getsMonth() + "/" + needRide.getsYear());
            time.setText(needRide.getsHour() + ":" + needRide.getsMinute());

            StorageReference imgRef = storageReference.child("users/" + needRide.getUserId() + "/profile.jpg");
            imgRef.getDownloadUrl().addOnSuccessListener(v -> {
                Glide.with(myContext).load(v).into(avatar);
            }).addOnFailureListener(v -> {
                avatar.setImageResource(R.drawable.ic_account);
                Toast.makeText(myContext, "Error:", Toast.LENGTH_LONG).show();
            });
        }

        @Override
        public void onClick(View v) {

        }
    }

    private class NeedRideAdapter extends RecyclerView.Adapter<NeedRideHolder> {

        @NonNull
        @Override
        public NeedRideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new NeedRideHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull NeedRideHolder holder, int position) {
            if (needRides != null) {
                NeedRide needRide = needRides.get(position);
                holder.bind(needRide);
            } else Log.d("MainActivity", "No need rides");
        }

        @Override
        public int getItemCount() {
            if (needRides != null) return needRides.size();
            else return 0;
        }
    }
}
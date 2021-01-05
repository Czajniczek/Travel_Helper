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
import android.renderscript.Element;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class NeedRideFragment extends Fragment {

    //region VARIABLES
    //LAYOUT
    private FloatingActionButton addFloatingButton;
    private RecyclerView recyclerView;
    private List<NeedRide> needRides;

    //FIREBASE
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private CollectionReference collection;
    private StorageReference imgRef;
    //private NeedRide needRide;

    //OTHERS
    private Activity myActivity;
    private Context myContext;
    private NeedRideAdapter needRideAdapter;
    //endregion

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

        //HOOKS
        recyclerView = view.findViewById(R.id.need_ride_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(myContext));
        addFloatingButton = view.findViewById(R.id.need_ride_floating_button);

        needRideAdapter = new NeedRideAdapter();
        recyclerView.setAdapter(needRideAdapter);

        addFloatingButton.setOnClickListener(v -> {
            myActivity.startActivity(new Intent(myContext, AddNeedRideActivity.class));
        });

        //Pobiera dane każdego itemu z bazy
        collection = firebaseFirestore.collection("Need ride");
        collection.addSnapshotListener((value, error) -> {
            if (value == null) return;
            for (DocumentChange dc : value.getDocumentChanges()) {
                switch (dc.getType()) {
                    case ADDED:
                        DocumentSnapshot doc = dc.getDocument();
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
                        DocumentReference documentReference = firebaseFirestore.collection("users").document(needRide.getUserId());
                        Task<DocumentSnapshot> userSnap = documentReference.get();
                        userSnap.addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task1.getResult();
                                needRide.setsUserName(documentSnapshot.getString("Username"));
                                needRide.setsPhoneNumber(documentSnapshot.getString("Phone number"));
                                needRides.add(needRide);
                                needRideAdapter.notifyDataSetChanged();
                            }
                        }).addOnFailureListener(e -> {
                            Toast.makeText(myContext, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
                }
            }
        });

        return view;
    }

    private class NeedRideHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView avatar;
        private TextView cityFrom, streetFrom, cityTo, streetTo, date, time, userName, phoneNumber;
        private NeedRide needRide;

        public NeedRideHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_need_ride, parent, false));

            itemView.setOnClickListener(this);

            avatar = itemView.findViewById(R.id.need_ride_image);
            cityFrom = itemView.findViewById(R.id.item_need_ride_city_from);
            streetFrom = itemView.findViewById(R.id.item_need_ride_street_from);
            cityTo = itemView.findViewById(R.id.item_need_ride_city_to);
            streetTo = itemView.findViewById(R.id.item_need_ride_street_to);
            date = itemView.findViewById(R.id.item_need_ride_date2);
            time = itemView.findViewById(R.id.item_need_ride_time2);
            userName = itemView.findViewById(R.id.item_need_ride_user_name);
            phoneNumber = itemView.findViewById(R.id.item_need_ride_phone_number);
        }

        public void bind(NeedRide needRide) {
            this.needRide = needRide;

            //Ustawienie wartości w widoku
            cityFrom.setText(needRide.getsFromCity());
            streetFrom.setText(needRide.getsFromStreet());
            cityTo.setText(needRide.getsToCity());
            streetTo.setText(needRide.getsToStreet());
            date.setText(needRide.getsDay() + "/" + needRide.getsMonth() + "/" + needRide.getsYear());
            time.setText(needRide.getsHour() + ":" + needRide.getsMinute());
            userName.setText(needRide.getsUserName());
            phoneNumber.setText(needRide.getsPhoneNumber());

            imgRef = storageReference.child("users/" + needRide.getUserId() + "/profile.jpg");
            imgRef.getDownloadUrl().addOnSuccessListener(v -> {
                Glide.with(myContext).load(v).into(avatar);
            }).addOnFailureListener(v -> {
                avatar.setImageResource(R.drawable.ic_account);
                Toast.makeText(myContext, "Error", Toast.LENGTH_LONG).show();
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
package com.example.travelhelper.NeedRide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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
import com.example.travelhelper.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import retrofit2.http.DELETE;

public class NeedRideFragment extends Fragment {

    //region VARIABLES
    //LAYOUT
    private RecyclerView recyclerView;
    private List<NeedRide> needRides;
    private FloatingActionButton addFloatingButton;

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

        //region ON CLICK LISTENERS
        addFloatingButton.setOnClickListener(v -> myActivity.startActivity(new Intent(myContext, AddNeedRideActivity.class)));
        //endregion

        collection = firebaseFirestore.collection("Need ride");
        //https://firebase.google.com/docs/firestore/query-data/listen
        collection.addSnapshotListener((value, error) -> {
            if (value == null) return;
            for (DocumentChange dc : value.getDocumentChanges()) {
                switch (dc.getType()) {
                    //case REMOVED: break;
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
                        DocumentReference documentReference = firebaseFirestore.collection("Users").document(needRide.getUserId());
                        Task<DocumentSnapshot> userSnap = documentReference.get();
                        userSnap.addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task1.getResult();
                                needRide.setsUserName(documentSnapshot.getString("Username"));
                                needRide.setsPhoneNumber(documentSnapshot.getString("Phone number"));
                                needRides.add(needRide);
                                needRideAdapter.notifyDataSetChanged();
                            }
                        }).addOnFailureListener(e -> Toast.makeText(myContext, getResources().getString(R.string.toast_error), Toast.LENGTH_LONG).show());
                }
            }
        });

        /*collection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    NeedRide needRide = new NeedRide();
                    needRide.setId(document.getId());
                    needRide.setsFromCity(document.getString("From city"));
                    needRide.setsFromStreet(document.getString("From street"));
                    needRide.setsToCity(document.getString("To city"));
                    needRide.setsToStreet(document.getString("To street"));
                    needRide.setsDay(document.getString("Day"));
                    needRide.setsMonth(document.getString("Month"));
                    needRide.setsYear(document.getString("Year"));
                    needRide.setsHour(document.getString("Hour"));
                    needRide.setsMinute(document.getString("Minute"));
                    needRide.setUserId(document.getString("User ID"));
                    DocumentReference documentReference = firebaseFirestore.collection("Users").document(needRide.getUserId());
                    Task<DocumentSnapshot> userSnap = documentReference.get();
                    userSnap.addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task1.getResult();
                            needRide.setsUserName(documentSnapshot.getString("Username"));
                            needRide.setsPhoneNumber(documentSnapshot.getString("Phone number"));
                            needRides.add(needRide);
                            needRideAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });*/

        return view;
    }

    private class NeedRideHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView avatar;
        private TextView cityFrom, streetFrom, cityTo, streetTo, date, time, userName, phoneNumber;
        //private NeedRide needRide;

        public NeedRideHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_need_ride, parent, false));

            itemView.setOnClickListener(this);

            avatar = itemView.findViewById(R.id.need_ride_image);
            userName = itemView.findViewById(R.id.item_need_ride_user_name);
            phoneNumber = itemView.findViewById(R.id.item_need_ride_phone_number);
            cityFrom = itemView.findViewById(R.id.item_need_ride_city_from);
            streetFrom = itemView.findViewById(R.id.item_need_ride_street_from);
            cityTo = itemView.findViewById(R.id.item_need_ride_city_to);
            streetTo = itemView.findViewById(R.id.item_need_ride_street_to);
            date = itemView.findViewById(R.id.item_need_ride_date2);
            time = itemView.findViewById(R.id.item_need_ride_time2);
        }

        @SuppressLint("SetTextI18n")
        public void bind(NeedRide needRide) {
            //this.needRide = needRide;

            imgRef = storageReference.child("Users/" + needRide.getUserId() + "/User photo");
            imgRef.getDownloadUrl().addOnSuccessListener(v -> Glide.with(myContext).load(v).into(avatar))
                    .addOnFailureListener(v -> {
                        avatar.setImageResource(R.drawable.ic_account);
                        //Toast.makeText(myContext, getResources().getString(R.string.toast_error_downloading_user_image), Toast.LENGTH_LONG).show();
                    });

            userName.setText(needRide.getsUserName());
            phoneNumber.setText(needRide.getsPhoneNumber());
            cityFrom.setText(needRide.getsFromCity());
            streetFrom.setText(needRide.getsFromStreet());
            cityTo.setText(needRide.getsToCity());
            streetTo.setText(needRide.getsToStreet());
            date.setText(needRide.getsDay() + "/" + needRide.getsMonth() + "/" + needRide.getsYear());
            time.setText(needRide.getsHour() + ":" + needRide.getsMinute());
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
            }
        }

        @Override
        public int getItemCount() {
            if (needRides != null) return needRides.size();
            else return 0;
        }
    }
}
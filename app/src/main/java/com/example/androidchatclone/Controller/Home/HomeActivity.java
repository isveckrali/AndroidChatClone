package com.example.androidchatclone.Controller.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidchatclone.Controller.Chat.ChatActivity;
import com.example.androidchatclone.Controller.Inbox.InboxActivity;
import com.example.androidchatclone.Controller.Login.SignInActivity;
import com.example.androidchatclone.Controller.Profile.ProfileActivity;
import com.example.androidchatclone.Controller.Util.Child;
import com.example.androidchatclone.Controller.Util.Helper;
import com.example.androidchatclone.Model.UserInfo;
import com.example.androidchatclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    private Context context = this;
    private ArrayList<ListItem> list = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private  Adapter adapter;
    private ListView listView;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        setTitle("New Chat");

        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listView = findViewById(R.id.listView);
        adapter = new Adapter();
        listView.setAdapter(adapter);

        databaseReference.child(Child.users).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    UserInfo info = snapshot.getValue(UserInfo.class);
                    if (!info.getUid().equals(firebaseAuth.getUid())) {
                        list.add(new ListItem(info.getUid(), info.getName(), info.getPhotoUrl()));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("recipientUid",list.get(position).getUid());
                intent.putExtra("recipientName",list.get(position).getName());
                startActivity(intent);
            }
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.navBottomBtn);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.navBtnInbox) {
                startActivity(new Intent(context, InboxActivity.class));
               // Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
            } else if (menuItem.getItemId() == R.id.navBtnProfile) {
                startActivity(new Intent(context, ProfileActivity.class));
            }
                
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(context, SignInActivity.class));
            finish();
        } else {

        }
        return super.onOptionsItemSelected(item);
    }


    class Adapter extends BaseAdapter {


        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = convertView;
            if (convertView == null) {
               view =  layoutInflater.inflate(R.layout.control_row_item_profile,null);
            }

            TextView tvName = view.findViewById(R.id.tvName);
            CircleImageView iwPhoto = view.findViewById(R.id.imageView);

            tvName.setText(list.get(position).getName());
            Helper.imageLoad(context,list.get(position).getProfileUrl(),iwPhoto);

            return view;
        }
    }

    class ListItem {
        String uid;
        String name;
        String profileUrl;

        public ListItem(String uid, String name, String profileUrl) {
            this.uid = uid;
            this.name = name;
            this.profileUrl = profileUrl;
        }

        public String getUid() {
            return uid;
        }

        public String getName() {
            return name;
        }

        public String getProfileUrl() {
            return profileUrl;
        }
    }
}

package com.example.androidchatclone.Controller.Inbox;

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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidchatclone.Controller.Chat.ChatActivity;
import com.example.androidchatclone.Controller.Login.SignInActivity;
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

public class InboxActivity extends AppCompatActivity {

    private ArrayList<ListItem> list = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private ListView listView;
    private Adapter adapter;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        setTitle("Chats");
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listView = findViewById(R.id.listView);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        adapter = new Adapter();
        listView.setAdapter(adapter);

        databaseReference.child(Child.CHAT_INBOX).orderByChild("senderUid").equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (final DataSnapshot infoInbox:dataSnapshot.getChildren()) {
                    databaseReference.child(Child.users).orderByChild("uid").equalTo(infoInbox.child("recipientUid").getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot infoUser:dataSnapshot.getChildren()) {
                                list.clear();
                                list.add(new ListItem(infoInbox.getKey(),
                                        infoInbox.child("inboxKey").getValue().toString(),
                                        infoInbox.child("isRead").getValue().toString(),
                                        infoUser.child("uid").getValue().toString(),
                                        infoUser.child("name").getValue().toString(),
                                        infoUser.child("photoUrl").getValue().toString()));
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (list.get(position).getIsRead().equals("1")) {
                    databaseReference.child(Child.CHAT_INBOX).child(list.get(position).getKey()).child("isRead").setValue("0");
                }

                Intent intent = new Intent(context,ChatActivity.class);
                intent.putExtra("recipientUid",list.get(position).getRecipientUid());
                intent.putExtra("recipientName",list.get(position).getName());
                startActivity(intent);
            }
        });
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
                view = layoutInflater.inflate(R.layout.control_row_item_profile,null);

            }

            TextView txtName = view.findViewById(R.id.tvName);
            CircleImageView circleImageView = view.findViewById(R.id.imageView);

            txtName.setText(list.get(position).getName());
            Helper.imageLoad(context,list.get(position).getPhotolUrl(),circleImageView);
            circleImageView.setBorderColor(getResources().getColor(R.color.colorProfileBorder));
            if (list.get(position).getIsRead().equals("1")) {
            circleImageView.setBorderWidth(4);
            } else {
            circleImageView.setBorderWidth(0);
            }
            return view;
        }
    }

    class  ListItem {

        String key;
        String inboxKey;
        String isRead;
        String recipientUid;
        String name;
        String photolUrl;

        public ListItem(String key, String inboxKey, String isRead, String recipientUid, String name, String photolUrl) {
            this.key = key;
            this.inboxKey = inboxKey;
            this.isRead = isRead;
            this.recipientUid = recipientUid;
            this.name = name;
            this.photolUrl = photolUrl;
        }

        public String getKey() {
            return key;
        }

        public String getInboxKey() {
            return inboxKey;
        }

        public String getIsRead() {
            return isRead;
        }

        public String getRecipientUid() {
            return recipientUid;
        }

        public String getName() {
            return name;
        }

        public String getPhotolUrl() {
            return photolUrl;
        }
    }
}

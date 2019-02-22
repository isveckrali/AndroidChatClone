package com.example.androidchatclone.Controller.Chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidchatclone.Controller.Login.SignInActivity;
import com.example.androidchatclone.Controller.Util.Child;
import com.example.androidchatclone.Controller.Util.Helper;
import com.example.androidchatclone.Model.ChatInbox;
import com.example.androidchatclone.Model.ChatLast;
import com.example.androidchatclone.Model.Chats;
import com.example.androidchatclone.Model.UserInfo;
import com.example.androidchatclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private ArrayList<ListItem> list = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private ListView listView;
    private EditText edtMessage;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String recipientUid;
    private String recipientName;
    private String senderUid;
    private Context context = this;
    private ChatInbox chatInbox;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listView = findViewById(R.id.listView);
        edtMessage = findViewById(R.id.editMessage);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        senderUid = firebaseAuth.getUid();

        recipientUid = getIntent().getExtras().getString("recipientUid");
        recipientName = getIntent().getExtras().getString("recipientName");
        setTitle(recipientName);
        //Toast.makeText(context, recipientUid + " " + recipientName , Toast.LENGTH_SHORT).show();
        adapter = new Adapter();
        listView.setAdapter(adapter);
        createChat();

        findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(Child.CHATS).push().setValue(
                        new Chats(chatInbox.getInboxKey(), senderUid, edtMessage.getText().toString().trim()), new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull final DatabaseReference databaseReferenceResult) {
                               edtMessage.setText("");
                                final String messageKey = databaseReferenceResult.getKey();
                                databaseReference.child(Child.CHAT_LAST)
                                        .orderByChild("inboxKey")
                                        .equalTo(chatInbox.getInboxKey())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                            databaseReference.child(Child.CHAT_LAST).child(snapshot.getKey()).child("messageKey").setValue(messageKey);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                databaseReference.child(Child.CHAT_INBOX)
                                        .orderByChild("inboxKey")
                                        .equalTo(chatInbox.getInboxKey())
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                                           if (recipientUid.equals(snapshot.child("senderUid").getValue().toString())) {
                                                databaseReference.child(Child.CHAT_INBOX).child(snapshot.getKey()).child("isRead").setValue("0");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                );
            }
        });
    }

    private void createChat() {

        databaseReference.child(Child.CHAT_INBOX)
                .orderByChild("senderUid")
                .equalTo(senderUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    if (snapshot.getValue(ChatInbox.class).getRecipientUid().equals(recipientUid)) {
                        chatInbox = snapshot.getValue(ChatInbox.class);
                    }
                }

                chatInboxAndChatLast();
                chats();
                chatLast();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void chatInboxAndChatLast() {
        if (chatInbox == null) {
            //create chat inbox
            String key = databaseReference.push().getKey();
            databaseReference.child(Child.CHAT_INBOX).push().setValue(
                    new ChatInbox(key,senderUid,recipientUid,"0")
            );

            databaseReference.child(Child.CHAT_INBOX).push().setValue(
                    new ChatInbox(key,recipientUid,senderUid,"0")
            );

            chatInbox = new ChatInbox(key,senderUid,recipientUid,"0");

            //create last chat
            databaseReference.child(Child.CHAT_LAST).push().setValue(
                    new ChatLast(key,"")
            );
        }

    }

    private void chats() {

        databaseReference.child(Child.CHATS)
                .orderByChild("inboxKey")
                .equalTo(chatInbox.getInboxKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chats chats = snapshot.getValue(Chats.class);
                    list.add(new ListItem(chats.getSenderUid(), chats.getMessage()));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void chatLast() {

        databaseReference.child(Child.CHAT_LAST)
                .orderByChild("inboxKey")
                .equalTo(chatInbox.getInboxKey())
                .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                databaseReference.child(Child.CHATS)
                        .child(dataSnapshot.child("messageKey").getValue().toString())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Chats chats = dataSnapshot.getValue(Chats.class);
                        list.add(new ListItem(chats.getSenderUid(), chats.getMessage()));
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                view = layoutInflater.inflate(R.layout.control_row_item_chat,null);
            }

            LinearLayout linearRow = view.findViewById(R.id.linearRow);
            LinearLayout linearChat = view.findViewById(R.id.linearChat);

            if (list.get(position).getSenderUid().equals(senderUid)) {
                linearChat.setBackgroundResource(R.drawable.draw_chat_i);
                linearRow.setGravity(Gravity.RIGHT);
            } else {
                linearChat.setBackgroundResource(R.drawable.draw_chat_she);
                linearRow.setGravity(Gravity.LEFT);
            }

            TextView txtMessage = view.findViewById(R.id.txtMessage);
            txtMessage.setText(list.get(position).getMessage());
            return view;
        }
    }


    class ListItem {
        String senderUid;
        String message;

        public ListItem(String senderUid, String message) {
            this.senderUid = senderUid;
            this.message = message;
        }

        public String getSenderUid() {
            return senderUid;
        }

        public String getMessage() {
            return message;
        }
    }
}

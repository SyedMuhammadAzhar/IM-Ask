package com.example.loginforans;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;


public class SearchActivity extends AppCompatActivity {



    private Toolbar ToolB;
    private ImageButton searchbutton;
    private EditText searchtext;
    private RecyclerView searchresult;
    private DatabaseReference Userdetailsref;
    FirebaseRecyclerAdapter<FindUsers, FindUserViewholder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Userdetailsref = FirebaseDatabase.getInstance().getReference().child("Users");

        ToolB = (Toolbar) findViewById(R.id.search_app_bar);
        setSupportActionBar(ToolB);
        getSupportActionBar().setTitle("Searchbar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        searchresult = (RecyclerView) findViewById(R.id.search_list);
        //searchresult.setVisibility(View.GONE);
        searchresult.setLayoutManager(new LinearLayoutManager(this));

        searchtext = (EditText) findViewById(R.id.search_box);

        searchbutton = (ImageButton) findViewById(R.id.search_button);


        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String SearchInputText = searchtext.getText().toString();

                SearchUsers(SearchInputText);
            }


        });


    }


    public void SearchUsers(String searchboxtext) {

        Toast.makeText(this, "Searching....", Toast.LENGTH_LONG).show();
        Query Searchpeople = Userdetailsref.orderByChild("Name").startAt(searchboxtext).endAt(searchboxtext + "\uf8ff");

        FirebaseRecyclerOptions<FindUsers> options =
                new FirebaseRecyclerOptions.Builder<FindUsers>()
                        .setQuery(Searchpeople, FindUsers.class)
                        .build();

         adapter = new FirebaseRecyclerAdapter<FindUsers, FindUserViewholder>(options) {
            @Override
            public FindUserViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.userdisplaylayout, parent, false);
                FindUserViewholder ViewHolder = new FindUserViewholder(view);
                return ViewHolder;
            }

            @Override
            public void onBindViewHolder(FindUserViewholder holder, int position, FindUsers model) {
                holder.setName(model.getName());
                holder.setSemester(model.getSemester());
                holder.setprofileurl(getApplicationContext(),model.getprofileurl());
            }
        };

        searchresult.setAdapter(adapter);
        //searchresult.setVisibility(View.VISIBLE);
        adapter.startListening();


    }


    public static class FindUserViewholder extends RecyclerView.ViewHolder{

       View mView;

        public FindUserViewholder(View itemview){

            super(itemview);

            mView = itemview;

       }

        public void setprofileurl(Context ctx, String profileurl){

            CircleImageView profilepic = (CircleImageView) mView.findViewById(R.id.profile_picture_list);
            Picasso.get().load(profileurl).placeholder(R.drawable.profile).into(profilepic);
        }

        public void setName(String Name){

            TextView Uname = (TextView) mView.findViewById(R.id.Username);
            Uname.setText(Name);


        }

        public void setSemester(String Semester){
            TextView Sem = (TextView) mView.findViewById(R.id.Semester);
            Sem.setText(Semester);

        }

    }



    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        //adapter.stopListening();
    }






// make sure to add startlistening for the firebase recycle adapter in onstart and onstop
}

package com.example.loginforans;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Commentsection extends AppCompatActivity {


    private DatabaseReference RefToUserinDatabase,RefToCommentsinDatabase;
    private FirebaseAuth mauth;


    private RecyclerView rv_commentlist;
    EditText commentInputText;
    CircleImageView commentuserdp,postCommentbtn;


    private String userclickedpostkey;


    String Current_Userr;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commentsection);




        Intent i = getIntent();
        userclickedpostkey=i.getStringExtra("postkey");








        RefToUserinDatabase= FirebaseDatabase.getInstance().getReference("Users");
        RefToCommentsinDatabase=FirebaseDatabase.getInstance().getReference().child("Comments").child(userclickedpostkey);





        mauth=FirebaseAuth.getInstance();

        Current_Userr=mauth.getCurrentUser().getUid();


































        rv_commentlist=findViewById(R.id.rv_commentsection);

        rv_commentlist.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setMeasurementCacheEnabled(false);
        rv_commentlist.setLayoutManager(linearLayoutManager);

        commentuserdp =findViewById(R.id.commentsection_dp);
        commentInputText=findViewById(R.id.commentsection_query);
        postCommentbtn=findViewById(R.id.commentsection_publish_comment);











        ////getting dp url for comment section and assignning dp
        RefToUserinDatabase.child(Current_Userr).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    String dp = dataSnapshot.child("profileurl").getValue().toString();
                    Picasso.get().load(dp).into(commentuserdp);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });










        postCommentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


              //  Toast.makeText(getApplicationContext(),"clicked",Toast.LENGTH_SHORT).show();


               final String commenttext=commentInputText.getText().toString();

              //  Toast.makeText(getApplicationContext(),"clicked2",Toast.LENGTH_SHORT).show();


                if(TextUtils.isEmpty(commenttext))
                {
                  //  Toast.makeText(getApplicationContext(),"enter text",Toast.LENGTH_SHORT).show();
                    commentInputText.setError("Enter Text please");
                }
                else
                {

                   // Toast.makeText(getApplicationContext(),Current_Userr,Toast.LENGTH_SHORT).show();
                    RefToUserinDatabase.child(Current_Userr).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if(dataSnapshot.exists())
                            {

                              //  Toast.makeText(getApplicationContext(),"Pass",Toast.LENGTH_SHORT).show();


                                Calendar calendarForDate =Calendar.getInstance();
                                SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");

                               String savecurrentdate=currentDate.format(calendarForDate.getTime());

                              //  Log.d(TAG, "Date: "+savecurrentdate);

                                Calendar calendarForTime = Calendar.getInstance();
                                SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss");
                                String savecurrenttime=currenttime.format(calendarForTime.getTime());
                               // Log.d(TAG, "Time: "+savecurrenttime);

                             //   Toast.makeText(getApplicationContext(),"Pass2",Toast.LENGTH_SHORT).show();


                                 String username= dataSnapshot.child("Name").getValue().toString();
                                 String dpurl  = dataSnapshot.child("profileurl").getValue().toString();




                                 String randomkey=savecurrenttime+savecurrentdate+Current_Userr;


                                Map<String,Object> mydata = new HashMap<>();


                                mydata.put("C_username",username);
                                mydata.put("C_comment",commenttext);
                                mydata.put("C_dpurl",dpurl);
                                mydata.put("C_time",savecurrenttime);
                                mydata.put("C_date",savecurrentdate);
                                mydata.put("C_uid",Current_Userr);


                                RefToCommentsinDatabase.child(randomkey).updateChildren(mydata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(getApplicationContext(),"Commented Successfully",Toast.LENGTH_SHORT).show();

                                        }
                                        else{

                                            Toast.makeText(getApplicationContext(),"Comment Error..",Toast.LENGTH_SHORT).show();
                                        }


                                    }
                                });


                                commentInputText.setText(null);






                            }



                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }










            }
        });















    }




    @Override
    protected void onStart() {
        super.onStart();

        Displayallcomments();
        //when activity runs it show comments


    }





    private void Displayallcomments()
    {



        FirebaseRecyclerOptions<commentmodal> options = new FirebaseRecyclerOptions.Builder<commentmodal>()
                .setQuery(RefToCommentsinDatabase,commentmodal.class)
                .build();




        //firebase adapter recycler class need two thing modal class and static class we have both

        FirebaseRecyclerAdapter<commentmodal,CommentsViewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<commentmodal, CommentsViewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CommentsViewholder commentsViewholder, int i, @NonNull commentmodal commentmodal) {


                Picasso.get().load(commentmodal.C_dpurl).into(commentsViewholder.cmntDp);
                commentsViewholder.cmntUsername.setText(commentmodal.C_username);
                commentsViewholder.cmntDate.setText(commentmodal.C_date);
                commentsViewholder.cmntTime.setText(commentmodal.C_time);
                commentsViewholder.cmntDescription.setText(commentmodal.C_comment);








            }

            @NonNull
            @Override
            public CommentsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_comment_layout,parent,false);

                CommentsViewholder holder = new CommentsViewholder(view);



                return holder;
            }
        };



        rv_commentlist.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();







    }









    public static class CommentsViewholder extends RecyclerView.ViewHolder
    {

        View mview;



        CircleImageView cmntDp;

        TextView cmntUsername,cmntDate,cmntTime,cmntDescription;







        public CommentsViewholder(@NonNull View itemView) {

            super(itemView);
            mview=itemView;



            cmntDp=itemView.findViewById(R.id.Cc_post_pic);
            cmntUsername=itemView.findViewById(R.id.Cc_user_name);
            cmntDate=itemView.findViewById(R.id.Cc_post_date);
            cmntTime=itemView.findViewById(R.id.Cc_post_time);
            cmntDescription=itemView.findViewById(R.id.Cc_post_description);



        }
    }



}

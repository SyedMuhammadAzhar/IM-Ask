package com.example.loginforans;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class home extends AppCompatActivity {


    String TAG = "MyTag";


    private FirebaseAuth auth;

    private FirebaseDatabase MyDatabase,MyDatabase2;
    public DatabaseReference RefToDatabaseUser,RefToDatabasePosts,RefToDatabaseLikes;

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mactionBarDrawerToggle;
    private RecyclerView rvPostList;

    private Toolbar mToolbar;


    private CircleImageView navprofileimage;
    private TextView navprofilename;

    String Currentuser;

    //////////////////////////////////////


    //////////Additing post//////


    /////////////////////////////////////
    CircleImageView post_dp, add_pic, publish_post;
    EditText writeQuery;
    ImageView ivpostingimage;

    Integer pick_image = 1;


   private Uri imagesuri2;



   FirebaseStorage mFirestore;
   StorageReference mReferencetofirebase;



    String savecurrentdate,savecurrenttime,randomName;
    String postPicUrl;

    private ProgressBar pb;




    boolean LikeChecker;



    String dpurl; //will be sending to comment activity for dp










    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        MyDatabase = FirebaseDatabase.getInstance();
      //  MyDatabase2=FirebaseDatabase.getInstance();
        RefToDatabaseUser = MyDatabase.getReference().child("Users");
        RefToDatabasePosts=MyDatabase.getReference().child("Posts");
        RefToDatabaseLikes=MyDatabase.getReference().child("Likes");











        ///////

        mFirestore=FirebaseStorage.getInstance();
        mReferencetofirebase=mFirestore.getReference();



        Log.d(TAG, "onStart: " + user);


        if (user == null) {

            Log.d(TAG, "onStart: check");

            SendtoLoginActivity();
        } else {


            CheckUserExistance();
            initviews();
            ivpostingimage.setVisibility(View.GONE); ///Hiding ImageView
            HideProgressBar();



            displayAllUsersposts();  //when app runs this method will fill the recycler view


            Currentuser = auth.getCurrentUser().getUid();
            Log.d(TAG, "onCreate: adas" + Currentuser);


            mToolbar = (Toolbar) findViewById(R.id.app_bar_mainpage);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle("Home");


            // btn1=findViewById(R.id.Btn_Signout_Account);
            drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
            mactionBarDrawerToggle = new ActionBarDrawerToggle(home.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
            drawerLayout.addDrawerListener(mactionBarDrawerToggle);
            mactionBarDrawerToggle.syncState();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


            navigationView = (NavigationView) findViewById(R.id.navigation_view);
            View navView = navigationView.inflateHeaderView(R.layout.navi_header);
            navprofileimage = (CircleImageView) navView.findViewById(R.id.nav_profile_image);
            navprofilename = (TextView) navView.findViewById(R.id.nav_user_full_name);


            RefToDatabaseUser.child(Currentuser).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {

                        if (dataSnapshot.hasChild("Name")) {

                            String name = dataSnapshot.child("Name").getValue().toString();
                            navprofilename.setText(name);

                        }
                        if (dataSnapshot.hasChild("profileurl")) {
                            String profileimage = (String) dataSnapshot.child("profileurl").getValue();
                            Picasso.get().load(profileimage).into(navprofileimage);
                            Picasso.get().load(profileimage).into(post_dp);

                        } else {
                            Toast.makeText(getApplicationContext(), "No name and pic in database", Toast.LENGTH_SHORT).show();
                        }


                        //   Log.d(TAG, "onDataChangeadsda: " + name + "" + profileimage);


                        // Picasso.get().setLoggingEnabled(true);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    UserMenuSelector(menuItem);

                    return false;
                }
            });


        }
















        /////////////////////////////////
        ////////////////////////////////
        //////Recyclerview settings/////
        ///////////////////////////////
        ///////////////////////////////
        //////////////////////////////





        rvPostList.setHasFixedSize(true);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

        linearLayoutManager.setStackFromEnd(true); //fills its content starting from the bottom of the view.

        linearLayoutManager.setReverseLayout(true); ///it will reverse layout new post will add on top

        rvPostList.setLayoutManager(linearLayoutManager);






    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (mactionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




    private void SendtoLoginActivity() {


        Log.d(TAG, "SendtoLoginActivity: bbbbb");
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        // i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }


    public void SendToRegistrationActivity() {

        Log.d(TAG, "SendToRegistrationActivity: adsd");
        Intent i = new Intent(getApplicationContext(), Registration.class);
        startActivity(i);
        finish();
    }

    private boolean CheckUserExistance() {

        final boolean[] flag = {false};

        final String Current_User_Id = auth.getCurrentUser().getUid();

        Log.d(TAG, "CheckUserExistance: ");

        RefToDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d(TAG, "onDataChange: ");
                if (!dataSnapshot.hasChild(Current_User_Id)) {

                    flag[0] = false;
                    SendToUserDetailsActivity();
                } else if (dataSnapshot.hasChild(Current_User_Id)) {

                    flag[0] = true;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return flag[0];
    }


    private void SendToUserDetailsActivity() {

        Intent i = new Intent(getApplicationContext(), UserDetails.class);
        // i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();


    }








    ///////////////////////////////////////////////////////////////////
    //Home Activity Addding Post /////////////////////////////////////
//////////////////////////////////////////////////////////////////////






    public void initviews() {

        post_dp = (CircleImageView) findViewById(R.id.Post_dp);
        add_pic = (CircleImageView) findViewById(R.id.add_photo_post);
        publish_post = (CircleImageView) findViewById(R.id.publish_post);
        writeQuery = (EditText) findViewById(R.id.ask_query);
        ivpostingimage = (ImageView) findViewById(R.id.image_of_post);
        pb=(ProgressBar)findViewById(R.id.progressBarhome);
        rvPostList=(RecyclerView)findViewById(R.id.all_post_rv);

    }


    public void selectImageForPost(View view) {
        //if user want to add picture alongside with query.

        rvPostList.setVisibility(View.GONE); //hiding recycler view

        ivpostingimage.getLayoutParams().height=600;
        ivpostingimage.getLayoutParams().width=1300;

        Intent gallery = new Intent();
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery, pick_image);




    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        Log.d(TAG, "onActivityResult: ");
        if (requestCode == pick_image && resultCode == RESULT_OK && data != null) {
            Log.d(TAG, "onActivityResult: 2" + requestCode);


            imagesuri2 = data.getData();
            try {  //this will show image on userdetail xml
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagesuri2);
                // Log.d(TAG, String.valueOf(bitmap));


                ivpostingimage.setImageBitmap(bitmap);

                ivpostingimage.setVisibility(View.VISIBLE);



            } catch (IOException e) {
                e.printStackTrace();

            }


        }

    }


    public boolean postingValidation()
    {
        //Validation for posting must write something before publishing post

        String Description=writeQuery.getText().toString();





        if(imagesuri2==null)
        {
            Toast.makeText(getApplicationContext(),"select image please",Toast.LENGTH_LONG).show();
            return false;
        }

       else if(TextUtils.isEmpty(Description))
        {
            Toast.makeText(getApplicationContext(),"write please",Toast.LENGTH_LONG).show();
            return false;
        }
        else
        {

            return true;
        }


    }








    ////////////////////////////
    //////////////////////////
    //Posting query or pic////
    /////////////////////////
    /////////////////////////



    public void postIt(View view)
    {

        if(!postingValidation())
        {
            return;
        }
        else {

            //Validation Pass

            ShowProgressBar();


           // Toast.makeText(getApplicationContext(),"Pass",Toast.LENGTH_LONG).show();



            Calendar calendarForDate =Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");

            savecurrentdate=currentDate.format(calendarForDate.getTime());

            Log.d(TAG, "Date: "+savecurrentdate);

            Calendar calendarForTime = Calendar.getInstance();
            SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss");
            savecurrenttime=currenttime.format(calendarForTime.getTime());
            Log.d(TAG, "Time: "+savecurrenttime);


            randomName=savecurrenttime+savecurrentdate;
            //Toast.makeText(getApplicationContext(),savecurrentdate+savecurrentdate,Toast.LENGTH_SHORT).show();




          final  StorageReference pathforpostpic = mReferencetofirebase.child("Posts").child(imagesuri2.getLastPathSegment()+randomName+".jpg"); //unique name for each pic

            pathforpostpic.putFile(imagesuri2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                  //  Toast.makeText(getApplicationContext(),"Image added successfully to firestore",Toast.LENGTH_LONG).show();
                    HideProgressBar();


                    ivpostingimage.setVisibility(View.GONE);




                    pathforpostpic.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //When we get url successfully


                        //    Toast.makeText(getApplicationContext(),"getting into the saving",Toast.LENGTH_SHORT).show();

                            postPicUrl=uri.toString();

                            savingPostDataToDatabase();

                          //  Toast.makeText(getApplicationContext(),"getting out of saving",Toast.LENGTH_SHORT).show();





                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //when url failed

                            Log.d(TAG, "onFailure: "+e.getMessage());
                        }
                    });




                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getApplicationContext(),"image not added to fire store",Toast.LENGTH_LONG).show();



                }
            });










        }













    }


    public void savingPostDataToDatabase()
    {


        RefToDatabaseUser.child(Currentuser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

             //   Toast.makeText(getApplicationContext(),"Entered",Toast.LENGTH_LONG).show();

                if(dataSnapshot.exists())
                {


                 //   Toast.makeText(getApplicationContext(),"Child Exists",Toast.LENGTH_LONG).show();


                    String username = dataSnapshot.child("Name").getValue().toString();
                     dpurl = dataSnapshot.child("profileurl").getValue().toString();


                    Map<String,Object> postsdata = new HashMap<>();

                    postsdata.put("uid",Currentuser);
                    postsdata.put("name",username);
                    postsdata.put("dpurl",dpurl);
                    String postDescription=writeQuery.getText().toString();
                    postsdata.put("Description",postDescription);
                    postsdata.put("postpicurl",postPicUrl);
                    postsdata.put("date",savecurrentdate);
                    postsdata.put("time",savecurrenttime);

                    RefToDatabasePosts.child(randomName+Currentuser).updateChildren(postsdata);

                    writeQuery.setText(null);  //textview is set null




                  //  Toast.makeText(getApplicationContext(),postPicUrl+""+writeQuery,Toast.LENGTH_SHORT);
                    Log.d(TAG, "onDataChange: post pic url checking"+postPicUrl+""+writeQuery);









                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        /////////
        //where activity related to posting finished and after displayalluserPosts processes is finished then make recycler view visiable

        displayAllUsersposts();


        rvPostList.setVisibility(View.VISIBLE);   //making recyclerview visiable so that it dont override on selecting image for post


    }




    public void displayAllUsersposts() {


        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>()
                .setQuery(RefToDatabasePosts,Posts.class)
                .build();



        //firebase adapter reequire two class 1st modal class second static class of view holder

       FirebaseRecyclerAdapter<Posts, postsviewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, postsviewholder>(options) {
           @Override
           protected void onBindViewHolder(@NonNull postsviewholder holder, int position, @NonNull Posts model) {





               final String userclickpostkey = getRef(position).getKey(); // here we are getting post key which is currentuser+randomnumber





               if(model.getPostpicurl()==null)
               {

                   holder.userpostimage.setVisibility(View.GONE);
                   Picasso.get().load(model.dpurl).into(holder.userdpinposts);
                   holder.usernameinposts.setText(model.getName());
                   holder.userdateinposts.setText(" " + model.getDate());
                   holder.usertimeinposts.setText(" " + model.getTime());
                   holder.userpostsdescription.setText(model.getDescription());
                  // rvPostList.smoothScrollToPosition(rvPostList.getAdapter().getItemCount()-1); //moving recyclerview to the top when post is added

               }
               else {


                   Picasso.get().load(model.dpurl).into(holder.userdpinposts);
                   holder.usernameinposts.setText(model.getName());
                   holder.userdateinposts.setText(" " + model.getDate());
                   holder.usertimeinposts.setText(" " + model.getTime());
                   holder.userpostsdescription.setText(model.getDescription());
                   Picasso.get().load(model.getPostpicurl()).into(holder.userpostimage);
                //   rvPostList.smoothScrollToPosition(rvPostList.getAdapter().getItemCount()-1); //moving recyclerview to the top when post is added


                   holder.mview.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {


                     //      Toast.makeText(getApplicationContext(),"postkey"+userclickpostkey,Toast.LENGTH_SHORT).show();

                           Intent clickpost = new Intent(getApplicationContext(), editpost.class);
                           clickpost.putExtra("postkey", userclickpostkey);
                           startActivity(clickpost);


                       }
                   });








                   holder.setLikeButtonStatus(userclickpostkey);
                   //it will checkbuttonstatus either post is like or not if it is like get like count and set red thumbs
                   //if not like 0 likes white thumb


                   holder.countcomment(userclickpostkey);



                    //////////////////////
                   ///////////////////////
                   /////for liking comment
                   ///////////////////////
                   ///////////////////////



                   holder.userpostlikebtn.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {

                           LikeChecker=true;

                           RefToDatabaseLikes.addValueEventListener(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                   if(LikeChecker==true) //if not this validation if user single time like post and autoliking and disliking will start infinately coz continuous change will ouccur and datasnapshot will call again and again
                                   {



                                   if(dataSnapshot.child(userclickpostkey).hasChild(Currentuser)) //it will check if post is already like
                                   {

                                       RefToDatabaseLikes.child(userclickpostkey).child(Currentuser).removeValue();
                                       LikeChecker=false; //post is disliked now


                                   }
                                   else
                                   {

                                       RefToDatabaseLikes.child(userclickpostkey).child(Currentuser).setValue(true);
                                       LikeChecker=false; //post is liked now bu id of current user id and id is saved inside like/userlickpostkey/

                                   }



                                   }

                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError databaseError) {

                               }
                           });




                       }
                   });


                   ///////////////////////////////////////
                   //moving to comment section////////////

                   holder.userpostcommentbtn.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {


                           Intent commentintent = new Intent(getApplicationContext(),Commentsection.class);
                           commentintent.putExtra("postkey",userclickpostkey);
                           //Toast.makeText(getApplicationContext(),dpurl,Toast.LENGTH_SHORT).show();
                           startActivity(commentintent);

                       }
                   });

                   holder.userpostcomments.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {


                           Intent commentintent = new Intent(getApplicationContext(),Commentsection.class);
                           commentintent.putExtra("postkey",userclickpostkey);
                           //Toast.makeText(getApplicationContext(),dpurl,Toast.LENGTH_SHORT).show();
                           startActivity(commentintent);

                       }
                   });













               }


           }

           @NonNull
           @Override
           public postsviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               //connecting to layout

               View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.users_posts,parent,false);

               postsviewholder holder = new postsviewholder(view);


               return holder; //it pass layout to below class which now we can access
           }
       };


       rvPostList.setAdapter(firebaseRecyclerAdapter);
       firebaseRecyclerAdapter.startListening();







    }

    public static class postsviewholder extends RecyclerView.ViewHolder{


         View mview;


        CircleImageView userdpinposts;
        TextView usernameinposts,userdateinposts,usertimeinposts,userpostsdescription,userpostlikes,userpostcomments;
        ImageView userpostimage;


        CircleImageView userpostlikebtn,userpostcommentbtn;




        Integer countlikes,countcomments;
        //for count like we must know about how many time post is liked the above method will help in it. we will get userclickedpostkey inside it we will check total current user  which mean the user which liked that post

        String Current_user;

        DatabaseReference RefTolikes,RefToComments;




        public postsviewholder(@NonNull View itemView) {
            super(itemView);


            mview=itemView;    //over all post that is "view" is stored in mview we will set click listner on it.

            userdpinposts=itemView.findViewById(R.id.posted_user_pic);

            usernameinposts=itemView.findViewById(R.id.posted_user_name);
            userdateinposts=itemView.findViewById(R.id.posted_post_date);
            usertimeinposts=itemView.findViewById(R.id.posted_post_time);
            userpostsdescription=itemView.findViewById(R.id.posted_post_description);
            userpostlikes=itemView.findViewById(R.id.posted_post_totallike);
            userpostcomments=itemView.findViewById(R.id.posted_post_total_comments);

            userpostimage=itemView.findViewById(R.id.posted_post_image);


            userpostlikebtn=itemView.findViewById(R.id.posted_post_likebtn);
            userpostcommentbtn=itemView.findViewById(R.id.posted_post_commentbtn);

            RefTolikes=FirebaseDatabase.getInstance().getReference().child("Likes");
            RefToComments=FirebaseDatabase.getInstance().getReference().child("Comments");

            Current_user=FirebaseAuth.getInstance().getCurrentUser().getUid();








        }



        //here we will count total likes

        public void setLikeButtonStatus(final String userclickpostkey){

            RefTolikes.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child(userclickpostkey).hasChild(Current_user))
                    {
                        countlikes=(int)dataSnapshot.child(userclickpostkey).getChildrenCount();
                        //it will get all like on a single post

                        userpostlikebtn.setImageResource(R.drawable.like);
                        userpostlikes.setText(countlikes+" Likes");



                    }
                    else
                    {
                        countlikes=(int)dataSnapshot.child(userclickpostkey).getChildrenCount();
                        //no like on post so zero likes

                        userpostlikebtn.setImageResource(R.drawable.dislike);
                        userpostlikes.setText(countlikes+" Likes");


                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }


         public void countcomment(final String userclickpostkey)
         {





             RefToComments.addValueEventListener(new ValueEventListener() {
                 @Override
                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                     if(dataSnapshot.child(userclickpostkey).exists())
                     {
                         countcomments=(int)dataSnapshot.child(userclickpostkey).getChildrenCount();
                         //it will get all like on a single post

                         userpostcomments.setText(countcomments+" Comments");



                     }


                 }

                 @Override
                 public void onCancelled(@NonNull DatabaseError databaseError) {

                 }
             });




        }

















    }



    private void sendtoSearch(){

        Intent sendIntent = new Intent(home.this, SearchActivity.class);
        startActivity(sendIntent);


    }
    private void sendtoProfile() {
        Intent i = new Intent(home.this, ViewPorfile.class);
        startActivity(i);


    }

    private void ShowProgressBar() {
        pb.setIndeterminate(true);
        pb.setVisibility(View.VISIBLE);
    }

    private void HideProgressBar() {

        pb.setVisibility(View.GONE);
    }



    ////////////////////////////////////


    ///Navigation menu //

    /////////////////////////////////////////
    private void UserMenuSelector(MenuItem menuItem) {

        switch (menuItem.getItemId()) {


            case R.id.nav_home: {
                Toast.makeText(getApplicationContext(), "Moving to top", Toast.LENGTH_SHORT).show();
                rvPostList.smoothScrollToPosition(rvPostList.getAdapter().getItemCount()-1);

                break;
            }

            case R.id.nav_search: {
                sendtoSearch();
              //  Toast.makeText(getApplicationContext(), "Search", Toast.LENGTH_SHORT).show();
                break;
            }

            case R.id.nav_profile: {
                sendtoProfile();
             //   Toast.makeText(getApplicationContext(), "Viewing Profile", Toast.LENGTH_SHORT).show();
                break;
            }

            case R.id.nav_logout: {
                auth.signOut();
                SendtoLoginActivity();
                break;
            }

        }
    }




}

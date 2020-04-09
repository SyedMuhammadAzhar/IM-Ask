package com.example.loginforans;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class editpost extends AppCompatActivity {



    ImageView editIV;
    TextView editTV;
    Button editEdButton,editDlbutton;



    String postclickedkey;






    private DatabaseReference Reftopostindatabase;


    private FirebaseAuth mauth;

    private String Current_user;  //to check to post of user





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editpost);


        initview();

        Intent intent =getIntent();
        postclickedkey=intent.getStringExtra("postkey");


        Toast.makeText(getApplicationContext(),postclickedkey,Toast.LENGTH_SHORT).show();
        Reftopostindatabase=FirebaseDatabase.getInstance().getReference().child("Posts");




        mauth=FirebaseAuth.getInstance();

        Current_user=mauth.getCurrentUser().getUid();






        ///Disabling buttons at start
        editEdButton.setVisibility(View.INVISIBLE);
        editDlbutton.setVisibility(View.INVISIBLE);
        //coz only the user that is currenlty online can edit and delete pic




        Reftopostindatabase.child(postclickedkey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if(dataSnapshot.exists())
                {

                    final String postDescription = dataSnapshot.child("Description").getValue().toString();
                    String posturl=dataSnapshot.child("postpicurl").getValue().toString();


                    editTV.setText(postDescription);
                    Picasso.get().load(posturl).into(editIV);

                    String userid=dataSnapshot.child("uid").getValue().toString();

                   // Toast.makeText(getApplicationContext(),"Current user "+Current_user+"userid"+userid,Toast.LENGTH_SHORT).show();

                    if(Current_user.equals(userid))
                    {


                        editEdButton.setVisibility(View.VISIBLE);
                        editDlbutton.setVisibility(View.VISIBLE);



                    }

                    editEdButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Toast.makeText(getApplicationContext(),"okay1",Toast.LENGTH_SHORT).show();
                            EditCurrentpost(postDescription);

                        }
                    });






                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






        //Deleting current post
        editDlbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DeleteCurrentpost();

            }
        });








    }




    public void initview(){

        editIV=findViewById(R.id.edit_post_image);
        editTV=findViewById(R.id.edit_post_description);
        editEdButton=findViewById(R.id.edit_post_editbutton);
        editDlbutton=findViewById(R.id.edit_post_deletebutton);
    }



    public void DeleteCurrentpost() {

        Reftopostindatabase.child(postclickedkey).removeValue(); //remove from realtime database
        SendToHomeActivity();


    }


    public void EditCurrentpost(String postDescription) {

        Toast.makeText(getApplicationContext(),"okay2",Toast.LENGTH_SHORT).show();


       final AlertDialog.Builder builder = new AlertDialog.Builder(editpost.this);
        builder.setTitle("Edit post: ");

        final EditText inputfield = new EditText(editpost.this);

        inputfield.setText(postDescription);

        builder.setView(inputfield);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {



                Reftopostindatabase.child(postclickedkey).child("Description").setValue(inputfield.getText().toString());

                Toast.makeText(getApplicationContext(),"Post updated",Toast.LENGTH_SHORT).show();


            }
        });


        builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

            }
        });





        AlertDialog dialog = builder.create();

        dialog.show();









    }





    public void SendToHomeActivity(){

        Intent i = new Intent(getApplicationContext(),home.class);
        // i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //new task if activity is already present it wont create its new instance but pooped other activities and use called activity
        //Clear task finishes all other activites before starting the targeted activity

        startActivity(i);
        finish(); //can't go to previous activity with back button

    }






}

package com.example.loginforans;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ViewPorfile extends AppCompatActivity {

    private CircleImageView ProPic;
    private TextView name;
    private EditText Emailo,Semester;
    private ImageButton Cmail,Cmester;
    private DatabaseReference UserDataRef,emailref;
    private FirebaseAuth Auth;
    public String firstmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_porfile);

        ProPic = (CircleImageView) findViewById(R.id.ProfilePicture);
        name = (TextView) findViewById(R.id.Namey);
        Semester = (EditText) findViewById(R.id.Semesterno);
        Emailo = (EditText) findViewById(R.id.Emaily);
        Cmail = (ImageButton) findViewById(R.id.Change_Email);
        Cmester = (ImageButton) findViewById(R.id.Change_Semester);


        Auth = FirebaseAuth.getInstance();
        String CurrentUID = Auth.getCurrentUser().getUid();
        UserDataRef = FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUID);


        UserDataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                String name1 = dataSnapshot.child("Name").getValue().toString();
                String email1 = dataSnapshot.child("Email").getValue().toString();
                String Semester1 = dataSnapshot.child("Semester").getValue().toString();
                String Image1 = dataSnapshot.child("profileurl").getValue().toString();


                firstmail = email1;

                name.setText(name1);
                Emailo.setText(email1);
                Semester.setText(Semester1);
                Picasso.get().load(Image1).into(ProPic);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Cmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = Emailo.getText().toString();

                if (email != firstmail) {

                    //emailref.child("Users").child("Email").setValue(email);

                    //emailref.setValue(email);
                    Toast.makeText(getApplicationContext(),"saved", Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(getApplicationContext(),"Enter a new email", Toast.LENGTH_SHORT).show();
                }
            }
        });









    }
}

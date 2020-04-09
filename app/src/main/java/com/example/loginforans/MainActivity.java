package com.example.loginforans;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    EditText et1,et2;
    TextView tv1,tv2,tv3;
    Button btn1;
   private ProgressBar pb;

    FirebaseAuth auth;
//    FirebaseAuth.AuthStateListener AuthListner;
//
//    FirebaseDatabase Mydatabase;
//    DatabaseReference RefToDatabse;





    String TAG ="MyTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



//        Mydatabase=FirebaseDatabase.getInstance();
//        RefToDatabse= Mydatabase.getReference().child("Users");




        auth=FirebaseAuth.getInstance();


        InitView();
        HideProgressBar();


    }


    public void Signup(View view)
    {

        if(!ValidateId() | !ValidatePass())
        {
            return;
        }
        else
        {



            String id = (et1.getEditableText().toString()+"@imsciences.com");
            String pass =et2.getEditableText().toString();
            ShowProgressBar();

            auth.signInWithEmailAndPassword(id,pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            Log.d(TAG, "onComplete: "+task);

                            if(task.isSuccessful())
                            {
                                Toast.makeText(getApplicationContext(),"Login",Toast.LENGTH_SHORT).show();
                                HideProgressBar();

                                SendToHomeActivity();

                            }
                            else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                            {
                                et2.setError("Invalid Password");
                                HideProgressBar();
                            }
                            else if(task.getException() instanceof FirebaseAuthInvalidUserException)
                            {
                                et1.setError("Email is not available");
                                HideProgressBar();
                            }

                        }
                    });

        }

    }





    private void InitView() {


        et1=findViewById(R.id.ET_Id);
        et2=findViewById(R.id.ET_Pass);
        tv2=findViewById(R.id.TV_Slogan_text);
        tv3=findViewById(R.id.TV_Signup_Text);
        btn1=findViewById(R.id.Btn_SignIn);
        pb=findViewById(R.id.progressBar);



    }


    public void GoToRegistration(View view)
    {
        Intent i = new Intent(getApplicationContext(),Registration.class);
        startActivity(i);

    }


    public boolean ValidateId()
    {
        Log.d(TAG, "ValidateId: ");
        String id=(et1.getEditableText().toString());

        if(id.equals(null))
        {
            et1.setError("ID is Required, Can't be empty");
            return false;
        }
        else
            et1.setError(null);
        return true;

    }

    public boolean ValidatePass()
    {
        Log.d(TAG, "ValidatePass: ");
        String pass=et2.getEditableText().toString();

        if(pass.equals(null))
        {
            et2.setError("Pass is Required, Can't be null");
            return false;
        }
        else if(pass.length()<6)
        {
            et2.setError("Enter at least 6 charachters");
            return false;
        }
        else
            et2.setError(null);
        return true;
    }

    private void ShowProgressBar() {
        pb.setIndeterminate(true);
        pb.setVisibility(View.VISIBLE);
    }

    private void HideProgressBar() {

        pb.setVisibility(View.GONE);
    }


    public void SendToHomeActivity(){

        Intent i = new Intent(getApplicationContext(),home.class);
       // i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //new task if activity is already present it wont create its new instance but pooped other activities and use called activity
        //Clear task finishes all other activites before starting the targeted activity

        startActivity(i);
        finish(); //can't go to previous activity with back button

    }



    @Override
    protected void onStart() {

        super.onStart();

        FirebaseUser user = auth.getCurrentUser();

        if(user!=null)
        {

            SendToHomeActivity();//if user is already loggedIn
        }





    }
//
//
//
//    private void SendtoLoginActivity() {
//
//        Intent i = new Intent(getApplicationContext(),MainActivity.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(i);
//        finish();
//    }
//


//    private void CheckUserExistance() {
//
//        Log.d(TAG, "CheckUserExistance: ");
//
//
//        final String Current_User_id=auth.getCurrentUser().getUid();
//
//        RefToDatabse.addValueEventListener(new ValueEventListener() {
//
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                Log.d(TAG, "onDataChange: ");
//
//                if(!dataSnapshot.hasChild(Current_User_id)) //this will check this user id in real database
//                {
//                    Log.d(TAG, "onDataChange:userdetail ");
//                    SendToUserDetailsActivity();
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//
//
//
//
//
//    }

//    private void SendToUserDetailsActivity() {
//
//        Intent i = new Intent(getApplicationContext(),UserDetails.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(i);
//        finish();
//
//
//
//    }

}

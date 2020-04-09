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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class Registration extends AppCompatActivity {



    EditText et1,et2;
    TextView tv1,tv2,tv3;
    Button btn1;
   private ProgressBar pb;

   private FirebaseAuth auth;
   private FirebaseAuth.AuthStateListener AuthListner;


    private String TAG="MyTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

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
            ShowProgressBar();
            String id =(et1.getEditableText().toString()+"@imsciences.com");
            String pass = et2.getEditableText().toString();
            auth.createUserWithEmailAndPassword(id,pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful())
                            {
                                Toast.makeText(getApplicationContext(),"Account Created",Toast.LENGTH_SHORT).show();
                                HideProgressBar();
                                SendToUserDetailsActivity();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if(e instanceof FirebaseAuthUserCollisionException)
                            {
                                et1.setError("Email already in use");
                            }
                            HideProgressBar();
                        }
                    });

        }


    }


    private void InitView() {


        et1=findViewById(R.id.ET_Id_R);
        et2=findViewById(R.id.ET_Pass_R);
        tv3=findViewById(R.id.TV_SignIn_Text_R);
        btn1=findViewById(R.id.Btn_Create_Account);
        pb=findViewById(R.id.progressBar_UD);



    }

   public void GoTOLogin(View view)
   {
       Intent i = new Intent(getApplicationContext(),MainActivity.class);
       startActivity(i);
   }



    public boolean ValidateId()
    {
        Log.d(TAG, "ValidateId: ");
        String id=(et1.getEditableText().toString());

        if(id.equals(null))
        {
            et1.setError("Email is Required, Can't be empty");
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

    private void SendToUserDetailsActivity() {

        Intent i = new Intent(getApplicationContext(),UserDetails.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();



    }











}

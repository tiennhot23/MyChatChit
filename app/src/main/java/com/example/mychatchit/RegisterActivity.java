package com.example.mychatchit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mychatchit.Common.Common;
import com.example.mychatchit.Common.Utils;
import com.example.mychatchit.Model.UserModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {
    private static final int MY_CAMERA_REQUEST_CODE = 2;
    private static final int MY_RESULT_LOAD_IMAGE = 3;

    @BindView(R.id.edt_first_name)
    public EditText edt_first_name;
    @BindView(R.id.edt_last_name)
    public EditText edt_last_name;
    @BindView(R.id.edt_phone)
    public EditText edt_phone;
    @BindView(R.id.edt_birthday)
    public EditText edt_birthday;
    @BindView(R.id.edt_status)
    public EditText edt_status;
    @BindView(R.id.edt_gender)
    public EditText edt_gender;
    @BindView(R.id.edt_home)
    public EditText edt_home;
    @BindView(R.id.edt_job)
    public EditText edt_job;
    @BindView(R.id.edt_bio)
    public EditText edt_bio;
    @BindView(R.id.img_avatar)
    public RoundedImageView img_avatar;
    @BindView(R.id.btn_register)
    public Button btn_register;

    @OnClick(R.id.img_avatar)
    void onAvatarClick(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, MY_RESULT_LOAD_IMAGE);
    }

    @OnClick(R.id.btn_register)
    void onRegisterClick(){
        if(!isEnterAllInfo()){
            return;
        }
        if(!Utils.checkNameRegex(edt_first_name.getText().toString())){
            edt_first_name.setError("Name contains only letters");
            return;
        }
        UserModel userModel = new UserModel();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MY_RESULT_LOAD_IMAGE){
            if(resultCode == RESULT_OK){
                try {
                    final Uri imageUri =data.getData();
                    InputStream inputStream =getContentResolver()
                            .openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    img_avatar.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "[ERROR]: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().build();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    Calendar calendar = Calendar.getInstance();
    boolean isSelectedBirhtday = false;

    FirebaseDatabase database;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
        run();
    }

    private void init(){
        ButterKnife.bind(this);
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference(Common.USER_REFERENCES);
        datePicker.addOnPositiveButtonClickListener(selection -> {
            calendar.setTimeInMillis(selection);
            edt_birthday.setText(simpleDateFormat.format(selection));
            isSelectedBirhtday = true;
        });
    }

    private void run(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        edt_phone.setText(user.getPhoneNumber());
        edt_phone.setEnabled(false);

        edt_birthday.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                datePicker.show(getSupportFragmentManager(), datePicker.toString());
            }
        });
    }

    private boolean isEnterAllInfo(){
        if(edt_first_name.getText().toString().trim().equals("")){
            edt_first_name.setError("Not Empty");
            return false;
        }
        if(edt_last_name.getText().toString().trim().equals("")){
            edt_last_name.setError("Not Empty");
            return false;
        }
        if(edt_birthday.getText().toString().trim().equals("")){
            edt_birthday.setError("Not Empty");
            return false;
        }
        if(edt_gender.getText().toString().trim().equals("")){
            edt_gender.setError("Not Empty");
            return false;
        }
        if(edt_status.getText().toString().trim().equals("")){
            edt_status.setError("Not Empty");
            return false;
        }
        if(edt_job.getText().toString().trim().equals("")){
            edt_job.setError("Not Empty");
            return false;
        }
        if(edt_home.getText().toString().trim().equals("")){
            edt_home.setError("Not Empty");
            return false;
        }
        if(edt_bio.getText().toString().trim().equals("")){
            edt_bio.setError("Not Empty");
            return false;
        }
        return true;
    }
}
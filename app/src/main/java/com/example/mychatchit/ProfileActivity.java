package com.example.mychatchit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mychatchit.Common.Common;
import com.example.mychatchit.Common.Utils;
import com.example.mychatchit.Model.UserModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    public Toolbar toolbar;
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
    @BindView(R.id.btn_save_info)
    public Button btn_save_info;
    @BindView(R.id.ic_gender)
    public ImageView ic_gender;

    private Uri fileUri;

    MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker().build();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    Calendar calendar = Calendar.getInstance();
    FirebaseDatabase database;
    DatabaseReference userRef;
    StorageReference storageReference;

    //region onClick
    @OnClick(R.id.btn_save_info)
    void onSaveInfoClick(){
        if(!Utils.checkEmptyComponents(new Object[]{edt_first_name, edt_last_name, edt_phone,
                edt_birthday, edt_gender, edt_status, edt_home, edt_job, edt_bio})){
            return;
        }
        if (!Utils.checkNameRegex(edt_first_name.getText().toString())) {
            edt_first_name.setError("Name contains only letters");
            return;
        }
        AlertDialog dialog = new AlertDialog.Builder(ProfileActivity.this)
                .setCancelable(false)
                .setMessage("Wait a second...")
                .create();
        dialog.show();
        Common.currentUser.setFirstName(Utils.formatName(edt_first_name.getText().toString()));
        Common.currentUser.setLastName(Utils.formatName(edt_last_name.getText().toString()));
        Common.currentUser.setPhone(edt_phone.getText().toString());
        Common.currentUser.setBirthday(calendar.getTimeInMillis());
        Common.currentUser.setGender(edt_gender.getText().toString());
        Common.currentUser.setStatus(edt_status.getText().toString());
        Common.currentUser.setHome(edt_home.getText().toString());
        Common.currentUser.setJob(edt_job.getText().toString());
        Common.currentUser.setBio(edt_bio.getText().toString());
        if (fileUri != null) {
            String firebaseStoragePath = new StringBuilder(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .append("/")
                    .append("avatar")
                    .toString();
            storageReference = FirebaseStorage.getInstance().getReference().child(firebaseStoragePath);
            UploadTask uploadTask = storageReference.putFile(fileUri);
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    Toast.makeText(this, "Failed to upload", Toast.LENGTH_SHORT).show();
                }
                return storageReference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String url = task.getResult().toString();
                    dialog.dismiss();
                    Common.currentUser.setAvatar(url);

                    userRef.child(Common.currentUser.getUid())
                            .setValue(Common.currentUser)
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "[ERROR while update user account]: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }).addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Update success", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "[ERROR]: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            });
        } else {
            dialog.dismiss();

            userRef.child(Common.currentUser.getUid())
                    .setValue(Common.currentUser)
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "[ERROR while add user account]: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }).addOnSuccessListener(unused -> {
                Toast.makeText(this, "Register success", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }

    @OnClick(R.id.img_avatar)
    void onAvatarClick(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, Common.MY_RESULT_LOAD_IMAGE);
    }
    //endregion

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.MY_RESULT_LOAD_IMAGE) {
            if (resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = data.getData();
                    InputStream inputStream = getContentResolver()
                            .openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    img_avatar.setImageBitmap(bitmap);
                    fileUri = imageUri;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "[ERROR]: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();
    }
    private void init(){
        ButterKnife.bind(this);

        //region firebase
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference(Common.USER_REFERENCES);
        storageReference = FirebaseStorage.getInstance().getReference();
        //endregion

        //region set default components
        calendar.setTimeInMillis(Common.currentUser.getBirthday());
        edt_first_name.setText(Common.currentUser.getFirstName());
        edt_last_name.setText(Common.currentUser.getLastName());
        edt_phone.setText(Common.currentUser.getPhone());
        edt_phone.setEnabled(false);
        edt_birthday.setText(simpleDateFormat.format(calendar.getTime()));
        edt_gender.setText(Common.currentUser.getGender());
        edt_status.setText(Common.currentUser.getStatus());
        edt_home.setText(Common.currentUser.getHome());
        edt_job.setText(Common.currentUser.getJob());
        edt_bio.setText(Common.currentUser.getBio());
        if(edt_gender.getText().toString().toLowerCase().equals("nam"))
            ic_gender.setImageResource(R.drawable.ic_male);
        else ic_gender.setImageResource(R.drawable.ic_female);
        Picasso.get().load(Common.currentUser.getAvatar()).into(img_avatar);
        datePicker.addOnPositiveButtonClickListener(selection -> {
            calendar.setTimeInMillis(selection);
            edt_birthday.setText(simpleDateFormat.format(selection));
        });
        edt_birthday.setOnFocusChangeListener((v,hasFocus) -> {
            if (hasFocus) {
                datePicker.show(getSupportFragmentManager(), datePicker.toString());
            }
        });
        //endregion

        //region toolbar
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
        //endregion
    }
}
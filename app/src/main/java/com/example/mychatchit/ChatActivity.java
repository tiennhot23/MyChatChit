package com.example.mychatchit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mychatchit.Common.Common;
import com.example.mychatchit.Common.Utils;
import com.example.mychatchit.Listener.IFirebaseLoadFailed;
import com.example.mychatchit.Listener.ILoadTimeFromFirebaseListener;
import com.example.mychatchit.Model.ChatInfoModel;
import com.example.mychatchit.Model.ChatMessageModel;
import com.example.mychatchit.R;
import com.example.mychatchit.ViewHolder.ChatPictureHolder;
import com.example.mychatchit.ViewHolder.ChatPictureReceiveHolder;
import com.example.mychatchit.ViewHolder.ChatTextHolder;
import com.example.mychatchit.ViewHolder.ChatTextReceiveHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ChatActivity extends AppCompatActivity implements IFirebaseLoadFailed, ILoadTimeFromFirebaseListener {

    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.txt_name)
    public TextView txt_name;
    @BindView(R.id.img_camera)
    public ImageView img_camera;
    @BindView(R.id.img_image)
    public ImageView img_image;
    @BindView(R.id.recycler)
    public RecyclerView recycler;
    @BindView(R.id.img_send)
    public ImageView img_send;
    @BindView(R.id.img_preview)
    public ImageView img_preview;
    @BindView(R.id.img_mic)
    public ImageView img_mic;
    @BindView(R.id.edt_chat)
    public AppCompatEditText edt_chat;

    FirebaseDatabase database;
    DatabaseReference chatRef, offsetRef;
    ILoadTimeFromFirebaseListener listener;
    IFirebaseLoadFailed errorListener;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    FirebaseRecyclerOptions<ChatMessageModel> options;
    FirebaseRecyclerAdapter<ChatMessageModel, RecyclerView.ViewHolder> adapter;

    Uri fileUri;
    StorageReference storageReference;
    LinearLayoutManager layoutManager;

    @OnClick(R.id.img_image)
    void onSelectImageClick(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, Common.MY_RESULT_LOAD_IMAGE);
    }

    @OnClick(R.id.img_camera)
    void onCaptureImageClick(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        fileUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
        );
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, Common.MY_CAMERA_REQUEST_CODE);
    }

    @OnClick(R.id.img_send)
    void onSubmitChatClick(){
        offsetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long offset = snapshot.getValue(Long.class);
                long estimatedServerTimeInMs = System.currentTimeMillis() + offset;
                listener.onLoadOnlyTimeSuccess(estimatedServerTimeInMs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorListener.onError(error.getMessage());
            }
        });
    }

    @OnClick(R.id.img_mic)
    void onRecordAudioClick(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak text");
        try{
            startActivityForResult(intent, Common.MY_MICRO_REQUEST_CODE);
        }catch (Exception e){
            Toast.makeText(this, "[ERROR]: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Common.MY_CAMERA_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                try{
                    Bitmap thumbnail = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), fileUri);
                    img_preview.setImageBitmap(thumbnail);
                    img_preview.setVisibility(View.VISIBLE);
                }catch(Exception e){
                    Toast.makeText(this, "[ERROR]: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }else if(requestCode == Common.MY_RESULT_LOAD_IMAGE){
            if(resultCode == RESULT_OK){
                try{
                    final Uri imageUri = data.getData();
                    InputStream inputStream = getContentResolver()
                            .openInputStream(imageUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(inputStream);
                    img_preview.setImageBitmap(selectedImage);
                    img_preview.setVisibility(View.VISIBLE);
                    fileUri = imageUri;
                }catch(Exception e){
                    Toast.makeText(this, "[ERROR]: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }else if(requestCode == Common.MY_MICRO_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                try{
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    edt_chat.setText(result.get(0));
                }catch(Exception e){
                    Toast.makeText(this, "[ERROR]: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(adapter != null) adapter.startListening();
    }

    @Override
    protected void onStop() {
        if(adapter != null) adapter.stopListening();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter != null) adapter.startListening();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        Common.roomSelected = "";
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initViews();
        loadChatContent();
    }

    private void initViews() {
        ButterKnife.bind(this);
        listener = this;
        errorListener = this;
        database = FirebaseDatabase.getInstance();
        chatRef = database.getReference(Common.ROOM_CHAT_REFERENCE);
        offsetRef = database.getReference(".info/serverTimeOffset");
        Query query = chatRef.child(Common.generateChatRoomId(Common.chatUser.getUid(),
                FirebaseAuth.getInstance().getCurrentUser().getUid()))
                .child(Common.CHAT_DETAIL_REFERENCE);
        options = new FirebaseRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class)
                .build();
        layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);

        txt_name.setText(Common.getName(Common.chatUser));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });
    }


    private void loadChatContent(){
        String receiverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        adapter = new FirebaseRecyclerAdapter<ChatMessageModel, RecyclerView.ViewHolder>(options) {
            @Override
            public int getItemViewType(int position) {
                if(adapter.getItem(position).getSenderId().equals(receiverId)){
                    return !adapter.getItem(position).isPicture()?0:1;
                }else{
                    return !adapter.getItem(position).isPicture()?2:3;
                }
            }

            @Override
            protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull ChatMessageModel model) {
                String[] content = null;
                if(!model.getContent().equals("----"))
                    content = model.getContent().split("----");
                if(holder instanceof ChatTextHolder){
                    ChatTextHolder chatTextHolder = (ChatTextHolder) holder;
                    if(content != null) {
                        chatTextHolder.txt_chat_message.setText(content[1]);
                        chatTextHolder.txt_translate.setText(content[0]);
                    }else{
                        chatTextHolder.txt_chat_message.setVisibility(View.GONE);
                        chatTextHolder.txt_translate.setVisibility(View.GONE);
                    }
                    chatTextHolder.txt_time.setText(DateUtils.getRelativeTimeSpanString(model.getTimeStamp(),
                            Calendar.getInstance().getTimeInMillis(), 0)
                            .toString());
                }else if (holder instanceof ChatTextReceiveHolder){
                    ChatTextReceiveHolder chatTextReceiveHolder = (ChatTextReceiveHolder) holder;
                    if(content != null) {
                        chatTextReceiveHolder.txt_chat_message.setText(content[1]);
                        chatTextReceiveHolder.txt_translate.setText(content[0]);
                    }else{
                        chatTextReceiveHolder.txt_chat_message.setVisibility(View.GONE);
                        chatTextReceiveHolder.txt_translate.setVisibility(View.GONE);
                    }
                    chatTextReceiveHolder.txt_time.setText(DateUtils.getRelativeTimeSpanString(model.getTimeStamp(),
                            Calendar.getInstance().getTimeInMillis(), 0)
                            .toString());
                }else if(holder instanceof ChatPictureHolder){
                    ChatPictureHolder chatPictureHolder = (ChatPictureHolder) holder;
                    if(content != null) {
                        chatPictureHolder.txt_chat_message.setText(content[1]);
                        chatPictureHolder.txt_translate.setText(content[0]);
                    }else{
                        chatPictureHolder.txt_chat_message.setVisibility(View.GONE);
                        chatPictureHolder.txt_translate.setVisibility(View.GONE);
                    }
                    chatPictureHolder.txt_time.setText(DateUtils.getRelativeTimeSpanString(model.getTimeStamp()));

                    Picasso.get().load(model.getPictureLink()).into(chatPictureHolder.img_preview);
                }else if(holder instanceof ChatPictureReceiveHolder){
                    ChatPictureReceiveHolder chatPictureReceiveHolder = (ChatPictureReceiveHolder) holder;
                    if(content != null) {
                        chatPictureReceiveHolder.txt_chat_message.setText(content[1]);
                        chatPictureReceiveHolder.txt_translate.setText(content[0]);
                    }else{
                        chatPictureReceiveHolder.txt_chat_message.setVisibility(View.GONE);
                        chatPictureReceiveHolder.txt_translate.setVisibility(View.GONE);
                    }
                    chatPictureReceiveHolder.txt_time.setText(DateUtils.getRelativeTimeSpanString(model.getTimeStamp()));

                    Picasso.get().load(model.getPictureLink()).into(chatPictureReceiveHolder.img_preview);
                }
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view;
                if(viewType == 0){
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.layout_message_text_own, parent, false);
                    return new ChatTextReceiveHolder(view);
                }else if (viewType == 1){
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.layout_message_picture_own, parent, false);
                    return new ChatPictureReceiveHolder(view);
                }else if (viewType == 2){
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.layout_message_text_friend, parent, false);
                    return new ChatTextHolder(view);
                }else{
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.layout_message_picture_friend, parent, false);
                    return new ChatPictureHolder(view);
                }
            }
        };

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendMessageCount = adapter.getItemCount();
                int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
                if(lastVisiblePosition == -1 ||
                        (positionStart >= (friendMessageCount-1) && (lastVisiblePosition == (positionStart -1)))){
                    recycler.scrollToPosition(positionStart);
                }
            }
        });

        recycler.setAdapter(adapter);
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, "[ERROR] while load time: " + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadOnlyTimeSuccess(long estimateTimeInMs) {
        ChatMessageModel chatMessageModel = new ChatMessageModel();
        chatMessageModel.setName(Common.getName(Common.currentUser));
        chatMessageModel.setContent(edt_chat.getText().toString());
        chatMessageModel.setTimeStamp(estimateTimeInMs);
        chatMessageModel.setSenderId(FirebaseAuth.getInstance().getCurrentUser().getUid());

        if(fileUri == null){
            chatMessageModel.setPicture(false);
            submitChatToFirebase(chatMessageModel, chatMessageModel.isPicture(), estimateTimeInMs);
        }else{
            uploadPicture(fileUri, chatMessageModel, estimateTimeInMs);
        }
    }

    private void uploadPicture(Uri fileUri, ChatMessageModel chatMessageModel, long estimateTimeInMs) {
        AlertDialog dialog = new AlertDialog.Builder(ChatActivity.this)
                .setCancelable(false)
                .setMessage("Please wait for upload finished...")
                .create();
        dialog.show();

        String fileName = Utils.getFileName(getContentResolver(), fileUri);
        String path = new StringBuilder(Common.chatUser.getUid())
                .append("/")
                .append(fileName)
                .toString();
        storageReference = FirebaseStorage.getInstance()
                .getReference()
                .child(path);
        UploadTask uploadTask = storageReference.putFile(fileUri);
        Task<Uri> task = uploadTask.continueWithTask(task1 -> {
            if(!task1.isSuccessful()){
                Toast.makeText(this, "Failed to upload", Toast.LENGTH_SHORT).show();
            }
            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task12 ->{
            if(task12.isSuccessful()){
                String url = task12.getResult().toString();
                dialog.dismiss();

                chatMessageModel.setPicture(true);
                chatMessageModel.setPictureLink(url);

                submitChatToFirebase(chatMessageModel, chatMessageModel.isPicture(), estimateTimeInMs);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "[ERROR]: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void submitChatToFirebase(ChatMessageModel chatMessageModel, boolean isPicture, long estimateTimeInMs) {
        chatRef.child(Common.generateChatRoomId(Common.chatUser.getUid(),
                FirebaseAuth.getInstance().getCurrentUser().getUid()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        translateText(chatMessageModel.getContent(), chatMessageModel, isPicture, estimateTimeInMs, snapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void translateText(String content, ChatMessageModel chatMessageModel, boolean isPicture, long estimateTimeInMs, DataSnapshot snapshot) {
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.VIETNAMESE)
                .setTargetLanguage(TranslateLanguage.ENGLISH)
                .build();

        Translator translator = Translation.getClient(options);
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        AlertDialog dialog = new AlertDialog.Builder(ChatActivity.this)
                .setCancelable(false)
                .setMessage("Downloading Translate conditions...")
                .create();
        dialog.show();
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(unused -> {
            dialog.dismiss();
            translator.translate(content).addOnSuccessListener(s -> {
                Common.translateText = s;
                chatMessageModel.setContent(new StringBuilder(Common.translateText)
                        .append("----").append(chatMessageModel.getContent()).toString());
                if(snapshot.exists()){
                    appendChat(chatMessageModel, isPicture, estimateTimeInMs);
                }else{
                    createChat(chatMessageModel, isPicture, estimateTimeInMs);
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "[ERROR]: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Toast.makeText(this, "[ERROR]: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void appendChat(ChatMessageModel chatMessageModel, boolean isPicture, long estimateTimeInMs) {
        Map<String, Object> update_data = new HashMap<>();
        update_data.put("lastUpdate", estimateTimeInMs);

        if(isPicture){
            update_data.put("lastMessage", "<Image>");
        }else{
            update_data.put("lastMessage", chatMessageModel.getContent());
        }

        FirebaseDatabase.getInstance()
                .getReference(Common.CHAT_LIST_REFERENCES)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(Common.chatUser.getUid())
                .updateChildren(update_data)
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "[ERROR]: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .addOnSuccessListener(unused -> {
                    FirebaseDatabase.getInstance()
                            .getReference(Common.CHAT_LIST_REFERENCES)
                            .child(Common.chatUser.getUid())
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .updateChildren(update_data)
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "[ERROR]: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            })
                            .addOnSuccessListener(unused1 -> {

                                String roomId = Common.generateChatRoomId(Common.chatUser.getUid(),
                                        FirebaseAuth.getInstance().getCurrentUser().getUid());
                                chatRef.child(roomId)
                                        .child(Common.CHAT_DETAIL_REFERENCE)
                                        .push()
                                        .setValue(chatMessageModel)
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnCompleteListener(task -> {
                                            if(task.isSuccessful()){
                                                edt_chat.setText("");
                                                edt_chat.requestFocus();
                                                if(adapter != null){
                                                    adapter.notifyDataSetChanged();
                                                }
                                                if(isPicture){
                                                    fileUri = null;
                                                    img_preview.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                            });
                });
    }

    private void createChat(ChatMessageModel chatMessageModel, boolean isPicture, long estimateTimeInMs) {
        ChatInfoModel chatInfoModel = new ChatInfoModel();
        chatInfoModel.setCreateId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        chatInfoModel.setFriendName(Common.getName(Common.chatUser));
        chatInfoModel.setFriendId(Common.chatUser.getUid());
        chatInfoModel.setCreateName(Common.getName(Common.currentUser));

        if(isPicture){
            chatInfoModel.setLastMessage("<Image>");
        }else{
            chatInfoModel.setLastMessage(chatMessageModel.getContent());
        }

        chatInfoModel.setLastUpdate(estimateTimeInMs);
        chatInfoModel.setCreateDate(estimateTimeInMs);

        FirebaseDatabase.getInstance()
                .getReference(Common.CHAT_LIST_REFERENCES)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(Common.chatUser.getUid())
                .setValue(chatInfoModel)
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "[ERROR]: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                })
                .addOnSuccessListener(unused -> {
                    FirebaseDatabase.getInstance()
                            .getReference(Common.CHAT_LIST_REFERENCES)
                            .child(Common.chatUser.getUid())
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(chatInfoModel)
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "[ERROR]: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            })
                            .addOnSuccessListener(unused1 -> {

                                String roomId = Common.generateChatRoomId(Common.chatUser.getUid(),
                                        FirebaseAuth.getInstance().getCurrentUser().getUid());
                                chatRef.child(roomId)
                                        .child(Common.CHAT_DETAIL_REFERENCE)
                                        .push()
                                        .setValue(chatMessageModel)
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnCompleteListener(task -> {
                                            if(task.isSuccessful()){
                                                edt_chat.setText("");
                                                edt_chat.requestFocus();
                                                if(adapter != null){
                                                    adapter.notifyDataSetChanged();
                                                }
                                                if(isPicture){
                                                    fileUri = null;
                                                    img_preview.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                            });
                });
    }
}
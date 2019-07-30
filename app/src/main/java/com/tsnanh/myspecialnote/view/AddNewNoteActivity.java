package com.tsnanh.myspecialnote.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tsnanh.myspecialnote.controller.Config;
import com.tsnanh.myspecialnote.R;
import com.tsnanh.myspecialnote.controller.Utilities;
import com.tsnanh.myspecialnote.model.NoteModel;

import java.io.BufferedInputStream;
import java.io.InputStream;

public class AddNewNoteActivity extends AppCompatActivity implements View.OnClickListener, MenuItem.OnMenuItemClickListener {

    private Toolbar toolbar;
    private FloatingActionButton fabSave;
    private EditText edtNoteTitle, edtNoteContent;
    private ImageView imgAttach, btnRemoveImg;
    private Bitmap bmpImage;
    private ProgressBar prgImage, prgThis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_note);

        /* Find View */
        toolbar = this.findViewById(R.id.tlb_add_new_note);
        edtNoteTitle = this.findViewById(R.id.edt_note_title);
        edtNoteContent = this.findViewById(R.id.edt_note_content);
        fabSave = this.findViewById(R.id.fab_save_note);
        imgAttach = this.findViewById(R.id.img_note);
        btnRemoveImg = this.findViewById(R.id.btn_remove_image);
        prgImage = this.findViewById(R.id.prg_image);
        prgThis = this.findViewById(R.id.prg_activity_add_note);
        prgThis.setVisibility(View.GONE);

        fabSave.setOnClickListener(this);
        btnRemoveImg.setOnClickListener(this);

        toolbar.setTitle("Tạo ghi chú mới");
        toolbar.setTitleTextColor(Color.BLACK);
        edtNoteTitle.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        edtNoteContent.setImeOptions(EditorInfo.IME_ACTION_DONE);

        this.setSupportActionBar(toolbar);
        assert this.getSupportActionBar() != null;
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent();
        intent.putExtra("result", 0);
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.action_bar_new_note, menu);

        MenuItem btnDelete = menu.findItem(R.id.btn_delete_note);
        btnDelete.setOnMenuItemClickListener(this);

        MenuItem btnSettings = menu.findItem(R.id.btn_settings);
        btnSettings.setOnMenuItemClickListener(this);

        MenuItem btnAttachImage = menu.findItem(R.id.btn_attach_img);
        btnAttachImage.setOnMenuItemClickListener(this);

        return true;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.fab_save_note) {

            String noteTitle = edtNoteTitle.getText().toString().trim();
            String noteContent = edtNoteContent.getText().toString().trim();

            if (noteTitle.isEmpty() && noteContent.isEmpty()) {
                edtNoteTitle.setError("Please input note title!");
                edtNoteContent.setError("Please input note content!");
            }
            else if (noteTitle.isEmpty()) {
                edtNoteTitle.setError("Please input note title!");
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            } else if (noteContent.isEmpty()) {
                edtNoteContent.setError("Please input note content!");
            } else {
                view.setVisibility(View.VISIBLE);
                prgThis.setVisibility(View.VISIBLE);
                NoteModel model = new NoteModel();
                model.setNoteTitle(noteTitle);
                model.setNoteContent(noteContent);
                model.setDatetime(Utilities.getDateTime());
                model.setColor(Utilities.randomColor());
                Utilities.hideSoftKeyBoard(this);

                Message message = new Message();
                message.obj = model;
                handler.sendMessage(message);
            }
        }
        if (id == R.id.btn_remove_image) {
            Utilities.hideSoftKeyBoard(this);
            imgAttach.setImageBitmap(null);
            imgAttach.setVisibility(View.GONE);
            btnRemoveImg.setVisibility(View.GONE);
            Toast.makeText(this, "Remove", Toast.LENGTH_SHORT).show();
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            NoteModel model = (NoteModel) message.obj;
            String pathImg;
            if (imgAttach.getVisibility() == View.VISIBLE) {
                String nameImg = Utilities.dateTimeFormat(model.getDatetime());
                pathImg = Utilities.saveImage(AddNewNoteActivity.this, bmpImage, nameImg, Config.MODE_NORMAL);
                Utilities.saveImage(AddNewNoteActivity.this, Utilities.centerCropForIcon(bmpImage), nameImg, Config.MODE_ICON);
//                Toast.makeText(AddNewNoteActivity.this, pathImg, Toast.LENGTH_SHORT).show();
            } else {
                pathImg = "";
            }
            model.setImage(pathImg);
            MainActivity
                    .daoSession.getDatabase()
                    .execSQL("insert into Notes (NoteTitle, NoteContent, DateTime, Image, Color) values ('"
                            + model.getNoteTitle() +"','"
                            + model.getNoteContent() +"','"
                            + model.getDatetime() +"','"
                            + model.getImage() + "','"
                            + model.getColor() + "')");
            setResult(Activity.RESULT_OK);
            finishActivity(Config.ADD_NOTE_REQUEST_CODE);
            finish();
            return true;
        }
    });
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.btn_attach_img:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Chọn hình ảnh đính kèm đi hihi :v"), Config.PICK_IMAGE_REQUEST_CODE);
                break;
            case R.id.btn_delete_note:
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", 1);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
                break;
            case R.id.btn_settings:
                /* Setting coming soon */
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Config.PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                prgImage.setVisibility(View.VISIBLE);
                imgAttach.setVisibility(View.VISIBLE);
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(data.getData());
                            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                            bmpImage = BitmapFactory.decodeStream(bufferedInputStream);

                            handlerUI.sendEmptyMessage(0);
                        } catch (Exception e) {
                            assert e.getMessage() != null;
                            Log.e("Error", e.getMessage());
                        }
                    }
                }.start();

            }
        }
    }

    private Handler handlerUI = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            imgAttach.setImageBitmap(bmpImage);
            imgAttach.setVisibility(View.VISIBLE);
            btnRemoveImg.setVisibility(View.VISIBLE);
            prgImage.setVisibility(View.GONE);
            return true;
        }
    });
}

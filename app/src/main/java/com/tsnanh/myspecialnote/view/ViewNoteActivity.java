package com.tsnanh.myspecialnote.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tsnanh.myspecialnote.controller.Config;
import com.tsnanh.myspecialnote.controller.NoteInterface;
import com.tsnanh.myspecialnote.R;
import com.tsnanh.myspecialnote.controller.Utilities;
import com.tsnanh.myspecialnote.model.NoteModel;

public class ViewNoteActivity extends AppCompatActivity implements MenuItem.OnMenuItemClickListener, View.OnClickListener, NoteInterface {

    private NoteModel noteModel;

    private Toolbar toolbar;
    private ImageView imgNote;
    private ProgressBar prgImage, prgThis;
    private TextView lblDateTime, lblTitle, lblContent;
    private FloatingActionButton fabEdit;
    private boolean backRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        /* Data from MainActivity */
        Bundle bundle = getIntent().getExtras();
        noteModel = (NoteModel) bundle.get(Config.MODEL_KEY);

        toolbar = this.findViewById(R.id.tlb_view_note);
        imgNote = this.findViewById(R.id.img_view_note);
        prgImage = this.findViewById(R.id.prg_view_image);
        prgThis = this.findViewById(R.id.prg_view_note);
        lblDateTime = this.findViewById(R.id.lbl_view_datetime);
        lblTitle = this.findViewById(R.id.lbl_note_title);
        lblContent = this.findViewById(R.id.lbl_note_content);
        fabEdit = this.findViewById(R.id.fab_edit_note);

        fabEdit.setOnClickListener(this);
        prgThis.setVisibility(View.GONE);

        /* Toolbar */
        toolbar.setTitle("Xem ghi chú");
        this.setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        boolean refresh = backRefresh;
        backRefresh = false;
        if (refresh) {
            setResult(Activity.RESULT_OK, new Intent().putExtra("refresh", 1));
        } else {
            setResult(Activity.RESULT_CANCELED);
        }
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        boolean refresh = backRefresh;
        backRefresh = false;
        if (refresh) {
            setResult(Activity.RESULT_OK, new Intent().putExtra("refresh", 1));
        } else {
            setResult(Activity.RESULT_CANCELED);
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.action_bar_view_note, menu);

        MenuItem itemAttach = menu.findItem(R.id.btn_view_attach_image);
        itemAttach.setVisible(false);
        itemAttach.setOnMenuItemClickListener(this);

        MenuItem itemDelete = menu.findItem(R.id.btn_view_delete_note);
        itemDelete.setOnMenuItemClickListener(this);

        MenuItem itemSettings = menu.findItem(R.id.btn_view_settings);
        itemSettings.setOnMenuItemClickListener(this);

        return true;
    }

    @Override
    public void showData() {
        if (noteModel != null) {
            if (!noteModel.getImage().isEmpty()) {
                imgNote.setVisibility(View.VISIBLE);
                prgImage.setVisibility(View.VISIBLE);
                lblDateTime.setText(noteModel.getDatetime());
                lblTitle.setText(noteModel.getNoteTitle());
                lblContent.setText(noteModel.getNoteContent());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap image = Utilities.getImageFromData(ViewNoteActivity.this, noteModel.getImage(), Config.MODE_NORMAL);
                        imgNote.setImageBitmap(image);
                        handlerImage.sendEmptyMessage(0);
                    }
                });
            } else {
                prgImage.setVisibility(View.GONE);
                imgNote.setVisibility(View.GONE);
                lblDateTime.setText(noteModel.getDatetime());
                lblTitle.setText(noteModel.getNoteTitle());
                lblContent.setText(noteModel.getNoteContent());
                prgThis.setVisibility(View.GONE);
            }
        }
    }
    Handler handlerImage = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            prgImage.setVisibility(View.GONE);
            return true;
        }
    });

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.btn_view_delete_note:
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setPositiveButton("Dm có :v", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MainActivity.daoSession.getNoteModelDao().delete(noteModel);
                                setResult(Activity.RESULT_OK, new Intent().putExtra("delete", 0));
                                finish();
                            }
                        }).setNegativeButton("Đoé", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).setTitle("Xoá?")
                        .setMessage("Bạn có muốn xoá cái ghi chú củ lìn này không?");
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case R.id.btn_view_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_edit_note) {
            Intent intent = new Intent(this, EditNoteActivity.class);
            intent.putExtra(Config.MODEL_KEY, noteModel);
            startActivityForResult(intent, Config.EDIT_NOTE_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Config.EDIT_NOTE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle.getInt("delete") == 0) {
                    setResult(Activity.RESULT_OK, new Intent().putExtra("refresh", 1));
                    finish();
                } else {
                    noteModel = (NoteModel) bundle.get("note");
                    showData();
                    backRefresh = true;
                }
            }
        }
    }
}

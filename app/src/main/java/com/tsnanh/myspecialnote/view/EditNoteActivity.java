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
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tsnanh.myspecialnote.controller.Config;
import com.tsnanh.myspecialnote.controller.NoteInterface;
import com.tsnanh.myspecialnote.R;
import com.tsnanh.myspecialnote.controller.Utilities;
import com.tsnanh.myspecialnote.model.NoteModel;

import java.io.BufferedInputStream;
import java.io.InputStream;

public class EditNoteActivity extends AppCompatActivity implements MenuItem.OnMenuItemClickListener, View.OnClickListener, NoteInterface {

    private NoteModel model;

    private Toolbar toolbar;
    private ImageView imgNote, btnRemove;
    private ProgressBar prgImage, prgThis;
    private EditText edtTitle, edtContent;
    private Bitmap bitmap;
    private FloatingActionButton fabSave;
    private int saveImage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        model = (NoteModel) getIntent().getExtras().get(Config.MODEL_KEY);

        toolbar = this.findViewById(R.id.tlb_edit_note);
        imgNote = this.findViewById(R.id.img_edit_note);
        btnRemove = this.findViewById(R.id.btn_edit_remove_image);
        edtTitle = this.findViewById(R.id.edt_edit_note_title);
        edtContent = this.findViewById(R.id.edt_edit_note_content);
        prgImage = this.findViewById(R.id.prg_edit_image);
        prgThis = this.findViewById(R.id.prg_edit_saving);
        fabSave = this.findViewById(R.id.fab_edit_save_note);

        prgImage.setVisibility(View.GONE);
        prgThis.setVisibility(View.GONE);
        fabSave.setOnClickListener(this);
        btnRemove.setOnClickListener(this);
        btnRemove.setVisibility(View.GONE);

        toolbar.setTitle("Sửa ghi chú");
        this.setSupportActionBar(toolbar);
        assert this.getSupportActionBar() != null;
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lưu lại?");
        builder.setMessage("Bạn có muốn lưu thay đổi?");

        builder.setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveNote();
            }
        });
        builder.setNeutralButton("Huỷ bỏ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent();
                intent.putExtra("delete", 0);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.action_bar_view_note, menu);

        MenuItem itemAttach = menu.findItem(R.id.btn_view_attach_image);
        itemAttach.setOnMenuItemClickListener(this);

        MenuItem itemDelete = menu.findItem(R.id.btn_view_delete_note);
        itemDelete.setOnMenuItemClickListener(this);

        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.btn_view_attach_image:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Chọn hình ảnh đính kèm đi hihi :v"), Config.PICK_IMAGE_REQUEST_CODE);
                break;
            case R.id.btn_view_delete_note:
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setPositiveButton("Dm có :v", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MainActivity.daoSession.getNoteModelDao().delete(model);
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
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Config.PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                prgImage.setVisibility(View.VISIBLE);
                imgNote.setVisibility(View.VISIBLE);
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(data.getData());
                            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                            bitmap = BitmapFactory.decodeStream(bufferedInputStream);

                            saveImage = 1;
                            handlerUI.sendEmptyMessage(0);
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage());
                        }
                    }
                }.start();
            }
        }
    }
    private Handler handlerUI = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            imgNote.setImageBitmap(bitmap);
            imgNote.setVisibility(View.VISIBLE);
            btnRemove.setVisibility(View.VISIBLE);
            prgImage.setVisibility(View.GONE);
        }
    };

    private void saveNote() {
        String noteTitle = edtTitle.getText().toString().trim();
        String noteContent = edtContent.getText().toString().trim();

        if (noteTitle.isEmpty() && noteContent.isEmpty()) {
            edtTitle.setError("Please input note title!");
            edtContent.setError("Please input note content!");
        }
        else if (noteTitle.isEmpty()) {
            edtTitle.setError("Please input note title!");
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        } else if (noteContent.isEmpty()) {
            edtContent.setError("Please input note content!");
        } else {
            prgThis.setVisibility(View.VISIBLE);
            model.setId(model.getId());
            model.setNoteTitle(noteTitle);
            model.setNoteContent(noteContent);
            model.setDatetime(Utilities.getDateTime());
            model.setColor(model.getColor());

            handler.sendEmptyMessage(0);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.fab_edit_save_note) {
            saveNote();
        }
        if (id == R.id.btn_edit_remove_image) {
            Toast.makeText(this, "Remove ", Toast.LENGTH_LONG).show();
            imgNote.setVisibility(View.GONE);
            imgNote.setImageBitmap(null);
        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            String pathImg;
            if (imgNote.getDrawable() != null && imgNote.getVisibility() == View.VISIBLE) {
                if (saveImage == 0) {
                    pathImg = model.getImage();
                } else {
                    if (!model.getImage().isEmpty()) {
                        Utilities.deleteOldImage(EditNoteActivity.this, model.getImage());
                    }
                    String date = Utilities.dateTimeFormat(Utilities.getDateTime());
                    pathImg = Utilities.saveImage(EditNoteActivity.this, bitmap, date, Config.MODE_NORMAL);
                    Utilities.saveImage(EditNoteActivity.this, bitmap, date, Config.MODE_ICON);
                }
            } else {
                if (!model.getImage().isEmpty()) {
                    Utilities.deleteOldImage(EditNoteActivity.this, model.getImage());
                }
                pathImg = "";
            }
            model.setImage(pathImg);
            MainActivity.daoSession.getNoteModelDao().update(model);
            Intent intent = new Intent();
            intent.putExtra("note", model);
            intent.putExtra("delete", 1);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lưu lại?");
        builder.setMessage("Bạn có muốn lưu thay đổi?");

        builder.setPositiveButton("Lưu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveNote();
            }
        });
        builder.setNeutralButton("Huỷ bỏ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void showData() {
        if (model != null) {
            if (!model.getImage().isEmpty()) {
                prgImage.setVisibility(View.VISIBLE);
                imgNote.setVisibility(View.VISIBLE);
                edtTitle.setText(model.getNoteTitle());
                edtContent.setText(model.getNoteContent());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap image = Utilities.getImageFromData(EditNoteActivity.this, model.getImage(), Config.MODE_NORMAL);
                        imgNote.setImageBitmap(image);
                        handlerImage.sendEmptyMessage(0);
                    }
                });
            } else {
                imgNote.setVisibility(View.GONE);
                edtTitle.setText(model.getNoteTitle());
                edtContent.setText(model.getNoteContent());
            }
        }
    }

    Handler handlerImage = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            prgImage.setVisibility(View.GONE);
            btnRemove.setVisibility(View.VISIBLE);
        }
    };
}

package com.tsnanh.myspecialnote.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tsnanh.myspecialnote.controller.Config;
import com.tsnanh.myspecialnote.DaoMaster;
import com.tsnanh.myspecialnote.DaoSession;
import com.tsnanh.myspecialnote.database.DatabaseOpenHelper;
import com.tsnanh.myspecialnote.controller.NoteAdapter;
import com.tsnanh.myspecialnote.R;
import com.tsnanh.myspecialnote.model.NoteModel;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, MenuItem.OnMenuItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    public static DaoSession daoSession;
    private ArrayList<NoteModel> arrayList = new ArrayList<>();

    private Toolbar toolbar;
    private ListView lstNotes;
    private FloatingActionButton fabAdd;
    private TextView lblNoNotes;
    private NoteAdapter noteAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectDatabase();

        /* Find View */
        toolbar = this.findViewById(R.id.tlb_note_view);
        swipeRefreshLayout = this.findViewById(R.id.pullToRefresh);
        lstNotes = this.findViewById(R.id.lst_notes);
        fabAdd = this.findViewById(R.id.fab_add);
        lblNoNotes = this.findViewById(R.id.lbl_note_empty);

        fabAdd.setOnClickListener(this);
        lstNotes.setOnItemClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);

        toolbar.setTitle("MS Note");
        toolbar.setTitleTextColor(Color.BLACK);

        this.setSupportActionBar(toolbar);
        noteAdapter = new NoteAdapter(this, arrayList);

        updateListNote();
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (arrayList.size() == 0 && arrayList.isEmpty()) {
                lblNoNotes.setVisibility(View.VISIBLE);
                lstNotes.setVisibility(View.INVISIBLE);
            } else {
                lblNoNotes.setVisibility(View.INVISIBLE);
                lstNotes.setVisibility(View.VISIBLE);
                if (lstNotes.getAdapter() == null) {
                    noteAdapter = new NoteAdapter(MainActivity.this, arrayList);
                    lstNotes.setAdapter(noteAdapter);
                } else {
                    noteAdapter.notifyDataSetChanged();

                }
            }
            return true;
        }
    });
    private void updateListNote() {
        new Thread() {
            @Override
            public void run() {
                arrayList.clear();
                arrayList.addAll(daoSession.getNoteModelDao().loadAll());
                handler.sendEmptyMessage(0);
            }
        }.start();
//        arrayList.clear();
//        arrayList.addAll(daoSession.getNoteModelDao().loadAll());
    }

    private void connectDatabase() {
        /* Connect to database */
        DatabaseOpenHelper helper = new DatabaseOpenHelper(this, Config.DATABASE_NAME);
        Database database = helper.getWritableDb();
        DaoMaster daoMaster = new DaoMaster(database);

        /* -- Important -- */
        daoSession = daoMaster.newSession();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setPositiveButton("Dm đưa luôn. Tao sợ m chắc!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                                .setPositiveButton("OK Thoát lẹ dm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                }).setTitle("Hihihi")
                                .setMessage("Em đùa tí thôi mà anh. Anh nhấn OK em thoát ngay ạ =)");
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }).setNegativeButton("Đoé", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setTitle("Thoát?")
                .setMessage("Nếu mày muốn thoát thì cho bố mày xin địa chỉ nhà? :V");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_main, menu);

        final MenuItem searchItem = menu.findItem(R.id.app_bar_search);

        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setQueryHint("Tìm kiếm trong MS Note");

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMenuItemVisibility(menu, searchItem, false);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                setMenuItemVisibility(menu, searchItem, true);
                return false;
            }
        });
        return true;
    }

    private void setMenuItemVisibility(final Menu menu, MenuItem searchItem, boolean b) {
            MenuItem item = menu.getItem(1);

            if (item != searchItem) {
                item.setVisible(b);
            }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.fab_add) {
            Intent intent = new Intent(this, AddNewNoteActivity.class);
            startActivityForResult(intent, Config.ADD_NOTE_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Config.ADD_NOTE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    int result = bundle.getInt("result");
                    if (result == 1) {
                        Toast.makeText(this, "Đã huỷ nhé hihi!", Toast.LENGTH_SHORT).show();
                    }
                }
                updateListNote();
            }
        } else if (requestCode == Config.NOTE_VIEW_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                updateListNote();
                Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        NoteModel model = arrayList.get(i);
        Intent intent = new Intent(this, ViewNoteActivity.class);
        intent.putExtra(Config.MODEL_KEY, model);
        startActivityForResult(intent, Config.NOTE_VIEW_REQUEST_CODE);
    }



    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.btn_settings) {
            Intent intent1 = new Intent(this, SettingsActivity.class);
            startActivity(intent1);
        }
        return false;
    }

    @Override
    public void onRefresh() {
        updateListNote();
        swipeRefreshLayout.setRefreshing(false);
    }
}

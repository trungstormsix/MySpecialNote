package com.tsnanh.myspecialnote.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tsnanh.myspecialnote.R;
import com.tsnanh.myspecialnote.model.NoteModel;

import java.util.ArrayList;

public class NoteAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<NoteModel> arrayList;
    public NoteAdapter(Context context, ArrayList<NoteModel> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public NoteModel getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return arrayList.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.item_list_note, viewGroup, false);

        TextView lblNoteTitle = v.findViewById(R.id.item_lbl_note_title);
        TextView lblNoteDatetime = v.findViewById(R.id.item_lbl_note_datetime);
        TextView lblNoteContent = v.findViewById(R.id.item_lbl_note_content);
        ImageView imgNote = v.findViewById(R.id.img_item_list);
        View colorLayout = v.findViewById(R.id.color_layout);
        ProgressBar progressBar = v.findViewById(R.id.prg_img_item_list);

        NoteModel model = arrayList.get(i);

        lblNoteTitle.setText(model.getNoteTitle());
        lblNoteContent.setText(model.getNoteContent());
        lblNoteDatetime.setText(model.getDatetime());
        colorLayout.setBackgroundColor(model.getColor());
        progressBar.setVisibility(View.VISIBLE);

        if (model.getImage().isEmpty()) {
            progressBar.setVisibility(View.GONE);
            imgNote.setVisibility(View.VISIBLE);
            imgNote.setImageResource(R.drawable.round_insert_photo_black_48);
        } else {
            progressBar.setVisibility(View.GONE);
            imgNote.setVisibility(View.VISIBLE);
            imgNote.setImageBitmap(Utilities.getImageFromData(context, model.getImage(), Config.MODE_ICON));
        }

//        if (v == null) {
//            LayoutInflater inflater = LayoutInflater.from(context);
//            v = inflater.inflate(R.layout.item_list_note, viewGroup, false);
//
//            Holder holder = new Holder();
//
//            holder.setColorLayout((LinearLayout) v.findViewById(R.id.color_layout));
//            holder.setPrgImgItemList((ProgressBar) v.findViewById(R.id.prg_img_item_list));
//            holder.setImgNote((ImageView) v.findViewById(R.id.img_item_list));
//            holder.setLblNoteTitle((TextView) v.findViewById(R.id.lbl_note_title));
//            holder.setLblNoteDatetime((TextView) v.findViewById(R.id.lbl_note_datetime));
//            holder.setLblNoteContent((TextView) v.findViewById(R.id.lbl_note_content));
//
//            v.setTag(holder);
//        }
//
//        final NoteModel model = arrayList.get(i);
//
//        final Holder holder = (Holder) v.getTag();
//
//        holder.getColorLayout().setBackgroundColor(model.getColor());
//        holder.getLblNoteTitle().setText(model.getNoteTitle());
//        holder.getLblNoteDatetime().setText(model.getDatetime());
//        holder.getLblNoteContent().setText(model.getNoteContent());
//
//        if (model.getImage().isEmpty()) {
//            holder.getPrgImgItemList().setVisibility(View.GONE);
//            holder.getImgNote().setImageResource(R.drawable.round_insert_photo_black_48);
//        } else {
//            holder.getPrgImgItemList().setVisibility(View.VISIBLE);
//            holder.getImgNote().setVisibility(View.VISIBLE);
//            final Handler handler = new Handler() {
//                @Override
//                public void handleMessage(@NonNull Message msg) {
//                    super.handleMessage(msg);
//
//                    holder.getColorLayout().setBackgroundColor(model.getColor());
//                    holder.getPrgImgItemList().setVisibility(View.GONE);
//                    holder.getImgNote().setImageBitmap((Bitmap) msg.obj);
//                }
//            };
//
//            new Thread() {
//
//                @Override
//                public void run() {
//                    super.run();
//
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Message msg = new Message();
//                            Bitmap bitmap = Utilities.getImageFromData(model.getImage(), Utilities.MODE_ICON);
//                            msg.obj = bitmap;
//                            handler.sendMessage(msg);
//                        }
//                    });
//                }
//            }.start();
//        }


        return v;
    }

    public void refresh(ArrayList<NoteModel> arrayList) {
        this.arrayList = arrayList;
    }

//    class Holder {
//        private ImageView imgNote;
//        private LinearLayout colorLayout;
//        private ProgressBar prgImgItemList;
//
//        public ProgressBar getPrgImgItemList() {
//            return prgImgItemList;
//        }
//
//        public void setPrgImgItemList(ProgressBar prgImgitemList) {
//            this.prgImgItemList = prgImgitemList;
//        }
//
//        public ImageView getImgNote() {
//            return imgNote;
//        }
//
//        public void setImgNote(ImageView imgNote) {
//            this.imgNote = imgNote;
//        }
//
//        public LinearLayout getColorLayout() {
//            return colorLayout;
//        }
//
//        public void setColorLayout(LinearLayout colorLayout) {
//            this.colorLayout = colorLayout;
//        }
//
//        private TextView lblNoteTitle;
//        private TextView lblNoteDatetime;
//        private TextView lblNoteContent;
//
//        public TextView getLblNoteContent() {
//            return lblNoteContent;
//        }
//
//        public void setLblNoteContent(TextView lblNoteContent) {
//            this.lblNoteContent = lblNoteContent;
//        }
//
//        public TextView getLblNoteTitle() {
//            return lblNoteTitle;
//        }
//
//        public void setLblNoteTitle(TextView lblNoteTitle) {
//            this.lblNoteTitle = lblNoteTitle;
//        }
//
//        public TextView getLblNoteDatetime() {
//            return lblNoteDatetime;
//        }
//
//        public void setLblNoteDatetime(TextView lblNoteDatetime) {
//            this.lblNoteDatetime = lblNoteDatetime;
//        }
//    }
}

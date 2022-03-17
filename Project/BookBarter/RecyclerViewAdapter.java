package com.csc301.students.BookBarter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> mBookNames = new ArrayList<>();
    private ArrayList<String> mCourseCode = new ArrayList<>();
    private ArrayList<String> mEdition = new ArrayList<>();
    private ArrayList<String> mAuthor = new ArrayList<>();

    private Context mContext;
    OnButtonClickListeners onButtonClickListeners;
    public RecyclerViewAdapter(ArrayList<String> mBookNames,
                               ArrayList<String> courseCode,
                               ArrayList<String> edition,
                               ArrayList<String> author, Context mContext) {
        this.mBookNames = mBookNames;
        this.mCourseCode = courseCode;
        this.mEdition = edition;
        this.mAuthor = author;
        this.mContext = mContext;
    }
    public void setOnButtonClickListeners(OnButtonClickListeners listener){
        this.onButtonClickListeners = listener;
    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bookName.setText(mBookNames.get(i));
        viewHolder.courseCode.setText(mCourseCode.get(i));
        viewHolder.edition.setText(mEdition.get(i));
        viewHolder.author.setText(mAuthor.get(i));
        viewHolder.number.setText(""+(i+1));
    }

    @Override
    public int getItemCount() {
        return mBookNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView bookName, courseCode, author, edition, number;
        Button addToList;

        LinearLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.book_number);
            bookName = itemView.findViewById(R.id.book_name);
            courseCode = itemView.findViewById(R.id.course_code);
            author = itemView.findViewById(R.id.author);
            edition = itemView.findViewById(R.id.edition);
            addToList=itemView.findViewById(R.id.addList);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            addToList.setOnClickListener(this);
            addToList.setText("Notify Me");
        }
        @Override
        public void onClick(View v) {
            if(onButtonClickListeners!=null){
                onButtonClickListeners.onClick(getAdapterPosition());
            }

        }
    }
    public interface OnButtonClickListeners{
        void onClick(int position);
    }
}

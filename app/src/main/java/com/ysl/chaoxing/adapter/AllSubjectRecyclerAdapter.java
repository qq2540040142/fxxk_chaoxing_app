package com.ysl.chaoxing.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ysl.chaoxing.R;
import com.ysl.chaoxing.data.AllSubject;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by ender on 2021/10/4 11:33
 * @author ysl
 */
public class AllSubjectRecyclerAdapter extends RecyclerView.Adapter<AllSubjectRecyclerAdapter.MyViewHolder> {
    private Context context;
    private AllSubject allSubject;
    private String TAG = "AllSubjectRecyclerAdapter";
    public AllSubjectRecyclerAdapter(Context context, AllSubject allSubject){
        this.context = context;
        this.allSubject = allSubject;
        /*Log.i(TAG, "SubjectNameSize----> " + allSubject.getSubjectName().size());
        Log.i(TAG, "ContentIdsize----> " + allSubject.getContentId().size());
        Log.i(TAG, "TeacherNamesize----> " + allSubject.getTeacherName().size());
        Log.i(TAG, "SchoolNamesize----> " + allSubject.getSchoolName().size());
        Log.i(TAG, "CourseIdsize----> " + allSubject.getCourseId().size());
        Log.i(TAG, "Cpisize----> " + allSubject.getCpi().size());*/
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView subjectName,teacherName,schoolName;

        MyViewHolder(View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.item_Main_Recyclerview_image);
            subjectName = itemView.findViewById(R.id.item_Main_Recyclerview_text_subjectName);
            schoolName= itemView.findViewById(R.id.item_Main_Recyclerview_text_schoolName);
            teacherName= itemView.findViewById(R.id.item_Main_Recyclerview_text_teacherName);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 创建ViewHolder，返回每一项的布局
        View inflater = LayoutInflater.from(context).inflate(R.layout.item_fragment_main_recyclerview, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(inflater);
        // item监听点击事件
        inflater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick.itemClick(view,myViewHolder.getLayoutPosition());
            }
        });
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.teacherName.setText(allSubject.getTeacherName().get(position));
        holder.subjectName.setText(allSubject.getSubjectName().get(position));
        holder.schoolName.setText(allSubject.getSchoolName().get(position));

    }

    @Override
    public int getItemCount() {
        return allSubject.getSubjectName().size();
    }
    //接口回调，定义点击事件
    private OnItemClick onItemClick;
    public interface OnItemClick{
        void itemClick(View view,int position);
    }
    public void setOnItemClick(OnItemClick onitemClick) {
        this.onItemClick = onitemClick;
    }

}

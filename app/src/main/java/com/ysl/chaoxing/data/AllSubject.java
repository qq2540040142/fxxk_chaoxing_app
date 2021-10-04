package com.ysl.chaoxing.data;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ender on 2021/10/3 21:00
 *
 * @author ysl
 */
public class AllSubject {
    /**
     * 课程的名字
     */
    private List<String> subjectName;
    /**
     * 课程的cpi
     */
    private List<Integer> cpi;
    /**
     * 课程的contentId
     */
    private List<Integer> contentId;
    /**
     * 课程的courseId
     */
    private List<Integer> courseId;
    /**
     * 课程的school
     */
    private List<String> schoolName;
    /**
     * 课程的教师名字
     */
    private List<String> teacherName;


    public AllSubject() {
        subjectName = new ArrayList<>();
        cpi = new ArrayList<>();
        contentId = new ArrayList<>();
        courseId = new ArrayList<>();
        schoolName = new ArrayList<>();
        teacherName = new ArrayList<>();
    }

    public void addName(String subjectName) {
        this.subjectName.add(subjectName);
    }

    public void addCpi(Integer cpi) {
        this.cpi.add(cpi);
    }

    public void addContentId(Integer contentId) {
        this.contentId.add(contentId);
    }

    public void addCourseId(Integer courseId) {
        this.courseId.add(courseId);
    }

    public void addTeacherName(String name) {
        this.teacherName.add(name);
    }

    public void addSchoolName(String name) {
        this.schoolName.add(name);
    }



    public List<String> getSubjectName() {
        return subjectName;
    }

    public List<String> getSchoolName() {
        return schoolName;
    }

    public List<String> getTeacherName() {
        return teacherName;
    }

    public List<Integer> getCpi() {
        return cpi;
    }

    public List<Integer> getContentId() {
        return contentId;
    }

    public List<Integer> getCourseId() {
        return courseId;
    }

}

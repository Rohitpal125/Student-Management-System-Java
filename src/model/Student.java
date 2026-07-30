package model;

import java.util.Objects;

public class Student {
    private int id;
    private String name;
    private int age;
    private String course;
    private int semester;
    private String email;
    private String phone;
    private double cgpa;

    public Student(int id, String name, int age, String course, int semester, String email, String phone, double cgpa){

        this.id = id;
        this. name = name;
        this. age = age;
        this.course = course;
        this.semester = semester;
        this.email = email;
        this.phone = phone;
        this.cgpa = cgpa;
    }

    public void setId(int id){

        this.id = id;
    }

    public int getId(){

        return id;
    }

    public void setName(String name){

        this.name = name;
    }

    public String getName(){

        return name;
    }

    public void setAge(int age){

        this.age = age;
    }
    public int getAge(){

        return age;
    }

    public void setCourse(String course){
        this.course = course;
    }

    public String getCourse(){

        return course;
    }

    public void setSemester(int semester){

        this.semester = semester;
    }

    public int getSemester(){

        return semester;
    }

    public void setEmail(String email){

        this.email = email;
    }

    public String getEmail(){

        return email;
    }

    public void setPhone(String phone){

        this.phone = phone;
    }
    public String getPhone(){
        return phone;
    }

    public void setCgpa(double cgpa){

        this.cgpa = cgpa;
    }

    public double getCgpa(){

        return cgpa;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        if(this==o)
            return true;
        Student student = (Student) o;
        return id == student.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public String toString(){
        return "ID: " + id +
                "\nName: " + name +
                "\nAge: " + age +
                "\n Course: " + course +
                "\nSemester: " + semester +
                "\nEmail: " + email +
                "\nPhoneNo: " + phone +
                "\nCGPA: " + cgpa;
    }

}

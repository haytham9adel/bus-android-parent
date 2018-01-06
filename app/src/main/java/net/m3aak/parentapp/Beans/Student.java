package net.m3aak.parentapp.Beans;

/**
 * Created by BD-2 on 8/24/2015.
 */
public class Student {

    //private variables
    int _id;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getS_fname() {
        return s_fname;
    }

    public void setS_fname(String s_fname) {
        this.s_fname = s_fname;
    }

    public String getS_contact() {
        return s_contact;
    }

    public void setS_contact(String s_contact) {
        this.s_contact = s_contact;
    }

    String s_contact;
    String student_id;
    String s_fname;
    String s_lname;
    String s_email;
String driverNumber;
    String driverName;

    public String getDriverNumber() {
        return driverNumber;
    }

    public void setDriverNumber(String driverNumber) {
        this.driverNumber = driverNumber;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getS_BlinkStatus() {
        return s_BlinkStatus;
    }

    public void setS_BlinkStatus(String s_BlinkStatus) {
        this.s_BlinkStatus = s_BlinkStatus;
    }

    String s_BlinkStatus;

    public String getS_latitude() {
        return s_latitude;
    }

    public void setS_latitude(String s_latitude) {
        this.s_latitude = s_latitude;
    }

    public String getS_longitude() {
        return s_longitude;
    }

    public void setS_longitude(String s_longitude) {
        this.s_longitude = s_longitude;
    }

    public String getS_class() {
        return s_class;
    }

    public void setS_class(String s_class) {
        this.s_class = s_class;
    }

    public String getS_gender() {
        return s_gender;
    }

    public void setS_gender(String s_gender) {
        this.s_gender = s_gender;
    }

    public String getS_dob() {
        return s_dob;
    }

    public void setS_dob(String s_dob) {
        this.s_dob = s_dob;
    }

    String s_latitude,s_longitude,s_class,s_gender,s_dob;

    public String getS_grand_name() {
        return s_grand_name;
    }

    public void setS_grand_name(String s_grand_name) {
        this.s_grand_name = s_grand_name;
    }

    public String getS_school_name() {
        return s_school_name;
    }

    public void setS_school_name(String s_school_name) {
        this.s_school_name = s_school_name;
    }

    public String getS_address() {
        return s_address;
    }

    public void setS_address(String s_address) {
        this.s_address = s_address;
    }

    public String getS_nationality() {
        return s_nationality;
    }

    public void setS_nationality(String s_nationality) {
        this.s_nationality = s_nationality;
    }

    public String getS_father_name() {
        return s_father_name;
    }

    public void setS_father_name(String s_father_name) {
        this.s_father_name = s_father_name;
    }

    public String getS_blood_type() {
        return s_blood_type;
    }

    public void setS_blood_type(String s_blood_type) {
        this.s_blood_type = s_blood_type;
    }

    public String getS_family_name() {
        return s_family_name;
    }

    public void setS_family_name(String s_family_name) {
        this.s_family_name = s_family_name;
    }

    public String getS_s_image_path() {
        return s_s_image_path;
    }

    public void setS_s_image_path(String s_s_image_path) {
        this.s_s_image_path = s_s_image_path;
    }

    String s_grand_name;
    String s_school_name;
    String s_address;
    String s_nationality;
    String s_father_name;
    String s_blood_type;
    String s_family_name;
    String s_s_image_path;

    String s_route_id;

    // Empty constructor
    public Student() {

    }

    // constructor
  /*  public Student(int id, String s_fname, String s_contact, String s_lname, String s_email, String student_id) {
        this._id = id;
        this.s_fname = s_fname;
        this.s_contact = s_contact;
        this.s_lname = s_lname;
        this.student_id = student_id;
        this.s_email = s_email;
    }

    // constructor
    public Student(String s_fname, String s_contact, String s_lname, String s_email, String student_id,String s_route_id) {
        this.s_fname = s_fname;
        this.s_contact = s_contact;
        this.s_lname = s_lname;
        this.student_id = student_id;
        this.s_email = s_email;
        this.s_route_id=s_route_id;
    }*/

    public String getS_route_id() {
        return this.s_route_id;
    }

    public void setS_route_id(String s_route_id) {
        this.s_route_id = s_route_id;
    }

    // getting ID
    public int getID() {
        return this._id;
    }

    // setting id
    public void setID(int id) {
        this._id = id;
    }

    // getting name
    public String getName() {
        return this.s_fname;
    }

    // setting name
    public void setName(String s_fname) {
        this.s_fname = s_fname;
    }

    // getting phone number
    public String getPhoneNumber() {
        return this.s_contact;
    }

    // setting phone number
    public void setPhoneNumber(String s_contact) {
        this.s_contact = s_contact;
    }

    public String getS_email() {
        return s_email;
    }

    public void setS_email(String s_email) {
        this.s_email = s_email;
    }

    public String getS_lname() {
        return s_lname;
    }

    public void setS_lname(String s_lname) {
        this.s_lname = s_lname;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }
}

package util;

public class StudentValidator {

//    ================NAME VALIDATION=============

    public static boolean isValidName(String name){
        return name!=null && !name.trim().isEmpty();
    }

//    ==============AGE VALIDATION================

    public static boolean isValidAge(int age){
        return age>=15 && age <=35;
    }

//    =============EMAIL VALIDATION================

    public static boolean isValidEmail(String email){
        return email!=null && email.contains("@") && email.contains(".");
    }

//    ===============PHONE VALIDATION===============

    public static boolean isValidPhone(String phone){
        return phone!=null && phone.matches("\\d{10}");
    }

//    ==================SEMESTER VALIDATION===========

    public static boolean isValidSemester(int semester){
        return semester >= 1 && semester <=8;
    }

//    ====================CGPA VALIDATION=============

    public static boolean isValidCgpa(int cgpa){
        return cgpa>=0 && cgpa <=10;
    }
}

package main;

import model.Student;
import service.StudentService;
import exception.StudentNotFoundException;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        StudentService service = new StudentService();

        while (true) {

            System.out.println("\n========== Student Management System ==========");
            System.out.println("1. Add Student");
            System.out.println("2. Display Students");
            System.out.println("3. Search Student");
            System.out.println("4. Update Student");
            System.out.println("5. Delete Student");
            System.out.println("6. Statistics");
            System.out.println("7. Sort Students");
            System.out.println("8. Filter Students");
            System.out.println("9. Exit");
            System.out.print("Enter your choice : ");

            try {

                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {

                    case 1:

                        System.out.print("Enter ID : ");
                        int id = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Enter Name : ");
                        String name = sc.nextLine();

                        System.out.print("Enter Age : ");
                        int age = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Enter Course : ");
                        String course = sc.nextLine();

                        System.out.print("Enter Semester : ");
                        int semester = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Enter Email : ");
                        String email = sc.nextLine();

                        System.out.print("Enter Phone : ");
                        String phone = sc.nextLine();

                        System.out.print("Enter CGPA : ");
                        double cgpa = sc.nextDouble();

                        Student student = new Student(
                                id,
                                name,
                                age,
                                course,
                                semester,
                                email,
                                phone,
                                cgpa
                        );

                        boolean added = service.addStudent(student);

                        if (added) {
                            System.out.println("Student Added Successfully.");
                        } else {
                            System.out.println("Student could not be added.");
                        }

                        break;

                    case 2:

                        service.displayStudents();

                        break;

                    case 3:

                        boolean searchMenu = true;

                        while (searchMenu) {

                            System.out.println("\n===== SEARCH MENU =====");
                            System.out.println("1. Search by ID");
                            System.out.println("2. Search by Name");
                            System.out.println("3. Search by Course");
                            System.out.println("4. Back");
                            System.out.print("Enter Your Choice: ");

                            int choice2 = sc.nextInt();
                            sc.nextLine();

                            switch (choice2) {

                                case 1:
                                    // Search by ID
                                    System.out.print("Enter Student ID : ");
                                    id = sc.nextInt();

                                    try {
                                        Student foundStudent = service.getStudentById(id);
                                        System.out.println(foundStudent);

                                    } catch (StudentNotFoundException e) {
                                        System.out.println(e.getMessage());
                                    }

                                    break;

                                case 2:
                                    // Search by Name
                                    System.out.print("Enter your Name: ");
                                    name = sc.nextLine();
                                    try {

                                        ArrayList<Student> result = service.searchStudentByName(name);

                                        service.displayStudents(result);

                                    }
                                    catch (StudentNotFoundException e) {

                                        System.out.println(e.getMessage());

                                    }
                                    break;

                                case 3:
                                    // Search by Course

                                    System.out.print("Enter Course: ");
                                    course = sc.nextLine();

                                    try {

                                        ArrayList<Student> result =
                                                service.searchStudentByCourse(course);

                                        service.displayStudents(result);

                                    } catch (StudentNotFoundException e) {

                                        System.out.println(e.getMessage());

                                    }

                                    break;

                                case 4:
                                    searchMenu = false;
                                    break;

                                default:
                                    System.out.println("Invalid Choice!");

                            }

                        }

                        break;

                    case 4:

                        System.out.print("Enter Student ID : ");
                        id = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Enter New Name : ");
                        name = sc.nextLine();

                        System.out.print("Enter New Age : ");
                        age = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Enter New Course : ");
                        course = sc.nextLine();

                        System.out.print("Enter New Semester : ");
                        semester = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Enter New Email : ");
                        email = sc.nextLine();

                        System.out.print("Enter New Phone : ");
                        phone = sc.nextLine();

                        System.out.print("Enter New CGPA : ");
                        cgpa = sc.nextDouble();

                        boolean updated = service.updateStudent(
                                id,
                                name,
                                age,
                                course,
                                semester,
                                email,
                                phone,
                                cgpa
                        );

                        if (updated) {
                            System.out.println("Student Updated Successfully.");
                        } else {
                            System.out.println("Student Not Found.");
                        }

                        break;

                    case 5:

                        System.out.print("Enter Student ID : ");
                        id = sc.nextInt();

                        boolean deleted = service.deleteStudent(id);

                        if (deleted) {
                            System.out.println("Student Deleted Successfully.");
                        } else {
                            System.out.println("Student Not Found.");
                        }

                        break;

                    case 6:
                        boolean staticsmenu = true;

                        while (staticsmenu){

                            System.out.println("\n========Statistics=========");

                            System.out.println("1. Total Students: ");
                            System.out.println("2. Average CGPA: ");
                            System.out.println("3. Highest CGPA Student: ");
                            System.out.println("4. Lowest CGPA Student: ");
                            System.out.println("5. Students Per Course: ");
                            System.out.println("6. Students Per Semester");
                            System.out.println("7. Back: ");
                            System.out.print("Enter your Choice: ");

                            int choice3 = sc.nextInt();
                            sc.nextLine();

                            switch (choice3){

                                case 1:
                                    System.out.println("Total Students: "+ service.getTotalStudents());

                                    break;

                                case 2:
                                    System.out.println("Average CGPA: " + service.getAverageCgpa());

                                    break;

                                case 3:
                                    Student highest = service.getHighestCgpa();
                                    if(highest!=null){
                                        System.out.println(highest);
                                    }
                                    else {
                                        System.out.println("No student found");
                                    }
                                    break;

                                case 4:
                                    Student lowest = service.getLowestCgpa();
                                    if(lowest!=null){
                                        System.out.println(lowest);
                                    }
                                    else {
                                        System.out.println("No student found");
                                    }
                                    break;

                                case 5:

                                    System.out.println("Students course");
                                    service.studentsPerCourse();
                                    break;

                                case 6:
                                    System.out.println("Students Semester");

                                    service.studentsPerSemester();
                                    break;

                                case 7:
                                    staticsmenu = false;
                                    break;

                                default:
                                    System.out.println("Invalid choice");
                            }

                        }


                    case 7:
                        boolean sortstudent = true;

                        while (sortstudent){

                            System.out.println("========== Sort Students ==========");

                            System.out.println("1.Sort by name");
                            System.out.println("2.Sort by CGPA");
                            System.out.println("3.Sort by Age");
                            System.out.println("4.Sort by Semester");
                            System.out.println("5.Back");
                            System.out.print("Enter your choice: ");

                            int chice3 = sc.nextInt();
                            sc.nextLine();

                            switch (chice3){

                                case 1:
                                    service.sortByName();

                                    break;

                                case 2:
                                    service.sortByCgpa();

                                    break;

                                case 3:
                                    service.sortByAge();

                                    break;

                                case 4:
                                    service.sortBySemester();
                                    break;

                                case 5:
                                    sortstudent = false;
                                    break;

                                default:
                                    System.out.println("Invalid choice");
                            }

                        }

                    case 8:

                        boolean filterstudent = true;

                        while (filterstudent){
                            System.out.println("========== Filter Students ==========");

                            System.out.println("1.Filter by CGPA");
                            System.out.println("2.Filter by AGE");
                            System.out.println("3.Filter by SEMESTER");
                            System.out.println("4.Filter by COURSE");
                            System.out.println("5.Back");
                            System.out.print("Enter your choice: ");

                            int choice4 = sc.nextInt();
                            sc.nextLine();

                            switch (choice4){

                                case 1:
                                    service.filterByCgpa();
                                    break;

                                case 2:
                                    service.filterByAge();
                                    break;

                                case 3:
                                    service.filterBySemester();
                                    break;

                                case 4:
                                    service.filterByCourse();
                                    break;

                                case 5:
                                    filterstudent = false;
                                    break;

                                default:
                                    System.out.println("Invalide choice");
                            }
                        }

                    case 9:

                        System.out.println("Thank You!");
                        System.exit(0);

                        break;

                    default:

                        System.out.println("Invalid Choice.");

                }

            } catch (InputMismatchException e) {

                System.out.println("Invalid Input! Please enter the correct data type.");
                sc.nextLine();

            } catch (Exception e) {

                System.out.println("Something went wrong.");
                System.out.println(e.getMessage());

            }

        }

    }

}
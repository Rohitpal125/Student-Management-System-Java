package main;

import model.Student;
import service.StudentService;

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
            System.out.println("6. Exit");
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

                        service.addStudent(student);

                        break;

                    case 2:

                        service.displayStudents();

                        break;

                    case 3:

                        System.out.print("Enter Student ID : ");
                        id = sc.nextInt();

                        Student s = service.searchStudentById(id);

                        if (s != null) {

                            System.out.println(s);

                        } else {

                            System.out.println("Student Not Found.");

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
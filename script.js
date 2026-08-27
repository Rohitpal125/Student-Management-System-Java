// ========================================
// Student Management System - JavaScript
// ========================================


// 1. Get HTML elements
const form = document.querySelector("form");
const addStudentButton = document.querySelector("body > button");
const table = document.querySelector("table");


// 2. Student ID counter
let studentId = 1;


// 3. Check JavaScript is working
console.log("Student Management System JavaScript is working!");


// 4. Add Student button
addStudentButton.addEventListener("click", function () {

    form.scrollIntoView({
        behavior: "smooth"
    });

});


// 5. Form submit
form.addEventListener("submit", function (event) {

    // Stop page from refreshing
    event.preventDefault();


    // 6. Get input values
    const inputs = form.querySelectorAll("input");


    const name = inputs[0].value;
    const age = inputs[1].value;
    const course = inputs[2].value;
    const semester = inputs[3].value;
    const email = inputs[4].value;
    const phone = inputs[5].value;
    const cgpa = inputs[6].value;


    // 7. Display values in console
    console.log("Name:", name);
    console.log("Age:", age);
    console.log("Course:", course);
    console.log("Semester:", semester);
    console.log("Email:", email);
    console.log("Phone:", phone);
    console.log("CGPA:", cgpa);


    // 8. Create a new table row
    const row = table.insertRow(-1);


    // 9. Create cells
    const idCell = row.insertCell(0);
    const nameCell = row.insertCell(1);
    const ageCell = row.insertCell(2);
    const courseCell = row.insertCell(3);
    const semesterCell = row.insertCell(4);
    const emailCell = row.insertCell(5);
    const phoneCell = row.insertCell(6);
    const cgpaCell = row.insertCell(7);


    // 10. Put student data into cells
    idCell.textContent = studentId;
    nameCell.textContent = name;
    ageCell.textContent = age;
    courseCell.textContent = course;
    semesterCell.textContent = semester;
    emailCell.textContent = email;
    phoneCell.textContent = phone;
    cgpaCell.textContent = cgpa;


    // 11. Increase ID for next student
    studentId++;


    // 12. Clear form
    form.reset();


    // 13. Show success message
    alert("Student added successfully!");


    // 14. Console message
    console.log("Student added successfully!");

});


fetch("https://jsonplaceholder.typicode.com/users")
    .then(response => response.json())
    .then(data => {

        data.forEach(student => {
            console.log(student.name);
        });

    });
// ========================================
// Student Management System - JavaScript
// ========================================


// 1. Get HTML elements
const form = document.querySelector("form");
const addStudentButton = document.querySelector("body > button");
const table = document.querySelector("table");


// 2. Check JavaScript is working
console.log("Student Management System JavaScript is working!");


// 3. Add Student button
addStudentButton.addEventListener("click", function () {

    form.scrollIntoView({
        behavior: "smooth"
    });

});


// ================= ADD STUDENT =================

form.addEventListener("submit", function (event) {

    // Stop page refresh
    event.preventDefault();


    // Get input values
    const inputs = form.querySelectorAll("input");

    const name = inputs[0].value;
    const age = inputs[1].value;
    const course = inputs[2].value;
    const semester = inputs[3].value;
    const email = inputs[4].value;
    const phone = inputs[5].value;
    const cgpa = inputs[6].value;


    // Create student object
    const student = {

        name: name,
        age: Number(age),
        course: course,
        semester: Number(semester),
        email: email,
        phone: phone,
        cgpa: Number(cgpa)

    };


    console.log("Student object:", student);


    // Send student to Spring Boot
    fetch("http://localhost:8080/api/students", {

        method: "POST",

        headers: {
            "Content-Type": "application/json"
        },

        body: JSON.stringify(student)

    })

        .then(response => response.text())

        .then(data => {

            console.log("Spring Boot response:", data);

            alert(data);

            form.reset();

            loadStudents();

        })

        .catch(error => {

            console.error("Error adding student:", error);

            alert("Student could not be added.");

        });

});


// ================= GET STUDENTS =================

function loadStudents() {

    fetch("http://localhost:8080/api/students")

        .then(response => response.json())

        .then(students => {

            console.log("Students from Spring Boot:", students);


            // Remove old table rows
            while (table.rows.length > 1) {
                table.deleteRow(1);
            }


            // Add students to table
            students.forEach(student => {

                const row = table.insertRow(-1);

                const idCell = row.insertCell(0);
                const nameCell = row.insertCell(1);
                const ageCell = row.insertCell(2);
                const courseCell = row.insertCell(3);
                const semesterCell = row.insertCell(4);
                const emailCell = row.insertCell(5);
                const phoneCell = row.insertCell(6);
                const cgpaCell = row.insertCell(7);
                const actionCell = row.insertCell(8);


                idCell.textContent = student.id;
                nameCell.textContent = student.name;
                ageCell.textContent = student.age;
                courseCell.textContent = student.course;
                semesterCell.textContent = student.semester;
                emailCell.textContent = student.email;
                phoneCell.textContent = student.phone;
                cgpaCell.textContent = student.cgpa;

                const updateButton = document.createElement("button");

                updateButton.textContent = "Update";

                updateButton.type = "button";

                updateButton.addEventListener("click", function () {

                    updateStudent(student);

                });

                actionCell.appendChild(updateButton);

                const deleteButton = document.createElement("button");

                deleteButton.textContent = "Delete";

                deleteButton.type = "button";

                deleteButton.addEventListener("click", function () {

                    deleteStudent(student.id);

                });

                actionCell.appendChild(deleteButton);

            });

        })

        .catch(error => {

            console.error("Error fetching students:", error);

        });


    // ================= DELETE STUDENT =================

    function deleteStudent(id) {

        const confirmDelete = confirm(
            "Are you sure you want to delete this student?"
        );

        if (!confirmDelete) {
            return;
        }

        fetch(`http://localhost:8080/api/students/${id}`, {

            method: "DELETE"

        })
            .then(response => response.text())
            .then(data => {

                console.log("Delete response:", data);

                alert(data);

                loadStudents();

            })
            .catch(error => {

                console.error("Error deleting student:", error);

                alert("Student could not be deleted.");

            });
    }

    // ================= UPDATE STUDENT =================

    function updateStudent() {

        const id = Number(prompt("Enter Student ID to update:"));

        if (!id) {
            alert("Invalid ID");
            return;
        }

        const name = prompt("Enter new name:");
        const age = Number(prompt("Enter new age:"));
        const course = prompt("Enter new course:");
        const semester = Number(prompt("Enter new semester:"));
        const email = prompt("Enter new email:");
        const phone = prompt("Enter new phone:");
        const cgpa = Number(prompt("Enter new CGPA:"));

        const student = {

            name: name,
            age: age,
            course: course,
            semester: semester,
            email: email,
            phone: phone,
            cgpa: cgpa

        };

        console.log("Updating student:", student);

        fetch(`http://localhost:8080/api/students/${id}`, {

            method: "PUT",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify(student)

        })

            .then(response => response.text())

            .then(data => {

                console.log("Spring Boot response:", data);

                alert(data);

                loadStudents();

            })

            .catch(error => {

                console.error("Error updating student:", error);

                alert("Student could not be updated.");

            });
    }

}


// Load students when page opens
loadStudents();

// ================= UPDATE STUDENT =================
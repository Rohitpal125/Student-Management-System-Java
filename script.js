const API_BASE = "http://localhost:8080";
const STUDENTS_URL = API_BASE + "/api/students";
const COURSES_URL = API_BASE + "/api/courses";



let students = [];          // all students from the server
let extraCourses = [];      // course names from /api/courses (if it exists)
let loadFailed = false;     // true when the server could not be reached
let editingId = null;       // null = adding a new student, number = editing
let pendingDeleteId = null; // student waiting for delete confirmation



const $ = (selector) => document.querySelector(selector);

const els = {
    addBtn: $("#addBtn"),
    errorBanner: $("#errorBanner"),
    errorBannerText: $("#errorBannerText"),
    retryBtn: $("#retryBtn"),

    statTotal: $("#statTotal"),
    statAvg: $("#statAvg"),
    statHigh: $("#statHigh"),
    statHighName: $("#statHighName"),
    statLow: $("#statLow"),
    statLowName: $("#statLowName"),

    search: $("#searchInput"),
    courseFilter: $("#courseFilter"),
    semesterFilter: $("#semesterFilter"),
    sort: $("#sortSelect"),
    clearBtn: $("#clearBtn"),
    resultCount: $("#resultCount"),

    table: $("#studentTable"),
    tbody: $("#studentBody"),
    emptyState: $("#emptyState"),
    emptyTitle: $("#emptyTitle"),
    emptyText: $("#emptyText"),
    emptyAction: $("#emptyAction"),

    drawer: $("#drawer"),
    drawerTitle: $("#drawerTitle"),
    form: $("#studentForm"),
    closeDrawer: $("#closeDrawer"),
    cancelBtn: $("#cancelBtn"),
    saveBtn: $("#saveBtn"),
    courseList: $("#courseList"),

    confirmDialog: $("#confirmDialog"),
    confirmText: $("#confirmText"),
    confirmOk: $("#confirmOk"),
    confirmCancel: $("#confirmCancel"),

    toasts: $("#toasts")
};

// The form inputs, in one place
const fields = {
    name: $("#fName"),
    age: $("#fAge"),
    course: $("#fCourse"),
    semester: $("#fSemester"),
    email: $("#fEmail"),
    phone: $("#fPhone"),
    cgpa: $("#fCgpa")
};



function showToast(message, isError = false) {
    const toast = document.createElement("div");
    toast.className = "toast" + (isError ? " error" : "");
    toast.textContent = message;
    els.toasts.appendChild(toast);

    setTimeout(() => toast.remove(), isError ? 5000 : 3000);
}

function showErrorBanner(message) {
    els.errorBannerText.textContent = message;
    els.errorBanner.hidden = false;
}

function hideErrorBanner() {
    els.errorBanner.hidden = true;
}

// The backend replies with text like "Student added successfully"
function isSuccess(text) {
    return /successfully/i.test(text);
}

// CGPA bar colour: green (7.5+), teal (5 to 7.5), red (below 5)
function cgpaLevel(cgpa) {
    if (cgpa >= 7.5) return "good";
    if (cgpa >= 5) return "mid";
    return "low";
}



async function loadStudents() {
    try {
        const response = await fetch(STUDENTS_URL);

        if (!response.ok) {
            throw new Error("Server replied with status " + response.status);
        }

        const data = await response.json();
        students = Array.isArray(data) ? data : [];
        loadFailed = false;
        hideErrorBanner();

    } catch (error) {
        console.error("Error loading students:", error);
        students = [];
        loadFailed = true;
        showErrorBanner(
            "Can't load students. Start the Spring Boot app, check that MySQL is " +
            "running, and make sure the server is at " + API_BASE + "."
        );
    }

    refreshCourseOptions();
    render();
}

// Optional: if your backend has GET /api/courses we use it for the course list.
// If it does not exist, we simply use the courses already used by students.
async function loadCourses() {
    try {
        const response = await fetch(COURSES_URL);
        if (!response.ok) return;

        const data = await response.json();
        if (!Array.isArray(data)) return;

        extraCourses = data
            .map((item) => (typeof item === "string" ? item : item.courseName || item.name))
            .filter(Boolean);

        refreshCourseOptions();

    } catch (error) {
        // No /api/courses endpoint - that's fine
    }
}

// Fill the course filter and the course suggestions in the form
function refreshCourseOptions() {
    const names = new Set(extraCourses);
    students.forEach((s) => {
        if (s.course) names.add(s.course);
    });

    const sorted = [...names].sort((a, b) => a.localeCompare(b));

    // Course filter (keep the current selection)
    const selected = els.courseFilter.value;
    els.courseFilter.replaceChildren(new Option("All courses", ""));
    sorted.forEach((name) => els.courseFilter.appendChild(new Option(name, name)));
    els.courseFilter.value = sorted.includes(selected) ? selected : "";

    // Suggestions in the form
    els.courseList.replaceChildren();
    sorted.forEach((name) => {
        const option = document.createElement("option");
        option.value = name;
        els.courseList.appendChild(option);
    });
}


// ---------------------------------------------------------
// 6. Search, filter and sort
// ---------------------------------------------------------

function getVisibleStudents() {
    const query = els.search.value.trim().toLowerCase();
    const course = els.courseFilter.value;
    const semester = els.semesterFilter.value;

    const rows = students.filter((s) => {
        if (course && s.course !== course) return false;
        if (semester && String(s.semester) !== semester) return false;

        if (query) {
            const text = [s.id, s.name, s.email, s.phone, s.course]
                .join(" ")
                .toLowerCase();
            if (!text.includes(query)) return false;
        }
        return true;
    });

    const sortBy = els.sort.value;

    rows.sort((a, b) => {
        switch (sortBy) {
            case "name":      return String(a.name).localeCompare(String(b.name));
            case "cgpa-desc": return b.cgpa - a.cgpa;
            case "cgpa-asc":  return a.cgpa - b.cgpa;
            case "age":       return a.age - b.age;
            case "semester":  return a.semester - b.semester;
            default:          return a.id - b.id;
        }
    });

    return rows;
}



function render() {
    renderStats();
    renderTable();
}

function renderStats() {
    if (students.length === 0) {
        els.statTotal.textContent = loadFailed ? "–" : "0";
        els.statAvg.textContent = "–";
        els.statHigh.textContent = "–";
        els.statLow.textContent = "–";
        els.statHighName.textContent = "";
        els.statLowName.textContent = "";
        return;
    }

    let total = 0;
    let highest = students[0];
    let lowest = students[0];

    students.forEach((s) => {
        total += Number(s.cgpa) || 0;
        if (s.cgpa > highest.cgpa) highest = s;
        if (s.cgpa < lowest.cgpa) lowest = s;
    });

    els.statTotal.textContent = students.length;
    els.statAvg.textContent = (total / students.length).toFixed(2);
    els.statHigh.textContent = Number(highest.cgpa).toFixed(1);
    els.statHighName.textContent = highest.name;
    els.statLow.textContent = Number(lowest.cgpa).toFixed(1);
    els.statLowName.textContent = lowest.name;
}

function renderTable() {
    const rows = getVisibleStudents();

    els.tbody.replaceChildren();


    if (loadFailed) {
        els.resultCount.textContent = "";
    } else if (rows.length === students.length) {
        els.resultCount.textContent = students.length + (students.length === 1 ? " student" : " students");
    } else {
        els.resultCount.textContent = "Showing " + rows.length + " of " + students.length + " students";
    }

    if (rows.length === 0) {
        els.table.hidden = true;
        els.emptyState.hidden = false;
        els.emptyAction.hidden = true;

        if (loadFailed) {
            els.emptyTitle.textContent = "Nothing to show yet";
            els.emptyText.textContent = "Students will appear here once the server connection works.";
        } else if (students.length === 0) {
            els.emptyTitle.textContent = "No students yet";
            els.emptyText.textContent = "Add the first student to start your list.";
            els.emptyAction.hidden = false;
        } else {
            els.emptyTitle.textContent = "No students match";
            els.emptyText.textContent = "Try a different search, or clear the filters.";
        }
        return;
    }

    els.table.hidden = false;
    els.emptyState.hidden = true;

    rows.forEach((student) => els.tbody.appendChild(buildRow(student)));
}

function makeCell(label, className) {
    const td = document.createElement("td");
    td.dataset.label = label;
    if (className) td.className = className;
    return td;
}

function buildRow(student) {
    const tr = document.createElement("tr");

    // ID
    const idCell = makeCell("ID", "num");
    idCell.textContent = student.id;

    // Name + email
    const studentCell = makeCell("Student", "cell-student");
    const nameLine = document.createElement("div");
    nameLine.className = "student-name";
    nameLine.textContent = student.name;
    const emailLine = document.createElement("div");
    emailLine.className = "student-email";
    emailLine.textContent = student.email;
    studentCell.append(nameLine, emailLine);

    // Age, course, semester, phone
    const ageCell = makeCell("Age", "num");
    ageCell.textContent = student.age;

    const courseCell = makeCell("Course");
    courseCell.textContent = student.course;

    const semesterCell = makeCell("Semester", "num");
    semesterCell.textContent = student.semester;

    const phoneCell = makeCell("Phone", "num");
    phoneCell.textContent = student.phone;

    // CGPA with a small bar
    const cgpaValue = Number(student.cgpa) || 0;
    const cgpaCell = makeCell("CGPA");
    const cgpaWrap = document.createElement("div");
    cgpaWrap.className = "cgpa";

    const bar = document.createElement("span");
    bar.className = "cgpa-bar";
    const fill = document.createElement("span");
    fill.className = "cgpa-fill " + cgpaLevel(cgpaValue);
    fill.style.width = Math.min(Math.max(cgpaValue, 0), 10) * 10 + "%";
    bar.appendChild(fill);

    const number = document.createElement("span");
    number.className = "cgpa-number";
    number.textContent = cgpaValue.toFixed(1);

    cgpaWrap.append(bar, number);
    cgpaCell.appendChild(cgpaWrap);

    // Buttons
    const actionCell = makeCell("Actions", "cell-actions");
    const actions = document.createElement("div");
    actions.className = "actions";

    const editBtn = document.createElement("button");
    editBtn.type = "button";
    editBtn.className = "btn btn-ghost btn-small";
    editBtn.textContent = "Edit";
    editBtn.setAttribute("aria-label", "Edit " + student.name);
    editBtn.addEventListener("click", () => openDrawer(student));

    const deleteBtn = document.createElement("button");
    deleteBtn.type = "button";
    deleteBtn.className = "btn btn-ghost btn-small btn-link-danger";
    deleteBtn.textContent = "Delete";
    deleteBtn.setAttribute("aria-label", "Delete " + student.name);
    deleteBtn.addEventListener("click", () => askDelete(student));

    actions.append(editBtn, deleteBtn);
    actionCell.appendChild(actions);

    tr.append(idCell, studentCell, ageCell, courseCell, semesterCell, phoneCell, cgpaCell, actionCell);
    return tr;
}




function clearFormErrors() {
    Object.keys(fields).forEach((key) => {
        const message = $("#err-" + key);
        if (message) message.textContent = "";
        fields[key].removeAttribute("aria-invalid");
    });
}

function openDrawer(student) {
    clearFormErrors();
    els.form.reset();

    if (student) {
        editingId = student.id;
        els.drawerTitle.textContent = "Edit student";
        els.saveBtn.textContent = "Save changes";

        fields.name.value = student.name;
        fields.age.value = student.age;
        fields.course.value = student.course;
        fields.semester.value = student.semester;
        fields.email.value = student.email;
        fields.phone.value = student.phone;
        fields.cgpa.value = student.cgpa;
    } else {
        editingId = null;
        els.drawerTitle.textContent = "Add student";
        els.saveBtn.textContent = "Save student";
    }

    els.saveBtn.disabled = false;
    els.drawer.showModal();
    fields.name.focus();
}

function closeDrawer() {
    els.drawer.close();
}

// Same rules as the Java StudentValidator, so errors show before sending
function validate(values) {
    const errors = {};

    if (!/^[A-Za-z]+( [A-Za-z]+)*$/.test(values.name)) {
        errors.name = "Use letters and single spaces only, for example Rohit Pal.";
    }

    if (!Number.isInteger(values.age) || values.age < 15 || values.age > 35) {
        errors.age = "Enter an age between 15 and 35.";
    }

    if (values.course === "") {
        errors.course = "Enter the course name.";
    }

    if (!Number.isInteger(values.semester) || values.semester < 1 || values.semester > 8) {
        errors.semester = "Enter a semester from 1 to 8.";
    }

    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(values.email)) {
        errors.email = "Enter a valid email, like name@example.com.";
    }

    if (!/^\d{10}$/.test(values.phone)) {
        errors.phone = "Enter exactly 10 digits.";
    }

    if (Number.isNaN(values.cgpa) || values.cgpa < 0 || values.cgpa > 10) {
        errors.cgpa = "Enter a CGPA between 0 and 10.";
    }

    return errors;
}

function readForm() {
    return {
        name: fields.name.value.trim(),
        age: fields.age.value === "" ? NaN : Number(fields.age.value),
        course: fields.course.value.trim(),
        semester: fields.semester.value === "" ? NaN : Number(fields.semester.value),
        email: fields.email.value.trim(),
        phone: fields.phone.value.trim(),
        cgpa: fields.cgpa.value === "" ? NaN : Number(fields.cgpa.value)
    };
}

async function saveStudent(event) {
    event.preventDefault();
    clearFormErrors();

    const student = readForm();
    const errors = validate(student);
    const errorKeys = Object.keys(errors);

    if (errorKeys.length > 0) {
        errorKeys.forEach((key) => {
            $("#err-" + key).textContent = errors[key];
            fields[key].setAttribute("aria-invalid", "true");
        });
        fields[errorKeys[0]].focus();
        return;
    }

    const isEditing = editingId !== null;
    const url = isEditing ? STUDENTS_URL + "/" + editingId : STUDENTS_URL;

    els.saveBtn.disabled = true;
    els.saveBtn.textContent = "Saving...";

    try {
        const response = await fetch(url, {
            method: isEditing ? "PUT" : "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(student)
        });

        const text = await response.text();

        if (response.ok && isSuccess(text)) {
            closeDrawer();
            showToast(isEditing ? "Student updated." : "Student added.");
            await loadStudents();
        } else {
            console.error("Server reply:", text);
            showToast(
                "Could not save the student. Check that the course already exists " +
                "in the courses table, then try again.",
                true
            );
        }

    } catch (error) {
        console.error("Error saving student:", error);
        showToast("Could not reach the server. Is the Spring Boot app running?", true);
    }

    els.saveBtn.disabled = false;
    els.saveBtn.textContent = isEditing ? "Save changes" : "Save student";
}


// ---------------------------------------------------------
// 9. Delete
// ---------------------------------------------------------

function askDelete(student) {
    pendingDeleteId = student.id;
    els.confirmText.textContent =
        student.name + " (ID " + student.id + ") will be removed permanently.";
    els.confirmDialog.showModal();
}

async function deleteStudent() {
    const id = pendingDeleteId;
    els.confirmDialog.close();
    if (id === null) return;

    try {
        const response = await fetch(STUDENTS_URL + "/" + id, { method: "DELETE" });
        const text = await response.text();

        if (response.ok && isSuccess(text)) {
            showToast("Student deleted.");
            await loadStudents();
        } else {
            console.error("Server reply:", text);
            showToast("Could not delete the student.", true);
        }

    } catch (error) {
        console.error("Error deleting student:", error);
        showToast("Could not reach the server. Is the Spring Boot app running?", true);
    }

    pendingDeleteId = null;
}



els.addBtn.addEventListener("click", () => openDrawer(null));
els.emptyAction.addEventListener("click", () => openDrawer(null));
els.retryBtn.addEventListener("click", () => {
    loadStudents();
    loadCourses();
});

els.search.addEventListener("input", renderTable);
els.courseFilter.addEventListener("change", renderTable);
els.semesterFilter.addEventListener("change", renderTable);
els.sort.addEventListener("change", renderTable);

els.clearBtn.addEventListener("click", () => {
    els.search.value = "";
    els.courseFilter.value = "";
    els.semesterFilter.value = "";
    els.sort.value = "id";
    renderTable();
});

els.form.addEventListener("submit", saveStudent);
els.closeDrawer.addEventListener("click", closeDrawer);
els.cancelBtn.addEventListener("click", closeDrawer);

// Clicking the dark area outside the panel closes it
els.drawer.addEventListener("click", (event) => {
    if (event.target === els.drawer) closeDrawer();
});

// Phone: allow digits only
fields.phone.addEventListener("input", () => {
    fields.phone.value = fields.phone.value.replace(/\D/g, "").slice(0, 10);
});

// Remove a field's error as soon as the user edits it
Object.keys(fields).forEach((key) => {
    fields[key].addEventListener("input", () => {
        $("#err-" + key).textContent = "";
        fields[key].removeAttribute("aria-invalid");
    });
});

els.confirmOk.addEventListener("click", deleteStudent);
els.confirmCancel.addEventListener("click", () => {
    pendingDeleteId = null;
    els.confirmDialog.close();
});



loadStudents();
loadCourses();
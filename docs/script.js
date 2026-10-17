const API_BASE = (location.hostname === "localhost" || location.hostname === "127.0.0.1")
    ? "http://localhost:8080"
    : "https://student-management-api-e13m.onrender.com";
const STUDENTS_URL = API_BASE + "/api/students";
const COURSES_URL = API_BASE + "/api/courses";
const LOGIN_URL = API_BASE + "/api/login";
const CHANGE_PASSWORD_URL = API_BASE + "/api/change-password";


// ---------------------------------------------------------
// 2. State (data we keep in memory while the page is open)
// ---------------------------------------------------------

let students = [];          // all students from the server
let extraCourses = [];      // course names from /api/courses (if it exists)
let loadFailed = false;     // true when the server could not be reached
let editingId = null;       // null = adding a new student, number = editing
let pendingDeleteId = null; // student waiting for delete confirmation
let authToken = null;       // set after admin login (null = normal visitor)
let adminName = "";         // name of the logged-in admin


// ---------------------------------------------------------
// 3. HTML elements
// ---------------------------------------------------------

const $ = (selector) => document.querySelector(selector);

const els = {
    addBtn: $("#addBtn"),
    loginBtn: $("#loginBtn"),
    logoutBtn: $("#logoutBtn"),
    passwordBtn: $("#passwordBtn"),
    adminLabel: $("#adminLabel"),

    passwordDialog: $("#passwordDialog"),
    passwordForm: $("#passwordForm"),
    oldPass: $("#oldPass"),
    newPass: $("#newPass"),
    confirmPass: $("#confirmPass"),
    passwordError: $("#passwordError"),
    passwordCancel: $("#passwordCancel"),
    passwordSubmit: $("#passwordSubmit"),

    loginDialog: $("#loginDialog"),
    loginForm: $("#loginForm"),
    loginUser: $("#loginUser"),
    loginPass: $("#loginPass"),
    loginError: $("#loginError"),
    loginCancel: $("#loginCancel"),
    loginSubmit: $("#loginSubmit"),

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


// ---------------------------------------------------------
// 4. Small helper functions
// ---------------------------------------------------------

// Show a short message at the bottom of the screen
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

// ---------- Admin login helpers ----------

function isAdmin() {
    return authToken !== null;
}

// Headers for requests that change data (add, update, delete)
function authHeaders(extraHeaders) {
    return Object.assign({}, extraHeaders, { Authorization: "Bearer " + authToken });
}

// The login is kept only while this browser tab is open
function saveSession(token, name) {
    authToken = token;
    adminName = name;
    try {
        sessionStorage.setItem("sms_token", token);
        sessionStorage.setItem("sms_admin", name);
    } catch (error) { /* storage not available - fine */ }
}

function clearSession() {
    authToken = null;
    adminName = "";
    try {
        sessionStorage.removeItem("sms_token");
        sessionStorage.removeItem("sms_admin");
    } catch (error) { /* storage not available - fine */ }
}

function restoreSession() {
    try {
        const token = sessionStorage.getItem("sms_token");
        if (token) {
            authToken = token;
            adminName = sessionStorage.getItem("sms_admin") || "admin";
        }
    } catch (error) { /* storage not available - fine */ }
}

// Show or hide the admin-only buttons
function updateAuthUI() {
    const admin = isAdmin();

    document.body.classList.toggle("is-admin", admin);
    els.addBtn.hidden = !admin;
    els.logoutBtn.hidden = !admin;
    els.passwordBtn.hidden = !admin;
    els.loginBtn.hidden = admin;
    els.adminLabel.hidden = !admin;
    els.adminLabel.textContent = admin ? "Signed in as " + adminName : "";
}

// Called when the server says "401 - login required"
function handleSessionExpired() {
    clearSession();
    updateAuthUI();
    render();
    showToast("Your admin session ended. Please log in again.", true);
}

// CGPA bar colour: green (7.5+), teal (5 to 7.5), red (below 5)
function cgpaLevel(cgpa) {
    if (cgpa >= 7.5) return "good";
    if (cgpa >= 5) return "mid";
    return "low";
}


// ---------------------------------------------------------
// 5. Load data from the server
// ---------------------------------------------------------

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


// ---------------------------------------------------------
// 7. Draw the page
// ---------------------------------------------------------

function render() {
    renderTable();   // renderTable also updates the summary numbers
}

// Summary numbers for the students currently shown (after course / semester / search filters)
function renderStats(list) {
    if (list.length === 0) {
        els.statTotal.textContent = loadFailed ? "–" : "0";
        els.statAvg.textContent = "–";
        els.statHigh.textContent = "–";
        els.statLow.textContent = "–";
        els.statHighName.textContent = "";
        els.statLowName.textContent = "";
        return;
    }

    let total = 0;
    let highest = list[0];
    let lowest = list[0];

    list.forEach((s) => {
        total += Number(s.cgpa) || 0;
        if (s.cgpa > highest.cgpa) highest = s;
        if (s.cgpa < lowest.cgpa) lowest = s;
    });

    els.statTotal.textContent = list.length;
    els.statAvg.textContent = (total / list.length).toFixed(2);
    els.statHigh.textContent = Number(highest.cgpa).toFixed(1);
    els.statHighName.textContent = highest.name;
    els.statLow.textContent = Number(lowest.cgpa).toFixed(1);
    els.statLowName.textContent = lowest.name;
}

function renderTable() {
    const rows = getVisibleStudents();

    renderStats(rows);

    els.tbody.replaceChildren();

    // Result count above the table
    if (loadFailed) {
        els.resultCount.textContent = "";
    } else if (rows.length === students.length) {
        els.resultCount.textContent = students.length + (students.length === 1 ? " student" : " students");
    } else {
        els.resultCount.textContent = "Showing " + rows.length + " of " + students.length + " students";
    }

    // Empty states
    if (rows.length === 0) {
        els.table.hidden = true;
        els.emptyState.hidden = false;
        els.emptyAction.hidden = true;

        if (loadFailed) {
            els.emptyTitle.textContent = "Nothing to show yet";
            els.emptyText.textContent = "Students will appear here once the server connection works.";
        } else if (students.length === 0) {
            els.emptyTitle.textContent = "No students yet";
            els.emptyText.textContent = isAdmin()
                ? "Add the first student to start your list."
                : "No students have been added yet.";
            els.emptyAction.hidden = !isAdmin();
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

// Make one <td>. "label" is used by the mobile card layout.
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

    tr.append(idCell, studentCell, ageCell, courseCell, semesterCell, phoneCell, cgpaCell);

    // Edit / Delete buttons are only for admins
    if (isAdmin()) {
        tr.append(actionCell);
    }

    return tr;
}


// ---------------------------------------------------------
// 8. Add / Edit panel
// ---------------------------------------------------------

function clearFormErrors() {
    Object.keys(fields).forEach((key) => {
        const message = $("#err-" + key);
        if (message) message.textContent = "";
        fields[key].removeAttribute("aria-invalid");
    });
}

function openDrawer(student) {
    if (!isAdmin()) return;

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
            headers: authHeaders({ "Content-Type": "application/json" }),
            body: JSON.stringify(student)
        });

        if (response.status === 401) {
            closeDrawer();
            handleSessionExpired();
            els.saveBtn.disabled = false;
            return;
        }

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
        const response = await fetch(STUDENTS_URL + "/" + id, {
            method: "DELETE",
            headers: authHeaders()
        });

        if (response.status === 401) {
            pendingDeleteId = null;
            handleSessionExpired();
            return;
        }

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


// ---------------------------------------------------------
// 9b. Admin login / logout
// ---------------------------------------------------------

function openLogin() {
    els.loginError.textContent = "";
    els.loginPass.value = "";
    els.loginDialog.showModal();
    els.loginUser.focus();
}

async function login(event) {
    event.preventDefault();

    const username = els.loginUser.value.trim();
    const password = els.loginPass.value;

    els.loginError.textContent = "";

    if (!username || !password) {
        els.loginError.textContent = "Enter your username and password.";
        return;
    }

    els.loginSubmit.disabled = true;
    els.loginSubmit.textContent = "Logging in...";

    try {
        const response = await fetch(LOGIN_URL, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username: username, password: password })
        });

        if (response.ok) {
            const data = await response.json();

            saveSession(data.token, data.username);
            els.loginDialog.close();
            els.loginForm.reset();
            updateAuthUI();
            render();
            showToast("Logged in as " + data.username + ".");

        } else if (response.status === 401) {
            els.loginError.textContent = "Wrong username or password.";
            els.loginPass.value = "";
            els.loginPass.focus();

        } else {
            els.loginError.textContent = "Login failed (server error " + response.status + ").";
        }

    } catch (error) {
        console.error("Login error:", error);
        els.loginError.textContent = "Could not reach the server. Is the Spring Boot app running?";
    }

    els.loginSubmit.disabled = false;
    els.loginSubmit.textContent = "Log in";
}

function openPasswordDialog() {
    if (!isAdmin()) return;

    els.passwordForm.reset();
    els.passwordError.textContent = "";
    els.passwordDialog.showModal();
    els.oldPass.focus();
}

async function changePassword(event) {
    event.preventDefault();

    const oldPassword = els.oldPass.value;
    const newPassword = els.newPass.value;
    const confirmPassword = els.confirmPass.value;

    els.passwordError.textContent = "";

    // Check on the page first, so simple mistakes need no server call
    if (!oldPassword || !newPassword || !confirmPassword) {
        els.passwordError.textContent = "Fill in all three fields.";
        return;
    }
    if (newPassword.length < 8) {
        els.passwordError.textContent = "New password must be at least 8 characters.";
        els.newPass.focus();
        return;
    }
    if (newPassword !== confirmPassword) {
        els.passwordError.textContent = "The new passwords do not match.";
        els.confirmPass.focus();
        return;
    }
    if (newPassword === oldPassword) {
        els.passwordError.textContent = "New password must be different from the current one.";
        els.newPass.focus();
        return;
    }

    els.passwordSubmit.disabled = true;
    els.passwordSubmit.textContent = "Saving...";

    try {
        const response = await fetch(CHANGE_PASSWORD_URL, {
            method: "POST",
            headers: authHeaders({ "Content-Type": "application/json" }),
            body: JSON.stringify({ oldPassword: oldPassword, newPassword: newPassword })
        });

        if (response.status === 401) {
            // Login token is no longer valid
            els.passwordDialog.close();
            handleSessionExpired();

        } else if (response.ok) {
            els.passwordDialog.close();
            els.passwordForm.reset();
            showToast("Password changed.");

        } else {
            // 400 = for example "Current password is wrong."
            let message = "Could not change the password (server error " + response.status + ").";
            try {
                const data = await response.json();
                if (data.message) message = data.message;
            } catch (parseError) { /* keep the default message */ }

            els.passwordError.textContent = message;
            els.oldPass.focus();
        }

    } catch (error) {
        console.error("Change password error:", error);
        els.passwordError.textContent = "Could not reach the server. Is the Spring Boot app running?";
    }

    els.passwordSubmit.disabled = false;
    els.passwordSubmit.textContent = "Change password";
}

function logout() {
    clearSession();
    updateAuthUI();
    render();
    showToast("Logged out.");
}


// ---------------------------------------------------------
// 10. Connect buttons and inputs to the functions above
// ---------------------------------------------------------

els.addBtn.addEventListener("click", () => openDrawer(null));
els.loginBtn.addEventListener("click", openLogin);
els.logoutBtn.addEventListener("click", logout);
els.passwordBtn.addEventListener("click", openPasswordDialog);
els.passwordForm.addEventListener("submit", changePassword);
els.passwordCancel.addEventListener("click", () => els.passwordDialog.close());
els.loginForm.addEventListener("submit", login);
els.loginCancel.addEventListener("click", () => els.loginDialog.close());
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


// ---------------------------------------------------------
// 11. Start
// ---------------------------------------------------------

restoreSession();
updateAuthUI();
loadStudents();
loadCourses();
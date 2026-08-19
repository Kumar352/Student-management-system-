-- ============================================================
-- Student Intelligence Platform
-- V1 - Initial Database Schema
-- MySQL 8.0+
-- ============================================================

-- ============================================================
-- 1. ROLES
-- ============================================================

CREATE TABLE roles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255),

    PRIMARY KEY (id),
    CONSTRAINT uk_roles_name UNIQUE (name)
) ENGINE=InnoDB;


-- ============================================================
-- 2. USERS
-- ============================================================

CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    role_id BIGINT NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100),
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    last_login_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT uk_users_email
        UNIQUE (email),

    CONSTRAINT fk_users_role
        FOREIGN KEY (role_id)
        REFERENCES roles(id),

    CONSTRAINT chk_users_status
        CHECK (status IN ('ACTIVE', 'INACTIVE', 'LOCKED', 'PENDING'))
) ENGINE=InnoDB;


-- ============================================================
-- 3. DEPARTMENTS
-- ============================================================

CREATE TABLE departments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    code VARCHAR(20) NOT NULL,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT uk_departments_code
        UNIQUE (code),

    CONSTRAINT uk_departments_name
        UNIQUE (name)
) ENGINE=InnoDB;


-- ============================================================
-- 4. PROGRAMS
-- ============================================================

CREATE TABLE programs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    department_id BIGINT NOT NULL,
    code VARCHAR(30) NOT NULL,
    name VARCHAR(150) NOT NULL,
    degree_type VARCHAR(50) NOT NULL,
    duration_years INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT uk_programs_code
        UNIQUE (code),

    CONSTRAINT fk_programs_department
        FOREIGN KEY (department_id)
        REFERENCES departments(id),

    CONSTRAINT chk_programs_duration
        CHECK (duration_years > 0 AND duration_years <= 10),

    INDEX idx_programs_department (department_id)
) ENGINE=InnoDB;


-- ============================================================
-- 5. SEMESTERS
-- ============================================================

CREATE TABLE semesters (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    term VARCHAR(30) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PLANNED',

    PRIMARY KEY (id),

    CONSTRAINT uk_semesters_period
        UNIQUE (academic_year, term),

    CONSTRAINT chk_semesters_dates
        CHECK (end_date > start_date),

    CONSTRAINT chk_semesters_status
        CHECK (status IN ('PLANNED', 'ACTIVE', 'COMPLETED', 'ARCHIVED'))
) ENGINE=InnoDB;


-- ============================================================
-- 6. STUDENTS
-- ============================================================

CREATE TABLE students (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    student_number VARCHAR(30) NOT NULL,
    program_id BIGINT NOT NULL,
    date_of_birth DATE NULL,
    admission_year INT NOT NULL,
    current_semester INT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT uk_students_user
        UNIQUE (user_id),

    CONSTRAINT uk_students_number
        UNIQUE (student_number),

    CONSTRAINT fk_students_user
        FOREIGN KEY (user_id)
        REFERENCES users(id),

    CONSTRAINT fk_students_program
        FOREIGN KEY (program_id)
        REFERENCES programs(id),

    CONSTRAINT chk_students_semester
        CHECK (current_semester > 0 AND current_semester <= 20),

    CONSTRAINT chk_students_status
        CHECK (status IN ('ACTIVE', 'INACTIVE', 'GRADUATED', 'SUSPENDED', 'WITHDRAWN')),

    INDEX idx_students_program (program_id),
    INDEX idx_students_status (status)
) ENGINE=InnoDB;


-- ============================================================
-- 7. FACULTY
-- ============================================================

CREATE TABLE faculty (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    employee_number VARCHAR(30) NOT NULL,
    department_id BIGINT NOT NULL,
    designation VARCHAR(100) NOT NULL,
    joining_date DATE NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT uk_faculty_user
        UNIQUE (user_id),

    CONSTRAINT uk_faculty_employee
        UNIQUE (employee_number),

    CONSTRAINT fk_faculty_user
        FOREIGN KEY (user_id)
        REFERENCES users(id),

    CONSTRAINT fk_faculty_department
        FOREIGN KEY (department_id)
        REFERENCES departments(id),

    CONSTRAINT chk_faculty_status
        CHECK (status IN ('ACTIVE', 'INACTIVE', 'ON_LEAVE')),

    INDEX idx_faculty_department (department_id)
) ENGINE=InnoDB;


-- ============================================================
-- 8. COURSES
-- ============================================================

CREATE TABLE courses (
    id BIGINT NOT NULL AUTO_INCREMENT,
    department_id BIGINT NOT NULL,
    course_code VARCHAR(30) NOT NULL,
    course_name VARCHAR(150) NOT NULL,
    credits DECIMAL(3,1) NOT NULL,
    semester_number INT NOT NULL,
    course_type VARCHAR(30) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT uk_courses_code
        UNIQUE (course_code),

    CONSTRAINT fk_courses_department
        FOREIGN KEY (department_id)
        REFERENCES departments(id),

    CONSTRAINT chk_courses_credits
        CHECK (credits > 0 AND credits <= 20),

    CONSTRAINT chk_courses_semester
        CHECK (semester_number > 0 AND semester_number <= 20),

    CONSTRAINT chk_courses_type
        CHECK (course_type IN ('CORE', 'ELECTIVE', 'LAB', 'PROJECT', 'OTHER')),

    INDEX idx_courses_department (department_id),
    INDEX idx_courses_semester (semester_number)
) ENGINE=InnoDB;


-- ============================================================
-- 9. ENROLLMENTS
-- ============================================================

CREATE TABLE enrollments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    semester_id BIGINT NOT NULL,
    enrollment_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ENROLLED',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT uk_enrollments_student_course_semester
        UNIQUE (student_id, course_id, semester_id),

    CONSTRAINT fk_enrollments_student
        FOREIGN KEY (student_id)
        REFERENCES students(id),

    CONSTRAINT fk_enrollments_course
        FOREIGN KEY (course_id)
        REFERENCES courses(id),

    CONSTRAINT fk_enrollments_semester
        FOREIGN KEY (semester_id)
        REFERENCES semesters(id),

    CONSTRAINT chk_enrollments_status
        CHECK (status IN ('ENROLLED', 'DROPPED', 'COMPLETED', 'WITHDRAWN')),

    INDEX idx_enrollments_student (student_id),
    INDEX idx_enrollments_course (course_id),
    INDEX idx_enrollments_semester (semester_id)
) ENGINE=InnoDB;


-- ============================================================
-- 10. FACULTY COURSE ASSIGNMENTS
-- ============================================================

CREATE TABLE faculty_course_assignments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    faculty_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    semester_id BIGINT NOT NULL,
    assigned_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT uk_faculty_course_semester
        UNIQUE (faculty_id, course_id, semester_id),

    CONSTRAINT fk_fca_faculty
        FOREIGN KEY (faculty_id)
        REFERENCES faculty(id),

    CONSTRAINT fk_fca_course
        FOREIGN KEY (course_id)
        REFERENCES courses(id),

    CONSTRAINT fk_fca_semester
        FOREIGN KEY (semester_id)
        REFERENCES semesters(id),

    INDEX idx_fca_faculty (faculty_id),
    INDEX idx_fca_course (course_id),
    INDEX idx_fca_semester (semester_id)
) ENGINE=InnoDB;


-- ============================================================
-- 11. ATTENDANCE SESSIONS
-- ============================================================

CREATE TABLE attendance_sessions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    course_id BIGINT NOT NULL,
    semester_id BIGINT NOT NULL,
    session_date DATE NOT NULL,
    session_type VARCHAR(30) NOT NULL DEFAULT 'LECTURE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT fk_attendance_sessions_course
        FOREIGN KEY (course_id)
        REFERENCES courses(id),

    CONSTRAINT fk_attendance_sessions_semester
        FOREIGN KEY (semester_id)
        REFERENCES semesters(id),

    CONSTRAINT chk_attendance_session_type
        CHECK (session_type IN ('LECTURE', 'LAB', 'TUTORIAL', 'OTHER')),

    INDEX idx_attendance_sessions_course (course_id),
    INDEX idx_attendance_sessions_semester (semester_id),
    INDEX idx_attendance_sessions_date (session_date)
) ENGINE=InnoDB;


-- ============================================================
-- 12. ATTENDANCE RECORDS
-- ============================================================

CREATE TABLE attendance_records (
    id BIGINT NOT NULL AUTO_INCREMENT,
    session_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    marked_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT uk_attendance_session_student
        UNIQUE (session_id, student_id),

    CONSTRAINT fk_attendance_records_session
        FOREIGN KEY (session_id)
        REFERENCES attendance_sessions(id),

    CONSTRAINT fk_attendance_records_student
        FOREIGN KEY (student_id)
        REFERENCES students(id),

    CONSTRAINT chk_attendance_status
        CHECK (status IN ('PRESENT', 'ABSENT', 'LATE', 'EXCUSED')),

    INDEX idx_attendance_records_student (student_id),
    INDEX idx_attendance_records_session (session_id)
) ENGINE=InnoDB;


-- ============================================================
-- 13. ASSESSMENTS
-- ============================================================

CREATE TABLE assessments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    course_id BIGINT NOT NULL,
    semester_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    assessment_type VARCHAR(50) NOT NULL,
    max_marks DECIMAL(6,2) NOT NULL,
    weightage DECIMAL(5,2) NOT NULL,
    assessment_date DATE NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT fk_assessments_course
        FOREIGN KEY (course_id)
        REFERENCES courses(id),

    CONSTRAINT fk_assessments_semester
        FOREIGN KEY (semester_id)
        REFERENCES semesters(id),

    CONSTRAINT chk_assessments_max_marks
        CHECK (max_marks > 0),

    CONSTRAINT chk_assessments_weightage
        CHECK (weightage > 0 AND weightage <= 100),

    INDEX idx_assessments_course (course_id),
    INDEX idx_assessments_semester (semester_id),
    INDEX idx_assessments_date (assessment_date)
) ENGINE=InnoDB;


-- ============================================================
-- 14. ASSESSMENT RESULTS
-- ============================================================

CREATE TABLE assessment_results (
    id BIGINT NOT NULL AUTO_INCREMENT,
    assessment_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    marks_obtained DECIMAL(6,2) NOT NULL,
    grade VARCHAR(5),
    graded_at DATETIME NULL,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT uk_assessment_result_student
        UNIQUE (assessment_id, student_id),

    CONSTRAINT fk_assessment_results_assessment
        FOREIGN KEY (assessment_id)
        REFERENCES assessments(id),

    CONSTRAINT fk_assessment_results_student
        FOREIGN KEY (student_id)
        REFERENCES students(id),

    CONSTRAINT chk_assessment_marks
        CHECK (marks_obtained >= 0),

    INDEX idx_assessment_results_student (student_id),
    INDEX idx_assessment_results_assessment (assessment_id)
) ENGINE=InnoDB;


-- ============================================================
-- 15. PERFORMANCE RECORDS
-- ============================================================

CREATE TABLE performance_records (
    id BIGINT NOT NULL AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    semester_id BIGINT NOT NULL,
    gpa DECIMAL(4,2),
    cgpa DECIMAL(4,2),
    credits_attempted DECIMAL(5,2),
    credits_earned DECIMAL(5,2),
    academic_status VARCHAR(30) NOT NULL,
    calculated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT uk_performance_student_semester
        UNIQUE (student_id, semester_id),

    CONSTRAINT fk_performance_student
        FOREIGN KEY (student_id)
        REFERENCES students(id),

    CONSTRAINT fk_performance_semester
        FOREIGN KEY (semester_id)
        REFERENCES semesters(id),

    CONSTRAINT chk_performance_gpa
        CHECK (gpa IS NULL OR (gpa >= 0 AND gpa <= 10)),

    CONSTRAINT chk_performance_cgpa
        CHECK (cgpa IS NULL OR (cgpa >= 0 AND cgpa <= 10)),

    CONSTRAINT chk_performance_credits
        CHECK (
            credits_attempted IS NULL
            OR credits_attempted >= 0
        ),

    CONSTRAINT chk_performance_earned
        CHECK (
            credits_earned IS NULL
            OR credits_earned >= 0
        ),

    INDEX idx_performance_student (student_id),
    INDEX idx_performance_semester (semester_id)
) ENGINE=InnoDB;


-- ============================================================
-- 16. RISK ASSESSMENTS
-- ============================================================

CREATE TABLE risk_assessments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    semester_id BIGINT NOT NULL,
    risk_level VARCHAR(20) NOT NULL,
    risk_score DECIMAL(5,2) NOT NULL,
    attendance_score DECIMAL(5,2),
    academic_score DECIMAL(5,2),
    assessment_score DECIMAL(5,2),
    trend_score DECIMAL(5,2),
    explanation TEXT,
    calculated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT fk_risk_student
        FOREIGN KEY (student_id)
        REFERENCES students(id),

    CONSTRAINT fk_risk_semester
        FOREIGN KEY (semester_id)
        REFERENCES semesters(id),

    CONSTRAINT chk_risk_level
        CHECK (risk_level IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),

    CONSTRAINT chk_risk_score
        CHECK (risk_score >= 0 AND risk_score <= 100),

    CONSTRAINT chk_risk_attendance_score
        CHECK (
            attendance_score IS NULL
            OR (attendance_score >= 0 AND attendance_score <= 100)
        ),

    CONSTRAINT chk_risk_academic_score
        CHECK (
            academic_score IS NULL
            OR (academic_score >= 0 AND academic_score <= 100)
        ),

    CONSTRAINT chk_risk_assessment_score
        CHECK (
            assessment_score IS NULL
            OR (assessment_score >= 0 AND assessment_score <= 100)
        ),

    CONSTRAINT chk_risk_trend_score
        CHECK (
            trend_score IS NULL
            OR (trend_score >= 0 AND trend_score <= 100)
        ),

    INDEX idx_risk_student (student_id),
    INDEX idx_risk_semester (semester_id),
    INDEX idx_risk_level (risk_level),
    INDEX idx_risk_score (risk_score)
) ENGINE=InnoDB;


-- ============================================================
-- 17. RECOMMENDATIONS
-- ============================================================

CREATE TABLE recommendations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    risk_assessment_id BIGINT NOT NULL,
    category VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resolved_at DATETIME NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_recommendations_student
        FOREIGN KEY (student_id)
        REFERENCES students(id),

    CONSTRAINT fk_recommendations_risk
        FOREIGN KEY (risk_assessment_id)
        REFERENCES risk_assessments(id),

    CONSTRAINT chk_recommendations_priority
        CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT')),

    CONSTRAINT chk_recommendations_status
        CHECK (status IN ('OPEN', 'IN_PROGRESS', 'RESOLVED', 'DISMISSED')),

    INDEX idx_recommendations_student (student_id),
    INDEX idx_recommendations_risk (risk_assessment_id),
    INDEX idx_recommendations_status (status)
) ENGINE=InnoDB;


-- ============================================================
-- 18. NOTIFICATIONS
-- ============================================================

CREATE TABLE notifications (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read_at DATETIME NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_notifications_user
        FOREIGN KEY (user_id)
        REFERENCES users(id),

    CONSTRAINT chk_notifications_priority
        CHECK (priority IN ('LOW', 'NORMAL', 'HIGH', 'URGENT')),

    INDEX idx_notifications_user (user_id),
    INDEX idx_notifications_read (user_id, is_read),
    INDEX idx_notifications_created (created_at)
) ENGINE=InnoDB;


-- ============================================================
-- 19. AUDIT LOGS
-- ============================================================

CREATE TABLE audit_logs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id BIGINT NULL,
    old_value JSON NULL,
    new_value JSON NULL,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    CONSTRAINT fk_audit_logs_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE SET NULL,

    INDEX idx_audit_logs_user (user_id),
    INDEX idx_audit_logs_entity (entity_type, entity_id),
    INDEX idx_audit_logs_created (created_at),
    INDEX idx_audit_logs_action (action)
) ENGINE=InnoDB;


-- ============================================================
-- INITIAL ROLE DATA
-- ============================================================

INSERT INTO roles (name, description)
VALUES
    ('STUDENT', 'Student user'),
    ('FACULTY', 'Faculty user'),
    ('ADMIN', 'System administrator');
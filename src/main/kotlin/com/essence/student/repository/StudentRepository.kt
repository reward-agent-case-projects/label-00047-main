package com.essence.student.repository

import com.essence.student.entity.Student
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StudentRepository : JpaRepository<Student, Long> {
    fun findByStudentNumber(studentNumber: String): Student?
    fun existsByStudentNumber(studentNumber: String): Boolean
}

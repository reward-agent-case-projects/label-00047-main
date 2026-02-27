package com.essence.student.service

import com.essence.student.entity.Score
import com.essence.student.entity.Student
import com.essence.student.repository.ScoreRepository
import com.essence.student.repository.StudentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode

data class StudentReportDTO(
    val studentNumber: String,
    val name: String,
    val className: String,
    val scores: Map<String, BigDecimal>,
    val averageScore: BigDecimal,
    val totalScore: BigDecimal
)

@Service
class StudentService(
    private val studentRepository: StudentRepository,
    private val scoreRepository: ScoreRepository
) {

    @Transactional
    fun addStudent(studentNumber: String, name: String, className: String): Student {
        if (studentRepository.existsByStudentNumber(studentNumber)) {
            throw IllegalArgumentException("Student number $studentNumber already exists.")
        }
        val student = Student(studentNumber = studentNumber, name = name, className = className)
        return studentRepository.save(student)
    }

    @Transactional
    fun addScore(studentNumber: String, subject: String, scoreValue: BigDecimal): Score {
        val student = studentRepository.findByStudentNumber(studentNumber)
            ?: throw IllegalArgumentException("Student not found with number: $studentNumber")
        
        // Check if score for subject already exists, update if needed or create new
        // For simplicity, we'll just add a new record. Real world might want to update.
        // Or we can check if subject exists for this student.
        
        val newScore = Score(student = student, subjectName = subject, scoreValue = scoreValue)
        student.scores.add(newScore) 
        // We save the score explicitly or let cascade handle it. 
        // Since we have CascadeType.ALL on Student, saving student would work, 
        // but saving score directly is also fine.
        return scoreRepository.save(newScore)
    }
    
    @Transactional(readOnly = true)
    fun getAllStudents(): List<Student> {
        return studentRepository.findAll()
    }

    @Transactional(readOnly = true)
    fun getRankedReport(): List<StudentReportDTO> {
        val students = studentRepository.findAll()
        
        val report = students.map { student ->
            val scoresMap = student.scores.associate { it.subjectName to it.scoreValue }
            val totalScore = student.scores.map { it.scoreValue }.fold(BigDecimal.ZERO, BigDecimal::add)
            val avgScore = if (student.scores.isNotEmpty()) {
                totalScore.divide(BigDecimal(student.scores.size), 2, RoundingMode.HALF_UP)
            } else {
                BigDecimal.ZERO
            }
            
            StudentReportDTO(
                studentNumber = student.studentNumber,
                name = student.name,
                className = student.className,
                scores = scoresMap,
                averageScore = avgScore,
                totalScore = totalScore
            )
        }
        
        // Sort by Average Score Descending
        return report.sortedByDescending { it.averageScore }
    }
}

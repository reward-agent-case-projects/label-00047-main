package com.essence.student.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "scores")
class Score(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    var student: Student,

    @Column(name = "subject_name", nullable = false)
    var subjectName: String,

    @Column(name = "score_value", nullable = false)
    var scoreValue: BigDecimal,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
)

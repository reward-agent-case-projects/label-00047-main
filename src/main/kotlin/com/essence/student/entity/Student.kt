package com.essence.student.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "students")
class Student(
    @Column(nullable = false, unique = true)
    var studentNumber: String,

    @Column(nullable = false)
    var name: String,

    @Column(name = "class_name", nullable = false)
    var className: String = "",

    @OneToMany(mappedBy = "student", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var scores: MutableList<Score> = mutableListOf(),

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
)

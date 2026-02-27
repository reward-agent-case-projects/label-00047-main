package com.essence.student.ui

import com.essence.student.service.StudentService
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.Scanner

@Component
class ConsoleUI(private val studentService: StudentService) : CommandLineRunner {

    private val scanner = Scanner(System.`in`)

    override fun run(vararg args: String?) {
        println("欢迎使用 Essence 学生成绩管理系统")
        while (true) {
            printMenu()
            // 使用 nextLine() 替代 next()，这样用户只按回车时也能刷新菜单
            val line = try {
                scanner.nextLine().trim()
            } catch (e: Exception) {
                // 防止某些环境下流关闭导致的死循环
                break
            }

            if (line.isEmpty()) {
                continue
            }

            try {
                when (line) {
                    "1" -> handleAddStudent()
                    "2" -> handleRecordScore()
                    "3" -> handleShowRanking()
                    "4" -> {
                        println("正在退出系统。再见！")
                        return
                    }
                    else -> println("无效选项，请重试。")
                }
            } catch (e: Exception) {
                println("错误: ${e.message}")
            }
            println("\n按回车键继续...")
            // 这里不需要额外的 scanner.nextLine() 了，因为上面已经是按行读取
            if (scanner.hasNextLine()) {
                scanner.nextLine()
            }
        }
    }

    private fun printMenu() {
        println("\n==========================================")
        println("          学生成绩管理系统       ")
        println("==========================================")
        println(" 1. 录入学生信息")
        println(" 2. 录入学生成绩")
        println(" 3. 查看排名报告")
        println(" 4. 退出")
        println("==========================================")
        print("请输入您的选择: ")
    }

    private fun handleAddStudent() {
        println("\n[录入学生]")
        print("请输入学号: ")
        val number = scanner.nextLine().trim()
        
        print("请输入姓名: ")
        val name = scanner.nextLine().trim()
        
        print("请输入班级: ")
        val className = scanner.nextLine().trim()

        if (number.isEmpty() || name.isEmpty() || className.isEmpty()) {
            println("❌ 添加失败: 学号、姓名、班级信息均不能为空。")
            return
        }

        try {
            studentService.addStudent(number, name, className)
            println("✅ 学生添加成功。")
        } catch (e: Exception) {
            println("❌ 添加学生失败: ${e.message}")
        }
    }

    private fun handleRecordScore() {
        println("\n[录入成绩]")
        print("请输入学号: ")
        val number = scanner.nextLine().trim()
        
        print("请输入科目名称: ")
        val subject = scanner.nextLine().trim()
        
        print("请输入成绩 (0-100): ")
        val scoreStr = scanner.nextLine().trim()
        
        try {
            val scoreVal = BigDecimal(scoreStr)
            if (scoreVal < BigDecimal.ZERO || scoreVal > BigDecimal(100)) {
                println("成绩必须在 0 到 100 之间。")
                return
            }
            studentService.addScore(number, subject, scoreVal)
            println("✅ 成绩录入成功。")
        } catch (e: NumberFormatException) {
            println("❌ 无效的成绩格式。")
        } catch (e: Exception) {
            println("❌ 录入成绩失败: ${e.message}")
        }
    }

    private fun handleShowRanking() {
        println("\n[学生排名报告]")
        val report = studentService.getRankedReport()
        
        if (report.isEmpty()) {
            println("未找到学生记录。")
            return
        }

        // 定义表头
        val headers = listOf("排名", "学号", "姓名", "班级", "平均分", "总分")
        
        // 计算每列最大宽度
        val colWidths = IntArray(headers.size) { 0 }
        
        // 1. 初始化表头宽度
        headers.forEachIndexed { i, h -> colWidths[i] = getDisplayWidth(h) }
        
        // 2. 遍历数据更新最大宽度
        report.forEachIndexed { index, dto ->
            colWidths[0] = maxOf(colWidths[0], getDisplayWidth((index + 1).toString()))
            colWidths[1] = maxOf(colWidths[1], getDisplayWidth(dto.studentNumber))
            colWidths[2] = maxOf(colWidths[2], getDisplayWidth(dto.name))
            colWidths[3] = maxOf(colWidths[3], getDisplayWidth(dto.className))
            colWidths[4] = maxOf(colWidths[4], getDisplayWidth(dto.averageScore.toString()))
            colWidths[5] = maxOf(colWidths[5], getDisplayWidth(dto.totalScore.toString()))
        }
        
        // 3. 增加一些额外的内边距 (左右各加1空格，共+2)
        for (i in colWidths.indices) colWidths[i] += 2 

        // 打印表格
        printSeparator(colWidths)
        printRow(headers, colWidths)
        printSeparator(colWidths)
        
        report.forEachIndexed { index, dto ->
            val row = listOf(
                (index + 1).toString(),
                dto.studentNumber,
                dto.name,
                dto.className,
                dto.averageScore.toString(),
                dto.totalScore.toString()
            )
            printRow(row, colWidths)
        }
        printSeparator(colWidths)
    }

    private fun printSeparator(widths: IntArray) {
        print("+")
        widths.forEach { w ->
            print("-".repeat(w) + "+")
        }
        println()
    }
    
    private fun printRow(columns: List<String>, widths: IntArray) {
        print("|")
        columns.forEachIndexed { i, col ->
            val contentWidth = getDisplayWidth(col)
            val totalPad = widths[i] - contentWidth
            // 左对齐：左边1个空格，右边补足剩余
            val rightPad = totalPad - 1
            print(" " + col + " ".repeat(maxOf(0, rightPad)) + "|")
        }
        println()
    }

    private fun getDisplayWidth(s: String): Int {
        var len = 0
        for (c in s) {
            // 简单的判断：ASCII算1，其他（主要是中文）算2
            // 这是一个近似值，但在大多数等宽字体的终端中对中文有效
            if (c.code > 127) {
                 len += 2
            } else {
                 len += 1
            }
        }
        return len
    }

    private fun maxOf(a: Int, b: Int): Int {
        return if (a >= b) a else b
    }
}

package com.example.calculadora2


import com.example.calculadora2.R
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvDisplay: TextView
    private var firstOperand: Double? = null
    private var pendingOperation: String? = null
    private var isNewOperation: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDisplay = findViewById(R.id.tvDisplay)

        setupNumberButtons()
        setupOperationButtons()
    }

    private fun setupNumberButtons() {
        val numberIds = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot
        )

        for (id in numberIds) {
            findViewById<Button>(id).setOnClickListener { btn ->
                val text = (btn as Button).text.toString()
                if (isNewOperation) {
                    tvDisplay.text = if (text == ".") "0." else text
                    isNewOperation = false
                } else {
                    if (text == "." && tvDisplay.text.contains(".")) return@setOnClickListener
                    tvDisplay.append(text)
                }
            }
        }
    }

    private fun setupOperationButtons() {
        val operations = mapOf(
            R.id.btnAdd to "+",
            R.id.btnSub to "-",
            R.id.btnMult to "*",
            R.id.btnDiv to "/"
        )

        for ((id, op) in operations) {
            findViewById<Button>(id).setOnClickListener {
                if (firstOperand != null && !isNewOperation) {
                    calculateResult()
                }
                firstOperand = tvDisplay.text.toString().toDoubleOrNull()
                pendingOperation = op
                isNewOperation = true
            }
        }

        findViewById<Button>(R.id.btnEquals).setOnClickListener {
            calculateResult()
        }

        findViewById<Button>(R.id.btnClear).setOnClickListener {
            tvDisplay.text = "0"
            firstOperand = null
            pendingOperation = null
            isNewOperation = true
        }
    }

    private fun calculateResult() {
        val secondOperand = tvDisplay.text.toString().toDoubleOrNull() ?: return
        if (firstOperand == null || pendingOperation == null) return

        val op1 = firstOperand!!
        val op2 = secondOperand

        val result = when (pendingOperation) {
            "+" -> op1 + op2
            "-" -> op1 - op2
            "*" -> op1 * op2
            "/" -> if (op2 != 0.0) op1 / op2 else Double.NaN
            else -> 0.0
        }

        if (result.isNaN()) {
            tvDisplay.text = "Error"
        } else {
            tvDisplay.text = if (result % 1 == 0.0) result.toLong().toString() else result.toString()
        }

        firstOperand = if (result.isNaN()) null else tvDisplay.text.toString().toDoubleOrNull()
        pendingOperation = null
        isNewOperation = true
    }}
package com.example.calculadora

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    var total = ""
    var numero1 = 0
    var numero2 = 0
    var operador = "+"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun button0(view: View) {
        var textoRecebido = findViewById<TextView>(R.id.textView)
        total = "$total" + "0"
        textoRecebido.setText("$total")
    }

    fun button1(view: View) {
        var textoRecebido = findViewById<TextView>(R.id.textView)
        total = "$total" + "1"
        textoRecebido.setText("$total")
    }

    fun button2(view: View) {
        var textoRecebido = findViewById<TextView>(R.id.textView)
        total = "$total" + "2"
        textoRecebido.setText("$total")
    }

    fun button3(view: View) {
        var textoRecebido = findViewById<TextView>(R.id.textView)
        total = "$total" + "3"
        textoRecebido.setText("$total")
    }

    fun button4(view: View) {
        var textoRecebido = findViewById<TextView>(R.id.textView)
        total = "$total" + "4"
        textoRecebido.setText("$total")
    }

    fun button5(view: View) {
        var textoRecebido = findViewById<TextView>(R.id.textView)
        total = "$total" + "5"
        textoRecebido.setText("$total")
    }

    fun button6(view: View) {
        var textoRecebido = findViewById<TextView>(R.id.textView)
        total = "$total" + "6"
        textoRecebido.setText("$total")
    }

    fun button7(view: View) {
        var textoRecebido = findViewById<TextView>(R.id.textView)
        total = "$total" + "7"
        textoRecebido.setText("$total")
    }

    fun button8(view: View) {
        var textoRecebido = findViewById<TextView>(R.id.textView)
        total = "$total" + "8"
        textoRecebido.setText("$total")
    }

    fun button9(view: View) {
        var textoRecebido = findViewById<TextView>(R.id.textView)
        total = "$total" + "9"
        textoRecebido.setText("$total")
    }

    fun buttonsoma(view: View) {
        operador = "+"
        definirOperacao()
    }

    fun buttonsubtracao(view: View) {
        operador = "-"
        definirOperacao()
    }

    fun buttonmultiplicacao(view: View) {
        operador = "*"
        definirOperacao()
    }

    fun buttondivisao(view: View) {
        operador = "/"
        definirOperacao()
    }

    fun definirOperacao() {
        var textoRecebido = findViewById<TextView>(R.id.textView)

        if (total.isNotEmpty()) {
            numero1 = total.toInt()
            total = ""
            textoRecebido.text = ""
        }
    }

    fun buttonresult(view: View) {
        var textoRecebido = findViewById<TextView>(R.id.textView)
        numero2 = total.toInt()
        var resultado = 0

        if (operador == "+") {
            resultado = numero1 + numero2
        } else if (operador == "-") {
            resultado = numero1 - numero2
        } else if (operador == "*") {
            resultado = numero1 * numero2
        } else if (operador == "/") {
            if (numero2 != 0) {
                resultado = numero1 / numero2
            } else {
                textoRecebido.setText("Erro!")
                return
            }
        }

        textoRecebido.setText(resultado.toString())
        total = resultado.toString()
    }

    fun buttoncancel(view: View) {
        var textoRecebido = findViewById<TextView>(R.id.textView)

        if (total.isNotEmpty()) {
            total = total.dropLast(1)
            textoRecebido.text = total
        }
    }

    fun buttoncancelentry(view: View) {
        var textoRecebido = findViewById<TextView>(R.id.textView)
        numero1 = 0
        numero2 = 0
        total = ""
        operador = "+"
        textoRecebido.text = ""
    }
}

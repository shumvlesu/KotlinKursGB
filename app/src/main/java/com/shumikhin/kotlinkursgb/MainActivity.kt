package com.shumikhin.kotlinkursgb

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.shumikhin.kotlinkursgb.data.HumanDate

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val oleg1 = HumanDate("Олег", 33)
        val buttonA: MaterialButton = findViewById(R.id.button_a)
        val buttonB: MaterialButton = findViewById(R.id.button_b)
        val textView: TextView = findViewById(R.id.textView)
        val textView2: TextView = findViewById(R.id.textView2)

        buttonA.setOnClickListener {
            val oleg2 = oleg1.copy()
            Toast.makeText(this, String.format("%s где макет?", oleg2.name), Toast.LENGTH_LONG)
                .show()
        }

        buttonB.setOnClickListener {
            val listNameHuman = arrayListOf<String>(*resources.getStringArray(R.array.names))
            val listH: MutableList<HumanDate> = ArrayList()
            val str = StringBuilder()

            for (humanName in listNameHuman) {
                listH.add(HumanDate(humanName, (20..100).random()))
                str.append(listH[listNameHuman.indexOf(humanName)].toString() + "\n")

            }

            textView.text = str

            str.clear()
            for (i in 0..listH.size step 2) {
                str.append(listH[i].toString() + "\n")
            }

            textView2.text = str

        }

    }
}
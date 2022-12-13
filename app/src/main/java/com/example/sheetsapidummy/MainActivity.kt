package com.example.sheetsapidummy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    lateinit var listOfPersons: ArrayList<Person>
    lateinit var rvMain: RecyclerView
    lateinit var adapter: RVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listOfPersons = arrayListOf()
        getData()

        rvMain = findViewById(R.id.rvMain)
        setupRV()
    }

    private fun getData() {
        initDummyData() // for testing only
        // TODO: get list of persons from sheets
    }

    private fun initDummyData() {
        for (age in 12 until 16) {
            val person = Person("Name $age", age)
            listOfPersons.add(person)
        }
    }

    private fun setupRV() {
        adapter = RVAdapter(listOfPersons)
        rvMain.adapter = adapter
    }
}
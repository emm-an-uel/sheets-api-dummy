package com.example.sheetsapidummy

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.api.client.googleapis.json.GoogleJsonError
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import java.io.InputStream
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var listOfPersons: ArrayList<Person>
    lateinit var rvMain: RecyclerView
    lateinit var adapter: RVAdapter
    lateinit var btnSync: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listOfPersons = arrayListOf()
        getData()

        rvMain = findViewById(R.id.rvMain)
        setupRV()

        btnSync = findViewById(R.id.btnSync)
        btnSync.setOnClickListener {
            // get data from google sheets
            val spreadsheetId = "1uKXpevSHQ_qmUrgehFdMXiaPRC2hkMITUv4gAXjujiY"
            val range = "A2:B6"
            getDataFromSheets(spreadsheetId, range)
        }
    }

    private fun getData() {
        initDummyData() // for testing only
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

    private fun getDataFromSheets(spreadsheetId: String, range: String) {
        val APPLICATION_NAME: String = "Sheets API Dummy"
        val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()
        val TOKENS_DIRECTORY_PATH: String = "tokens"
        val CREDENTIALS_FILE_PATH: String = "credentials.json"
        val GOOGLE_APPLICATION_CREDENTIALS: String = "application_default_credentials.json"

        val stream: InputStream = resources.assets.open(GOOGLE_APPLICATION_CREDENTIALS)

        val credentials: GoogleCredentials = GoogleCredentials.fromStream(stream)
            .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS))
        val requestInitializer: HttpRequestInitializer = HttpCredentialsAdapter(credentials)

        // create sheets api client
        val service: Sheets = Sheets.Builder(NetHttpTransport(),
        GsonFactory.getDefaultInstance(),
        requestInitializer)
            .setApplicationName(APPLICATION_NAME)
            .build()

        var result: ValueRange? = null
        try {
            // gets values of cells in the specified range
            result = service.spreadsheets().values().get(spreadsheetId, range).execute()
        } catch (e: GoogleJsonResponseException) {
            val error: GoogleJsonError = e.details
            if (error.code == 404) {
                Toast.makeText(this, "Spreadsheet not found with id: $spreadsheetId", Toast.LENGTH_SHORT).show()
            }
        }

        if (result == null) {
            Toast.makeText(this, "Sheets returned empty list", Toast.LENGTH_SHORT).show()
        } else {
            val values: List<List<Any>> = result.getValues()
            for (row: List<Any> in values) {
                val name: String = row[0] as String
                val age: Int = row[1] as Int
                val person = Person(name, age)
                listOfPersons.add(person)
            }
            adapter.notifyDataSetChanged()
        }
    }
}
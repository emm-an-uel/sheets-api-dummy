package com.example.sheetsapidummy

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import java.io.InputStreamReader
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
        val SCOPES: List<String> = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY)

        fun getCredentials(HTTP_TRANSPORT: NetHttpTransport): Credential {
            // load client secrets
            val `in` = this.assets.open(CREDENTIALS_FILE_PATH) // note that I saved the client secrets json file in the 'assets' folder of this project

            val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(`in`))

            // build flow and trigger user authorization request
            val flow: GoogleAuthorizationCodeFlow = GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(FileDataStoreFactory(java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build()

            val receiver: LocalServerReceiver = LocalServerReceiver.Builder().setPort(8888).build()
            return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
        }

        // build new authorized api client service
        val HTTP_TRANSPORT: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val service: Sheets = Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
            .setApplicationName(APPLICATION_NAME)
            .build()
        val response: ValueRange = service.spreadsheets().values()
            .get(spreadsheetId, range)
            .execute()
        val values: List<List<Any>> = response.getValues()
        if (values.isEmpty()) {
            Toast.makeText(this, "Sheets returned empty list", Toast.LENGTH_SHORT).show()
        } else {
            for (row: List<Any> in values) {
                val name = row[0] as String
                val age = row[1] as Int
                val person = Person(name, age)
                listOfPersons.add(person)
            }
            adapter.notifyDataSetChanged()
        }
    }
}
package com.example.discoveryapp

import android.content.Context
import android.icu.text.IDNA.Info
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import com.github.kittinunf.fuel.core.extensions.jsonBody


import java.net.URL
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {
    lateinit var userAdapter: UserAdapter
    lateinit var userList:List<User>
    lateinit var btnAdd: Button
    lateinit var id: EditText
    lateinit var amount: EditText
    lateinit var pracID: EditText
    private val CodeDelay: Long = 7000
    private val Delay: Long = 5000
    lateinit var message:List<Message>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bar: ProgressBar= findViewById(R.id.progressBar)

        bar.visibility = View.VISIBLE


        InfoGetter()
        userAdapter = UserAdapter()


        Handler().postDelayed({ Feed()} , CodeDelay )



        val add: Button = findViewById(R.id.btnAdd)


        add.setOnClickListener(){
            val popup = PopupWindow(this)
            popup.setFocusable(true);
            popup.update();
            val view = layoutInflater.inflate(R.layout.add_layout, null)
            popup.contentView = view

             btnAdd = view.findViewById<Button>(R.id.btnAddSub)
             id = view.findViewById<EditText>(R.id.txtID)
             pracID = view.findViewById<EditText>(R.id.txtPracID)
             amount =   view.findViewById<EditText>(R.id.txtAmount)

            btnAdd.setOnClickListener(){

                if (id.text.toString().isEmpty() || amount.text.toString().isEmpty() || pracID.text.toString().isEmpty()) {
                    Toast.makeText(
                        this@MainActivity,
                        "Please enter all the values",
                        Toast.LENGTH_SHORT
                    ).show();
                    return@setOnClickListener
                }
                // calling a method to post the data and passing our name and job.
                Post()

                Handler().postDelayed({Toast.makeText(
                    this@MainActivity,
                    message[0].message,
                    Toast.LENGTH_SHORT
                ).show();
                    popup.dismiss()} , Delay )


            }
            popup.showAtLocation( view, Gravity.CENTER , 0 , 0)

        }
    }

    private fun Feed(){
        val feed: RecyclerView = findViewById(R.id.feed)
        val bar: ProgressBar= findViewById(R.id.progressBar)

        feed.apply {  layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = userAdapter
        }
        Handler(Looper.getMainLooper()).post {
            userAdapter.submitList(userList)
        }
        bar.visibility = View.INVISIBLE
    }

    private fun InfoGetter() {
        val executor =  Executors.newSingleThreadExecutor()

        executor.execute {
            val url = URL("https://opsc.azurewebsites.net/Dis.php")
            val json = url.readText()
            userList = Gson().fromJson(json, Array<User>::class.java).toList()

        }
    }

    private fun Post(){
        val executor =  Executors.newSingleThreadExecutor()

        executor.execute {
            val user = UserInput(id.text.toString().toInt() , amount.text.toString().toInt() , pracID.text.toString())
            val (_, _, result) =  "https://opsc.azurewebsites.net/Add.php".httpPost()
                .jsonBody(Gson().toJson(user).toString())
                .responseString()
            val Json = "[" + result.component1() + "]"
            message = Gson().fromJson(Json, Array<Message>::class.java).toList()
            Handler(Looper.getMainLooper()).post{

                Log.d("Test" , result.toString())
            }
        }
    }

    //return message to a object.

}
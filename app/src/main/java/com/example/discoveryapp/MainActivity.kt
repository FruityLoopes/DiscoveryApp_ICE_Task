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
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    lateinit var userAdapter: UserAdapter
    lateinit var userList:List<User>
    lateinit var btnAdd: Button
    lateinit var id: EditText
    lateinit var amount: EditText
    lateinit var pracID: EditText
    private val CodeDelay: Long = 2000
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
                postData(id.text.toString().toInt(), amount.text.toString().toInt(), pracID.text.toString());

                popup.dismiss()

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

    private fun postData(id: Int , amount: Int , pracID:String){
        val retrofit = Retrofit.Builder()
            .baseUrl("https://opsc.azurewebsites.net/Add.php/")
            // as we are sending data in json format so
            // we have to add Gson converter factory
            .addConverterFactory(GsonConverterFactory.create())
            // at last we are building our retrofit builder.
            .build()
        // below line is to create an instance for our retrofit api class.
        val retrofitAPI = retrofit.create(RetrofitAPI::class.java)

        // passing data from our text fields to our modal class.
        val userInput: UserInput = UserInput(id, amount, pracID)

        // calling a method to create a post and passing our modal class.
        val call: Call<UserInput?>? = retrofitAPI.postData(userInput)

        // on below line we are executing our method.
        call!!.enqueue(object : Callback<UserInput?> {
            override fun onResponse(call: Call<UserInput?>?, response: Response<UserInput?>) {
                // this method is called when we get response from our api.
                Toast.makeText(this@MainActivity, "User Data as been added", Toast.LENGTH_SHORT).show()





                // we are getting response from our body
                // and passing it to our modal class.
                val response: UserInput? = response.body()

              Log.d("Test" , response.toString())
            }

            override fun onFailure(call: Call<UserInput?>?, t: Throwable) {
                // setting text to our text view when
                // we get error response from API.
                Log.d("Error found is : " , t.message.toString())
            }
        })
    }
}
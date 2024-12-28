package com.example.logindashboardapp.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.logindashboardapp.R
import com.example.logindashboardapp.ui.login.LoginActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class DashboardActivity : AppCompatActivity() {

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val nameTextView = findViewById<TextView>(R.id.nameTextView)
        val ageTextView = findViewById<TextView>(R.id.ageTextView)
        val idTextView = findViewById<TextView>(R.id.idTextView)
        val genderTextView = findViewById<TextView>(R.id.genderTextView)
        val profileImageView = findViewById<ImageView>(R.id.profileImageView)
        val logoutButton = findViewById<Button>(R.id.logoutButton)

        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val name = sharedPreferences.getString("name", "N/A")
        val lastName = sharedPreferences.getString("lastName", "N/A")
        val age = sharedPreferences.getInt("age", 0)
        val id = sharedPreferences.getString("id", "N/A")
        val gender = sharedPreferences.getString("gender", "N/A")

        nameTextView.text = "Name: $name $lastName"
        ageTextView.text = "Age: $age"
        idTextView.text = "ID: $id"
        genderTextView.text = "Gender: $gender"

        fetchRandomDogImage(profileImageView)

        logoutButton.setOnClickListener {
            sharedPreferences.edit().clear().apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun fetchRandomDogImage(imageView: ImageView) {
        val request = Request.Builder()
            .url("https://dog.ceo/api/breeds/image/random")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    showErrorDialog("No se pudo cargar la foto de perfil")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let {
                    val jsonObject = JSONObject(it)
                    val imageUrl = jsonObject.getString("message")
                    runOnUiThread {
                        Glide.with(this@DashboardActivity).load(imageUrl).into(imageView)
                    }
                }
            }
        })
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
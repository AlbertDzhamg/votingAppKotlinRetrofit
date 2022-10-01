package com.example.ktor_klient

import android.content.Intent
import android.os.Bundle
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.ktor_klient.adapters.UserItemViewAdapter
import com.example.ktor_klient.databinding.ActivityMainBinding
import com.example.ktor_klient.models.User
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import kotlinx.coroutines.MainScope


class MainActivity : AppCompatActivity() {
    private val mainScope = MainScope()

    private lateinit var mAdapter: UserItemViewAdapter
    private lateinit var b: ActivityMainBinding
    private val usersItemsList: MutableList<User> = mutableListOf()
    var recyclerView: RecyclerView? = null
    var constraintLayout: RelativeLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.ChoiceId.setOnClickListener {
            val intent = Intent(this, ChoiceActivity::class.java)
            startActivity(intent)
        }
        b.VoteId.setOnClickListener {
            val intent = Intent(this, VoteActivity::class.java)
            startActivity(intent)
        }
        b.QuestionId.setOnClickListener {
            val intent = Intent(this, QuestionActivity::class.java)
            startActivity(intent)
        }
        b.UserId.setOnClickListener {
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
        }

    }
}
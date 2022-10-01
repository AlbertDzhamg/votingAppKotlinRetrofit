package com.example.ktor_klient

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ktor_klient.adapters.UserItemViewAdapter
import com.example.ktor_klient.api.ApiFactory
import com.example.ktor_klient.api.resources.Users
import com.example.ktor_klient.components.SwipeActionsCallback
import com.example.ktor_klient.databinding.ActivityMainBinding
import com.example.ktor_klient.databinding.UserListBinding
import com.example.ktor_klient.models.User
import com.example.ktor_klient.models.UserRequest
import com.google.android.material.snackbar.Snackbar
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class UserActivity : AppCompatActivity() {
    private val mainScope = MainScope()

    private lateinit var mAdapter: UserItemViewAdapter
    private lateinit var b: UserListBinding
    private val usersItemsList: MutableList<User> = mutableListOf()
    var recyclerView: RecyclerView? = null
    var constraintLayout: RelativeLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = UserListBinding.inflate(layoutInflater)
        setContentView(b.root)

        mainScope.launch {
            fetchUsers()
        }
        setUpAdapter()
        enableSwipeToDeleteAndUndo()

        b.floatingActionButton.setOnClickListener {
            addUser()
        }
    }

    private fun setUpAdapter() {
        recyclerView = findViewById(R.id.UsersList)
        constraintLayout = findViewById(R.id.constraintLayout)
        mAdapter = UserItemViewAdapter(this, usersItemsList)
        b.UsersList.adapter = mAdapter
        b.UsersList.layoutManager = LinearLayoutManager(this)
    }

    private suspend fun fetchUsers() {
        val api = ApiFactory.getClient()
        kotlin.runCatching {
            api.get(Users()).body<List<User>>()
        }.onSuccess {
            usersItemsList.addAll(it)
            mAdapter.notifyDataSetChanged()
        }.onFailure {
            Log.e("REQUEST", it.toString())
            Toast.makeText(this, "Ошибка сервера!", Toast.LENGTH_LONG).show()
        }
    }

    private fun enableSwipeToDeleteAndUndo() {
        val swipeActionsCallback: SwipeActionsCallback = object : SwipeActionsCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val item = mAdapter.getData()[position]

                if (direction == ItemTouchHelper.LEFT) {
                    mainScope.launch {
                        kotlin.runCatching {
                            mAdapter.removeItem(item.Id, position)
                        }.onSuccess {
                            val restoreAction = Snackbar
                                .make(
                                    constraintLayout!!,
                                    "Элемент удален.",
                                    Snackbar.LENGTH_LONG
                                )
                            restoreAction.setAction("ОТМЕНИТЬ") {
                                mainScope.launch {
                                    mAdapter.restoreItem(item, position)
                                    recyclerView!!.scrollToPosition(position)
                                }
                            }
                            restoreAction.setActionTextColor(Color.YELLOW)
                            restoreAction.show()
                        }
                    }
                }
                else if (direction == ItemTouchHelper.RIGHT) {
                    val dialogBuilder = AlertDialog.Builder(this@UserActivity)
                    val dialogLayout = LinearLayout(
                        this@UserActivity
                    )
                    val lp = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )

                    dialogLayout.layoutParams = lp
                    dialogLayout.orientation = LinearLayout.VERTICAL

                    val FirstNameInput = EditText(dialogLayout.context)
                    FirstNameInput.layoutParams=lp
                    FirstNameInput.hint="Имя"
                    FirstNameInput.setPadding(25)

                    val LastNameInput = EditText(dialogLayout.context)
                    LastNameInput.layoutParams=lp
                    LastNameInput.hint="Фамилия"
                    LastNameInput.setPadding(25)

                    val EmailInput = EditText(dialogLayout.context)
                    EmailInput.layoutParams=lp
                    EmailInput.hint="Email"
                    EmailInput.setPadding(25)

                    val PhoneInput = EditText(dialogLayout.context)
                    PhoneInput.layoutParams=lp
                    PhoneInput.hint="Телефон"
                    PhoneInput.setPadding(25)

                    val StatusInput = CheckBox(dialogLayout.context)
                    StatusInput.layoutParams=lp
                    StatusInput.hint="Статус голосующего"
                    StatusInput.setPadding(25)

                    dialogLayout.addView(FirstNameInput)
                    dialogLayout.addView(LastNameInput)
                    dialogLayout.addView(EmailInput)
                    dialogLayout.addView(PhoneInput)
                    dialogLayout.addView(StatusInput)


                    dialogBuilder.setView(dialogLayout)

                    FirstNameInput.text = Editable.Factory.getInstance().newEditable(item.FirstName)
                    LastNameInput.text = Editable.Factory.getInstance().newEditable(item.LastName)
                    EmailInput.text = Editable.Factory.getInstance().newEditable(item.Email)
                    PhoneInput.text = Editable.Factory.getInstance().newEditable(item.Phone)
                    StatusInput.text = Editable.Factory.getInstance().newEditable("Согласие")
                    StatusInput.isChecked = item.Status

                    dialogBuilder.setTitle("Редактировать пользователя: ${item.FirstName} ${item.LastName}")

                    dialogBuilder.setPositiveButton("Сохранить") { dialog, _ ->
                        mainScope.launch {
                            mAdapter.editItem(
                                User(
                                    Id = item.Id,
                                    FirstName = FirstNameInput.text.toString(),
                                    LastName = LastNameInput.text.toString(),
                                    Email = EmailInput.text.toString(),
                                    Phone = PhoneInput.text.toString(),
                                    Status = StatusInput.isChecked
                                ),
                                position
                            )
                        }
                        dialog.cancel()
                    }
                    dialogBuilder.setNegativeButton("Отменить") { dialog, _ -> dialog.cancel() }
                    val dialog = dialogBuilder.create()
                    dialog.show()
                }
                recyclerView?.adapter?.notifyItemChanged(position)
            }

        }

        val itemTouchHelper = ItemTouchHelper(swipeActionsCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun addUser(){
        val dialogBuilder = AlertDialog.Builder(this@UserActivity)
        val dialogLayout = LinearLayout(
            this@UserActivity
        )
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        dialogLayout.layoutParams = lp
        dialogLayout.orientation = LinearLayout.VERTICAL

        val FirstNameInput = EditText(dialogLayout.context)
        FirstNameInput.layoutParams=lp
        FirstNameInput.hint="Имя"
        FirstNameInput.setPadding(25)

        val LastNameInput = EditText(dialogLayout.context)
        LastNameInput.layoutParams=lp
        LastNameInput.hint="Фамилия"
        LastNameInput.setPadding(25)

        val EmailInput = EditText(dialogLayout.context)
        EmailInput.layoutParams=lp
        EmailInput.hint="Email"
        EmailInput.setPadding(25)

        val PhoneInput = EditText(dialogLayout.context)
        PhoneInput.layoutParams=lp
        PhoneInput.hint="Телефон"
        PhoneInput.setPadding(25)

        val StatusInput = CheckBox(dialogLayout.context)
        StatusInput.layoutParams=lp
        StatusInput.hint="Статус голосующего"
        StatusInput.setPadding(25)

        dialogLayout.addView(FirstNameInput)
        dialogLayout.addView(LastNameInput)
        dialogLayout.addView(EmailInput)
        dialogLayout.addView(PhoneInput)
        dialogLayout.addView(StatusInput)

        dialogBuilder.setView(dialogLayout)

        dialogBuilder.setTitle("Добавить пользователя")

        dialogBuilder.setPositiveButton("Сохранить") { dialog, _ ->
            mainScope.launch {
                mAdapter.addItem(
                    UserRequest(
                        FirstName = FirstNameInput.text.toString(),
                        LastName = LastNameInput.text.toString(),
                        Email = EmailInput.text.toString(),
                        Phone = PhoneInput.text.toString(),
                        Status = StatusInput.isChecked
                    )
                )
            }
            dialog.cancel()
        }
        dialogBuilder.setNegativeButton("Отменить") { dialog, _ -> dialog.cancel() }
        val dialog = dialogBuilder.create()
        dialog.show()
    }
}
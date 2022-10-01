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
import com.example.ktor_klient.adapters.ChoiceItemViewAdapter
import com.example.ktor_klient.api.ApiFactory
import com.example.ktor_klient.api.resources.Choices
import com.example.ktor_klient.components.SwipeActionsCallback
import com.example.ktor_klient.databinding.ChoiceListBinding
import com.example.ktor_klient.models.*
import com.google.android.material.snackbar.Snackbar
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class ChoiceActivity : AppCompatActivity() {
    private val mainScope = MainScope()

    private lateinit var mAdapter: ChoiceItemViewAdapter
    private lateinit var b: ChoiceListBinding
    private val choiceItemsList: MutableList<Choice> = mutableListOf()
    var recyclerView: RecyclerView? = null
    var constraintLayout: RelativeLayout? = null
    var questionId: Int = -1
    var userId: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ChoiceListBinding.inflate(layoutInflater)
        setContentView(b.root)
        val props = intent.extras
        if (props != null) {
            questionId = props.getInt("question")
            userId = props.getInt("user")
        }


        mainScope.launch {
            fetchChoices()
        }
        setUpAdapter()
        enableSwipeToDeleteAndUndo()

        b.floatingActionButton.setOnClickListener {
            addChoice()
        }
    }

    private fun setUpAdapter() {
        recyclerView = findViewById(R.id.ChoicesList)
        constraintLayout = findViewById(R.id.constraintLayout)
        mAdapter = ChoiceItemViewAdapter(this, choiceItemsList)
        b.ChoicesList.adapter = mAdapter
        b.ChoicesList.layoutManager = LinearLayoutManager(this)
    }

    private suspend fun fetchChoices() {
        if (questionId > -1 && userId > -1) {
            val api = ApiFactory.getClient()
            kotlin.runCatching {
                api.get(Choices.QuestionUser.Params(quesId=questionId, usrId=userId)).body<List<Choice>>()
            }.onSuccess {
                choiceItemsList.addAll(it)
                mAdapter.notifyDataSetChanged()
            }.onFailure {
                Log.e("REQUEST", it.toString())
                Toast.makeText(this, "Ошибка сервера!", Toast.LENGTH_LONG).show()
            }
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
                    val dialogBuilder = AlertDialog.Builder(this@ChoiceActivity)
                    val dialogLayout = LinearLayout(
                        this@ChoiceActivity
                    )
                    val lp = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )

                    dialogLayout.layoutParams = lp
                    dialogLayout.orientation = LinearLayout.VERTICAL

                    val QuestionInput = EditText(dialogLayout.context)
                    QuestionInput.layoutParams=lp
                    QuestionInput.hint="Вопросы голосования"
                    QuestionInput.setPadding(25)

                    val UserInput = EditText(dialogLayout.context)
                    UserInput.layoutParams=lp
                    UserInput.hint="Пользователь"
                    UserInput.setPadding(25)

                    val ChoiceUserInput = EditText(dialogLayout.context)
                    ChoiceUserInput.layoutParams=lp
                    ChoiceUserInput.hint="Выбор голосующего"
                    ChoiceUserInput.setPadding(25)

                    dialogLayout.addView(QuestionInput)
                    dialogLayout.addView(UserInput)
                    dialogLayout.addView(ChoiceUserInput)

                    dialogBuilder.setView(dialogLayout)

                    QuestionInput.text = Editable.Factory.getInstance().newEditable(item.Question.toString())
                    UserInput.text = Editable.Factory.getInstance().newEditable(item.User.toString())
                    ChoiceUserInput.text = Editable.Factory.getInstance().newEditable(item.ChoiceUser)

                    dialogBuilder.setTitle("Редактировать выбор пользователя: ${item.User}")

                    dialogBuilder.setPositiveButton("Сохранить") { dialog, _ ->
                        mainScope.launch {
                            mAdapter.editItem(
                                Choice(
                                    Id = item.Id,
                                    Question = questionId,
                                    User = userId,
                                    ChoiceUser = ChoiceUserInput.text.toString(),
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

    private fun addChoice(){
        val dialogBuilder = AlertDialog.Builder(this@ChoiceActivity)
        val dialogLayout = LinearLayout(
            this@ChoiceActivity
        )
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        dialogLayout.layoutParams = lp
        dialogLayout.orientation = LinearLayout.VERTICAL

        val QuestionInput = EditText(dialogLayout.context)
        QuestionInput.layoutParams=lp
        QuestionInput.hint="Вопросы голосования"
        QuestionInput.setPadding(25)

        val UserInput = EditText(dialogLayout.context)
        UserInput.layoutParams=lp
        UserInput.hint="Пользователь"
        UserInput.setPadding(25)

        val ChoiceUserInput = EditText(dialogLayout.context)
        ChoiceUserInput.layoutParams=lp
        ChoiceUserInput.hint="Выбор голосующего"
        ChoiceUserInput.setPadding(25)

        dialogLayout.addView(QuestionInput)
        dialogLayout.addView(UserInput)
        dialogLayout.addView(ChoiceUserInput)

        dialogBuilder.setView(dialogLayout)

        dialogBuilder.setTitle("Добавить пользователя")

        dialogBuilder.setPositiveButton("Сохранить") { dialog, _ ->
            mainScope.launch {
                mAdapter.addItem(
                    ChoiceRequest(
                        Question = questionId,
                        User = userId,
                        ChoiceUser = ChoiceUserInput.text.toString(),
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
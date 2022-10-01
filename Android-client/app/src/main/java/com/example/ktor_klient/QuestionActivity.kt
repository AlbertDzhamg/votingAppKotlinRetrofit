package com.example.ktor_klient

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ktor_klient.adapters.QuestionItemViewAdapter
import com.example.ktor_klient.api.ApiFactory
import com.example.ktor_klient.api.resources.Questions
import com.example.ktor_klient.components.SwipeActionsCallback
import com.example.ktor_klient.databinding.QuestionListBinding
import com.example.ktor_klient.models.*
import com.google.android.material.snackbar.Snackbar
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class QuestionActivity : AppCompatActivity() {
    private val mainScope = MainScope()

    private lateinit var mAdapter: QuestionItemViewAdapter
    private lateinit var b: QuestionListBinding
    private val questionItemsList: MutableList<Question> = mutableListOf()
    var recyclerView: RecyclerView? = null
    var constraintLayout: RelativeLayout? = null
    var voteId: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = QuestionListBinding.inflate(layoutInflater)
        setContentView(b.root)
        val props = intent.extras
        if (props != null) {
            voteId = props.getInt("vote")
        }


        mainScope.launch {
            fetchQuestions()
        }
        setUpAdapter()
        enableSwipeToDeleteAndUndo()

        b.floatingActionButton.setOnClickListener {
            addQuestion()
        }
    }

    private fun setUpAdapter() {
        recyclerView = findViewById(R.id.QuestionsList)
        constraintLayout = findViewById(R.id.constraintLayout)
        mAdapter = QuestionItemViewAdapter(this, questionItemsList)
        b.QuestionsList.adapter = mAdapter
        b.QuestionsList.layoutManager = LinearLayoutManager(this)
    }

    private suspend fun fetchQuestions() {
        if (voteId > -1) {
            val api = ApiFactory.getClient()
            kotlin.runCatching {
                api.get(Questions.Vote.Id(id=voteId)).body<List<Question>>()
            }.onSuccess {
                questionItemsList.addAll(it)
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
                    val dialogBuilder = AlertDialog.Builder(this@QuestionActivity)
                    val dialogLayout = LinearLayout(
                        this@QuestionActivity
                    )
                    val lp = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )

                    dialogLayout.layoutParams = lp
                    dialogLayout.orientation = LinearLayout.VERTICAL

                    val VoteInput = EditText(dialogLayout.context)
                    VoteInput.layoutParams=lp
                    VoteInput.hint="Голосование"
                    VoteInput.setPadding(25)

                    val ContentInput = EditText(dialogLayout.context)
                    ContentInput.layoutParams=lp
                    ContentInput.hint="Содержание вопроса"
                    ContentInput.setPadding(25)

                    val DateVoteInput = EditText(dialogLayout.context)
                    DateVoteInput.layoutParams=lp
                    DateVoteInput.hint="Дата голосования"
                    DateVoteInput.setPadding(25)

                    dialogLayout.addView(VoteInput)
                    dialogLayout.addView(ContentInput)
                    dialogLayout.addView(DateVoteInput)

                    dialogBuilder.setView(dialogLayout)

                    VoteInput.text = Editable.Factory.getInstance().newEditable(item.Vote.toString())
                    ContentInput.text = Editable.Factory.getInstance().newEditable(item.Content)
                    DateVoteInput.text = Editable.Factory.getInstance().newEditable(item.DateVote)

                    dialogBuilder.setTitle("Редактировать вопрос голосования: ${item.Content}")

                    dialogBuilder.setPositiveButton("Сохранить") { dialog, _ ->
                        mainScope.launch {
                            mAdapter.editItem(
                                Question(
                                    Id = item.Id,
                                    Vote = voteId,
                                    Content = ContentInput.text.toString(),
                                    DateVote = DateVoteInput.text.toString(),
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

    private fun addQuestion(){
        val dialogBuilder = AlertDialog.Builder(this@QuestionActivity)
        val dialogLayout = LinearLayout(
            this@QuestionActivity
        )
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        dialogLayout.layoutParams = lp
        dialogLayout.orientation = LinearLayout.VERTICAL

        val VoteInput = EditText(dialogLayout.context)
        VoteInput.layoutParams=lp
        VoteInput.hint="Голосование"
        VoteInput.setPadding(25)

        val ContentInput = EditText(dialogLayout.context)
        ContentInput.layoutParams=lp
        ContentInput.hint="Содержание вопроса"
        ContentInput.setPadding(25)

        val DateVoteInput = EditText(dialogLayout.context)
        DateVoteInput.layoutParams=lp
        DateVoteInput.hint="Дата голосования"
        DateVoteInput.setPadding(25)

        dialogLayout.addView(VoteInput)
        dialogLayout.addView(ContentInput)
        dialogLayout.addView(DateVoteInput)

        dialogBuilder.setView(dialogLayout)

        dialogBuilder.setTitle("Добавить вопрос голосования")

        dialogBuilder.setPositiveButton("Сохранить") { dialog, _ ->
            mainScope.launch {
                mAdapter.addItem(
                    QuestionRequest(
                        Vote = voteId,
                        Content = ContentInput.text.toString(),
                        DateVote = DateVoteInput.text.toString(),
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
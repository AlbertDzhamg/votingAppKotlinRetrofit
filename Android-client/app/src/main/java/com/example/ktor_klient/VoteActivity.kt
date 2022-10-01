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
import com.example.ktor_klient.adapters.VoteItemViewAdapter
import com.example.ktor_klient.api.ApiFactory
import com.example.ktor_klient.api.resources.Votes
import com.example.ktor_klient.components.SwipeActionsCallback
import com.example.ktor_klient.databinding.VoteListBinding
import com.example.ktor_klient.models.Vote
import com.example.ktor_klient.models.VoteRequest
import com.google.android.material.snackbar.Snackbar
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class VoteActivity : AppCompatActivity() {
    private val mainScope = MainScope()

    private lateinit var mAdapter: VoteItemViewAdapter
    private lateinit var b: VoteListBinding
    private val votesItemsList: MutableList<Vote> = mutableListOf()
    var recyclerView: RecyclerView? = null
    var constraintLayout: RelativeLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = VoteListBinding.inflate(layoutInflater)
        setContentView(b.root)

        mainScope.launch {
            fetchVotes()
        }
        setUpAdapter()
        enableSwipeToDeleteAndUndo()

        b.floatingActionButton.setOnClickListener {
            addVote()
        }
    }

    private fun setUpAdapter() {
        recyclerView = findViewById(R.id.VotesList)
        constraintLayout = findViewById(R.id.constraintLayout)
        mAdapter = VoteItemViewAdapter(this, votesItemsList)
        b.VotesList.adapter = mAdapter
        b.VotesList.layoutManager = LinearLayoutManager(this)
    }

    private suspend fun fetchVotes() {
        val api = ApiFactory.getClient()
        kotlin.runCatching {
            api.get(Votes()).body<List<Vote>>()
        }.onSuccess {
            votesItemsList.addAll(it)
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
                    val dialogBuilder = AlertDialog.Builder(this@VoteActivity)
                    val dialogLayout = LinearLayout(
                        this@VoteActivity
                    )
                    val lp = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )

                    dialogLayout.layoutParams = lp
                    dialogLayout.orientation = LinearLayout.VERTICAL

                    val TitleInput = EditText(dialogLayout.context)
                    TitleInput.layoutParams=lp
                    TitleInput.hint="Тема голосования"
                    TitleInput.setPadding(25)

                    val DateStartInput = EditText(dialogLayout.context)
                    DateStartInput.layoutParams=lp
                    DateStartInput.hint="Дата начала голосования"
                    DateStartInput.setPadding(25)

                    val DateFinishInput = EditText(dialogLayout.context)
                    DateFinishInput.layoutParams=lp
                    DateFinishInput.hint="Дата окончания голосования"
                    DateFinishInput.setPadding(25)

                    val StatusInput = EditText(dialogLayout.context)
                    StatusInput.layoutParams=lp
                    StatusInput.hint="Статус темы голосования"
                    StatusInput.setPadding(25)

                    dialogLayout.addView(TitleInput)
                    dialogLayout.addView(DateStartInput)
                    dialogLayout.addView(DateFinishInput)
                    dialogLayout.addView(StatusInput)


                    dialogBuilder.setView(dialogLayout)

                    TitleInput.text = Editable.Factory.getInstance().newEditable(item.Title)
                    DateStartInput.text = Editable.Factory.getInstance().newEditable(item.DateStart)
                    DateFinishInput.text = Editable.Factory.getInstance().newEditable(item.DateFinish)
                    StatusInput.text = Editable.Factory.getInstance().newEditable(item.Status)

                    dialogBuilder.setTitle("Редактировать голосование: ${item.Title}")

                    dialogBuilder.setPositiveButton("Сохранить") { dialog, _ ->
                        mainScope.launch {
                            mAdapter.editItem(
                                Vote(
                                    Id = item.Id,
                                    Title = TitleInput.text.toString(),
                                    DateStart = DateStartInput.text.toString(),
                                    DateFinish = DateFinishInput.text.toString(),
                                    Status = StatusInput.text.toString()
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

    private fun addVote(){
        val dialogBuilder = AlertDialog.Builder(this@VoteActivity)
        val dialogLayout = LinearLayout(
            this@VoteActivity
        )
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        dialogLayout.layoutParams = lp
        dialogLayout.orientation = LinearLayout.VERTICAL

        val TitleInput = EditText(dialogLayout.context)
        TitleInput.layoutParams=lp
        TitleInput.hint="Тема голосования"
        TitleInput.setPadding(25)

        val DateStartInput = EditText(dialogLayout.context)
        DateStartInput.layoutParams=lp
        DateStartInput.hint="Дата начала голосования"
        DateStartInput.setPadding(25)

        val DateFinishInput = EditText(dialogLayout.context)
        DateFinishInput.layoutParams=lp
        DateFinishInput.hint="Дата окончания голосования"
        DateFinishInput.setPadding(25)

        val StatusInput = EditText(dialogLayout.context)
        StatusInput.layoutParams=lp
        StatusInput.hint="Статус темы голосования"
        StatusInput.setPadding(25)

        dialogLayout.addView(TitleInput)
        dialogLayout.addView(DateStartInput)
        dialogLayout.addView(DateFinishInput)
        dialogLayout.addView(StatusInput)

        dialogBuilder.setView(dialogLayout)

        dialogBuilder.setTitle("Добавить пользователя")

        dialogBuilder.setPositiveButton("Сохранить") { dialog, _ ->
            mainScope.launch {
                mAdapter.addItem(
                    VoteRequest(
                        Title = TitleInput.text.toString(),
                        DateStart = DateStartInput.text.toString(),
                        DateFinish = DateFinishInput.text.toString(),
                        Status = StatusInput.text.toString()
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
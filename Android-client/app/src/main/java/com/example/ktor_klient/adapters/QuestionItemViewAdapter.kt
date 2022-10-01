package com.example.ktor_klient.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ktor_klient.api.ApiFactory
import com.example.ktor_klient.api.resources.Questions
import com.example.ktor_klient.databinding.RecyclerItemLayoutBinding
import com.example.ktor_klient.models.Question
import com.example.ktor_klient.models.QuestionRequest
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.http.*


class QuestionItemViewAdapter(private val context: Context, private val itemList:MutableList<Question>)
    : RecyclerView.Adapter<QuestionItemViewAdapter.QuestionItemViewHolder>() {

    private val api = ApiFactory.getClient()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionItemViewHolder {
        val binding = RecyclerItemLayoutBinding.inflate(LayoutInflater.from(context),parent,false)
        return QuestionItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item, context)
    }

    override fun getItemCount(): Int = itemList.size

    suspend fun removeItem(id: Int, position: Int) {
        api.delete(Questions.Id(id=id))
        itemList.removeAt(position)
        notifyItemRemoved(position)
    }

    suspend fun restoreItem(item: Question, position: Int) {
        api.post(Questions()){
            contentType(ContentType.Application.Json)
            setBody(item)
        }
        itemList.add(position, item)
        notifyItemInserted(position)
    }

    suspend fun editItem(item: Question, position: Int){
        api.patch(Questions.Id(id=item.Id)){
            contentType(ContentType.Application.Json)
            setBody(QuestionRequest(
                Vote = item.Vote,
                Content = item.Content,
                DateVote = item.DateVote
            ))
        }
        itemList[position].Vote = item.Vote
        itemList[position].Content = item.Content
        itemList[position].DateVote = item.DateVote
        notifyItemChanged(position)
    }

    suspend fun addItem(item: QuestionRequest){
        api.post(Questions()){
            contentType(ContentType.Application.Json)
            setBody(item)
        }
        itemList.add(Question(
            Id=itemCount,
            Vote = item.Vote,
            Content = item.Content,
            DateVote = item.DateVote
        ))
        notifyItemInserted(itemList.size)
    }

    fun getData() = itemList

    class QuestionItemViewHolder(itemLayoutBinding: RecyclerItemLayoutBinding)
        : RecyclerView.ViewHolder(itemLayoutBinding.root) {

        private val binding = itemLayoutBinding

        fun bind(item: Question, context: Context) {
            binding.label.text = item.Content
        }
    }
}
package com.example.ktor_klient.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ktor_klient.api.ApiFactory
import com.example.ktor_klient.api.resources.Choices
import com.example.ktor_klient.databinding.RecyclerItemLayoutBinding
import com.example.ktor_klient.models.Choice
import com.example.ktor_klient.models.ChoiceRequest
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.http.*


class ChoiceItemViewAdapter(private val context: Context, private val itemList:MutableList<Choice>)
    : RecyclerView.Adapter<ChoiceItemViewAdapter.ChoiceItemViewHolder>() {

    private val api = ApiFactory.getClient()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChoiceItemViewHolder {
        val binding = RecyclerItemLayoutBinding.inflate(LayoutInflater.from(context),parent,false)
        return ChoiceItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChoiceItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item, context)
    }

    override fun getItemCount(): Int = itemList.size

    suspend fun removeItem(id: Int, position: Int) {
        api.delete(Choices.Id(id=id))
        itemList.removeAt(position)
        notifyItemRemoved(position)
    }

    suspend fun restoreItem(item: Choice, position: Int) {
        api.post(Choices()){
            contentType(ContentType.Application.Json)
            setBody(item)
        }
        itemList.add(position, item)
        notifyItemInserted(position)
    }

    suspend fun editItem(item: Choice, position: Int){
        api.patch(Choices.Id(id=item.Id)){
            contentType(ContentType.Application.Json)
            setBody(ChoiceRequest(
                Question = item.Question,
                User = item.User,
                ChoiceUser = item.ChoiceUser
            ))
        }
        itemList[position].Question = item.Question
        itemList[position].User = item.User
        itemList[position].ChoiceUser = item.ChoiceUser
        notifyItemChanged(position)
    }

    suspend fun addItem(item: ChoiceRequest){
        api.post(Choices()){
            contentType(ContentType.Application.Json)
            setBody(item)
        }
        itemList.add(Choice(
            Id=itemCount,
            Question = item.Question,
            User = item.User,
            ChoiceUser = item.ChoiceUser
        ))
        notifyItemInserted(itemList.size)
    }

    fun getData() = itemList

    class ChoiceItemViewHolder(itemLayoutBinding: RecyclerItemLayoutBinding)
        : RecyclerView.ViewHolder(itemLayoutBinding.root) {

        private val binding = itemLayoutBinding

        fun bind(item: Choice, context: Context) {
            binding.label.text = item.ChoiceUser
        }
    }
}
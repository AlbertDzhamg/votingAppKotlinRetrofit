package com.example.ktor_klient.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.ktor_klient.api.ApiFactory
import com.example.ktor_klient.databinding.RecyclerItemLayoutBinding
import com.example.ktor_klient.models.Vote
import com.example.ktor_klient.api.resources.Votes
import com.example.ktor_klient.models.VoteRequest
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.http.*


class VoteItemViewAdapter(
    private val context: Context,
    private val itemList: MutableList<Vote>
) : RecyclerView.Adapter<VoteItemViewAdapter.VoteItemViewHolder>() {

    private val api = ApiFactory.getClient()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoteItemViewHolder {
        val binding = RecyclerItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return VoteItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VoteItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = itemList.size
    suspend fun removeItem(id: Int, position: Int) {
        api.delete(Votes.Id(id = id))
        itemList.removeAt(position)
        notifyItemRemoved(position)
    }

    suspend fun restoreItem(item: Vote, position: Int) {
        api.post(Votes()) {
            contentType(ContentType.Application.Json)
            setBody(item)
        }
        itemList.add(position, item)
        notifyItemInserted(position)
    }

    suspend fun editItem(item: Vote, position: Int) {
        kotlin.runCatching {
            api.patch(Votes.Id(id = item.Id)) {
                contentType(ContentType.Application.Json)
                setBody(VoteRequest(
                    item.Title,
                    item.DateStart,
                    item.DateFinish,
                    item.Status
                ))
            }
        }.onSuccess {
            itemList[position].Title = item.Title
            itemList[position].DateStart = item.DateStart
            itemList[position].DateFinish = item.DateFinish
            itemList[position].Status = item.Status
            notifyItemChanged(position)
        }.onFailure {
            Toast.makeText(context, "Ошибка сервера!", Toast.LENGTH_LONG).show()
        }
    }

    suspend fun addItem(item: VoteRequest) {
        api.post(Votes()) {
            contentType(ContentType.Application.Json)
            setBody(item)
        }
        itemList.add(
            Vote(
                Id = itemList.size,
                Title = item.Title,
                DateStart = item.DateStart,
                DateFinish = item.DateFinish,
                Status = item.Status
            )
        )
        notifyItemInserted(itemList.size)
    }

    fun getData() = itemList

    class VoteItemViewHolder(usersItemLayoutBinding: RecyclerItemLayoutBinding) :
        RecyclerView.ViewHolder(usersItemLayoutBinding.root) {

        private val binding = usersItemLayoutBinding

        fun bind(userItem: Vote) {
            binding.label.text = userItem.Title
        }
    }
}
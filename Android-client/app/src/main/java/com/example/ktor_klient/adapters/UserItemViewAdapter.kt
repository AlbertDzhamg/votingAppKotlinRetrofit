package com.example.ktor_klient.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.ktor_klient.api.ApiFactory
import com.example.ktor_klient.databinding.RecyclerItemLayoutBinding
import com.example.ktor_klient.models.User
import com.example.ktor_klient.api.resources.Users
import com.example.ktor_klient.models.UserRequest
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.http.*


class UserItemViewAdapter(
    private val context: Context,
    private val itemList: MutableList<User>
) : RecyclerView.Adapter<UserItemViewAdapter.UserItemViewHolder>() {

    private val api = ApiFactory.getClient()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserItemViewHolder {
        val binding = RecyclerItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return UserItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = itemList.size
    suspend fun removeItem(id: Int, position: Int) {
        api.delete(Users.Id(id = id))
        itemList.removeAt(position)
        notifyItemRemoved(position)
    }

    suspend fun restoreItem(item: User, position: Int) {
        api.post(Users()) {
            contentType(ContentType.Application.Json)
            setBody(item)
        }
        itemList.add(position, item)
        notifyItemInserted(position)
    }

    suspend fun editItem(item: User, position: Int) {
        kotlin.runCatching {
            api.patch(Users.Id(id = item.Id)) {
                contentType(ContentType.Application.Json)
                setBody(UserRequest(
                    item.FirstName,
                    item.LastName,
                    item.Email,
                    item.Phone,
                    item.Status
                ))
            }
        }.onSuccess {
            itemList[position].FirstName = item.FirstName
            itemList[position].LastName = item.LastName
            itemList[position].Email = item.Email
            itemList[position].Phone = item.Phone
            itemList[position].Status = item.Status
            notifyItemChanged(position)
        }.onFailure {
            Toast.makeText(context, "Ошибка сервера!", Toast.LENGTH_LONG).show()
        }
    }

    suspend fun addItem(item: UserRequest) {
        api.post(Users()) {
            contentType(ContentType.Application.Json)
            setBody(item)
        }
        itemList.add(
            User(
                Id = itemList.size,
                FirstName = item.FirstName,
                LastName = item.LastName,
                Email = item.Email,
                Phone = item.Phone,
                Status = item.Status
            )
        )
        notifyItemInserted(itemList.size)
    }

    fun getData() = itemList

    class UserItemViewHolder(usersItemLayoutBinding: RecyclerItemLayoutBinding) :
        RecyclerView.ViewHolder(usersItemLayoutBinding.root) {

        private val binding = usersItemLayoutBinding

        fun bind(userItem: User) {
            binding.label.text = userItem.FirstName + " " + userItem.LastName
        }
    }
}
package ru.students.services

import org.ktorm.database.Database

class DatabaseService {
    companion object {
        private val database = Database.connect(
            url = "jdbc:postgresql://localhost:5432/students",
            driver = "org.postgresql.Driver",
            user = "admin",
            password = "87654321"
        )

        fun getConnection(): Database = database;
    }

}
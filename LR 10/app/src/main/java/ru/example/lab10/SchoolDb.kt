package ru.example.lab10

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SchoolDb(context: Context) : SQLiteOpenHelper(context, "school.db", null, 2) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        // Включаем проверку внешних ключей
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Создаем таблицу групп (версия 2 сразу с email)
        db.execSQL("CREATE TABLE groups (id INTEGER PRIMARY KEY, title TEXT)")

        // Создаем таблицу студентов
        db.execSQL("""
            CREATE TABLE student (
                id INTEGER PRIMARY KEY, 
                name TEXT, 
                group_id INTEGER, 
                email TEXT DEFAULT '',
                FOREIGN KEY(group_id) REFERENCES groups(id) ON DELETE RESTRICT
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Миграция с версии 1 на 2: добавляем email
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE student ADD COLUMN email TEXT DEFAULT ''")
        }
    }
}
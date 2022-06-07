package com.tgapps.sgakaizen

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

val TABLE_NAME = "kaizen"
val ID = "id"
val NAME = "nome"
val FRE = "fre"
val SETOR = "setor"
val OPT1 = "opt1"
val OPT2 = "opt2"
val OPT3 = "opt3"
class LocalDatabase(ctx: Context) : SQLiteOpenHelper(ctx, TABLE_NAME,null,1) {
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE = """CREATE TABLE $TABLE_NAME($ID INT PRIMARY KEY,$NAME VARCHAR, $FRE VARCHAR, $SETOR VARCHAR,$OPT1 INT,$OPT2 INT, $OPT3 INT)"""
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME;"
        db?.execSQL(DROP_TABLE)
        onCreate(db)
    }
    fun initializeRow() {
        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(ID, 1)
        cv.put(NAME, "")
        cv.put(FRE, "")
        cv.put(SETOR, "")
        cv.put(OPT1, 0)
        cv.put(OPT2, 0)
        cv.put(OPT3, 0)
        db.insert(TABLE_NAME, null, cv)
    }
}
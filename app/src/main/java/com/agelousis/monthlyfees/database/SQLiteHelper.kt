package com.agelousis.monthlyfees.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        // Table Names
        const val USERS_TABLE_NAME = "users"
        const val GROUPS_TABLE_NAME = "groups"
        const val PERSONS_TABLE_NAME = "persons"

        // Generic Columns
        const val ID = "id"
        const val USER_ID = "user_id"

        // Users Table columns
        const val USERNAME = "username"
        const val PASSWORD = "password"
        const val BIOMETRICS = "biometrics"
        const val PROFILE_IMAGE = "profile_image"

        // Groups Table Columns
        const val GROUP_NAME = "group_name"

        // Users Table Columns
        const val GROUP_ID = "group_id"
        const val FIRST_NAME = "first_name"
        const val PHONE = "phone"
        const val PARENT_NAME = "parent_name"
        const val PARENT_PHONE = "parent_phone"
        const val EMAIL = "email"
        const val ACTIVE = "active"
        const val FREE = "free"
        const val PAYMENT_AMOUNT = "payment_amount"
        const val PAYMENT_DATE = "payment_date"
        const val SKIP_PAYMENT = "skip_payment"
        const val PAYMENT_NOTE = "payment_note"

        // Database Information
        private const val DB_NAME = "MONTHLY_FEES"

        // database version
        private const val DB_VERSION = 1

        // Creating users table query
        private const val USERS_TABLE_CREATION_QUERY = "CREATE TABLE $USERS_TABLE_NAME($ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$USERNAME TEXT, $PASSWORD TEXT, $BIOMETRICS BOOLEAN, $PROFILE_IMAGE TEXT);"

        //Creating groups table query
        private const val GROUPS_TABLE_CREATION_QUERY = "CREATE TABLE $GROUPS_TABLE_NAME($ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$USER_ID INTEGER, $GROUP_NAME TEXT);"

        private const val PERSONS_TABLE_CREATION_QUERY = "CREATE TABLE $PERSONS_TABLE_NAME($ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$USER_ID INTEGER, $GROUP_ID INTEGER, $FIRST_NAME TEXT, $PHONE TEXT, $PARENT_NAME TEXT, $PARENT_PHONE TEXT, $EMAIL TEXT," +
                "$ACTIVE BOOLEAN, $FREE BOOLEAN, $PAYMENT_AMOUNT DOUBLE, $PAYMENT_DATE TEXT, $SKIP_PAYMENT BOOLEAN, $PAYMENT_NOTE TEXT);"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(USERS_TABLE_CREATION_QUERY)
        db?.execSQL(GROUPS_TABLE_CREATION_QUERY)
        db?.execSQL(PERSONS_TABLE_CREATION_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $USERS_TABLE_NAME")
        db?.execSQL("DROP TABLE IF EXISTS $GROUPS_TABLE_NAME")
        db?.execSQL("DROP TABLE IF EXISTS $PERSONS_TABLE_NAME")
        onCreate(db)
    }

}
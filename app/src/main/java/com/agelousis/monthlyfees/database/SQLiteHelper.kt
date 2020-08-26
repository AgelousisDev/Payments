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
        const val PAYMENTS_TABLE_NAME = "payments"

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
        const val COLOR = "color"

        // Persons Table Columns
        const val GROUP_ID = "group_id"
        const val FIRST_NAME = "first_name"
        const val PHONE = "phone"
        const val PARENT_NAME = "parent_name"
        const val PARENT_PHONE = "parent_phone"
        const val EMAIL = "email"
        const val ACTIVE = "active"
        const val FREE = "free"

        // Payments Table Columns
        const val PERSON_ID = "person_id"
        const val PAYMENT_AMOUNT = "payment_amount"
        const val START_DATE = "start_date"
        const val PAYMENT_DATE = "payment_date"
        const val END_DATE = "end_date"
        const val SKIP_PAYMENT = "skip_payment"
        const val PAYMENT_NOTE = "payment_note"

        // Database Information
        private const val DB_NAME = "MONTHLY_FEES.db"

        // database version
        private const val DB_VERSION = 1

        // Creating users table query
        private const val USERS_TABLE_CREATION_QUERY = "CREATE TABLE $USERS_TABLE_NAME($ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$USERNAME TEXT, $PASSWORD TEXT, $BIOMETRICS BOOLEAN, $PROFILE_IMAGE TEXT);"

        //Creating groups table query
        private const val GROUPS_TABLE_CREATION_QUERY = "CREATE TABLE $GROUPS_TABLE_NAME($ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$USER_ID INTEGER, $GROUP_NAME TEXT, $COLOR INTEGER);"

        //Creating persons group query
        private const val PERSONS_TABLE_CREATION_QUERY = "CREATE TABLE $PERSONS_TABLE_NAME($ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$USER_ID INTEGER, $GROUP_ID INTEGER, $FIRST_NAME TEXT, $PHONE TEXT, $PARENT_NAME TEXT, $PARENT_PHONE TEXT, $EMAIL TEXT," +
                "$ACTIVE BOOLEAN, $FREE BOOLEAN);"

        //Creating payments group query
        private const val PAYMENTS_TABLE_CREATION_QUERY = "CREATE TABLE $PAYMENTS_TABLE_NAME($ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$PERSON_ID INTEGER, $PAYMENT_AMOUNT DOUBLE, $START_DATE TEXT, $PAYMENT_DATE TEXT, $END_DATE TEXT, $SKIP_PAYMENT BOOLEAN," +
                "$PAYMENT_NOTE TEXT);"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(USERS_TABLE_CREATION_QUERY)
        db?.execSQL(GROUPS_TABLE_CREATION_QUERY)
        db?.execSQL(PERSONS_TABLE_CREATION_QUERY)
        db?.execSQL(PAYMENTS_TABLE_CREATION_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $USERS_TABLE_NAME")
        db?.execSQL("DROP TABLE IF EXISTS $GROUPS_TABLE_NAME")
        db?.execSQL("DROP TABLE IF EXISTS $PERSONS_TABLE_NAME")
        db?.execSQL("DROP TABLE IF EXISTS $PAYMENTS_TABLE_NAME")
        onCreate(db)
    }

}
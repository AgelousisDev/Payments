package com.agelousis.monthlyfees.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import com.agelousis.monthlyfees.login.models.UserModel
import com.agelousis.monthlyfees.main.ui.payments.models.GroupModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

typealias UserBlock = (UserModel?) -> Unit
typealias UsersBlock = (List<UserModel>) -> Unit
typealias GroupInsertionSuccessBlock = () -> Unit

class DBManager(context: Context) {

    private var sqLiteHelper: SQLiteHelper? = null
    private var database: SQLiteDatabase? = null

    var userModel: UserModel? = null
        set(value) {
            field = value
            userModel?.let { userModel ->
                val cursor = database?.query(
                    SQLiteHelper.USERS_TABLE_NAME,
                    arrayOf(SQLiteHelper.ID),
                    "${SQLiteHelper.USERNAME}=? AND ${SQLiteHelper.PASSWORD}=?",
                    arrayOf(userModel.username, userModel.password),
                    null,
                    null,
                    null,
                    null
                )
                if ((cursor?.count ?: 0) == 0) {
                    val contentValue = ContentValues()
                    contentValue.put(SQLiteHelper.USERNAME, userModel.username)
                    contentValue.put(SQLiteHelper.PASSWORD, userModel.password)
                    contentValue.put(SQLiteHelper.BIOMETRICS, userModel.biometrics == true)
                    contentValue.put(SQLiteHelper.PROFILE_IMAGE, userModel.profileImage)
                    database?.insert(SQLiteHelper.USERS_TABLE_NAME, null, contentValue)
                }
                cursor?.close()
            }
        }

    init {
        sqLiteHelper = SQLiteHelper(context = context)
        database = sqLiteHelper?.writableDatabase
    }

    suspend fun searchUser(userModel: UserModel, userBlock: UserBlock) {
        withContext(Dispatchers.Default) {
            val cursor = database?.query(
                SQLiteHelper.USERS_TABLE_NAME,
                arrayOf(SQLiteHelper.ID, SQLiteHelper.USERNAME, SQLiteHelper.PASSWORD, SQLiteHelper.BIOMETRICS, SQLiteHelper.PROFILE_IMAGE),
                "${SQLiteHelper.USERNAME}=? AND ${SQLiteHelper.PASSWORD}=?",
                arrayOf(userModel.username, userModel.password),
                null,
                null,
                null,
                null
            )
            cursor?.moveToFirst()
            val savedUserModel = if ((cursor?.count ?: 0) > 0) {
                UserModel(
                    id = cursor?.getIntOrNull(index = cursor.getColumnIndex(SQLiteHelper.ID)),
                    username = cursor?.getStringOrNull(cursor.getColumnIndex(SQLiteHelper.USERNAME)),
                    password = cursor?.getStringOrNull(cursor.getColumnIndex(SQLiteHelper.PASSWORD)),
                    biometrics = cursor?.getIntOrNull(cursor.getColumnIndex(SQLiteHelper.BIOMETRICS)) ?: 0 > 0,
                    profileImage = cursor?.getStringOrNull(cursor.getColumnIndex(SQLiteHelper.PROFILE_IMAGE))
                )
            }
            else null
            cursor?.close()
            withContext(Dispatchers.Main) {
                userBlock(savedUserModel)
            }
        }
    }

    suspend fun checkUsers(usersBlock: UsersBlock) {
        withContext(Dispatchers.Default) {
            val usersList = arrayListOf<UserModel>()
            val cursor = database?.query(SQLiteHelper.USERS_TABLE_NAME, arrayOf(SQLiteHelper.ID, SQLiteHelper.USERNAME, SQLiteHelper.PASSWORD, SQLiteHelper.BIOMETRICS, SQLiteHelper.PROFILE_IMAGE), null, null, null, null, null)
            if (cursor?.moveToFirst() == true)
                do {
                    usersList.add(
                        UserModel(
                            username = cursor.getStringOrNull(cursor.getColumnIndex(SQLiteHelper.USERNAME)),
                            password = cursor.getStringOrNull(cursor.getColumnIndex(SQLiteHelper.PASSWORD)),
                            biometrics = cursor.getIntOrNull(cursor.getColumnIndex(SQLiteHelper.BIOMETRICS)) ?: 0 > 0,
                            profileImage = cursor.getStringOrNull(cursor.getColumnIndex(SQLiteHelper.PROFILE_IMAGE))
                        )
                    )
                } while (cursor.moveToNext())
            cursor?.close()
            withContext(Dispatchers.Main) {
                usersBlock(usersList)
            }
        }
    }

    suspend fun updateUser(userModel: UserModel, userBlock: UserBlock) {
        withContext(Dispatchers.Default) {
            database?.update(
                SQLiteHelper.USERS_TABLE_NAME,
                ContentValues().also {
                    it.put(SQLiteHelper.USERNAME, userModel.username)
                    it.put(SQLiteHelper.PASSWORD, userModel.password)
                    it.put(SQLiteHelper.PROFILE_IMAGE, userModel.profileImage)
                    it.put(SQLiteHelper.BIOMETRICS, userModel.biometrics)
                },
                "id=${userModel.id}",
                null
            )
            withContext(Dispatchers.Main) {
                userBlock(userModel)
            }
        }
    }

    suspend fun insertGroup(userId: Int?, groupModel: GroupModel, groupInsertionSuccessBlock: GroupInsertionSuccessBlock) {
        withContext(Dispatchers.Default) {
            database?.insert(
                SQLiteHelper.GROUPS_TABLE_NAME,
                null,
                ContentValues().also {
                    it.put(SQLiteHelper.USER_ID, userId)
                    it.put(SQLiteHelper.GROUP_NAME, groupModel.groupName)
                }
            )
            withContext(Dispatchers.Main) {
                groupInsertionSuccessBlock()
            }
        }
    }

    fun delete(id: Long) {
        database?.delete(SQLiteHelper.USERS_TABLE_NAME,  "${SQLiteHelper.ID}=$id", null)
    }

    fun close() {
        sqLiteHelper?.close()
    }

}
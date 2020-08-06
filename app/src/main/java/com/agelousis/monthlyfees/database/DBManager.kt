package com.agelousis.monthlyfees.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.core.database.getDoubleOrNull
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import com.agelousis.monthlyfees.login.models.UserModel
import com.agelousis.monthlyfees.main.ui.payments.models.GroupModel
import com.agelousis.monthlyfees.main.ui.payments.models.PaymentAmountModel
import com.agelousis.monthlyfees.main.ui.payments.models.PaymentModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

typealias UserBlock = (UserModel?) -> Unit
typealias UsersBlock = (List<UserModel>) -> Unit
typealias GroupInsertionSuccessBlock = () -> Unit
typealias PaymentsClosure = (List<PaymentModel>) -> Unit

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

    suspend fun initializePayments(userId: Int?, paymentsClosure: PaymentsClosure) {
        withContext(Dispatchers.Default) {
            val payments = arrayListOf<PaymentModel>()
            val paymentsCursor = database?.query(
                SQLiteHelper.PERSONS_TABLE_NAME,
                null,
                "${SQLiteHelper.USER_ID}=?",
                arrayOf(userId?.toString()),
                null,
                null,
                null)
            if (paymentsCursor?.moveToFirst() == true)
                do {
                    val groupsCursor = database?.query(
                        SQLiteHelper.GROUPS_TABLE_NAME,
                        arrayOf(SQLiteHelper.GROUP_NAME),
                        "${SQLiteHelper.ID}=? AND ${SQLiteHelper.USER_ID}=?",
                        arrayOf(userId?.toString(), paymentsCursor.getIntOrNull(paymentsCursor.getColumnIndex(SQLiteHelper.GROUP_ID))?.toString()),
                        null,
                        null,
                        null
                    )
                    groupsCursor?.moveToFirst() ?: continue
                    payments.add(
                        PaymentModel(
                            groupId = paymentsCursor.getIntOrNull(paymentsCursor.getColumnIndex(SQLiteHelper.GROUP_ID)),
                            groupName = groupsCursor.getStringOrNull(groupsCursor.getColumnIndex(SQLiteHelper.GROUP_NAME)),
                            firstName = paymentsCursor.getStringOrNull(paymentsCursor.getColumnIndex(SQLiteHelper.FIRST_NAME)),
                            phone = paymentsCursor.getStringOrNull(paymentsCursor.getColumnIndex(SQLiteHelper.PHONE)),
                            parentName = paymentsCursor.getStringOrNull(paymentsCursor.getColumnIndex(SQLiteHelper.PARENT_NAME)),
                            parentPhone = paymentsCursor.getStringOrNull(paymentsCursor.getColumnIndex(SQLiteHelper.PARENT_PHONE)),
                            email = paymentsCursor.getStringOrNull(paymentsCursor.getColumnIndex(SQLiteHelper.EMAIL)),
                            active = paymentsCursor.getIntOrNull(paymentsCursor.getColumnIndex(SQLiteHelper.ACTIVE)) ?: 0 > 0,
                            free = paymentsCursor.getIntOrNull(paymentsCursor.getColumnIndex(SQLiteHelper.FREE)) ?: 0 > 0,
                            paymentAmountModel = PaymentAmountModel(
                                paymentAmount = paymentsCursor.getDoubleOrNull(paymentsCursor.getColumnIndex(SQLiteHelper.PAYMENT_AMOUNT)),
                                paymentDate = paymentsCursor.getStringOrNull(paymentsCursor.getColumnIndex(SQLiteHelper.PAYMENT_DATE)),
                                skipPayment = paymentsCursor.getIntOrNull(paymentsCursor.getColumnIndex(SQLiteHelper.SKIP_PAYMENT)) ?: 0 > 0,
                                paymentNote = paymentsCursor.getStringOrNull(paymentsCursor.getColumnIndex(SQLiteHelper.PAYMENT_NOTE))
                            )
                        )
                    )
                    groupsCursor.close()
                }
                while (paymentsCursor.moveToNext())
            paymentsCursor?.close()
            withContext(Dispatchers.Main) {
                paymentsClosure(payments)
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
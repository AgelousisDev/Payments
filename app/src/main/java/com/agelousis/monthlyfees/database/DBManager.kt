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
import com.agelousis.monthlyfees.main.ui.payments.models.PersonModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

typealias UserBlock = (UserModel?) -> Unit
typealias UsersBlock = (List<UserModel>) -> Unit
typealias GroupInsertionSuccessBlock = () -> Unit
typealias PaymentsClosure = (List<Any>) -> Unit
typealias PaymentInsertionSuccessBlock = () -> Unit
typealias GroupsSuccessBlock = (List<GroupModel>) -> Unit

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
                "${SQLiteHelper.ID}=?",
                arrayOf(userModel.id?.toString())
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

    suspend fun insertPayment(userId: Int?, personModel: PersonModel, paymentInsertionSuccessBlock: PaymentInsertionSuccessBlock) {
        withContext(Dispatchers.Default) {
            val personId = database?.insert(
                SQLiteHelper.PERSONS_TABLE_NAME,
                null,
                ContentValues().also {
                    it.put(SQLiteHelper.USER_ID, userId)
                    it.put(SQLiteHelper.GROUP_ID, personModel.groupId)
                    it.put(SQLiteHelper.FIRST_NAME, personModel.firstName)
                    it.put(SQLiteHelper.PHONE, personModel.phone)
                    it.put(SQLiteHelper.PARENT_NAME, personModel.parentName)
                    it.put(SQLiteHelper.PARENT_PHONE, personModel.parentPhone)
                    it.put(SQLiteHelper.EMAIL, personModel.email)
                    it.put(SQLiteHelper.ACTIVE, personModel.active)
                    it.put(SQLiteHelper.FREE, personModel.free)
                }
            )
            personModel.payments?.forEach { paymentAmountModel ->
                database?.insert(
                    SQLiteHelper.PAYMENTS_TABLE_NAME,
                    null,
                    ContentValues().also {
                        it.put(SQLiteHelper.PERSON_ID, personId?.toInt())
                        it.put(SQLiteHelper.PAYMENT_AMOUNT, paymentAmountModel.paymentAmount)
                        it.put(SQLiteHelper.PAYMENT_DATE, paymentAmountModel.paymentDate)
                        it.put(SQLiteHelper.SKIP_PAYMENT, paymentAmountModel.skipPayment)
                        it.put(SQLiteHelper.PAYMENT_NOTE, paymentAmountModel.paymentNote)
                    }
                )
            }
            withContext(Dispatchers.Main) {
                paymentInsertionSuccessBlock()
            }
        }
    }

    suspend fun updatePayment(userId: Int?, personModel: PersonModel, paymentInsertionSuccessBlock: PaymentInsertionSuccessBlock) {
        withContext(Dispatchers.Default) {
            database?.update(
                SQLiteHelper.PERSONS_TABLE_NAME,
                ContentValues().also {
                    it.put(SQLiteHelper.USER_ID, userId)
                    it.put(SQLiteHelper.GROUP_ID, personModel.groupId)
                    it.put(SQLiteHelper.FIRST_NAME, personModel.firstName)
                    it.put(SQLiteHelper.PHONE, personModel.phone)
                    it.put(SQLiteHelper.PARENT_NAME, personModel.parentName)
                    it.put(SQLiteHelper.PARENT_PHONE, personModel.parentPhone)
                    it.put(SQLiteHelper.EMAIL, personModel.email)
                    it.put(SQLiteHelper.ACTIVE, personModel.active)
                    it.put(SQLiteHelper.FREE, personModel.free)
                },
                "${SQLiteHelper.ID}=?",
                arrayOf(personModel.paymentId?.toString())
            )
            database?.delete(
                SQLiteHelper.PAYMENTS_TABLE_NAME,
                "${SQLiteHelper.PERSON_ID}=?",
                arrayOf(personModel.paymentId?.toString())
            )
            personModel.payments?.forEach { paymentAmountModel ->
                database?.insert(
                    SQLiteHelper.PAYMENTS_TABLE_NAME,
                    null,
                    ContentValues().also {
                        it.put(SQLiteHelper.PERSON_ID, personModel.paymentId)
                        it.put(SQLiteHelper.PAYMENT_AMOUNT, paymentAmountModel.paymentAmount)
                        it.put(SQLiteHelper.PAYMENT_DATE, paymentAmountModel.paymentDate)
                        it.put(SQLiteHelper.SKIP_PAYMENT, paymentAmountModel.skipPayment)
                        it.put(SQLiteHelper.PAYMENT_NOTE, paymentAmountModel.paymentNote)
                    }
                )
            }
            withContext(Dispatchers.Main) {
                paymentInsertionSuccessBlock()
            }
        }
    }

    suspend fun initializePayments(userId: Int?, paymentsClosure: PaymentsClosure) {
        withContext(Dispatchers.Default) {
            val genericList = arrayListOf<Any>()
            val groups = arrayListOf<GroupModel>()
            //initialize groups first
            val groupsCursor = database?.query(
                SQLiteHelper.GROUPS_TABLE_NAME,
                null,
                "${SQLiteHelper.USER_ID}=?",
                arrayOf(userId?.toString()),
                null,
                null,
                null
            )
            if (groupsCursor?.moveToFirst() == true && groupsCursor.count > 0)
                do {
                    groups.add(
                        GroupModel(
                            groupId = groupsCursor.getIntOrNull(groupsCursor.getColumnIndex(SQLiteHelper.ID)),
                            groupName = groupsCursor.getStringOrNull(groupsCursor.getColumnIndex(SQLiteHelper.GROUP_NAME))
                        )
                    )
                }
                while(groupsCursor.moveToNext())
            groupsCursor?.close()
            genericList.addAll(
                groups
            )
            val personsCursor = database?.query(
                SQLiteHelper.PERSONS_TABLE_NAME,
                null,
                "${SQLiteHelper.USER_ID}=?",
                arrayOf(userId?.toString()),
                null,
                null,
                null)
            if (personsCursor?.moveToFirst() == true && personsCursor.count > 0)
                do {
                    val paymentsCursor = database?.query(
                        SQLiteHelper.PAYMENTS_TABLE_NAME,
                        null,
                        "${SQLiteHelper.PERSON_ID}=?",
                        arrayOf(personsCursor.getIntOrNull(personsCursor.getColumnIndex(SQLiteHelper.ID))?.toString()),
                        null,
                        null,
                        null
                    )
                    val payments = arrayListOf<PaymentAmountModel>()
                    if (paymentsCursor?.moveToFirst() == true && paymentsCursor.count > 0) {
                        do {
                            payments.add(
                                PaymentAmountModel(
                                    paymentId = paymentsCursor.getIntOrNull(paymentsCursor.getColumnIndex(SQLiteHelper.ID)),
                                    paymentAmount = paymentsCursor.getDoubleOrNull(paymentsCursor.getColumnIndex(SQLiteHelper.PAYMENT_AMOUNT)),
                                    paymentNote = paymentsCursor.getStringOrNull(paymentsCursor.getColumnIndex(SQLiteHelper.PAYMENT_NOTE)),
                                    paymentDate = paymentsCursor.getStringOrNull(paymentsCursor.getColumnIndex(SQLiteHelper.PAYMENT_DATE)),
                                    skipPayment = paymentsCursor.getIntOrNull(paymentsCursor.getColumnIndex(SQLiteHelper.SKIP_PAYMENT)) ?: 0 > 0
                                )
                            )
                        }
                        while(paymentsCursor.moveToNext())
                    }
                    paymentsCursor?.close()
                    genericList.add(
                        PersonModel(
                            paymentId = personsCursor.getIntOrNull(personsCursor.getColumnIndex(SQLiteHelper.ID)),
                            groupId = personsCursor.getIntOrNull(personsCursor.getColumnIndex(SQLiteHelper.GROUP_ID)),
                            groupName = groups.firstOrNull { it.groupId == personsCursor.getIntOrNull(personsCursor.getColumnIndex(SQLiteHelper.GROUP_ID)) }?.groupName,
                            firstName = personsCursor.getStringOrNull(personsCursor.getColumnIndex(SQLiteHelper.FIRST_NAME)),
                            phone = personsCursor.getStringOrNull(personsCursor.getColumnIndex(SQLiteHelper.PHONE)),
                            parentName = personsCursor.getStringOrNull(personsCursor.getColumnIndex(SQLiteHelper.PARENT_NAME)),
                            parentPhone = personsCursor.getStringOrNull(personsCursor.getColumnIndex(SQLiteHelper.PARENT_PHONE)),
                            email = personsCursor.getStringOrNull(personsCursor.getColumnIndex(SQLiteHelper.EMAIL)),
                            active = personsCursor.getIntOrNull(personsCursor.getColumnIndex(SQLiteHelper.ACTIVE)) ?: 0 > 0,
                            free = personsCursor.getIntOrNull(personsCursor.getColumnIndex(SQLiteHelper.FREE)) ?: 0 > 0,
                            payments = payments
                        )
                    )
                    genericList.removeAll {
                        (it as? GroupModel)?.groupId == personsCursor.getIntOrNull(personsCursor.getColumnIndex(SQLiteHelper.GROUP_ID))
                    }
                }
                while (personsCursor.moveToNext())
            personsCursor?.close()
            withContext(Dispatchers.Main) {
                paymentsClosure(genericList)
            }
        }
    }

    suspend fun initializeGroups(userId: Int?, groupsSuccessBlock: GroupsSuccessBlock) {
        withContext(Dispatchers.Default) {
            val groups = arrayListOf<GroupModel>()
            val groupsCursor = database?.query(
                SQLiteHelper.GROUPS_TABLE_NAME,
                null,
                "${SQLiteHelper.USER_ID}=?",
                arrayOf(userId?.toString()),
                null,
                null,
                null
            )
            if (groupsCursor?.moveToFirst() == true && groupsCursor.count > 0)
                do {
                    groups.add(
                        GroupModel(
                            groupId = groupsCursor.getIntOrNull(groupsCursor.getColumnIndex(SQLiteHelper.ID)),
                            groupName = groupsCursor.getStringOrNull(groupsCursor.getColumnIndex(SQLiteHelper.GROUP_NAME))
                        )
                    )
                }
                while(groupsCursor.moveToNext())
            groupsCursor?.close()
            withContext(Dispatchers.Main) {
                groupsSuccessBlock(groups)
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
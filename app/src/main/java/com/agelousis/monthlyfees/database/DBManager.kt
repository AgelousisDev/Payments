package com.agelousis.monthlyfees.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.core.database.getBlobOrNull
import androidx.core.database.getDoubleOrNull
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import com.agelousis.monthlyfees.login.models.UserModel
import com.agelousis.monthlyfees.main.ui.files.models.FileDataModel
import com.agelousis.monthlyfees.main.ui.payments.models.GroupModel
import com.agelousis.monthlyfees.main.ui.payments.models.PaymentAmountModel
import com.agelousis.monthlyfees.main.ui.payments.models.PersonModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

typealias UserBlock = (UserModel?) -> Unit
typealias UsersBlock = (List<UserModel>) -> Unit
typealias InsertionSuccessBlock = () -> Unit
typealias PaymentsClosure = (List<Any>) -> Unit
typealias PersonsClosure = (List<PersonModel>) -> Unit
typealias DeletionSuccessBlock = () -> Unit
typealias GroupsSuccessBlock = (List<GroupModel>) -> Unit
typealias FilesSuccessBlock = (List<FileDataModel>) -> Unit

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
                    contentValue.put(SQLiteHelper.ADDRESS, userModel.address)
                    contentValue.put(SQLiteHelper.ID_CARD_NUMBER, userModel.idCardNumber)
                    contentValue.put(SQLiteHelper.SOCIAL_INSURANCE_NUMBER, userModel.socialInsuranceNumber)
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
                    profileImage = cursor?.getStringOrNull(cursor.getColumnIndex(SQLiteHelper.PROFILE_IMAGE)),
                    address = cursor?.getStringOrNull(cursor.getColumnIndex(SQLiteHelper.ADDRESS)),
                    idCardNumber = cursor?.getStringOrNull(cursor.getColumnIndex(SQLiteHelper.ID_CARD_NUMBER)),
                    socialInsuranceNumber = cursor?.getStringOrNull(cursor.getColumnIndex(SQLiteHelper.SOCIAL_INSURANCE_NUMBER))
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
                            profileImage = cursor.getStringOrNull(cursor.getColumnIndex(SQLiteHelper.PROFILE_IMAGE)),
                            address = cursor.getStringOrNull(cursor.getColumnIndex(SQLiteHelper.ADDRESS)),
                            idCardNumber = cursor.getStringOrNull(cursor.getColumnIndex(SQLiteHelper.ID_CARD_NUMBER)),
                            socialInsuranceNumber = cursor.getStringOrNull(cursor.getColumnIndex(SQLiteHelper.SOCIAL_INSURANCE_NUMBER))
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
                    it.put(SQLiteHelper.ADDRESS, userModel.address)
                    it.put(SQLiteHelper.ID_CARD_NUMBER, userModel.idCardNumber)
                    it.put(SQLiteHelper.SOCIAL_INSURANCE_NUMBER, userModel.socialInsuranceNumber)
                },
                "${SQLiteHelper.ID}=?",
                arrayOf(userModel.id?.toString())
            )
            withContext(Dispatchers.Main) {
                userBlock(userModel)
            }
        }
    }

    suspend fun insertGroup(userId: Int?, groupModel: GroupModel, insertionSuccessBlock: InsertionSuccessBlock) {
        withContext(Dispatchers.Default) {
            database?.insert(
                SQLiteHelper.GROUPS_TABLE_NAME,
                null,
                ContentValues().also {
                    it.put(SQLiteHelper.USER_ID, userId ?: return@withContext)
                    it.put(SQLiteHelper.GROUP_NAME, groupModel.groupName)
                    it.put(SQLiteHelper.COLOR, groupModel.color)
                }
            )
            withContext(Dispatchers.Main) {
                insertionSuccessBlock()
            }
        }
    }

    suspend fun insertPayment(userId: Int?, personModel: PersonModel, insertionSuccessBlock: InsertionSuccessBlock) {
        withContext(Dispatchers.Default) {
            val personId = database?.insert(
                SQLiteHelper.PERSONS_TABLE_NAME,
                null,
                ContentValues().also {
                    it.put(SQLiteHelper.USER_ID, userId ?: return@withContext)
                    it.put(SQLiteHelper.GROUP_ID, personModel.groupId)
                    it.put(SQLiteHelper.FIRST_NAME, personModel.firstName)
                    it.put(SQLiteHelper.SURNAME, personModel.surname)
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
                        it.put(SQLiteHelper.START_DATE, paymentAmountModel.startDate)
                        it.put(SQLiteHelper.PAYMENT_DATE, paymentAmountModel.paymentDate)
                        it.put(SQLiteHelper.END_DATE, paymentAmountModel.endDate)
                        it.put(SQLiteHelper.SKIP_PAYMENT, paymentAmountModel.skipPayment)
                        it.put(SQLiteHelper.PAYMENT_NOTE, paymentAmountModel.paymentNote)
                    }
                )
            }
            withContext(Dispatchers.Main) {
                insertionSuccessBlock()
            }
        }
    }

    suspend fun updatePayment(userId: Int?, personModel: PersonModel, insertionSuccessBlock: InsertionSuccessBlock) {
        withContext(Dispatchers.Default) {
            database?.update(
                SQLiteHelper.PERSONS_TABLE_NAME,
                ContentValues().also {
                    it.put(SQLiteHelper.USER_ID, userId)
                    it.put(SQLiteHelper.GROUP_ID, personModel.groupId)
                    it.put(SQLiteHelper.FIRST_NAME, personModel.firstName)
                    it.put(SQLiteHelper.SURNAME, personModel.surname)
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
                        it.put(SQLiteHelper.START_DATE, paymentAmountModel.startDate)
                        it.put(SQLiteHelper.PAYMENT_DATE, paymentAmountModel.paymentDate)
                        it.put(SQLiteHelper.END_DATE, paymentAmountModel.endDate)
                        it.put(SQLiteHelper.SKIP_PAYMENT, paymentAmountModel.skipPayment)
                        it.put(SQLiteHelper.PAYMENT_NOTE, paymentAmountModel.paymentNote)
                    }
                )
            }
            withContext(Dispatchers.Main) {
                insertionSuccessBlock()
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
                arrayOf(userId?.toString() ?: return@withContext),
                null,
                null,
                null
            )
            if (groupsCursor?.moveToFirst() == true && groupsCursor.count > 0)
                do {
                    groups.add(
                        GroupModel(
                            groupId = groupsCursor.getIntOrNull(groupsCursor.getColumnIndex(SQLiteHelper.ID)),
                            groupName = groupsCursor.getStringOrNull(groupsCursor.getColumnIndex(SQLiteHelper.GROUP_NAME)),
                            color = groupsCursor.getIntOrNull(groupsCursor.getColumnIndex(SQLiteHelper.COLOR))
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
                arrayOf(userId?.toString() ?: return@withContext),
                null,
                null,
                null)
            if (personsCursor?.moveToFirst() == true && personsCursor.count > 0)
                do {
                    val paymentsCursor = database?.query(
                        SQLiteHelper.PAYMENTS_TABLE_NAME,
                        null,
                        "${SQLiteHelper.PERSON_ID}=?",
                        arrayOf(personsCursor.getIntOrNull(personsCursor.getColumnIndex(SQLiteHelper.ID))?.toString() ?: return@withContext),
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
                                    startDate = paymentsCursor.getStringOrNull(index = paymentsCursor.getColumnIndex(SQLiteHelper.START_DATE)),
                                    paymentDate = paymentsCursor.getStringOrNull(paymentsCursor.getColumnIndex(SQLiteHelper.PAYMENT_DATE)),
                                    endDate = paymentsCursor.getStringOrNull(paymentsCursor.getColumnIndex(SQLiteHelper.END_DATE)),
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
                            surname = personsCursor.getStringOrNull(personsCursor.getColumnIndex(SQLiteHelper.SURNAME)),
                            phone = personsCursor.getStringOrNull(personsCursor.getColumnIndex(SQLiteHelper.PHONE)),
                            parentName = personsCursor.getStringOrNull(personsCursor.getColumnIndex(SQLiteHelper.PARENT_NAME)),
                            parentPhone = personsCursor.getStringOrNull(personsCursor.getColumnIndex(SQLiteHelper.PARENT_PHONE)),
                            email = personsCursor.getStringOrNull(personsCursor.getColumnIndex(SQLiteHelper.EMAIL)),
                            active = personsCursor.getIntOrNull(personsCursor.getColumnIndex(SQLiteHelper.ACTIVE)) ?: 0 > 0,
                            free = personsCursor.getIntOrNull(personsCursor.getColumnIndex(SQLiteHelper.FREE)) ?: 0 > 0,
                            payments = payments,
                            groupColor = groups.firstOrNull { it.groupId == personsCursor.getIntOrNull(personsCursor.getColumnIndex(SQLiteHelper.GROUP_ID)) }?.color
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

    suspend fun initializePayments(userId: Int?, groupId: Int?, personsClosure: PersonsClosure) {
        withContext(Dispatchers.Default) {
            val persons = arrayListOf<PersonModel>()
            val personsCursor = database?.query(
                SQLiteHelper.PERSONS_TABLE_NAME,
                null,
                "${SQLiteHelper.USER_ID}=? AND ${SQLiteHelper.GROUP_ID}=?",
                arrayOf(userId?.toString() ?: return@withContext, groupId?.toString() ?: return@withContext),
                null,
                null,
                null
            )
            val groupCursor = database?.query(
                SQLiteHelper.GROUPS_TABLE_NAME,
                null,
                "${SQLiteHelper.ID}=? AND ${SQLiteHelper.USER_ID}=?",
                arrayOf(groupId?.toString() ?: return@withContext, userId?.toString() ?: return@withContext),
                null,
                null,
                null
            )
            groupCursor?.moveToFirst()
            if (personsCursor?.moveToFirst() == true && personsCursor.count > 0)
                do {
                    val paymentsCursor = database?.query(
                        SQLiteHelper.PAYMENTS_TABLE_NAME,
                        null,
                        "${SQLiteHelper.PERSON_ID}=?",
                        arrayOf(personsCursor.getIntOrNull(personsCursor.getColumnIndex(SQLiteHelper.ID))?.toString() ?: return@withContext),
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
                                    startDate = paymentsCursor.getStringOrNull(index = paymentsCursor.getColumnIndex(SQLiteHelper.START_DATE)),
                                    paymentDate = paymentsCursor.getStringOrNull(paymentsCursor.getColumnIndex(SQLiteHelper.PAYMENT_DATE)),
                                    endDate = paymentsCursor.getStringOrNull(paymentsCursor.getColumnIndex(SQLiteHelper.END_DATE)),
                                    skipPayment = paymentsCursor.getIntOrNull(paymentsCursor.getColumnIndex(SQLiteHelper.SKIP_PAYMENT)) ?: 0 > 0
                                )
                            )
                        }
                        while(paymentsCursor.moveToNext())
                    }
                    paymentsCursor?.close()
                    persons.add(
                        PersonModel(
                            paymentId = personsCursor.getIntOrNull(personsCursor.getColumnIndex(SQLiteHelper.ID)),
                            groupId = personsCursor.getIntOrNull(personsCursor.getColumnIndex(SQLiteHelper.GROUP_ID)),
                            groupName = groupCursor?.getStringOrNull(groupCursor.getColumnIndex(SQLiteHelper.GROUP_NAME)),
                            firstName = personsCursor.getStringOrNull(personsCursor.getColumnIndex(SQLiteHelper.FIRST_NAME)),
                            surname = personsCursor.getStringOrNull(personsCursor.getColumnIndex(SQLiteHelper.SURNAME)),
                            phone = personsCursor.getStringOrNull(personsCursor.getColumnIndex(SQLiteHelper.PHONE)),
                            parentName = personsCursor.getStringOrNull(personsCursor.getColumnIndex(SQLiteHelper.PARENT_NAME)),
                            parentPhone = personsCursor.getStringOrNull(personsCursor.getColumnIndex(SQLiteHelper.PARENT_PHONE)),
                            email = personsCursor.getStringOrNull(personsCursor.getColumnIndex(SQLiteHelper.EMAIL)),
                            active = personsCursor.getIntOrNull(personsCursor.getColumnIndex(SQLiteHelper.ACTIVE)) ?: 0 > 0,
                            free = personsCursor.getIntOrNull(personsCursor.getColumnIndex(SQLiteHelper.FREE)) ?: 0 > 0,
                            payments = payments,
                            groupColor = groupCursor?.getIntOrNull(groupCursor.getColumnIndex(SQLiteHelper.COLOR))
                        )
                    )
                }
                while(personsCursor.moveToNext())
            groupCursor?.close()
            personsCursor?.close()
            withContext(Dispatchers.Main) {
                personsClosure(persons)
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
                arrayOf(userId?.toString() ?: return@withContext),
                null,
                null,
                null
            )
            if (groupsCursor?.moveToFirst() == true && groupsCursor.count > 0)
                do {
                    groups.add(
                        GroupModel(
                            groupId = groupsCursor.getIntOrNull(groupsCursor.getColumnIndex(SQLiteHelper.ID)),
                            groupName = groupsCursor.getStringOrNull(groupsCursor.getColumnIndex(SQLiteHelper.GROUP_NAME)),
                            color = groupsCursor.getIntOrNull(groupsCursor.getColumnIndex(SQLiteHelper.COLOR))
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

    suspend fun deleteGroup(groupId: Int?, deletionSuccessBlock: DeletionSuccessBlock) {
        withContext(Dispatchers.Default) {
            database?.delete(
                SQLiteHelper.GROUPS_TABLE_NAME,
                "${SQLiteHelper.ID}=?",
                arrayOf(groupId?.toString() ?: return@withContext)
            )
            database?.delete(
                SQLiteHelper.PERSONS_TABLE_NAME,
                "${SQLiteHelper.GROUP_ID}=?",
                arrayOf(groupId?.toString() ?: return@withContext)
            )
            withContext(Dispatchers.Main) {
                deletionSuccessBlock()
            }
        }
    }

    suspend fun deletePayment(paymentId: Int?, deletionSuccessBlock: DeletionSuccessBlock) {
        withContext(Dispatchers.Default) {
            database?.delete(
                SQLiteHelper.PERSONS_TABLE_NAME,
                "${SQLiteHelper.ID}=?",
                arrayOf(paymentId?.toString() ?: return@withContext)
            )
            withContext(Dispatchers.Main) {
                deletionSuccessBlock()
            }
        }
    }

    suspend fun insertFile(userId: Int?, fileDataModel: FileDataModel, insertionSuccessBlock: InsertionSuccessBlock? = null) {
        withContext(Dispatchers.Default) {
            database?.insert(
                SQLiteHelper.FILES_TABLE_NAME,
                null,
                ContentValues().also {
                    it.put(SQLiteHelper.USER_ID, userId ?: return@withContext)
                    it.put(SQLiteHelper.DESCRIPTION, fileDataModel.description)
                    it.put(SQLiteHelper.FILENAME, fileDataModel.fileName)
                    it.put(SQLiteHelper.DATE_TIME, fileDataModel.dateTime)
                    it.put(SQLiteHelper.FILE_DATA, fileDataModel.fileData)
                }
            )
            withContext(Dispatchers.Main) {
                insertionSuccessBlock?.invoke()
            }
        }
    }

    suspend fun initializeFiles(userId: Int?, filesSuccessBlock: FilesSuccessBlock) {
        withContext(Dispatchers.Default) {
            val files = arrayListOf<FileDataModel>()
            val filesCursor = database?.query(
                SQLiteHelper.FILES_TABLE_NAME,
                null,
                "${SQLiteHelper.USER_ID}=?",
                arrayOf(userId?.toString() ?: return@withContext),
                null,
                null,
                null
            )
            if (filesCursor?.moveToFirst() == true && filesCursor.count > 0)
                do {
                    files.add(
                        FileDataModel(
                            fileId = filesCursor.getIntOrNull(filesCursor.getColumnIndex(SQLiteHelper.ID)),
                            description = filesCursor.getStringOrNull(filesCursor.getColumnIndex(SQLiteHelper.DESCRIPTION)),
                            fileName = filesCursor.getStringOrNull(filesCursor.getColumnIndex(SQLiteHelper.FILENAME)),
                            dateTime = filesCursor.getStringOrNull(filesCursor.getColumnIndex(SQLiteHelper.DATE_TIME))
                        ).also {
                            it.fileData = filesCursor.getBlobOrNull(filesCursor.getColumnIndex(SQLiteHelper.FILE_DATA))
                        }
                    )
                }
                while(filesCursor.moveToNext())
            filesCursor?.close()
            withContext(Dispatchers.Main) {
                filesSuccessBlock(files)
            }
        }
    }

    suspend fun deleteFile(fileId: Int?, deletionSuccessBlock: DeletionSuccessBlock) {
        withContext(Dispatchers.Default) {
            database?.delete(
                SQLiteHelper.FILES_TABLE_NAME,
                "${SQLiteHelper.ID}=?",
                arrayOf(fileId?.toString() ?: return@withContext)
            )
            withContext(Dispatchers.Main) {
                deletionSuccessBlock()
            }
        }
    }

    fun close() {
        sqLiteHelper?.close()
    }

}
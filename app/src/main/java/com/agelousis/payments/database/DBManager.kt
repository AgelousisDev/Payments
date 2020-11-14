package com.agelousis.payments.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.core.database.getBlobOrNull
import androidx.core.database.getDoubleOrNull
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import com.agelousis.payments.login.models.UserModel
import com.agelousis.payments.main.ui.files.models.FileDataModel
import com.agelousis.payments.main.ui.payments.models.GroupModel
import com.agelousis.payments.main.ui.payments.models.PaymentAmountModel
import com.agelousis.payments.main.ui.payments.models.PersonModel
import com.agelousis.payments.utils.extensions.valueEnumOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

typealias UserBlock = (UserModel?) -> Unit
typealias UsersBlock = (List<UserModel>) -> Unit
typealias InsertionSuccessBlock = () -> Unit
typealias UpdateSuccessBlock = () -> Unit
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
                    contentValue.put(SQLiteHelper.FIRST_NAME, userModel.firstName)
                    contentValue.put(SQLiteHelper.SURNAME, userModel.lastName)
                    contentValue.put(SQLiteHelper.PROFILE_IMAGE_DATA, userModel.profileImageData)
                    contentValue.put(SQLiteHelper.VAT, userModel.vat)
                    contentValue.put(SQLiteHelper.DEFAULT_PAYMENT_AMOUNT, userModel.defaultPaymentAmount)
                    contentValue.put(SQLiteHelper.DEFAULT_MESSAGE_TEMPLATE, userModel.defaultMessageTemplate)
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
                arrayOf(SQLiteHelper.ID, SQLiteHelper.USERNAME, SQLiteHelper.PASSWORD, SQLiteHelper.BIOMETRICS, SQLiteHelper.PROFILE_IMAGE, SQLiteHelper.ADDRESS, SQLiteHelper.ID_CARD_NUMBER, SQLiteHelper.SOCIAL_INSURANCE_NUMBER, SQLiteHelper.FIRST_NAME, SQLiteHelper.SURNAME, SQLiteHelper.PROFILE_IMAGE_DATA, SQLiteHelper.VAT, SQLiteHelper.DEFAULT_PAYMENT_AMOUNT, SQLiteHelper.DEFAULT_MESSAGE_TEMPLATE),
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
                    socialInsuranceNumber = cursor?.getStringOrNull(cursor.getColumnIndex(SQLiteHelper.SOCIAL_INSURANCE_NUMBER)),
                    firstName = cursor?.getStringOrNull(cursor.getColumnIndex(SQLiteHelper.FIRST_NAME)),
                    lastName = cursor?.getStringOrNull(cursor.getColumnIndex(SQLiteHelper.SURNAME)),
                    vat = cursor?.getIntOrNull(cursor.getColumnIndex(SQLiteHelper.VAT)),
                    defaultPaymentAmount = cursor?.getDoubleOrNull(cursor.getColumnIndex(SQLiteHelper.DEFAULT_PAYMENT_AMOUNT)),
                    defaultMessageTemplate = cursor?.getStringOrNull(cursor.getColumnIndex(SQLiteHelper.DEFAULT_MESSAGE_TEMPLATE))
                ).also {
                    it.profileImageData = cursor?.getBlobOrNull(cursor.getColumnIndex(SQLiteHelper.PROFILE_IMAGE_DATA))
                }
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
            val cursor = database?.query(SQLiteHelper.USERS_TABLE_NAME, arrayOf(SQLiteHelper.ID, SQLiteHelper.USERNAME, SQLiteHelper.PASSWORD, SQLiteHelper.BIOMETRICS, SQLiteHelper.PROFILE_IMAGE, SQLiteHelper.ADDRESS, SQLiteHelper.ID_CARD_NUMBER, SQLiteHelper.SOCIAL_INSURANCE_NUMBER, SQLiteHelper.FIRST_NAME, SQLiteHelper.SURNAME, SQLiteHelper.PROFILE_IMAGE_DATA, SQLiteHelper.VAT, SQLiteHelper.DEFAULT_PAYMENT_AMOUNT, SQLiteHelper.DEFAULT_MESSAGE_TEMPLATE), null, null, null, null, null)
            if (cursor?.moveToFirst() == true)
                do {
                    usersList.add(
                        UserModel(
                            id = cursor.getIntOrNull(cursor.getColumnIndex(SQLiteHelper.ID)),
                            username = cursor.getStringOrNull(cursor.getColumnIndex(SQLiteHelper.USERNAME)),
                            password = cursor.getStringOrNull(cursor.getColumnIndex(SQLiteHelper.PASSWORD)),
                            biometrics = cursor.getIntOrNull(cursor.getColumnIndex(SQLiteHelper.BIOMETRICS)) ?: 0 > 0,
                            profileImage = cursor.getStringOrNull(cursor.getColumnIndex(SQLiteHelper.PROFILE_IMAGE)),
                            address = cursor.getStringOrNull(cursor.getColumnIndex(SQLiteHelper.ADDRESS)),
                            idCardNumber = cursor.getStringOrNull(cursor.getColumnIndex(SQLiteHelper.ID_CARD_NUMBER)),
                            socialInsuranceNumber = cursor.getStringOrNull(cursor.getColumnIndex(SQLiteHelper.SOCIAL_INSURANCE_NUMBER)),
                            firstName = cursor.getStringOrNull(cursor.getColumnIndex(SQLiteHelper.FIRST_NAME)),
                            lastName = cursor.getStringOrNull(cursor.getColumnIndex(SQLiteHelper.SURNAME)),
                            vat = cursor.getIntOrNull(cursor.getColumnIndex(SQLiteHelper.VAT)),
                            defaultPaymentAmount = cursor.getDoubleOrNull(cursor.getColumnIndex(SQLiteHelper.DEFAULT_PAYMENT_AMOUNT)),
                            defaultMessageTemplate = cursor.getStringOrNull(cursor.getColumnIndex(SQLiteHelper.DEFAULT_MESSAGE_TEMPLATE))
                        ).also {
                            it.profileImageData = cursor.getBlobOrNull(cursor.getColumnIndex(SQLiteHelper.PROFILE_IMAGE_DATA))
                        }
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
                ContentValues().also { contentValues ->
                    contentValues.put(SQLiteHelper.USERNAME, userModel.username)
                    contentValues.put(SQLiteHelper.PASSWORD, userModel.password)
                    contentValues.put(SQLiteHelper.PROFILE_IMAGE, userModel.profileImage)
                    contentValues.put(SQLiteHelper.BIOMETRICS, userModel.biometrics)
                    contentValues.put(SQLiteHelper.ADDRESS, userModel.address)
                    contentValues.put(SQLiteHelper.ID_CARD_NUMBER, userModel.idCardNumber)
                    contentValues.put(SQLiteHelper.SOCIAL_INSURANCE_NUMBER, userModel.socialInsuranceNumber)
                    contentValues.put(SQLiteHelper.FIRST_NAME, userModel.firstName)
                    contentValues.put(SQLiteHelper.SURNAME, userModel.lastName)
                    userModel.profileImageData?.let { contentValues.put(SQLiteHelper.PROFILE_IMAGE_DATA, it) }
                    contentValues.put(SQLiteHelper.VAT, userModel.vat)
                    contentValues.put(SQLiteHelper.DEFAULT_PAYMENT_AMOUNT, userModel.defaultPaymentAmount)
                    contentValues.put(SQLiteHelper.DEFAULT_MESSAGE_TEMPLATE, userModel.defaultMessageTemplate)
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
            val cursor = database?.query(
                SQLiteHelper.GROUPS_TABLE_NAME,
                arrayOf(SQLiteHelper.ID),
                "${SQLiteHelper.GROUP_NAME}=?",
                arrayOf(groupModel.groupName),
                null,
                null,
                null,
                null
            )
            if (cursor?.count ?: 0 == 0)
                database?.insert(
                    SQLiteHelper.GROUPS_TABLE_NAME,
                    null,
                    ContentValues().also {
                        it.put(SQLiteHelper.USER_ID, userId ?: return@withContext)
                        it.put(SQLiteHelper.GROUP_NAME, groupModel.groupName)
                        it.put(SQLiteHelper.COLOR, groupModel.color)
                        it.put(SQLiteHelper.GROUP_IMAGE, groupModel.groupImage)
                        it.put(SQLiteHelper.GROUP_IMAGE_DATA, groupModel.groupImageData)
                    }
                )
            cursor?.close()
            withContext(Dispatchers.Main) {
                insertionSuccessBlock()
            }
        }
    }

    suspend fun updateGroup(groupModel: GroupModel, updateSuccessBlock: UpdateSuccessBlock) {
        withContext(Dispatchers.Default) {
            database?.update(
                SQLiteHelper.GROUPS_TABLE_NAME,
                ContentValues().also {
                    it.put(SQLiteHelper.GROUP_NAME, groupModel.groupName)
                    it.put(SQLiteHelper.COLOR, groupModel.color)
                    it.put(SQLiteHelper.GROUP_IMAGE, groupModel.groupImage)
                    it.put(SQLiteHelper.GROUP_IMAGE_DATA, groupModel.groupImageData)
                },
                "${SQLiteHelper.ID}=?",
                arrayOf(groupModel.groupId?.toString())
            )
            withContext(Dispatchers.Main) {
                updateSuccessBlock()
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
                    it.put(SQLiteHelper.MESSAGE_TEMPLATE, personModel.messageTemplate)
                    it.put(SQLiteHelper.PAYMENT_TYPE, personModel.paymentType?.name ?: "")
                }
            )
            personModel.payments?.forEach { paymentAmountModel ->
                database?.insert(
                    SQLiteHelper.PAYMENTS_TABLE_NAME,
                    null,
                    ContentValues().also {
                        it.put(SQLiteHelper.PERSON_ID, personId?.toInt())
                        it.put(SQLiteHelper.PAYMENT_AMOUNT, paymentAmountModel.paymentAmount)
                        it.put(SQLiteHelper.PAYMENT_MONTH, paymentAmountModel.paymentMonth)
                        it.put(SQLiteHelper.PAYMENT_DATE, paymentAmountModel.paymentDate)
                        it.put(SQLiteHelper.SKIP_PAYMENT, paymentAmountModel.skipPayment)
                        it.put(SQLiteHelper.PAYMENT_NOTE, paymentAmountModel.paymentNote)
                        it.put(SQLiteHelper.PAYMENT_DATE_NOTIFICATION, paymentAmountModel.paymentDateNotification)
                        it.put(SQLiteHelper.SINGLE_PAYMENT, paymentAmountModel.singlePayment)
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
                    it.put(SQLiteHelper.MESSAGE_TEMPLATE, personModel.messageTemplate)
                    it.put(SQLiteHelper.PAYMENT_TYPE, personModel.paymentType?.name ?: "")
                },
                "${SQLiteHelper.ID}=?",
                arrayOf(personModel.personId?.toString())
            )
            database?.delete(
                SQLiteHelper.PAYMENTS_TABLE_NAME,
                "${SQLiteHelper.PERSON_ID}=?",
                arrayOf(personModel.personId?.toString())
            )
            personModel.payments?.forEach { paymentAmountModel ->
                database?.insert(
                    SQLiteHelper.PAYMENTS_TABLE_NAME,
                    null,
                    ContentValues().also {
                        it.put(SQLiteHelper.PERSON_ID, personModel.personId)
                        it.put(SQLiteHelper.PAYMENT_AMOUNT, paymentAmountModel.paymentAmount)
                        it.put(SQLiteHelper.PAYMENT_MONTH, paymentAmountModel.paymentMonth)
                        it.put(SQLiteHelper.PAYMENT_DATE, paymentAmountModel.paymentDate)
                        it.put(SQLiteHelper.SKIP_PAYMENT, paymentAmountModel.skipPayment)
                        it.put(SQLiteHelper.PAYMENT_NOTE, paymentAmountModel.paymentNote)
                        it.put(SQLiteHelper.PAYMENT_DATE_NOTIFICATION, paymentAmountModel.paymentDateNotification)
                        it.put(SQLiteHelper.SINGLE_PAYMENT, paymentAmountModel.singlePayment)
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
                            color = groupsCursor.getIntOrNull(groupsCursor.getColumnIndex(SQLiteHelper.COLOR)),
                            groupImage = groupsCursor.getStringOrNull(groupsCursor.getColumnIndex(SQLiteHelper.GROUP_IMAGE))
                        ).also {
                            it.groupImageData = groupsCursor.getBlobOrNull(groupsCursor.getColumnIndex(SQLiteHelper.GROUP_IMAGE_DATA))
                        }
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
                                    paymentMonth = paymentsCursor.getStringOrNull(index = paymentsCursor.getColumnIndex(SQLiteHelper.PAYMENT_MONTH)),
                                    paymentDate = paymentsCursor.getStringOrNull(paymentsCursor.getColumnIndex(SQLiteHelper.PAYMENT_DATE)),
                                    skipPayment = paymentsCursor.getIntOrNull(paymentsCursor.getColumnIndex(SQLiteHelper.SKIP_PAYMENT)) ?: 0 > 0,
                                    paymentDateNotification = paymentsCursor.getIntOrNull(paymentsCursor.getColumnIndex(SQLiteHelper.PAYMENT_DATE_NOTIFICATION)) ?: 0 > 0,
                                    singlePayment = paymentsCursor.getIntOrNull(paymentsCursor.getColumnIndex(SQLiteHelper.SINGLE_PAYMENT)) ?: 0 > 0
                                )
                            )
                        }
                        while(paymentsCursor.moveToNext())
                    }
                    paymentsCursor?.close()
                    genericList.add(
                        PersonModel(
                            personId = personsCursor.getIntOrNull(personsCursor.getColumnIndex(SQLiteHelper.ID)),
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
                            messageTemplate = personsCursor.getStringOrNull(personsCursor.getColumnIndex(SQLiteHelper.MESSAGE_TEMPLATE)),
                            payments = payments,
                            groupColor = groups.firstOrNull { it.groupId == personsCursor.getIntOrNull(personsCursor.getColumnIndex(SQLiteHelper.GROUP_ID)) }?.color,
                            groupImage = groups.firstOrNull { it.groupId == personsCursor.getIntOrNull(personsCursor.getColumnIndex(SQLiteHelper.GROUP_ID)) }?.groupImage,
                            paymentType = valueEnumOrNull(name = personsCursor.getStringOrNull(personsCursor.getColumnIndex(SQLiteHelper.PAYMENT_TYPE)) ?: "")
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
                                    paymentMonth = paymentsCursor.getStringOrNull(index = paymentsCursor.getColumnIndex(SQLiteHelper.PAYMENT_MONTH)),
                                    paymentDate = paymentsCursor.getStringOrNull(paymentsCursor.getColumnIndex(SQLiteHelper.PAYMENT_DATE)),
                                    skipPayment = paymentsCursor.getIntOrNull(paymentsCursor.getColumnIndex(SQLiteHelper.SKIP_PAYMENT)) ?: 0 > 0,
                                    paymentDateNotification = paymentsCursor.getIntOrNull(paymentsCursor.getColumnIndex(SQLiteHelper.PAYMENT_DATE_NOTIFICATION)) ?: 0 > 0,
                                    singlePayment = paymentsCursor.getIntOrNull(paymentsCursor.getColumnIndex(SQLiteHelper.SINGLE_PAYMENT)) ?: 0 > 0
                                )
                            )
                        }
                        while(paymentsCursor.moveToNext())
                    }
                    paymentsCursor?.close()
                    persons.add(
                        PersonModel(
                            personId = personsCursor.getIntOrNull(personsCursor.getColumnIndex(SQLiteHelper.ID)),
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
                            messageTemplate = personsCursor.getStringOrNull(personsCursor.getColumnIndex(SQLiteHelper.MESSAGE_TEMPLATE)),
                            payments = payments,
                            groupColor = groupCursor?.getIntOrNull(groupCursor.getColumnIndex(SQLiteHelper.COLOR)),
                            groupImage = groupCursor?.getStringOrNull(groupCursor.getColumnIndex(SQLiteHelper.GROUP_IMAGE)),
                            paymentType = valueEnumOrNull(name = personsCursor.getStringOrNull(personsCursor.getColumnIndex(SQLiteHelper.PAYMENT_TYPE)) ?: "")
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
                            color = groupsCursor.getIntOrNull(groupsCursor.getColumnIndex(SQLiteHelper.COLOR)),
                            groupImage = groupsCursor.getStringOrNull(groupsCursor.getColumnIndex(SQLiteHelper.GROUP_IMAGE))
                        ).also {
                            it.groupImageData = groupsCursor.getBlobOrNull(groupsCursor.getColumnIndex(SQLiteHelper.GROUP_IMAGE_DATA))
                        }
                    )
                }
                while(groupsCursor.moveToNext())
            groupsCursor?.close()
            withContext(Dispatchers.Main) {
                groupsSuccessBlock(groups)
            }
        }
    }

    suspend fun initializeGroups(groupsSuccessBlock: GroupsSuccessBlock) {
        withContext(Dispatchers.Default) {
            val groups = arrayListOf<GroupModel>()
            val groupsCursor = database?.query(
                SQLiteHelper.GROUPS_TABLE_NAME,
                null,
                null,
                null,
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
                            color = groupsCursor.getIntOrNull(groupsCursor.getColumnIndex(SQLiteHelper.COLOR)),
                            groupImage = groupsCursor.getStringOrNull(groupsCursor.getColumnIndex(SQLiteHelper.GROUP_IMAGE))
                        ).also {
                            it.groupImageData = groupsCursor.getBlobOrNull(groupsCursor.getColumnIndex(SQLiteHelper.GROUP_IMAGE_DATA))
                        }
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

    suspend fun clearPayments(userId: Int?, deletionSuccessBlock: DeletionSuccessBlock) {
        withContext(Dispatchers.Default) {
            database?.delete(
                SQLiteHelper.PERSONS_TABLE_NAME,
                "${SQLiteHelper.USER_ID}=?",
                arrayOf(userId?.toString() ?: return@withContext)
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
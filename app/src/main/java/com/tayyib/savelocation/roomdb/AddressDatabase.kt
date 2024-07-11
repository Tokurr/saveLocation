package com.tayyib.savelocation.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Address::class], version = 1)
abstract class AddressDatabase : RoomDatabase() {
    abstract fun userDao(): AddressDao

}
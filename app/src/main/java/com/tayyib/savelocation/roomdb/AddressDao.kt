package com.tayyib.savelocation.roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tayyib.savelocation.roomdb.Address

@Dao
interface AddressDao {

    @Query("select * from address")
    suspend fun getAll() :List<Address>

    @Insert
    suspend fun insert(address: Address)


}
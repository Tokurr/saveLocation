package com.tayyib.savelocation.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.tayyib.savelocation.roomdb.Address
import com.tayyib.savelocation.roomdb.AddressDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapsViewModel (application: Application): AndroidViewModel(application){

    fun addDataSqlite(address: Address)
    {
        viewModelScope.launch {
            withContext(Dispatchers.IO)
            {
                val db = Room.databaseBuilder(
                    getApplication(),
                    AddressDatabase::class.java, "AddressDatabase"
                ).build()

                val addressDao = db.userDao()

                addressDao.insert(address)

            }

        }
    }


}
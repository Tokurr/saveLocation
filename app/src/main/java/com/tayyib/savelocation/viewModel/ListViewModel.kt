package com.tayyib.savelocation.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.tayyib.savelocation.roomdb.Address
import com.tayyib.savelocation.roomdb.AddressDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListViewModel (application: Application): AndroidViewModel(application) {

    val addressList = MutableLiveData<List<Address>>()



    fun getAllAddress() {


        viewModelScope.launch(Dispatchers.IO) {
            val db = Room.databaseBuilder(
                getApplication(),
                AddressDatabase::class.java, "AddressDatabase"
            ).build()

            val addressDao = db.userDao()


            addressList.postValue(addressDao.getAll())
            println("listviewModel $addressList")
        }




    }
}
package com.tayyib.savelocation.roomdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Address (
    @ColumnInfo(name = "street") var street : String?,
    @ColumnInfo(name = "neighborhood") var neighborhood : String?,
    @ColumnInfo(name = "city") var city : String?,
    @ColumnInfo(name = "number") var number : String?,
    @ColumnInfo(name = "country") var country : String?,
    @ColumnInfo(name = "fullAddress") var fullAddress : String?



) {

    @PrimaryKey(autoGenerate = true) var id : Int = 0

}
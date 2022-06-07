package com.tgapps.sgakaizen

import java.text.SimpleDateFormat
import java.util.*

class GetDate{
    fun getCurrentDate():String{
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.format(Date())}
}
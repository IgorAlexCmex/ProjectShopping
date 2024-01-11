package activity

import android.app.Application
import db.MainDb
import utilities.myLog

class MainAppShoppingList:Application() {
    val db by lazy { MainDb.getDataBase(this) }
}
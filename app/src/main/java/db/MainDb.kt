package db


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import tables.LibraryElements
import tables.NoteList
import tables.ShoppingList
import tables.ShoppingListName

@Database(entities = [LibraryElements::class,
    NoteList::class,ShoppingList::class,ShoppingListName::class], version = 1,
    exportSchema = true
)

 abstract class MainDb:RoomDatabase() {
     abstract fun getDaoDb():DaoDb
     companion object {
         @Volatile
         private var INSTANCE: MainDb? = null

         @OptIn(InternalCoroutinesApi::class)
         fun getDataBase(context: Context): MainDb {
             return INSTANCE ?: synchronized(this) {
                 val instance = Room.databaseBuilder(
                     context.applicationContext,
                     MainDb::class.java,
                     "my.db"
                 ).build()
                 instance
             }
         }
     }
}

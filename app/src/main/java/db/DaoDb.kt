package db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import tables.LibraryElements
import tables.NoteList
import tables.ShoppingList
import tables.ShoppingListName

@Dao
interface DaoDb {
  //============Insert==============
    @Insert
  suspend fun insertTabNoteList(noteList: NoteList)

  @Insert
suspend fun insertTabNameList(nameList: ShoppingListName)
    @Insert
suspend fun insertTabItem(item:ShoppingList)
    @Insert
    suspend fun insertTabLibrary(itemLibrary:LibraryElements)

//=======================================

//================READ========================

    @Query("SELECT * FROM note_list")
    fun readAllNoteList() : kotlinx.coroutines.flow.Flow<List<NoteList>>

  @Query("SELECT * FROM shopping_list_names")
  fun readAllNameList() : kotlinx.coroutines.flow.Flow<List<ShoppingListName>>

    @Query("SELECT * FROM shopping_list WHERE idNameList LIKE:idName")
    fun readItemListByIdName(idName:Int) : kotlinx.coroutines.flow.Flow<List<ShoppingList>>

    @Query("SELECT * FROM library_elements")//весь список библиотеки
    fun readAllLibraryList() : kotlinx.coroutines.flow.Flow<List<LibraryElements>>
    @Query("SELECT * FROM library_elements WHERE nameElement LIKE:nameShop")
   suspend  fun readLibraryByName(nameShop:String): List<LibraryElements>

//==============================================

//==================DELETE======================
    @Query("DELETE  FROM note_list WHERE id IS :idNote")
    suspend fun deleteNote(idNote:Int)

  @Query("DELETE  FROM shopping_list_names WHERE id IS :idNote")
  suspend fun deleteName(idNote:Int)

    @Query("DELETE  FROM shopping_list WHERE id IS :idItem")
    suspend fun deleteItem(idItem:Int)
    @Query("DELETE FROM shopping_list WHERE idNameList LIKE:idName")
    suspend fun deleteItemListByName(idName: Int)
    @Query("DELETE  FROM library_elements WHERE id IS :idItemLibrary")
    suspend fun deleteItemLibrary(idItemLibrary:Int)
//==========================================

//=================UPDATE===========================
  @Update
  suspend fun updateNoteList(noteList: NoteList)

  @Update
  suspend fun updateNameList(nameList: ShoppingListName)

    @Update
    suspend fun updateItemList(itemList: ShoppingList)

    @Update
    suspend fun updateItemLibraryList(itemLibrary: LibraryElements)
//==================================================
}
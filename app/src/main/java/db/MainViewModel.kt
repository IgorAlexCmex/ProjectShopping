package db

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import tables.LibraryElements
import tables.NoteList
import tables.ShoppingList
import tables.ShoppingListName

class MainViewModel( db: MainDb):ViewModel() {
    var libraryElements=MutableLiveData<List<LibraryElements>>()
//=====================READ=======================================
    private val daoDd=db.getDaoDb()
     var notesListData:LiveData<List<NoteList>> =daoDd.readAllNoteList().asLiveData()
     var namesListData:LiveData<List<ShoppingListName>> =daoDd.readAllNameList().asLiveData()
    fun readItemByIdName(idName:Int):LiveData<List<ShoppingList>> {
        return daoDd.readItemListByIdName(idName).asLiveData()
    }

     fun readLibrary(name:String)=viewModelScope.launch{
        libraryElements.postValue(daoDd.readLibraryByName(name))
    }
//=====================Insert==================================
     fun insertNote(noteList: NoteList) =viewModelScope.launch{
        daoDd.insertTabNoteList(noteList)
    }
    fun insertName(name: ShoppingListName) =viewModelScope.launch{
        daoDd.insertTabNameList(name)
    }

    fun insertItem(item: ShoppingList) =viewModelScope.launch{
        daoDd.insertTabItem(item)
       if(daoDd.readLibraryByName(item.nameItem).isEmpty()){
           daoDd.insertTabLibrary(LibraryElements(null,item.nameItem,null))
       }
    }

    fun insertItemLibrary(itemLibrary: LibraryElements) =viewModelScope.launch{
        daoDd.insertTabLibrary(itemLibrary)
    }
//=====================Update===================================
    fun updateNote(noteList: NoteList) =viewModelScope.launch{
        daoDd.updateNoteList(noteList)
    }

    fun updateName(name: ShoppingListName) =viewModelScope.launch{
        daoDd.updateNameList(name)
    }

    fun updateItem(item: ShoppingList) =viewModelScope.launch{
        daoDd.updateItemList(item)
    }

    fun updateItemLibrary(itemLibrary: LibraryElements) =viewModelScope.launch{
        daoDd.updateItemLibraryList(itemLibrary)
    }
//=======================Delete===================================
    fun deleteNote(id: Int) =viewModelScope.launch{
        daoDd.deleteNote(id)
    }

    fun deleteNameAndListItem(idName: Int) =viewModelScope.launch{
        daoDd.deleteName(idName)
        daoDd.deleteItemListByName(idName)
    }

    fun deleteItem(id: Int) =viewModelScope.launch{
        daoDd.deleteItem(id)
    }

    fun deleteItemLibrary(id: Int) =viewModelScope.launch{
        daoDd.deleteItemLibrary(id)
    }
    fun deleteItemListByName(idName:Int)=viewModelScope.launch {
        daoDd.deleteItemListByName(idName)
    }
//===================================================================
    class MainViewModelFactory( val database: MainDb): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(database) as T
            }
            throw IllegalArgumentException("Error")
        }
    }
}
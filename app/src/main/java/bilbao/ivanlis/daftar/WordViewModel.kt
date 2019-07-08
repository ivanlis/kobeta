package bilbao.ivanlis.daftar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import bilbao.ivanlis.daftar.database.NotebookDb
import bilbao.ivanlis.daftar.database.NotebookRepository
import bilbao.ivanlis.daftar.dialog.DeletionDialogFragment
import kotlinx.coroutines.*
import timber.log.Timber

abstract class WordViewModel(application: Application, wordId: Long):
    AndroidViewModel(application), DeletionDialogFragment.DeletionDialogListener {

    var repository: NotebookRepository = NotebookRepository(NotebookDb.getInstance(application).notebookDao())
    protected val wordId = wordId

    protected var viewModelJob = Job()
    protected val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    protected val _saveData = MutableLiveData<Boolean>()
    val saveData: LiveData<Boolean>
        get() = _saveData

    private val _executeDelete = MutableLiveData<Boolean>()
    val executeDelete: LiveData<Boolean>
        get() = _executeDelete

    private val _deleteRequest = MutableLiveData<Boolean>()
    val deleteRequest: LiveData<Boolean>
        get() = _deleteRequest

    init {
        _saveData.value = false
        _executeDelete.value = false
    }

    fun onSaveClicked() {
        _saveData.value = true
    }

    override fun onConfirmedDeleteRequest() {
        _executeDelete.value = true
    }

    fun onExecuteDeleteComplete() {
        _executeDelete.value = false
    }

    fun onDeleteRequest() {
        _deleteRequest.value = true
    }

    fun onDeleteRequestComplete() {
        _deleteRequest.value = false
    }

    fun onSaveData(userInput: WordFormInput) {

//        _saveData.value?.let {
//            if (!it)
//                return
//        }

        Timber.d("onSaveData()")

        uiScope.launch {

            withContext(Dispatchers.IO) {
                repository.updateWordById(wordId, userInput.translation)
                executeSave(userInput)
            }
        }
    }

    fun onSaveDataComplete() { _saveData.value = false }

    fun onExecuteDeleteWord() {

        onExecuteDeleteComplete()

        Timber.d("Deleting word $wordId...")
        uiScope.launch {
            withContext(Dispatchers.IO) {
                repository.deleteWordById(wordId)
            }
        }
    }

    protected abstract suspend fun executeSave(userInput: WordFormInput)
}
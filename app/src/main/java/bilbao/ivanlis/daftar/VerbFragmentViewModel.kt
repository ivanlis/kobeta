package bilbao.ivanlis.daftar

import android.app.Application
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import bilbao.ivanlis.daftar.constants.WordScreenMode


class VerbFragmentViewModel(application: Application, wordId: Long, mode: WordScreenMode = WordScreenMode.EDIT,
                            userAnswer: WordFormInput? = null):
        WordViewModel(application, wordId, mode, userAnswer) {


    val verbForms = repository.extractArabicVerbForms(wordId)

    val verbTranslation = MediatorLiveData<String>()
    val verbPast = MediatorLiveData<String>()
    val verbNonpast = MediatorLiveData<String>()
    val verbVerbalNoun = MediatorLiveData<String>()

    init {
        verbTranslation.addSource(verbForms) {
            if (verbTranslation.value == null)
                it?.let { vForms ->
                    verbTranslation.value = vForms.translation
                }
        }
        verbPast.addSource(verbForms) {
            if (modeToContentFromDBDisplay(screenMode) && verbPast.value == null) {
                if (screenMode == WordScreenMode.EDIT) {
                    it?.let { vForms ->
                        verbPast.value = vForms.pastForm
                    }
                }
            }
        }
        verbNonpast.addSource(verbForms) {
            if (modeToContentFromDBDisplay(screenMode) && verbNonpast.value == null) {
                if (screenMode == WordScreenMode.EDIT) {
                    it?.let { vForms ->
                        verbNonpast.value = vForms.nonpastForm
                    }
                }
            }
        }
        verbVerbalNoun.addSource(verbForms) {
            if (modeToContentFromDBDisplay(screenMode) && verbVerbalNoun.value == null) {
                if (screenMode == WordScreenMode.EDIT) {
                    it?.let { vForms ->
                        verbVerbalNoun.value = vForms.verbalNounForm
                    }
                }
            }
        }
    }

    override suspend fun executeSave(userInput: WordFormInput) {
        verbForms.value?.let { verbFormsVal ->
            repository.updateWordRecordByWordAndForm(wordId, verbFormsVal.pastFormId, userInput.pastForm)
            repository.updateWordRecordByWordAndForm(wordId, verbFormsVal.nonpastFormId, userInput.nonpastForm)
            repository.updateWordRecordByWordAndForm(wordId, verbFormsVal.verbalNounFormId, userInput.verbnounForm)
        }
    }
}

class VerbFragmentViewModelFactory(private val application: Application,
                                   private val wordId: Long,
                                   private val mode: WordScreenMode = WordScreenMode.EDIT,
                                   private val userAnswer: WordFormInput? = null):
    ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VerbFragmentViewModel::class.java)) {
            return VerbFragmentViewModel(application, wordId, mode, userAnswer) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

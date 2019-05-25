package bilbao.ivanlis.kobeta.database

import android.content.Context
import android.util.Log.d
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Language::class,
        PartOfSpeech::class, Form::class, Lesson::class,
        Word::class, Score::class, WordRecord::class],
    version = 1, exportSchema = false)
abstract class NotebookDb : RoomDatabase() {

    abstract fun notebookDao(): NotebookDao

    // make this a singleton
    companion object {
        @Volatile
        private var INSTANCE: NotebookDb? = null

        fun getInstance(context: Context): NotebookDb {

            d("NotebookDb.getInstance", "Accessing DB instance...")

            return INSTANCE ?: synchronized(this) {
                // create database
                d("NotebookDb.getInstance", "Calling buildDatabase()...")
                val instance = buildDatabase(context)
                d("NotebookDb.getInstance", "Instance ready")
                INSTANCE = instance
                instance
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                NotebookDb::class.java, "notebook_data")

                .addCallback(

                    object : Callback() {
                        override fun onOpen(db: SupportSQLiteDatabase) {

                            d("onOpen", "On database opening...")

                            super.onOpen(db)

                            //TODO: call prePopulateArabic() in an appropriate thread
                            val ioScope = CoroutineScope(Dispatchers.IO)
                            ioScope.launch {
                                prePopulateArabic(context)

                                //TODO: temporary
                                addFakeLessons(context)
                            }
                        }

                        override fun onCreate(db: SupportSQLiteDatabase) {
                            d("onOpen", "On database creation...")
                            super.onCreate(db)
                        }
                    }

                )
                .fallbackToDestructiveMigration()

                .build()

        //TODO: suspend needed?
        private fun prePopulateArabic(context: Context) {

            if (getInstance(context).notebookDao().getLanguageCount() > 0)
                d("prePopulateArabic", "There are registered languages. Not adding anything.")
            else {

                d("prePopulateArabic", "Inserting Arabic and everything related.")

                getInstance(context).notebookDao().registerLanguage("", "arabic")

                getInstance(context).notebookDao().registerPartOfSpeech(
                    "arabic",
                    "", "noun"
                )
                getInstance(context).notebookDao().registerPartOfSpeech(
                    "arabic",
                    "", "verb"
                )
                getInstance(context).notebookDao().registerPartOfSpeech(
                    "arabic",
                    "", "particle"
                )

                getInstance(context).notebookDao().registerForm(
                    "arabic", "noun",
                    "", "singular", true
                )
                getInstance(context).notebookDao().registerForm(
                    "arabic", "noun",
                    "", "plural", false
                )
                getInstance(context).notebookDao().registerForm(
                    "arabic", "verb",
                    "", "past", true
                )
                getInstance(context).notebookDao().registerForm(
                    "arabic", "verb",
                    "", "nonpast", false
                )
                getInstance(context).notebookDao().registerForm(
                    "arabic", "verb",
                    "", "verbalnoun", false
                )
                getInstance(context).notebookDao().registerForm(
                    "arabic", "particle",
                    "", "particle", true
                )
            }
        }

        private fun addFakeLessons(context: Context) {
            val currentLessonCount = getInstance(context).notebookDao().getLessonCount()
            if (currentLessonCount > 0)
                d("addFakeLessons", "We already have a lot of lessons, not creating anything")
            else {
                d("addFakeLessons", "Adding fake lessons...")
                val fakeLesson1 = Lesson(name = "Fake lesson 1.", creationDateTime = System.currentTimeMillis())
                val newId = getInstance(context).notebookDao().insertLesson(fakeLesson1)
                d("addFakeLessons", "Fake id = ${newId}")
            }
        }
    }

}
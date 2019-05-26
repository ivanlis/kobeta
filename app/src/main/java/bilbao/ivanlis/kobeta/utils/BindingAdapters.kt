package bilbao.ivanlis.kobeta.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import bilbao.ivanlis.kobeta.database.LessonItemForList
import java.text.SimpleDateFormat

@BindingAdapter("lessonNameFormatted")
fun TextView.lessonNameFormatted(item: LessonItemForList?) {
    item?.let {
        text = item.name
    }
}

@BindingAdapter("creationDateTimeFormatted")
fun TextView.creationDateTimeFormatted(item: LessonItemForList?) {
    item?.let {
        text = SimpleDateFormat("yyyy-MM-dd").format(item.creationDateTime)
    }
}

@BindingAdapter("wordCountFormatted")
fun TextView.wordCountFormatted(item: LessonItemForList?) {
    item?.let {
        text = item.wordCount.toString()
    }
}
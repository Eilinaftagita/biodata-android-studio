package com.gitata.uas_aplikasi_biodata.helper

import android.database.Cursor
import  com.gitata.uas_aplikasi_biodata.db.DatabaseContract
import  com.gitata.uas_aplikasi_biodata.entity.Note
import java.util.*

object MappingHelper {

    fun mapCursorToArrayList(notesCursor: Cursor?): ArrayList<Note> {
        val notesList = ArrayList<Note>()

        notesCursor?.apply {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseContract.NoteColumns._ID))
                val nim = getString(getColumnIndexOrThrow(DatabaseContract.NoteColumns.NIM))
                val nama = getString(getColumnIndexOrThrow(DatabaseContract.NoteColumns.NAMA))
                val alamat = getString(getColumnIndexOrThrow(DatabaseContract.NoteColumns.ALAMAT))
                val email = getString(getColumnIndexOrThrow(DatabaseContract.NoteColumns.EMAIL))
                val date = getString(getColumnIndexOrThrow(DatabaseContract.NoteColumns.DATE))
                notesList.add(Note(id, nim, nama, alamat, email, date))
            }
        }
        return notesList
    }
}
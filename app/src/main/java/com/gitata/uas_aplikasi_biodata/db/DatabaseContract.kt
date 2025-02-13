package com.gitata.uas_aplikasi_biodata.db

import android.provider.BaseColumns

internal class DatabaseContract {
    internal class NoteColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "note"
            const val _ID = "_id"
            const val NIM = "nim"
            const val NAMA = "nama"
            const val ALAMAT = "alamat"
            const val EMAIL = "email"
            const val DATE = "date"

        }

    }
}
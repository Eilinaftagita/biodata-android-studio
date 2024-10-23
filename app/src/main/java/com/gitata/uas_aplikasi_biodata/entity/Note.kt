package com.gitata.uas_aplikasi_biodata.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Note(
    var id: Int = 0,
    var nim: String? = null,
    var nama: String? = null,
    var alamat: String? = null,
    var email: String? = null,
    var date: String? = null
) :Parcelable
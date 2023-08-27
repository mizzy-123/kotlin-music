package com.tubes.musicappproject

object dataGridLibrary {
    private val gridNameLibrary: Array<String> = arrayOf("ALBUM LIST", "TRACK LIST", "FOLDER LIST", "PLAYLIST")

    private val gridIdLibrary: Array<String> = arrayOf("1", "2", "3", "4")

    private val gridImageLibrary = intArrayOf(
        R.drawable.album,
        R.drawable.cover_white,
        R.drawable.folder,
        R.drawable.playlist
    )

    val dataGrid: ArrayList<GridLibrary>
        get() {
            val list = arrayListOf<GridLibrary>()
            for (posisition in gridImageLibrary.indices){
                val library = GridLibrary()
                library.nameLibrary = gridNameLibrary[posisition]
                library.imageLibrary = gridImageLibrary[posisition]
                library.id = gridIdLibrary[posisition]
                list.add(library)
            }
            return list
        }
}
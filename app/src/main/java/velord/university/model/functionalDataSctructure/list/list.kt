package velord.university.model.functionalDataSctructure.list

import java.util.*

fun List<String>.sortByString(sorter: String): List<String> =
    this.map { it to it.contains(sorter, true) }
        .sortedWith(compareBy( { !it.second }, { it.first.toUpperCase(Locale.ROOT) } ))
        .map { it.first }
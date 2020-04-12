package util.list

fun List<String>.sortByString(sorter: String): List<String> =
    this.map { it to it.contains(sorter, true) }
        .sortedWith(compareBy( { !it.second }, { it.first.toUpperCase() } ))
        .map { it.first }
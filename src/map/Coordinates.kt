package map

class Coordinates(row : Int, col : Int) {
    private var _columnNumber = col
    private var _rowNumber = row

    fun getCoordinates() : Pair<Int, Int> {
        return Pair(_columnNumber, _rowNumber)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Coordinates

        if (_columnNumber != other._columnNumber) return false
        if (_rowNumber != other._rowNumber) return false

        return true
    }

    override fun hashCode(): Int {
        var result = _columnNumber
        result = 31 * result + _rowNumber
        return result
    }


}
package velord.university.repository.db.transaction

import velord.university.repository.db.transaction.hub.BaseTransaction

object SongWithPosTransaction : BaseTransaction() {

    override val TAG: String = "SongWithPosTransaction"
}
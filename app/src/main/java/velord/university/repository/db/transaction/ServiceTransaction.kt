package velord.university.repository.db.transaction

import velord.university.repository.db.dao.MiniPlayerServiceSongDao
import velord.university.repository.db.transaction.hub.BaseTransaction

object ServiceTransaction : BaseTransaction() {

    override val TAG: String = "ServiceTransaction"

}
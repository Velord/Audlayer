package velord.university.repository.db.transaction

import velord.university.repository.db.dao.RadioStationDao
import velord.university.repository.db.transaction.hub.BaseTransaction

object RadioTransaction : BaseTransaction() {

    override val TAG: String = "RadioTransaction"


}
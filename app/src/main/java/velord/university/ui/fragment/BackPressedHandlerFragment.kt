package velord.university.ui.fragment

abstract class BackPressedHandlerFragment : LoggerSelfLifecycleFragment() {

    override val TAG: String
        get() = "BackPressedHandlerFragment"

    abstract fun onBackPressed(): Boolean
}
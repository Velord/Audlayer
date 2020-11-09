package velord.university.ui.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

val replaceFragment: (FragmentManager, Fragment, Int) -> Unit =
    { fm, fragment, containerId ->
        fm.beginTransaction()
            .replace(containerId, fragment)
            .addToBackStack(null)
            .commit()
    }

val initFragment: (FragmentManager, Fragment, Int) -> Unit =
    {fm,  fragment, containerId ->
        val currentFragment =
            fm.findFragmentById(containerId)

        if (currentFragment == null)
            replaceFragment(fm, fragment, containerId)
    }

val addFragment: (FragmentManager, Fragment, Int) -> Unit =
    { fm,  fragment, containerId ->
        fm.beginTransaction()
            .add(containerId, fragment)
            .addToBackStack(null)
            .commit()
    }

inline fun <reified T: Fragment> FragmentManager.findBy(): T =
    this.fragments.reversed().find {
        T::class.java.isAssignableFrom(it.javaClass)
    } as T

inline fun <reified T: Fragment> FragmentManager.firstAs(): T? {
    val frag = this.fragments.first()
    val classOf = T::class.java.isAssignableFrom(frag.javaClass)
    return if(classOf) frag as T
    else null
}
package velord.university.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

val addFragment: (FragmentManager, Fragment, Int) -> Unit =
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
            addFragment(fm, fragment, containerId)
    }

val replaceFragment: (FragmentManager, Fragment, Int) -> Unit =
    { fm,  fragment, containerId ->
        fm.beginTransaction()
            .add(containerId, fragment)
            .commit()
    }
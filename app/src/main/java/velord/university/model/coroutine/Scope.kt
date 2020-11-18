package velord.university.model.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

suspend fun <T> onMain(f: suspend () -> T): T = withContext(Dispatchers.Main) { f() }

suspend fun <T> onIO(f: suspend () -> T): T = withContext(Dispatchers.IO) { f() }

suspend fun <T> onDef(f: suspend () -> T): T = withContext(Dispatchers.Default) { f() }

fun getScope(): CoroutineScope = CoroutineScope(Job() + Dispatchers.Default)
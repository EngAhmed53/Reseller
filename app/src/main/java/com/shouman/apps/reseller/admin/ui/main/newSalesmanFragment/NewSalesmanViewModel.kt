package com.shouman.apps.reseller.admin.ui.main.newSalesmanFragment

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.*
import com.google.firebase.dynamiclinks.DynamicLink.AndroidParameters
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.shouman.apps.reseller.admin.data.database.ResellerDatabase
import com.shouman.apps.reseller.admin.preferences.UserPreferences
import com.shouman.apps.reseller.admin.repository.BranchesRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception

enum class InvitationLinkBuildStatus {
    STARTED,
    TIMED_OUT,
    ERROR
}

class NewSalesmanViewModel(application: Application) : AndroidViewModel(application) {

    private val _invitationLinkBuildStatus = MediatorLiveData<InvitationLinkBuildStatus?>()
    val invitationLinkBuildStatus: LiveData<InvitationLinkBuildStatus?>
        get() = _invitationLinkBuildStatus

    private val _shareIntent = MediatorLiveData<Intent?>()
    val shareIntent: LiveData<Intent?>
        get() = _shareIntent

    val isProgressVisible: LiveData<Boolean> = Transformations.map(_invitationLinkBuildStatus) {
        it == InvitationLinkBuildStatus.STARTED
    }

    private val TIMER = 10000L
    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    private val database = ResellerDatabase.getInstance(application)
    private val branchesRepository = BranchesRepository(database)

    val miniBranchesList = branchesRepository.miniDatabaseBranchesList

    val selectedBranchId = MutableLiveData<Long?>().apply {
        this.value = -1
    }

    fun setDynamicLink() {
        viewModelScope.launch {
            val job = withTimeoutOrNull(TIMER) {
                _invitationLinkBuildStatus.value = InvitationLinkBuildStatus.STARTED
                try {
                    shareDynamicLink()
                }catch (e:Exception) {
                    _invitationLinkBuildStatus.value = InvitationLinkBuildStatus.ERROR
                }
            }
            if (job == null) _invitationLinkBuildStatus.value = InvitationLinkBuildStatus.TIMED_OUT
        }
    }


    private fun buildDeepLink(): Uri {
        val builder = Uri.Builder()
        builder.scheme("https")
            .authority("resellersApp.com")
            .appendPath(UserPreferences.getFirebaseUID(getApplication()))
            .appendPath(selectedBranchId.value!!.toString())

        return builder.build()
    }

    private suspend fun buildDynamicLink(): Uri? {
        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(buildDeepLink())
            .setDomainUriPrefix("https://resellerApp.page.link") // Open links with this app on Android
            .setAndroidParameters(AndroidParameters.Builder().build())
            .buildShortDynamicLink().await()
        println(dynamicLink.shortLink.toString())
        return dynamicLink.shortLink
    }

    private suspend fun shareDynamicLink() {
        buildDynamicLink()?.let {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, "Firebase Deep Link")
            intent.putExtra(Intent.EXTRA_TEXT, it.toString())
            _shareIntent.value = intent
        }
    }

    fun restoreInvitationState() {
        _invitationLinkBuildStatus.value = null
    }

    fun restoreIntentState() {
        _shareIntent.value = null
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}
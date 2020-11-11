package velord.university.application.broadcast.hub

import android.content.Context

const val PERM_PRIVATE_MINI_PLAYER = "velord.university.PERM_PRIVATE_MINI_PLAYER"
const val PERM_PRIVATE_RADIO = "velord.university.PERM_PRIVATE_RADIO"

object AppBroadcastHub {

    fun Context.doAction(
        type: BroadcastActionType
    ) = when(type) {
        BroadcastActionType.STOP_PLAYER_SERVICE -> PlayerBroadcastAction
            .Stop(this).toService()
        BroadcastActionType.STOP_PLAYER_UI -> PlayerBroadcastAction
            .Stop(this).toUI()
        BroadcastActionType.PLAY_PLAYER_SERVICE -> PlayerBroadcastAction
            .Play(this).toService()
        BroadcastActionType.PLAY_PLAYER_UI -> PlayerBroadcastAction
            .Play(this).toUI()
        BroadcastActionType.LIKE_PLAYER_SERVICE -> PlayerBroadcastAction
            .Like(this).toService()
        BroadcastActionType.LIKE_PLAYER_UI -> PlayerBroadcastAction
            .Like(this).toUI()
        BroadcastActionType.UNLIKE_PLAYER_SERVICE -> PlayerBroadcastAction
            .UnLike(this).toService()
        BroadcastActionType.UNLIKE_PLAYER_UI -> PlayerBroadcastAction
            .UnLike(this).toUI()
        BroadcastActionType.SKIP_PLAYER_SERVICE -> PlayerBroadcastAction
            .SkipNext(this).toService()
        BroadcastActionType.SKIP_PLAYER_UI -> PlayerBroadcastAction
            .SkipNext(this).toUI()
        BroadcastActionType.SKIP_PREV_PLAYER_SERVICE -> PlayerBroadcastAction
            .SkipPrev(this).toService()
        BroadcastActionType.SKIP_PREV_PLAYER_UI -> PlayerBroadcastAction
            .SkipPrev(this).toUI()
        BroadcastActionType.SHUFFLE_PLAYER_SERVICE -> PlayerBroadcastAction
            .Shuffle(this).toService()
        BroadcastActionType.SHUFFLE_PLAYER_UI -> PlayerBroadcastAction
            .Shuffle(this).toUI()
        BroadcastActionType.UN_SHUFFLE_PLAYER_SERVICE -> PlayerBroadcastAction
            .UnShuffle(this).toService()
        BroadcastActionType.UN_SHUFFLE_PLAYER_UI -> PlayerBroadcastAction
            .UnShuffle(this).toUI()
        BroadcastActionType.LOOP_PLAYER_SERVICE -> PlayerBroadcastAction
            .Loop(this).toService()
        BroadcastActionType.LOOP_PLAYER_UI -> PlayerBroadcastAction
            .Loop(this).toUI()
        BroadcastActionType.LOOP_ALL_PLAYER_SERVICE -> PlayerBroadcastAction
            .LoopAll(this).toService()
        BroadcastActionType.LOOP_ALL_PLAYER_UI -> PlayerBroadcastAction
            .LoopAll(this).toUI()
        BroadcastActionType.LOOP_NOT_PLAYER_SERVICE -> PlayerBroadcastAction
            .LoopNot(this).toService()
        BroadcastActionType.LOOP_NOT_PLAYER_UI -> PlayerBroadcastAction
            .LoopNot(this).toUI()
        BroadcastActionType.HIDE_PLAYER_UI -> PlayerBroadcastAction
            .Hide(this).toUI()
        BroadcastActionType.SHOW_PLAYER_UI -> PlayerBroadcastAction
            .Show(this).toUI()
        BroadcastActionType.GET_INFO_PLAYER_SERVICE -> PlayerBroadcastAction
            .GetInfo(this).toService()
        BroadcastActionType.PLAY_OR_STOP_PLAYER_SERVICE -> PlayerBroadcastAction
            .PlayOrStop(this).toService()
        BroadcastActionType.UNAVAILABLE_PLAYER_UI -> PlayerBroadcastAction
            .Unavailable(this).toUI()
        BroadcastActionType.CLICK_ON_ICON_PLAYER_UI -> PlayerBroadcastAction
            .ClickOnIcon(this).toUI()
        //radio
        BroadcastActionType.STOP_RADIO_SERVICE -> RadioBroadcastAction
            .Stop(this).toService()
        BroadcastActionType.STOP_RADIO_UI -> RadioBroadcastAction
            .Stop(this).toUI()
        BroadcastActionType.PLAY_RADIO_SERVICE -> RadioBroadcastAction
            .Play(this).toService()
        BroadcastActionType.PLAY_RADIO_UI -> RadioBroadcastAction
            .Play(this).toUI()
        BroadcastActionType.LIKE_RADIO_SERVICE -> RadioBroadcastAction
            .Like(this).toService()
        BroadcastActionType.LIKE_RADIO_UI -> RadioBroadcastAction
            .Like(this).toUI()
        BroadcastActionType.UNLIKE_RADIO_SERVICE -> RadioBroadcastAction
            .UnLike(this).toService()
        BroadcastActionType.UNLIKE_RADIO_UI -> RadioBroadcastAction
            .UnLike(this).toUI()
        BroadcastActionType.HIDE_RADIO_UI -> RadioBroadcastAction
            .Hide(this).toUI()
        BroadcastActionType.SHOW_RADIO_UI -> RadioBroadcastAction
            .Show(this).toUI()
        BroadcastActionType.GET_INFO_RADIO_SERVICE -> RadioBroadcastAction
            .GetInfo(this).toService()
        BroadcastActionType.PLAY_OR_STOP_RADIO_SERVICE -> RadioBroadcastAction
            .PlayOrStop(this).toService()
        BroadcastActionType.UNAVAILABLE_RADIO_UI -> RadioBroadcastAction
            .Unavailable(this).toUI()
        BroadcastActionType.CLICK_ON_ICON_RADIO_UI -> RadioBroadcastAction
            .ClickOnIcon(this).toUI()
    }

    fun Context.clickOnRadioIcon() =
        RadioBroadcastAction.ClickOnIcon(this).toUI()

    fun Context.pathIsWrongUI(path: String) =
        PlayerBroadcastAction.PathWrong(this).toUI(path)

    fun Context.iconUI(icon: String) =
        PlayerBroadcastAction.Icon(this).toUI(icon)

    fun Context.playAllInFolderService(path: String) =
        PlayerBroadcastAction.PlayAllInFolder(this).toService(path)

    fun Context.playNextAllInFolderService(path: String) =
        PlayerBroadcastAction.PlayNextAllInFolder(this).toService(path)

    fun Context.shuffleAndPlayAllInFolderService(path: String) =
        PlayerBroadcastAction.ShuffleAndPlayAllInFolder(this).toService(path)

    fun Context.addToQueueService(path: String) =
        PlayerBroadcastAction.AddToQueue(this).toService(path)

    fun Context.rewindService(duration: Int) =
        PlayerBroadcastAction.Rewind(this).toService(duration)

    fun Context.rewindUI(duration: Int) =
        PlayerBroadcastAction.Rewind(this).toUI(duration)

    fun Context.playByPathService(path: String) =
        PlayerBroadcastAction.PlayByPath(this).toService(path)

    fun Context.playByPathUI(path: String) =
        PlayerBroadcastAction.PlayByPath(this).toUI(path)

    fun Context.songNameUI(name: String) =
        PlayerBroadcastAction.SongName(this).toUI(name)

    fun Context.songArtistUI(artist: String) =
        PlayerBroadcastAction.SongArtist(this).toUI(artist)

    fun Context.songHQUI(isHQ: Boolean) =
        PlayerBroadcastAction.SongHq(this).toUI(isHQ)

    fun Context.songDurationUI(duration: Int) =
        PlayerBroadcastAction.SongDuration(this).toUI(duration)

    //radio
    fun Context.radioArtistUI(artist: String) =
        RadioBroadcastAction.Artist(this).toUI(artist)

    fun Context.radioNameUI(name: String) =
        RadioBroadcastAction.Name(this).toUI(name)

    fun Context.playByUrlRadioService(url: String) =
        RadioBroadcastAction.PlayByUrl(this).toService(url)

    fun Context.iconRadioUI(icon: String) =
        RadioBroadcastAction.Icon(this).toUI(icon)

    fun Context.radioUrlIsWrongUI(url: String) =
        RadioBroadcastAction.UrlWrong(this).toUI(url)
}

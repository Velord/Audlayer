package velord.university.application.broadcast.hub

enum class BroadcastActionType {
    STOP_PLAYER_SERVICE,
    STOP_PLAYER_UI,
    PLAY_PLAYER_SERVICE,
    PLAY_PLAYER_UI,
    LIKE_PLAYER_SERVICE,
    LIKE_PLAYER_UI,
    UNLIKE_PLAYER_SERVICE,
    UNLIKE_PLAYER_UI,
    SKIP_PLAYER_SERVICE,
    SKIP_PLAYER_UI,
    SKIP_PREV_PLAYER_SERVICE,
    SKIP_PREV_PLAYER_UI,
    SHUFFLE_PLAYER_SERVICE,
    SHUFFLE_PLAYER_UI,
    UN_SHUFFLE_PLAYER_SERVICE,
    UN_SHUFFLE_PLAYER_UI,
    LOOP_PLAYER_SERVICE,
    LOOP_PLAYER_UI,
    LOOP_ALL_PLAYER_SERVICE,
    LOOP_ALL_PLAYER_UI,
    LOOP_NOT_PLAYER_SERVICE,
    LOOP_NOT_PLAYER_UI,
    HIDE_PLAYER_UI,
    SHOW_PLAYER_UI,
    GET_INFO_PLAYER_SERVICE,
    PLAY_OR_STOP_PLAYER_SERVICE,
    UNAVAILABLE_PLAYER_UI,
    CLICK_ON_ICON_PLAYER_UI,
    //radio
    STOP_RADIO_SERVICE,
    STOP_RADIO_UI,
    PLAY_RADIO_SERVICE,
    PLAY_RADIO_UI,
    LIKE_RADIO_SERVICE,
    LIKE_RADIO_UI,
    UNLIKE_RADIO_SERVICE,
    UNLIKE_RADIO_UI,
    HIDE_RADIO_UI,
    SHOW_RADIO_UI,
    GET_INFO_RADIO_SERVICE,
    PLAY_OR_STOP_RADIO_SERVICE,
    UNAVAILABLE_RADIO_UI,
    CLICK_ON_ICON_RADIO_UI,
}
package com.melodiousplayer.android.model

data class VersionUpdateResultBean(
    var code: Int?,
    var msg: String?,
    var versionUpdate: VersionUpdate?
)

data class VersionUpdate(
    var name: String?,
    var update: String?,
    var new_version: String?,
    var apk_file_url: String?,
    var update_log: String?,
    var target_size: String?,
    var constraint: Boolean?
)

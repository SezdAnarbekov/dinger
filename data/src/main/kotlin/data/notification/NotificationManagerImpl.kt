package data.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.annotation.StringRes
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import org.jetbrains.annotations.Contract
import org.stoyicker.dinger.data.R
import java.util.Locale

internal class NotificationManagerImpl(
        private val context: Context,
        private val notificationID: NotificationID,
        private val groupNotification: GroupNotification) : NotificationManager {
    @Contract(value = "_, _, _, _, null, true, _, _, _ -> fail")
    override fun pop(
            @StringRes channelName: Int,
            title: String,
            body: String,
            @NotificationCategory category: String,
            groupName: String?,
            isGroupSummary: Boolean,
            @NotificationPriority priority: Long,
            @NotificationVisibility visibility: Long,
            clickHandler: PendingIntent?) {
        if (isGroupSummary && groupNotification.isGroupShown(context, groupName!!)) {
            return
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.getSystemService<android.app.NotificationManager>(
                    android.app.NotificationManager::class.java)
                    .createNotificationChannel(NotificationChannel(
                            getChannelId(context, channelName),
                            context.getString(channelName), getChannelImportance(priority)))
        }
        NotificationManagerCompat.from(context).let {
            @Suppress("DEPRECATION") // Deprecated from API 26 on, not before
            it.notify(notificationID.next(context), Notification.Builder(context)
                    .setAutoCancel(true)
                    .setContentIntent(clickHandler)
                    .setContentText(body)
                    .setContentTitle(title)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setOngoing(false)
                    .setOnlyAlertOnce(true)
                    .setPriority(priority.toInt())
                    .setSmallIcon(R.drawable.ic_notification)
                    .setTicker(body)
                    .apply {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            setShowWhen(true)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                                setGroupSummary(isGroupSummary)
                                if (groupName != null) {
                                    setGroup(groupName)
                                }
                                setLocalOnly(true)
                                setSortKey("${System.currentTimeMillis()}")
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    setCategory(NotificationCompat.CATEGORY_SERVICE)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)
                                        setChannelId(getChannelId(context, channelName))
                                        setGroupAlertBehavior(
                                                NotificationCompat.GROUP_ALERT_SUMMARY)
                                    }
                                    setVisibility(visibility.toInt())
                                }
                            }
                        }
                    }
                    .build())
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun getChannelImportance(@NotificationPriority priority: Long) = when (priority) {
    NotificationManager.PRIORITY_LOW -> android.app.NotificationManager.IMPORTANCE_LOW
    NotificationManager.PRIORITY_MEDIUM -> android.app.NotificationManager.IMPORTANCE_DEFAULT
    NotificationManager.PRIORITY_HIGH -> android.app.NotificationManager.IMPORTANCE_HIGH
    else -> throw IllegalArgumentException("Illegal priority $priority")
}

/**
 * Required because a channel name may (and should) be localized, but the id must not be.
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
private fun getChannelId(context: Context, @StringRes channelName: Int) =
        Configuration(context.resources.configuration).run {
        setLocale(Locale.ENGLISH)
        context.createConfigurationContext(this).resources.getString(channelName)
    }

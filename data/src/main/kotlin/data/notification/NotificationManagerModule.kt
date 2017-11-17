package data.notification

import android.content.Context
import dagger.Module
import dagger.Provides
import data.RootModule
import javax.inject.Singleton

@Module(includes = arrayOf(RootModule::class))
internal class NotificationManagerModule {
    @Provides
    @Singleton
    fun notificationID(): NotificationID = NotificationIDImpl()

    @Provides
    @Singleton
    fun groupNotification(): GroupNotification = GroupNotificationImpl()

    @Provides
    @Singleton
    fun autoSwipeReportHandler(
            context: Context, notificationID: NotificationID, groupNotification: GroupNotification)
            : NotificationManager = NotificationManagerImpl(
            context, notificationID, groupNotification)
}

package com.shadowwing.jarvis;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class BriefingWorker extends Worker {
    public BriefingWorker(@NonNull Context context, @NonNull WorkerParameters params) { super(context, params); }
    @NonNull @Override public Result doWork() {
        Context c = getApplicationContext();
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager nm = (NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.createNotificationChannel(new NotificationChannel("jarvis_briefing", "JARVIS Briefings", NotificationManager.IMPORTANCE_LOW));
        }
        NotificationCompat.Builder n = new NotificationCompat.Builder(c, "jarvis_briefing")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Shadow Wing JARVIS")
            .setContentText("Your background check ran. Open JARVIS for the latest local task briefing.")
            .setPriority(NotificationCompat.PRIORITY_LOW).setAutoCancel(true);
        try { NotificationManagerCompat.from(c).notify(701, n.build()); } catch (SecurityException ignored) {}
        return Result.success();
    }
}

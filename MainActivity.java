package com.shadowwing.jarvis;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.core.app.ActivityCompat;
import androidx.work.*;

import java.util.*;
import java.text.SimpleDateFormat;

public class MainActivity extends Activity {
    private static final String PREF = "shadowwing_private";
    private static final String TASKS = "tasks";
    private static final String APPS = "apps";
    private SharedPreferences prefs;
    private LinearLayout chat;
    private EditText command;
    private TextView briefing;
    private final String[] appNames = {"Fiverr", "Fiverr Web", "Instagram", "Gmail", "Facebook", "LinkedIn"};
    private final String[] appUrls = {
        "https://www.fiverr.com/", "https://www.fiverr.com/",
        "https://www.instagram.com/", "https://mail.google.com/",
        "https://www.facebook.com/", "https://www.linkedin.com/"
    };
    private final String[] appKeys = {"fiverr_app", "fiverr_web", "instagram", "gmail", "facebook", "linkedin"};

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        prefs = getSharedPreferences(PREF, MODE_PRIVATE);
        if (Build.VERSION.SDK_INT >= 33)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 41);
        buildUi();
        scheduleBriefingWorker();
    }

    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density); }
    private TextView text(String s, int size, int color) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color);
        t.setPadding(dp(8), dp(7), dp(8), dp(7)); return t;
    }
    private Button button(String label, Runnable action) {
        Button b = new Button(this); b.setText(label); b.setOnClickListener(v -> action.run()); return b;
    }
    private void buildUi() {
        ScrollView scroll = new ScrollView(this);
        LinearLayout page = new LinearLayout(this); page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(16), dp(22), dp(16), dp(24));
        page.setBackgroundColor(0xFF090D15);
        scroll.addView(page);
        TextView brand = text("SHADOW WING TECHNOLOGY", 13, 0xFFE8BD63);
        brand.setGravity(Gravity.CENTER); page.addView(brand);
        TextView title = text("JARVIS", 34, 0xFFFFFFFF); title.setGravity(Gravity.CENTER); page.addView(title);
        TextView sub = text("PERSONAL AI COMMAND CENTRE · PRIVATE BETA", 11, 0xFF9EABC0);
        sub.setGravity(Gravity.CENTER); page.addView(sub);

        briefing = text(makeBriefing(), 14, 0xFFE8EAF0);
        briefing.setBackgroundColor(0xFF172235); page.addView(briefing);

        TextView h = text("COMMAND CHAT", 15, 0xFFE8BD63); page.addView(h);
        chat = new LinearLayout(this); chat.setOrientation(LinearLayout.VERTICAL);
        chat.setPadding(dp(10), dp(10), dp(10), dp(10)); chat.setBackgroundColor(0xFF101827);
        page.addView(chat);
        addChat("JARVIS", "Welcome, Boss. Give me a task. I can organize it and prepare an approval request. External posting, messages, negotiations and orders stay locked until a supported integration is configured and you approve the exact action.");

        command = new EditText(this); command.setTextColor(0xFFFFFFFF); command.setHintTextColor(0xFF9EABC0);
        command.setHint("Type a task or project instruction…"); page.addView(command);
        page.addView(button("ADD TASK FOR APPROVAL", this::addTask));

        TextView taskHead = text("APPROVAL QUEUE", 15, 0xFFE8BD63); page.addView(taskHead);
        page.addView(text("Review each action before it can be run. This starter build records approvals locally; it does not publish or send anything.", 12, 0xFFB5C0D0));
        page.addView(button("VIEW / APPROVE TASKS", this::showTasks));

        TextView appHead = text("APP ACCESS ALLOWLIST", 15, 0xFFE8BD63); page.addView(appHead);
        page.addView(text("Only apps you enable below can be opened from JARVIS. WhatsApp is intentionally not included.", 12, 0xFFB5C0D0));
        for (int i=0; i<appNames.length; i++) {
            final int index = i;
            CheckBox cb = new CheckBox(this); cb.setText(appNames[i]); cb.setTextColor(0xFFFFFFFF);
            cb.setChecked(prefs.getBoolean(appKeys[i], false));
            cb.setOnCheckedChangeListener((v, checked) -> prefs.edit().putBoolean(appKeys[index], checked).apply());
            page.addView(cb);
            page.addView(button("OPEN " + appNames[i].toUpperCase(Locale.ROOT), () -> openApprovedApp(index)));
        }

        page.addView(text("SECURITY STATUS", 15, 0xFFE8BD63));
        page.addView(text("• No social passwords or OTPs are requested or stored.\n• No accessibility scraping or hidden account control.\n• App access is opt-in and locally allowlisted.\n• External actions require exact approval.\n• Background checks use Android WorkManager and may be deferred by Android battery policies.", 12, 0xFFB5C0D0));
        page.addView(button("REFRESH BRIEFING", () -> briefing.setText(makeBriefing())));
        setContentView(scroll);
    }

    private void addChat(String who, String message) {
        TextView t = text(who + "\n" + message, 13, 0xFFE5EAF2);
        t.setBackgroundColor(who.equals("YOU") ? 0xFF3A2D1D : 0xFF1A2A40);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, dp(4), 0, dp(4)); chat.addView(t, lp);
    }

    private void addTask() {
        String value = command.getText().toString().trim();
        if (value.isEmpty()) { command.setError("Enter a task first"); return; }
        String id = UUID.randomUUID().toString();
        String record = id + "|" + System.currentTimeMillis() + "|PENDING|" + value.replace("|", " ");
        String all = prefs.getString(TASKS, "");
        prefs.edit().putString(TASKS, all.isEmpty() ? record : all + "\n" + record).apply();
        addChat("YOU", value);
        addChat("JARVIS", "Task saved in your local approval queue. I have not posted, messaged, negotiated, accepted an order, or changed an account. Review the task in Approval Queue.");
        command.setText("");
        briefing.setText(makeBriefing());
    }

    private List<String[]> readTasks() {
        List<String[]> result = new ArrayList<>();
        String raw = prefs.getString(TASKS, "");
        for (String line : raw.split("\n")) {
            if (line.trim().isEmpty()) continue;
            String[] p = line.split("\\|", 4);
            if (p.length == 4) result.add(p);
        }
        return result;
    }
    private void writeTasks(List<String[]> list) {
        StringBuilder b = new StringBuilder();
        for (String[] p : list) { if (b.length()>0)b.append("\n"); b.append(String.join("|", p)); }
        prefs.edit().putString(TASKS, b.toString()).apply();
    }
    private void showTasks() {
        List<String[]> list = readTasks();
        if (list.isEmpty()) { new AlertDialog.Builder(this).setTitle("Approval Queue").setMessage("No tasks yet.").setPositiveButton("OK", null).show(); return; }
        String[] labels = new String[list.size()];
        for (int i=0;i<list.size();i++) labels[i] = list.get(i)[2] + " — " + list.get(i)[3];
        new AlertDialog.Builder(this).setTitle("Select a task")
            .setItems(labels, (d, which) -> showTaskActions(list.get(which)[0])).setNegativeButton("Close", null).show();
    }
    private void showTaskActions(String id) {
        List<String[]> list = readTasks(); String[] target = null;
        for (String[] p : list) if (p[0].equals(id)) target=p;
        if (target==null)return;
        String[] item = target;
        new AlertDialog.Builder(this).setTitle("Review exact task")
            .setMessage(item[3] + "\n\nStatus: " + item[2] + "\n\nApproving records your consent for this task only. It does not execute external actions in this starter build.")
            .setPositiveButton("APPROVE TASK", (d,w)->{
                for(String[] p:list)if(p[0].equals(id))p[2]="APPROVED_LOCAL_ONLY";
                writeTasks(list); briefing.setText(makeBriefing());
                new AlertDialog.Builder(this).setMessage("Approval recorded locally. No external action was performed because no platform integration is configured.").setPositiveButton("OK",null).show();
            })
            .setNeutralButton("REJECT", (d,w)->{
                for(String[] p:list)if(p[0].equals(id))p[2]="REJECTED";
                writeTasks(list); briefing.setText(makeBriefing());
            }).setNegativeButton("Cancel",null).show();
    }
    private void openApprovedApp(int i) {
        if (!prefs.getBoolean(appKeys[i], false)) {
            new AlertDialog.Builder(this).setMessage("This app is not allowlisted. Enable its checkbox first.").setPositiveButton("OK",null).show(); return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(appUrls[i]));
        try { startActivity(intent); } catch(Exception e) {
            new AlertDialog.Builder(this).setMessage("Could not open this app or website on this device.").setPositiveButton("OK",null).show();
        }
    }
    private String makeBriefing() {
        List<String[]> tasks=readTasks(); int pending=0, approved=0, rejected=0;
        for(String[] p:tasks){if(p[2].equals("PENDING"))pending++;else if(p[2].startsWith("APPROVED"))approved++;else if(p[2].equals("REJECTED"))rejected++;}
        return "BOSS BRIEFING\nTasks recorded: "+tasks.size()+"\nAwaiting approval: "+pending+"\nApproved locally (not executed): "+approved+"\nRejected: "+rejected+"\n\nNo social posts or messages have been sent by this starter build.";
    }
    private void scheduleBriefingWorker() {
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(BriefingWorker.class, 15, java.util.concurrent.TimeUnit.MINUTES).build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("shadowwing-briefing", ExistingPeriodicWorkPolicy.KEEP, request);
    }
}

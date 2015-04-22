package com.example.wifistatus;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


public class MainFragment extends Fragment {

    private WifiStatusReceiver mReceiver = new WifiStatusReceiver();

    public MainFragment() {
    }

    // В этой метке показывается статус wifi
    private TextView mStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // http://developer.android.com/guide/components/fragments.html#Creating
        View v = inflater.inflate(R.layout.main_fragment, container, false);
        // Достаем из контейнера view дочерние элементы и работаем с ними
        mStatus = (TextView) v.findViewById(R.id.text_status);

        // Стартовая инициализация состояния WiFi
        ConnectivityManager conMngr = (ConnectivityManager) getActivity().
                getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo wifi = conMngr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        updateWiFiStatus(wifi.isConnectedOrConnecting());

        v.findViewById(R.id.message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessage();
            }
        });

        v.findViewById(R.id.notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNotification();
            }
        });

        return v;
    }

    // Метод показывает сообщениме
    private void showMessage()
    {
        Toast.makeText(getActivity(), R.string.message, Toast.LENGTH_SHORT).show();
    }

    // Чтобы можно было работать(закрыть, изменить) с уведомлением в дальнейшем назначаем ему id
    private static final int NOTIFICATION_ID = 0;

    // Метод отсылает уведомление
    private void sendNotification()
    {
        // Можете тут почитать, но там некоторые вещи устарели: http://habrahabr.ru/post/140928/
        // Как надо сейчас: http://developer.android.com/design/patterns/notifications.html

        // Здесь может быть, например, аватарка человека который нам написал, аватарка берется с сервера(не обязательно из реурсов приложения)
        Bitmap icon = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.icon);

        // Действия которые будут совершены по нажатию на нотификейшн
        Intent notificationIntent = new Intent(getActivity(), MainActivity.class);
        int requestID = (int) System.currentTimeMillis();
        PendingIntent contentIntent = PendingIntent.getActivity(getActivity(),
                requestID,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Для создания нотификейшна лучше использовать специальная класс из библиотеки обратной совместимости
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity())
                .setSmallIcon(R.drawable.ic_announcement_white)
                .setLargeIcon(icon)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setContentTitle("My notification")
                .setContentText("Hello World!")
                .setTicker("Something happen");

        // Отправляем нотификейшн
        NotificationManager notificationManager  =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    @Override
    public void onResume() {
        super.onResume();
        // Подписываемся на уведомления
        IntentFilter filters = new IntentFilter();
        filters.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        getActivity().registerReceiver(mReceiver, filters);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Отписываемся от уведомлений
        getActivity().unregisterReceiver(mReceiver);
    }

    // Состояние изменилось
    private void updateWiFiStatus(boolean state)
    {
        mStatus.setText(state ? R.string.text_status_on : R.string.text_status_off);
    }

    // Класс отслеживающий изменения состояния WiFi
    private class WifiStatusReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager conMan =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = conMan.getActiveNetworkInfo();
            boolean state = netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI;
            updateWiFiStatus(state);
        }
    }
}

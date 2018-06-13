package br.com.agilles.capstone.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import br.com.agilles.capstone.R;
import br.com.agilles.capstone.ui.ListaOcorrenciasActivity;
import br.com.agilles.capstone.ui.widget.service.OcorrenciasWidgetService;
import br.com.agilles.capstone.utils.Constantes;

/**
 * Implementation of App Widget functionality.
 */
public class OcorrenciasWidget extends AppWidgetProvider implements Constantes {

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        if (intent != null) {
            if (intent.getAction().equalsIgnoreCase(CARREGA_OCORRENCIAS_WIDGET)) {
                int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

                if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lv_collection);
                }
            } else if (intent.getAction().equalsIgnoreCase(FILTRA_OCORRENCIA)) {
                String firestoreKeyID = intent.getStringExtra(FILTRA_OCORRENCIA_ITEM);
                Intent it = new Intent(context, ListaOcorrenciasActivity.class);
                it.putExtra(FILTRA_OCORRENCIA_ITEM, firestoreKeyID);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(it);
            }
        }

        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            Intent intentService = new Intent(context, OcorrenciasWidgetService.class);
            intentService.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ocorrencias_widget);
            views.setRemoteAdapter(R.id.lv_collection, intentService);
            views.setEmptyView(R.id.lv_collection, R.id.tv_loading);

            Intent intentCarregaOcorrencias = new Intent(context, OcorrenciasWidget.class);
            intentCarregaOcorrencias.setAction(CARREGA_OCORRENCIAS_WIDGET);
            intentCarregaOcorrencias.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent pendingIntentCarregaOcorrencias = PendingIntent.getBroadcast(context, 0, intentCarregaOcorrencias, 0);
            views.setOnClickPendingIntent(R.id.iv_update_collection, pendingIntentCarregaOcorrencias);

            Intent intentAbreApp = new Intent(context, ListaOcorrenciasActivity.class);
            PendingIntent pendingIntentAbreApp = PendingIntent.getActivity(context, 0, intentAbreApp, 0);
            views.setOnClickPendingIntent(R.id.iv_open_app, pendingIntentAbreApp);

            Intent ocorrenciaFiltrada = new Intent(context, OcorrenciasWidget.class);
            ocorrenciaFiltrada.setAction(FILTRA_OCORRENCIA);
            PendingIntent pendingFiltraOcorrencias = PendingIntent.getBroadcast(context, 0, ocorrenciaFiltrada, 0);
            views.setPendingIntentTemplate(R.id.lv_collection, pendingFiltraOcorrencias);

            appWidgetManager.updateAppWidget(appWidgetId, views);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lv_collection);


        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

}


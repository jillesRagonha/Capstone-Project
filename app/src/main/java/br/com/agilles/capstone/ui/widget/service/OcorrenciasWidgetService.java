package br.com.agilles.capstone.ui.widget.service;

import android.content.Intent;
import android.widget.RemoteViewsService;

import br.com.agilles.capstone.ui.widget.adapter.OcorrenciasWidgetAdapter;

public class OcorrenciasWidgetService extends RemoteViewsService{

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new OcorrenciasWidgetAdapter(this, intent);
    }
}

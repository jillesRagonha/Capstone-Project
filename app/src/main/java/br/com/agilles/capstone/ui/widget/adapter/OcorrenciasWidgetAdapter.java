package br.com.agilles.capstone.ui.widget.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import br.com.agilles.capstone.R;
import br.com.agilles.capstone.models.Ocorrencia;
import br.com.agilles.capstone.ui.ListaOcorrenciasActivity;
import br.com.agilles.capstone.ui.widget.OcorrenciasWidget;
import br.com.agilles.capstone.utils.FirebaseUtils;

public class OcorrenciasWidgetAdapter implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private List<Ocorrencia> ocorrencias = new ArrayList<>();

    private int size;


    public OcorrenciasWidgetAdapter(Context mContext, Intent intent) {
        this.mContext = mContext;
        float densidade = mContext.getResources().getDisplayMetrics().density;
        size = (int) (50 * densidade + 0.5f);
    }

    @Override
    public void onCreate() {
        ocorrencias = FirebaseUtils.pegarInstancia().getOcorrencias();
    }

    @Override
    public void onDataSetChanged() {
        ocorrencias = FirebaseUtils.pegarInstancia().getOcorrencias();
    }

    @Override
    public void onDestroy() {
        ocorrencias.clear();


    }

    @Override
    public int getCount() {
        return ocorrencias.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
        views.setTextViewText(R.id.tv_model, ocorrencias.get(position).getData());
        views.setTextViewText(R.id.tv_brand, ocorrencias.get(position).getNatureza());
        try {
            Bitmap bmp = Glide.with(mContext.getApplicationContext())
                    .asBitmap()
                    .load(ocorrencias.get(position).getFoto())
                    .submit(size, size)
                    .get();
            views.setImageViewBitmap(R.id.iv_ocorrencia, bmp);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        Intent intentFiltro = new Intent();
        intentFiltro.putExtra(OcorrenciasWidget.FILTRA_OCORRENCIA_ITEM, ocorrencias.get(position).getFirestoreIdKey());
        views.setOnClickFillInIntent(R.id.rl_container, intentFiltro);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}

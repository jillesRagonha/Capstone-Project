package br.com.agilles.capstone.ui.recyclerview.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


import br.com.agilles.capstone.R;
import br.com.agilles.capstone.models.Ocorrencia;
import br.com.agilles.capstone.ui.recyclerview.adapter.listener.OnItemClickListener;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ListaOcorrenciasAdapter extends RecyclerView.Adapter<ListaOcorrenciasAdapter.OcorrenciasViewHolder> {

    private final List<Ocorrencia> ocorrencias;
    private final Context context;
    private OnItemClickListener onItemClickListener;

    public ListaOcorrenciasAdapter(Context context, List<Ocorrencia> ocorrencia) {
        this.context = context;
        this.ocorrencias = ocorrencia;

    }

    @NonNull
    @Override
    public ListaOcorrenciasAdapter.OcorrenciasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewCriada = LayoutInflater.from(context)
                .inflate(R.layout.item_ocorrencia, parent, false);
        return new OcorrenciasViewHolder(viewCriada);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaOcorrenciasAdapter.OcorrenciasViewHolder holder, int position) {
        Ocorrencia ocorrencia = ocorrencias.get(position);
        holder.vinculaOcorrencia(ocorrencia);

    }

    @Override
    public int getItemCount() {
        return ocorrencias.size();
    }

    class OcorrenciasViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_ocorrencia_titulo)
        TextView mTituloTextView;

        @BindView(R.id.item_ocorrencia_texto_bairro)
        TextView mBairroTextView;

        @BindView(R.id.item_ocorrencia_texto_data)
        TextView mDataTextView;

        private Ocorrencia ocorrencia;


        public OcorrenciasViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(ocorrencia);
                }
            });

        }

        public void vinculaOcorrencia(Ocorrencia ocorrencia) {
            this.ocorrencia = ocorrencia;
            preencheCampos(ocorrencia);

        }

        private void preencheCampos(Ocorrencia ocorrencia) {
            mBairroTextView.setText(ocorrencia.getEndereco().getBairro());
            mTituloTextView.setText(ocorrencia.getNatureza());
            mDataTextView.setText(ocorrencia.getData());
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}

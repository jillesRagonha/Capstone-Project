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
import br.com.agilles.capstone.models.Pessoa;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ListaPessoasAdapter extends RecyclerView.Adapter<ListaPessoasAdapter.PessoasViewHolder> {

    private final List<Pessoa> pessoas;
    private final Context context;

    public ListaPessoasAdapter(List<Pessoa> pessoas, Context context) {
        this.pessoas = pessoas;
        this.context = context;
    }

    @NonNull
    @Override
    public PessoasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewCriada = LayoutInflater.from(context)
                .inflate(R.layout.item_pessoa, parent, false);
        return new PessoasViewHolder(viewCriada);
    }

    @Override
    public void onBindViewHolder(@NonNull PessoasViewHolder holder, int position) {
        Pessoa pessoa = pessoas.get(position);
        holder.vinculaPessoa(pessoa);
    }

    @Override
    public int getItemCount() {
        return pessoas.size();
    }


    class PessoasViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.item_pessoa_nome)
        TextView txtNomePessoa;
        @BindView(R.id.item_pessoa_condicao)
        TextView txtCondicaoPessoa;

        private Pessoa pessoa = new Pessoa();

        public PessoasViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void vinculaPessoa(Pessoa pessoa) {
            this.pessoa = pessoa;
            preencheCampos(pessoa);
        }

        private void preencheCampos(Pessoa pessoa) {
            txtCondicaoPessoa.setText(pessoa.getCondicao());
            txtNomePessoa.setText(pessoa.getNome());

        }
    }
}

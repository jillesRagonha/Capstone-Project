package br.com.agilles.capstone.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import br.com.agilles.capstone.R;
import br.com.agilles.capstone.models.Ocorrencia;
import br.com.agilles.capstone.ui.recyclerview.adapter.ListaPessoasAdapter;
import br.com.agilles.capstone.ui.widget.OcorrenciasWidget;
import br.com.agilles.capstone.utils.Constantes;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DetalhesOcorrenciaActivity extends AppCompatActivity implements Constantes {

    @BindView(R.id.detalhes_imagem_principal)
    ImageView mImagemPrincipal;
    @BindView(R.id.detalhes_data)
    TextView txtData;
    @BindView(R.id.detalhe_natureza)
    TextView txtNatureza;
    @BindView(R.id.detalhe_descricao)
    TextView txtDescricao;
    @BindView(R.id.recycler_view_pessoas)
    RecyclerView rvPessoas;
    ListaPessoasAdapter adapter;
    Ocorrencia ocorrencia;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_ocorrencia);

        ButterKnife.bind(this);
        setaToolbar();
        ocorrencia = (Ocorrencia) getIntent().getSerializableExtra(CHAVE_OCORRENCIA);
        preencheCampos(ocorrencia);
        criaListaPessoas(ocorrencia);
    }

    private void setaToolbar() {
        setSupportActionBar(mToolbar);
        setTitle(getString(R.string.tituloToolbarDetalhes));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void criaListaPessoas(Ocorrencia ocorrencia) {
        adapter = new ListaPessoasAdapter(ocorrencia.getPessoas(), this);
        rvPessoas.setAdapter(adapter);
    }

    private void preencheCampos(Ocorrencia ocorrencia) {
        Picasso.get()
                .load(ocorrencia.getFoto())
                .into(mImagemPrincipal);
        txtData.setText(ocorrencia.getData());
        txtNatureza.setText(ocorrencia.getNatureza());
        txtDescricao.setText(ocorrencia.getDescricao());
    }

    @OnClick(R.id.detalhe_btn_editar)
    public void editarOcorrencia(View v) {
        Intent vaiParaEdicao = new Intent(this, FormularioOcorrenciaActivity.class);
        vaiParaEdicao.putExtra(CHAVE_OCORRENCIA, ocorrencia);
        startActivity(vaiParaEdicao);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
        super.onBackPressed();

    }
}

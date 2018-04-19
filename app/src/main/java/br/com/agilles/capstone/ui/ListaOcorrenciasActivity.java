package br.com.agilles.capstone.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.agilles.capstone.R;
import br.com.agilles.capstone.models.Endereco;
import br.com.agilles.capstone.models.Ocorrencia;
import br.com.agilles.capstone.ui.recyclerview.adapter.ListaOcorrenciasAdapter;
import br.com.agilles.capstone.ui.recyclerview.adapter.listener.OnItemClickListener;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ListaOcorrenciasActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.lista_ocorrencias_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private ListaOcorrenciasAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_ocorrencias);

        ButterKnife.bind(this);
        setaToolbar();

        configuraNavDrawer();
        mNavigationView.setNavigationItemSelectedListener(this);



        List<Ocorrencia> ocorrencias = criaOcorrenciaTemporaria();
        configuraRecyclerView(ocorrencias);
    }

    private void configuraNavDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open_drawer, R.string.close_drawer);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }


    private void configuraRecyclerView(List<Ocorrencia> ocorrencia) {
        configuraAdapter(ocorrencia, mRecyclerView);
    }

    private void configuraAdapter(List<Ocorrencia> ocorrencia, RecyclerView mRecyclerView) {
        adapter = new ListaOcorrenciasAdapter(this, ocorrencia);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Ocorrencia ocorrencia) {
                Toast.makeText(ListaOcorrenciasActivity.this, "Ocorrencia: " + ocorrencia.getNatureza(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private List<Ocorrencia> criaOcorrenciaTemporaria() {
        List<Ocorrencia> ocorrencias = new ArrayList<>();
        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setNatureza("Roubo de Veículos");
        ocorrencia.setData("17/04/2018");
        Endereco endereco = new Endereco();
        endereco.setBairro("Parque das nações");
        ocorrencia.setEndereco(endereco);
        ocorrencias.add(ocorrencia);

        Ocorrencia ocorrencia1 = new Ocorrencia();
        ocorrencia1.setNatureza("Tráfico de Entorpecentes");
        ocorrencia1.setData("12/04/2018");
        Endereco endereco1 = new Endereco();
        endereco1.setBairro("Egisto Ragazzo");
        ocorrencia1.setEndereco(endereco1);
        ocorrencias.add(ocorrencia1);

        Ocorrencia ocorrencia2 = new Ocorrencia();
        ocorrencia2.setNatureza("Furto de residência");
        ocorrencia2.setData("12/02/2018");
        Endereco endereco2 = new Endereco();
        endereco2.setBairro("Campos Elíseos");
        ocorrencia2.setEndereco(endereco2);
        ocorrencias.add(ocorrencia2);

        Ocorrencia ocorrencia3 = new Ocorrencia();
        ocorrencia3.setNatureza("Acidente de trânsito");
        ocorrencia3.setData("12/02/2013");
        Endereco endereco3 = new Endereco();
        endereco3.setBairro("Anel Viário");
        ocorrencia3.setEndereco(endereco3);
        ocorrencias.add(ocorrencia3);

        return ocorrencias;
    }

    @Override
    public void onBackPressed() {
        /**
         * faz navdrawer fechar com backbutton
         */
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    private void setaToolbar() {
        setSupportActionBar(mToolbar);
        setTitle("Lista de Ocorrências");

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_lista_ocorrencias:
                Toast.makeText(this, "Lista Ocorrencias", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_favorito:
                Toast.makeText(this, "Favoritos", Toast.LENGTH_SHORT).show();
                break;

        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}

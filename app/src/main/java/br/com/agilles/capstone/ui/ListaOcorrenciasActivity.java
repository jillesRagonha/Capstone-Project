package br.com.agilles.capstone.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.agilles.capstone.R;
import br.com.agilles.capstone.models.Ocorrencia;
import br.com.agilles.capstone.models.Pessoa;
import br.com.agilles.capstone.ui.recyclerview.adapter.ListaOcorrenciasAdapter;
import br.com.agilles.capstone.ui.recyclerview.adapter.listener.OnItemClickListener;
import br.com.agilles.capstone.ui.widget.OcorrenciasWidget;
import br.com.agilles.capstone.ui.widget.service.OcorrenciasWidgetService;
import br.com.agilles.capstone.utils.Constantes;
import br.com.agilles.capstone.utils.FirebaseUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ListaOcorrenciasActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Constantes {

    @BindView(R.id.lista_ocorrencias_recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.lista_ocorrencias_progress_bar)
    ProgressBar mProgressbar;

    @BindView(R.id.lista_ocorrencias_texto_loading)
    TextView mTextViewLoading;

    TextView txtNomeUsuarioLogado;
    TextView txtEmailUsuarioLogado;
    CircleImageView imagePhotoUrlUsuarioLogado;
    AlertDialog mAlertDialog;


    private ListaOcorrenciasAdapter adapter;
    private List<Ocorrencia> listaOcorrencias = new ArrayList<>();


    //FIREBASE
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    FirebaseStorage mFirebaseStorage = new FirebaseUtils().getmFirebaseStorage();
    StorageReference mFotosOcorrenciasStorageReference = new FirebaseUtils().getmFotosOcorrenciasStorageReference();
    FirebaseFirestore db = new FirebaseUtils().getmFirebaseFirestore();
    FirebaseUser usuario = new FirebaseUtils().getUser();
    private List<Ocorrencia> listaOcorrenciasParaWidget = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_ocorrencias);

        inicializaFirebase();
        ButterKnife.bind(this);
        setaToolbar();

        configuraNavDrawer();
        mNavigationView.setNavigationItemSelectedListener(this);


    }

    private void inicializaFirebase() {

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                usuario = firebaseAuth.getCurrentUser();
                if (usuarioLogado(usuario)) {
                    carregaDadosUsuario(usuario);
                    carregaOcorrencias(usuario);
                } else {
                    if (estaConectado(ListaOcorrenciasActivity.this)) {
                        vaiParaLogin();
                    } else {
                        mAlertDialog = criarDialogAlertaSemConexao().create();
                        mAlertDialog.show();
                    }

                }
            }
        };
    }

    private AlertDialog.Builder criarDialogAlertaSemConexao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.mensagem_dialog_sem_conexao);
        builder.setPositiveButton(R.string.dialog_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAndRemoveTask();
            }
        });
        return builder;
    }

    private boolean estaConectado(Context contexto) {
        ConnectivityManager connMgr = (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            return true;
        }
        return false;

    }


    private void carregaOcorrencias(FirebaseUser user) {
        listaOcorrencias = new ArrayList<>();

        db.collection("ocorrencias")
                .document("usuario")
                .collection(user.getEmail())

                .orderBy("dataCriacao", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Ocorrencia ocorrencia = document.toObject(Ocorrencia.class);
                                ocorrencia.setFirestoreIdKey(document.getId());

                                listaOcorrencias.add(ocorrencia);

                            }
                            configuraAdapter(listaOcorrencias, mRecyclerView);
                            FirebaseUtils.pegarInstancia().setOcorrencias(listaOcorrencias);


                        }
                    }
                });
    }


    private void vaiParaLogin() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build()))
                        .build(),
                CODIGO_REQUISICAO_LOGIN);
    }

    private void carregaDadosUsuario(FirebaseUser usuario) {
        txtEmailUsuarioLogado.setText(usuario.getEmail());
        txtNomeUsuarioLogado.setText(usuario.getDisplayName());
        Picasso.get().load(usuario.getPhotoUrl())
                .into(imagePhotoUrlUsuarioLogado);
    }

    private boolean usuarioLogado(FirebaseUser usuario) {
        return usuario != null;
    }

    private void configuraNavDrawer() {
        View header = mNavigationView.getHeaderView(0);
        txtNomeUsuarioLogado = header.findViewById(R.id.nav_text_nome);
        txtEmailUsuarioLogado = header.findViewById(R.id.nav_text_email);
        imagePhotoUrlUsuarioLogado = header.findViewById(R.id.nav_image_perfil);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open_drawer, R.string.close_drawer);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }


    private void configuraRecyclerView(List<Ocorrencia> ocorrencia) {
        configuraAdapter(ocorrencia, mRecyclerView);
    }

    private void configuraAdapter(List<Ocorrencia> ocorrencia, RecyclerView mRecyclerView) {

        adapter = new ListaOcorrenciasAdapter(this, ocorrencia);
        if (estaNoModoPaisagem()) {
            GridLayoutManager layoutManager = new GridLayoutManager(this, 3);

            mRecyclerView.setLayoutManager(layoutManager);
        }
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Ocorrencia ocorrencia) {
                vaiParaDetalhe(ocorrencia);
            }
        });
        mProgressbar.setVisibility(View.GONE);
        mTextViewLoading.setVisibility(View.GONE);

        if (ocorrencia.isEmpty()) {
            mTextViewLoading.setVisibility(View.VISIBLE);
            mTextViewLoading.setText(R.string.texto_nenhuma_ocorrencia);
        }


    }

    private boolean estaNoModoPaisagem() {
        return getResources().getBoolean(R.bool.modoPaisagem);
    }

    private void vaiParaDetalhe(Ocorrencia ocorrencia) {
        Intent intent = new Intent(this, DetalhesOcorrenciaActivity.class);
        intent.putExtra(CHAVE_OCORRENCIA, ocorrencia);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, mRecyclerView, "transicaoCompartilhada"); //TODO adicionar efeito de transicao entre imagem
        startActivity(intent, optionsCompat.toBundle());
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
        setTitle(getString(R.string.tituloToolbar));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_lista_ocorrencias:
                carregaOcorrencias(usuario);
                break;
            case R.id.menu_favorito:
                Toast.makeText(this, "Funcionalidade Indisponível, aguarde por atualizações", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_ocorrencia_prisao:
                carregaOcorrenciasComPrisao();
                break;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void carregaOcorrenciasComPrisao() {
        if (!listaOcorrencias.isEmpty()) {
            mProgressbar.setVisibility(View.VISIBLE);
            mTextViewLoading.setVisibility(View.VISIBLE);

            List<Ocorrencia> ocorrenciasComPrisao = new ArrayList<>();
            for (Ocorrencia o : listaOcorrencias) {
                for (Pessoa p : o.getPessoas()) {
                    if (p.isPreso()) {
                        ocorrenciasComPrisao.add(o);
                    }
                }
            }
            mProgressbar.setVisibility(View.GONE);
            mTextViewLoading.setVisibility(View.GONE);

            configuraAdapter(ocorrenciasComPrisao, mRecyclerView);

        }
    }

    public void vaiParaFormulario(View view) {
        Intent intentVaiParaFormulario = new Intent(this, FormularioOcorrenciaActivity.class);
        startActivity(intentVaiParaFormulario);
        overridePendingTransition(R.anim.side_in, R.anim.side_out);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODIGO_REQUISICAO_LOGIN) {
            if (resultCode == RESULT_OK) {

            } else if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }
}

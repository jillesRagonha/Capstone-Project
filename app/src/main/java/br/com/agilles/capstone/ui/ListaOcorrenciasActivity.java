package br.com.agilles.capstone.ui;

import android.content.Intent;
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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.agilles.capstone.R;
import br.com.agilles.capstone.models.Ocorrencia;
import br.com.agilles.capstone.ui.recyclerview.adapter.ListaOcorrenciasAdapter;
import br.com.agilles.capstone.ui.recyclerview.adapter.listener.OnItemClickListener;
import br.com.agilles.capstone.utils.Constantes;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ListaOcorrenciasActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Constantes {

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "ListaOcorrencias";

    @BindView(R.id.lista_ocorrencias_recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    TextView txtNomeUsuarioLogado;
    TextView txtEmailUsuarioLogado;
    CircleImageView imagePhotoUrlUsuarioLogado;

    private ListaOcorrenciasAdapter adapter;
    private List<Ocorrencia> listaOcorrencias;

    //FIREBASE
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseData;
    private DatabaseReference mOcorrenciasDatabaseReference;
    FirebaseStorage mFirebaseStorage;
    StorageReference mFotosOcorrenciasStorageReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        mFirebaseData = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser usuario = mFirebaseAuth.getCurrentUser();
                if (usuarioLogado(usuario)) {
                    mFotosOcorrenciasStorageReference = mFirebaseStorage.getReference().child(usuario.getEmail()).child("fotos_ocorrencias");
                    mOcorrenciasDatabaseReference = mFirebaseData.getReference().child("ocorrencias").child(usuario.getUid());
                    carregaDadosUsuario(usuario);
                    carregaOcorrencias(usuario);
                } else {
                    vaiParaLogin();

                }
            }
        };
    }

    private void carregaOcorrencias(FirebaseUser user) {
        listaOcorrencias = new ArrayList<>();

        db.collection(user.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Ocorrencia ocorrencia = document.toObject(Ocorrencia.class);
                                listaOcorrencias.add(ocorrencia);

                            }
                            configuraAdapter(listaOcorrencias, mRecyclerView);
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
                                new AuthUI.IdpConfig.PhoneBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build()))
                        .build(),
                CODIGO_REQUISICAO_LOGIN);
    }

    private void carregaDadosUsuario(FirebaseUser usuario) {
        txtEmailUsuarioLogado.setText(usuario.getEmail());
        txtNomeUsuarioLogado.setText(usuario.getDisplayName());
        Glide.with(this)
                .load(usuario.getPhotoUrl())
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
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Ocorrencia ocorrencia) {
                vaiParaDetalhe(ocorrencia);
                //TODO vai para tela de detalhes da ocorrencia
            }
        });

    }

    private void vaiParaDetalhe(Ocorrencia ocorrencia) {
        Intent intent = new Intent(this, DetalhesOcorrenciaActivity.class);
        intent.putExtra("ocorrencia", ocorrencia);
        startActivity(intent);
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
                break;
            case R.id.menu_favorito:
                break;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void vaiParaFormulario(View view) {
        Intent intentVaiParaFormulario = new Intent(this, FormularioOcorrenciaActivity.class);
        startActivity(intentVaiParaFormulario);

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

}

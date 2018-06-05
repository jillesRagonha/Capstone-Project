package br.com.agilles.capstone.ui;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nex3z.notificationbadge.NotificationBadge;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.agilles.capstone.R;
import br.com.agilles.capstone.models.Ocorrencia;
import br.com.agilles.capstone.models.Pessoa;
import br.com.agilles.capstone.models.Usuario;
import br.com.agilles.capstone.utils.Constantes;
import br.com.agilles.capstone.utils.DataUtils;
import br.com.agilles.capstone.utils.FirebaseUtils;
import br.com.agilles.capstone.utils.ImagemUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FormularioOcorrenciaActivity extends AppCompatActivity implements Constantes {

    @BindView(R.id.badge)
    NotificationBadge mBadge;

    @BindView(R.id.formulario_add_ocorrencia_icone_calendario)
    ImageView mImageCalendario;

    @BindView(R.id.formulario_add_ocorrencia_txt_data)
    TextView txtData;

    @BindView(R.id.formulario_add_ocorrencia_editNatureza)
    EditText mEditTextNatureza;

    @BindView(R.id.formulario_add_ocorrencia_editDescricao)
    EditText mEditTextDescicao;

    @BindView(R.id.activity_formulario_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.activity_formulario_imagem_principal)
    ImageView mImageView;

    @BindView(R.id.formulario_add_ocorrencia_botao_ok)
    Button botaoOk;

    private Ocorrencia ocorrenciaEdicao;
    private Uri imageUri;
    Ocorrencia ocorrencia = new Ocorrencia();
    List<Pessoa> pessoas = new ArrayList<>();

    FirebaseStorage mFirebaseStorage = new FirebaseUtils().getmFirebaseStorage();
    StorageReference mFotosOcorrenciasStorageReference = new FirebaseUtils().getmFotosOcorrenciasStorageReference();
    FirebaseUser user = new FirebaseUtils().getUser();

    FirebaseFirestore mFirebasestore = new FirebaseUtils().getmFirebaseFirestore();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_ocorrencia);
        ButterKnife.bind(this);

        configuraToolbar();
        ocorrenciaEdicao = (Ocorrencia) getIntent().getSerializableExtra(CHAVE_OCORRENCIA);
        if (ocorrenciaEdicao != null) {
            preencheCampos();
        }

    }

    private void preencheCampos() {
        txtData.setText(ocorrenciaEdicao.getData());
        mEditTextNatureza.setText(ocorrenciaEdicao.getNatureza());
        mEditTextDescicao.setText(ocorrenciaEdicao.getDescricao());
        if (!ocorrenciaEdicao.getPessoas().isEmpty()) {
            pessoas = ocorrenciaEdicao.getPessoas();
            alteraBadgeQuantidade();
        }
        if (!ocorrenciaEdicao.getFoto().isEmpty()) {
            Picasso.get().load(ocorrenciaEdicao.getFoto())
                    .into(mImageView);
        }
        botaoOk.setText(R.string.texto_botao_atualizar);
        ocorrencia = ocorrenciaEdicao;

    }


    private void configuraTitulo() {
        String titulo = mEditTextNatureza.getText().toString();
        if (!titulo.isEmpty()) {
            mToolbar.setTitle(titulo);
        }
    }

    private void configuraToolbar() {
        setSupportActionBar(mToolbar);
        setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void escolheFotoOcorrencia(View view) {
        if (!temPermissao()) {
            solicitaPermissao();
        } else {
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");

            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");

            Intent chooserIntent = Intent.createChooser(getIntent, getString(R.string.seleciona_foto));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

            startActivityForResult(chooserIntent, CODIGO_REQUISICAO_IMAGEM);
        }

    }

    private void solicitaPermissao() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }

    private boolean temPermissao() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODIGO_REQUISICAO_IMAGEM && resultCode == RESULT_OK) {
            try {
                setaImagem(data);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(FormularioOcorrenciaActivity.this, R.string.erro_seleciona_imagem, Toast.LENGTH_LONG).show();
            }

        } else if (requestCode == CODIGO_REQUISICAO_IMAGEM && resultCode == RESULT_CANCELED) {
            Toast.makeText(FormularioOcorrenciaActivity.this, R.string.erro_nao_selecionou_imagem, Toast.LENGTH_LONG).show();
        }
        if (ehResultadoComPessoa(requestCode, resultCode, data)) {
            Pessoa p = (Pessoa) data.getSerializableExtra(CHAVE_PESSOA);
            adicionaNovaPessoa(p);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {

                    Toast.makeText(this, R.string.texto_permissao_negada, Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }

    private void adicionaNovaPessoa(Pessoa p) {
        pessoas.add(p);
        alteraBadgeQuantidade();
    }

    private boolean ehResultadoComPessoa(int requestCode, int resultCode, Intent data) {
        return ehCodigoRequisicaoInserePessoa(requestCode) && ehCodigoResultadoPessoaCriada(resultCode) && temPessoa(data);
    }

    private boolean temPessoa(Intent data) {
        return data.hasExtra(CHAVE_PESSOA);
    }

    private boolean ehCodigoResultadoPessoaCriada(int resultCode) {
        return resultCode == CODIGO_RESULTADO_PESSOA_INSERIDA;

    }

    private boolean ehCodigoRequisicaoInserePessoa(int requestCode) {
        return requestCode == CODIGO_REQUISICAO_PESSOA;
    }

    private void setaImagem(Intent data) throws FileNotFoundException {
        imageUri = data.getData();
        File arquivoFInal = pegaArquivoDaImagem();

        ImagemUtils imagemUtils = new ImagemUtils();
        imageUri = imagemUtils.carregaFoto(arquivoFInal.getPath());

        Picasso.get()
                .load(imageUri)
                .into(mImageView);

    }

    @NonNull
    private File pegaArquivoDaImagem() {
        Cursor cursor = getContentResolver().query(imageUri, null, null, null, null);
        int nomeArquivo = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        cursor.moveToFirst();
        return new File(cursor.getString(nomeArquivo));
    }


    @OnClick(R.id.formulario_add_ocorrencia_icone_calendario)
    public void escolherData(ImageView view) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                txtData.setText(DataUtils.dataEmTexto(dayOfMonth, month, year));

            }
        }, mYear, mMonth, mDay);
        dpd.show();

    }

    @OnClick(R.id.formulario_add_ocorrencia_icone_pessoa)
    public void adicionarPessoa(ImageView view) {
        vaiParaInsercaoDePessoa();

    }

    private void vaiParaInsercaoDePessoa() {
        Intent vaiParaPessoa = new Intent(this, FormularioPessoasActivity.class);
        startActivityForResult(vaiParaPessoa, CODIGO_REQUISICAO_PESSOA);

    }

    private void inserePessoaNaOcorrencia(Pessoa p) {
        pessoas.add(p);
        alteraBadgeQuantidade();

    }

    private void alteraBadgeQuantidade() {
        if (mBadge.getVisibility() != View.VISIBLE) {
            mBadge.setVisibility(View.VISIBLE);
        }
        mBadge.setText(String.valueOf(pessoas.size()));
    }

    @OnClick(R.id.formulario_add_ocorrencia_botao_ok)
    public void inserirOcorrencia(Button botaoOk) {
        pegaDadosOcorrencia();

    }

    private void pegaDadosOcorrencia() {
        ocorrencia.setPessoas(pessoas);
        ocorrencia.setData(txtData.getText().toString());
        ocorrencia.setNatureza(mEditTextNatureza.getText().toString());
        ocorrencia.setDescricao(mEditTextDescicao.getText().toString());

        if (user != null) {
            Usuario usuarioLogado = new Usuario();
            usuarioLogado.setNome(user.getDisplayName());
            usuarioLogado.setEmail(user.getEmail());
            ocorrencia.setUsuario(usuarioLogado);
        }
        if (imageUri != null) {
            salvaFotoeExibeMensagem();
        } else {
            salvaOcorrenciaSemFoto();
        }

    }

    private void salvaOcorrenciaSemFoto() {
        if (ocorrencia.getFirestoreIdKey() != null) {
            atualizaOcorrencia(ocorrencia);
        } else {
            salvaNovaOcorrencia(ocorrencia);
        }
        voltaParaHome();
    }

    private void salvaFotoeExibeMensagem() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.titulo_dialogo_salvando_foto));
        progressDialog.show();

        StorageReference fotoRef = mFotosOcorrenciasStorageReference.child(user.getEmail()).child(imageUri.getLastPathSegment());
        fotoRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                ocorrencia.setFoto(downloadUrl.toString());
                if (ocorrencia.getFirestoreIdKey() != null) {
                    atualizaOcorrencia(ocorrencia);
                } else {
                    salvaNovaOcorrencia(ocorrencia);
                }
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                        .getTotalByteCount());
                progressDialog.setMessage(getString(R.string.texto_porcentagem_foto_upload) + (int) progress + "%");
                //TODO adicionar mensagem de erro caso campo esteja vazio
            }
        });
    }


    private void salvaNovaOcorrencia(Ocorrencia ocorrencia) {
        mFirebasestore.collection("ocorrencias").document("usuario").collection(ocorrencia.getUsuario().getEmail())
                .add(ocorrencia);
        voltaParaHome();


    }

    private void atualizaOcorrencia(Ocorrencia ocorrencia) {
        mFirebasestore.collection("ocorrencias").document("usuario").collection(ocorrencia.getUsuario().getEmail())
                .document(ocorrencia.getFirestoreIdKey())
                .set(ocorrencia);
        Toast.makeText(this, "Ocorrência Atualizada", Toast.LENGTH_SHORT).show();//todo criar alert ao inves de toast
        voltaParaHome();
    }

    private void voltaParaHome() {
        Intent voltaParaHome = new Intent(this, ListaOcorrenciasActivity.class);
        voltaParaHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(voltaParaHome);

    }

}

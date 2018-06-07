package br.com.agilles.capstone.ui;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;

import br.com.agilles.capstone.R;
import br.com.agilles.capstone.models.Pessoa;
import br.com.agilles.capstone.utils.Constantes;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FormularioPessoasActivity extends AppCompatActivity implements Constantes {

    @BindView(R.id.formulario_add_pessoa_editNome)
    EditText mEditTextNomePessoa;

    @BindView(R.id.formulario_add_pessoa_editDocumento)
    EditText mEditTextDocumentoPessoa;

    @BindView(R.id.activity_formulario_pessoas_text_input_nome)
    TextInputLayout mTextInputLayoutNome;

    @BindView(R.id.activity_formulario_pessoas_text_input_documento)
    TextInputLayout mTextInputLayoutDocumento;

    @BindView(R.id.activity_formulario_pessoas_radio_group_documento)
    RadioGroup mRadioDocumento;

    @BindView(R.id.formulario_add_pessoa_radio_group_condicao)
    RadioGroup mRadioCondicao;

    @BindView(R.id.switchPrisao)
    Switch mPrisaoSwitch;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private Pessoa pessoa = new Pessoa();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_pessoas);
        ButterKnife.bind(this);
        setaToolbar();


    }

    private void setaToolbar() {
        setSupportActionBar(mToolbar);
        setTitle("Inserir Pessoas na Ocorrência");
    }

    @OnClick(R.id.botao_inserir_pessoa)
    public void adicionaPessoa(Button b) {
        if (validaCampos()) {
            if (!nomeVazio()) {
                pessoa.setNome(mEditTextNomePessoa.getText().toString());
            }
            if (!documentoVazio()) {
                pessoa.setTipoDocumento(pegaDocumentoSelecionado());
                pessoa.setNumDocumento(mEditTextDocumentoPessoa.getText().toString());
            }
            pessoa.setCondicao(recuperaCondicao());
            pessoa.setPreso(mPrisaoSwitch.isChecked());

            retornaPessoaAdicionada(pessoa);
        }
    }

    private boolean nomeVazio() {
        return mEditTextNomePessoa.getText().toString().isEmpty();
    }

    private String recuperaCondicao() {
        switch (mRadioCondicao.getCheckedRadioButtonId()) {
            case R.id.opcao_condicao_testemunha:
                return "Testemunha";
            case R.id.opcao_condicao_autor:
                return "Autor";
            case R.id.opcao_condicao_vitima:
                return "Vítima";
        }
        return "";
    }

    private String pegaDocumentoSelecionado() {
        switch (mRadioDocumento.getCheckedRadioButtonId()) {
            case R.id.opcao_cnh:
                return "cnh";
            case R.id.opcao_cpf:
                return "cpf";
            case R.id.opcao_rg:
                return "rg";
        }
        return "";
    }

    private boolean documentoVazio() {
        return mEditTextDocumentoPessoa.getText().toString().isEmpty();
    }

    private void retornaPessoaAdicionada(Pessoa pessoa) {
        Intent resultadoPessoa = new Intent();
        resultadoPessoa.putExtra(CHAVE_PESSOA, pessoa);
        setResult(CODIGO_RESULTADO_PESSOA_INSERIDA, resultadoPessoa);
        finish();
        overridePendingTransition(R.anim.side_in , R.anim.side_out);

    }

    private boolean validaCampos() {
        if (mEditTextNomePessoa.getText().toString().isEmpty()) {
            mTextInputLayoutNome.setErrorEnabled(true);
            mTextInputLayoutNome.setError(getResources().getString(R.string.erro_nome_pessoa));
            return false;

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.side_in , R.anim.side_out);

    }
}

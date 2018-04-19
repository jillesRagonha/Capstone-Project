package br.com.agilles.capstone.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import br.com.agilles.capstone.R;
import butterknife.BindView;

public class FormularioOcorrenciaActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.activity_formulario_bottom_navigation)
    BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_ocorrencia);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bottom_menu_ocorrencia:
                Toast.makeText(this, "Menu Ocorrencias", Toast.LENGTH_SHORT).show();
                break;

            case R.id.bottom_menu_pessoa:
                Toast.makeText(this, "Menu Pessoas", Toast.LENGTH_SHORT).show();
                break;

            case R.id.bottom_menu_foto:
                Toast.makeText(this, "Menu Fotos", Toast.LENGTH_SHORT).show();
                break;
        }


        return true;
    }
}

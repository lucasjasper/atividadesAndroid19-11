package com.example.gerenciadordecontatos;

import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ArrayAdapter<String> adapter;

    EditText inputNome, inputEmail, inputTelefone;
    ListView listContatos;
    Button botaoSalvar, botaoCancelar;

    String nome, email, telefone;

    SQLiteDatabase bd = null;

    final String NOME_BD= "Contatos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        inputNome = findViewById(R.id.inputNome);
        inputEmail = findViewById(R.id.inputEmail);
        inputTelefone = findViewById(R.id.inputTelefone);
        botaoSalvar = findViewById(R.id.botaoSalvar);
        botaoCancelar = findViewById(R.id.botaoCancelar);

        botaoSalvar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                salvar();

            }

        });

        botaoCancelar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                limpar();

            }

        });

        try{
            bd = this.openOrCreateDatabase(NOME_BD, MODE_PRIVATE, null);

        } catch (Exception e){

            Mensagem("Erro ao conectar ao banco");
            finish();

        }

        String sql = new StringBuilder("CREATE TABLE IF NOT EXISTS contatos(")
                               .append("   id INTEGER PRIMARY KEY,")
                               .append("   nome VARCHAR(250) NOT NULL,")
                               .append("   email VARCHAR(50) NOT NULL,")
                               .append("   telefone VARCHAR(50) NOT NULL)")
                               .toString();
        this.bd.execSQL(sql);

        adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1);
        listContatos = findViewById(R.id.listContatos);
        listContatos.setAdapter(adapter);

        listarContatos();

    }

    @Override
    protected void onDestroy() {

        if(this.bd != null)

            this.bd.close();

        super.onDestroy();

    }

    void listarContatos(){
        Cursor cursor = this.bd.rawQuery("SELECT nome, email, telefone FROM contatos", null);
        adapter.clear();
        if(cursor != null && cursor.getCount() > 0){

            int nomeIdx = cursor.getColumnIndex("nome");
            int emailIdx = cursor.getColumnIndex("email");
            int telefoneIdx = cursor.getColumnIndex("telefone");

            while(cursor.moveToNext()) {

                String nome = cursor.getString(nomeIdx);
                String email = cursor.getString(emailIdx);
                String telefone = cursor.getString(telefoneIdx);

                String contato = new StringBuilder("Nome: ").append(nome).append("\n")
                                           .append("Email: ").append(email).append("\n")
                                           .append("Telefone: ").append(telefone).toString();

                this.adapter.add(contato);
            }

            this.adapter.notifyDataSetChanged();
        }
    }

    void Mensagem(String txt){
        Toast.makeText(this, txt, Toast.LENGTH_LONG).show();
    }

    void salvar() {

        nome = inputNome.getText().toString();
        email = inputEmail.getText().toString();
        telefone = inputTelefone.getText().toString();

        if (!inputNome.getText().toString().equals("") && (!inputTelefone.getText().toString().equals("") || !inputEmail.getText().toString().equals(""))) {

            if (inputEmail.getText().toString().equals(""))

                email = "Sem E-mail";

            else if (inputTelefone.getText().toString().equals(""))

                telefone = "Sem Telefone";

            String sql = new StringBuilder("INSERT INTO contatos(nome, email, telefone) VALUES (\"")
                                   .append(nome + "\", \"")
                                   .append(email + "\", \"")
                                   .append(telefone + "\")")
                                   .toString();

            this.bd.execSQL(sql);
            this.listarContatos();
            this.limpar();

        }

    }

    void limpar() {

        inputNome.setText("");
        inputEmail.setText("");
        inputTelefone.setText("");

    }
}

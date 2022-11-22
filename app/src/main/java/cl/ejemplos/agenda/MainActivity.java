package cl.ejemplos.agenda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.UUID;

import cl.ejemplos.agenda.modelo.Persona;

public class MainActivity extends AppCompatActivity {
    private EditText etNombreP, etApellidoP, etCorreoP, etPasswordP;
    private ListView lvPersonas;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayList<Persona> listaPersonas = new ArrayList<>();
    private ArrayAdapter<Persona> personaArrayAdapter;
    private Persona personaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etNombreP=(EditText) findViewById(R.id.txt_nombrePersona);
        etApellidoP=(EditText) findViewById(R.id.txt_apellidoPersona);
        etCorreoP=(EditText) findViewById(R.id.txt_correoPersona);
        etPasswordP=(EditText) findViewById(R.id.txt_passwordPersona);
        lvPersonas=(ListView) findViewById(R.id.lv_datosPersonas);

        inicializarFirebase();
        listarDatos();
        lvPersonas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                personaSeleccionada = (Persona) adapterView.getItemAtPosition(i);
                etNombreP.setText(personaSeleccionada.getNombre());
                etApellidoP.setText(personaSeleccionada.getApellido());
                etCorreoP.setText(personaSeleccionada.getCorreo());
                etPasswordP.setText(personaSeleccionada.getPassword());
            }
        });
    }

    private void listarDatos() {
        databaseReference.child("Persona").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaPersonas.clear();
                for(DataSnapshot obj: snapshot.getChildren()) {
                    Persona p = obj.getValue(Persona.class);
                    listaPersonas.add(p);
                }
                    personaArrayAdapter = new ArrayAdapter<Persona>(MainActivity.this,
                            android.R.layout.simple_list_item_1,
                            listaPersonas);
                    lvPersonas.setAdapter(personaArrayAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        String nombre=etNombreP.getText().toString();
        String apellido=etApellidoP.getText().toString();
        String correo=etCorreoP.getText().toString();
        String password=etPasswordP.getText().toString();

        switch(item.getItemId())
        {

            case R.id.icon_add:


                if(validacion())
                {
                    Persona p= new Persona();
                    p.setUid(UUID.randomUUID().toString());
                    p.setNombre(nombre);
                    p.setApellido(apellido);
                    p.setCorreo(correo);
                    p.setPassword(password);

                    databaseReference.child("Persona").child(p.getUid()).setValue(p);
                    limpiar();
                    Toast.makeText(this, "Agregado",Toast.LENGTH_SHORT).show();
                }



                break;
            case R.id.icon_delete:
                Persona p= new Persona();
                p.setUid(personaSeleccionada.getUid());
                databaseReference.child("Persona").child(p.getUid()).removeValue();
                limpiar();
                Toast.makeText(this, "Eliminado",Toast.LENGTH_SHORT).show();
                break;

            case R.id.icon_save:
                Persona per=new Persona();
                per.setUid(personaSeleccionada.getUid());
                per.setNombre(nombre);
                per.setApellido(apellido);
                per.setCorreo(correo);
                per.setPassword(password);

                databaseReference.child("Persona").child(per.getUid()).setValue(per);
                limpiar();

                Toast.makeText(this,"Actualizado: "+per.getNombre(),Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }


    private boolean validacion()
    {
        if(etNombreP.getText().toString().equals(""))
        {
            etNombreP.setError("Required");
            return false;
        }
        if(etApellidoP.getText().toString().equals(""))
        {
            etApellidoP.setError("Required");
            return false;
        }
        if(etCorreoP.getText().toString().equals(""))
        {
            etCorreoP.setError("Required");
            return false;
        }
        if(etPasswordP.getText().toString().equals(""))
        {
            etPasswordP.setError("Required");
            return false;
        }
        return true;
    }
    private void limpiar()
    {
        etNombreP.setText("");
        etApellidoP.setText("");
        etCorreoP.setText("");
        etPasswordP.setText("");
    }

}
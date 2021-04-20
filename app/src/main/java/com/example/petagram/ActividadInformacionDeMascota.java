package com.example.petagram;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.petagram.Modelo.Mascota;
import com.example.petagram.Utilidades.Datos;
import com.example.petagram.Utilidades.FormateadorDeImagenes;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class ActividadInformacionDeMascota extends AppCompatActivity {

    TextView TvNombreMascota, TvUsuario, TvEspecieMascota, TvRazaMascota, TvDescripcionMascota, TvEdadMascota, TvGeneroMascota, TvColorMascota, TvRecompensaMascota, TvFechaYHora, TvUltimaConocida, TvTamanoMascota;
    ImageView ImvMascota;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_de_mascota);
        InicializarViews();
        Intent intent = getIntent();
        Mascota mascota = Datos.getMascotas().get(intent.getIntExtra("position",0));
        RellenarCampos(mascota);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void RellenarCampos(Mascota mascota) {
        TvNombreMascota.setText(mascota.getNombre());
        TvUsuario.setText(mascota.getUsuario());
        TvEdadMascota.setText(String.valueOf(mascota.getEdad()));
        TvGeneroMascota.setText(mascota.getGenero());
        TvColorMascota.setText(mascota.getColor());
        TvRecompensaMascota.setText(String.valueOf(mascota.getRecompensa()));
        TvDescripcionMascota.setText(mascota.getDescripcion());
        TvRazaMascota.setText(mascota.getRaza());
        TvEspecieMascota.setText(mascota.getEspecie());
        final DateTimeFormatter input = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        DateTimeFormatter output = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        TemporalAccessor date = input.parse(mascota.getFecha_y_hora());
        TvFechaYHora.setText(output.format(date));
        TvTamanoMascota.setText(mascota.getTamano());
        TvUltimaConocida.setText(mascota.getUltima_posicion_conocida());
        ImvMascota.setImageBitmap(FormateadorDeImagenes.DesdeBase64(mascota.getImagen()));
    }

    private void InicializarViews() {
        TvNombreMascota = findViewById(R.id.TvNombreMascota);
        TvUsuario = findViewById(R.id.TVusuario);
        TvEdadMascota = findViewById(R.id.TvEdadMascota);
        TvGeneroMascota = findViewById(R.id.TvGeneroMascota);
        TvColorMascota = findViewById(R.id.TvColorMascota);
        TvRecompensaMascota = findViewById(R.id.TvRecompensaMascota);
        TvDescripcionMascota = findViewById(R.id.TvDescripcionMascota);
        TvRazaMascota = findViewById(R.id.TvRazaMascota);
        TvEspecieMascota = findViewById(R.id.TvEspecieMascota);
        TvFechaYHora = findViewById(R.id.TvFechaYHora);
        TvTamanoMascota = findViewById(R.id.TvTamanoMascota);
        TvUltimaConocida = findViewById(R.id.TvUltimaPosicionConocida);
        ImvMascota = findViewById(R.id.ImvMascota);
    }
}
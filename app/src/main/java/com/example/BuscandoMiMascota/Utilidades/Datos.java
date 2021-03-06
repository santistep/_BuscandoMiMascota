package com.example.BuscandoMiMascota.Utilidades;

import android.app.Activity;
import android.location.Address;
import android.location.Location;
import android.util.Log;

import com.example.BuscandoMiMascota.Actividades.FragmentListadoMascotas;
import com.example.BuscandoMiMascota.Modelo.Mascota;
import com.example.BuscandoMiMascota.Actividades.ActividadPrincipal;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Datos implements AsyncResponse {

    private static Datos single_instance = null;


    public static ArrayList<Mascota> TodasLasMascotas;
    public static ArrayList<Mascota> ArrayEnUso = new ArrayList<>();
    private static ListenerDeDatos listenerDeDatos;
    private Activity contexto;

    private Datos() {
    }

    public static Datos getInstance() {
        if (single_instance == null)
            single_instance = new Datos();

        return single_instance;
    }

    public static void AgregarMascota(Mascota mascota) {
        ArrayEnUso.add(mascota);
        TodasLasMascotas.add(mascota);
        int position = ArrayEnUso.indexOf(mascota);
        listenerDeDatos.OnSeAgregoMascota(position);
    }

    public static void ModificarMascota(Mascota modificada) {
        Mascota originaltodaslasmascotas = null;
        Mascota originalarrayenuso = null;
        for (Mascota m : TodasLasMascotas) {
            if (m.getPk() == modificada.getPk()) {
                originaltodaslasmascotas = m;
            }
        }
        for (Mascota m : ArrayEnUso) {
            if (m.getPk() == modificada.getPk()) {
                originalarrayenuso = m;
            }
        }
        int positionarrayenuso = ArrayEnUso.indexOf(originalarrayenuso);
        int positiontodaslasmascotas = TodasLasMascotas.indexOf(originaltodaslasmascotas);
        Log.d("Milog", "ModificarMascota:" + positionarrayenuso);
        ArrayEnUso.set(positionarrayenuso, modificada);
        TodasLasMascotas.set(positiontodaslasmascotas, modificada);
        listenerDeDatos.OnSeModificoMascota(positionarrayenuso);
    }

    public static void EliminarMascota(Mascota mascota) {
        int llavePrimariaParaEliminar = mascota.getPk();
        String JsonParaEnviar = String.format("{\"pk\" : \"%d\"}", llavePrimariaParaEliminar);
        Log.d("Milog", "EliminarMascota " + JsonParaEnviar);

        EnviarJSON enviarJSON = new EnviarJSON(Datos.getInstance().contexto, RutasUrl.RutaDeProduccion + "/mascota/eliminarmascotamovil/", JsonParaEnviar);
        enviarJSON.setDelegate(Datos.getInstance());
        enviarJSON.execute();
        int position = ArrayEnUso.indexOf(mascota);
        TodasLasMascotas.remove(mascota);
        ArrayEnUso.remove(mascota);
        listenerDeDatos.OnSeEliminoMascota(position);
    }

    public static void InicializarDataSet(Activity contexto) {
        if (TodasLasMascotas == null) {
            Datos.getInstance().contexto = contexto;
            String JSON_URL = "https://aalza.pythonanywhere.com/mascota/json/";
            TraeJSON traeJSON = new TraeJSON(contexto, JSON_URL);
            traeJSON.setDelegate(Datos.getInstance());
            traeJSON.execute();
        }
    }

    public static void setListenerDeDatos(ListenerDeDatos listenerDeDatos) {
        Datos.listenerDeDatos = listenerDeDatos;
    }

    public static ArrayList<Mascota> getMascotasDelUsuario(final Activity contexto) {
        ArrayList<Mascota> ordenado = new ArrayList<>();
        for (Mascota m : TodasLasMascotas) {
            if (m.getUsuario().equals(SesionDeUsuario.ConseguirEmailDeSesion(contexto))) {
                ordenado.add(m);
            }
        }
        ArrayEnUso = ordenado;
        return ArrayEnUso;
    }

    public static ArrayList<Mascota> getMascotasMasRecientes() {

        ArrayList<Mascota> ordenado = new ArrayList<>(TodasLasMascotas);
        Collections.sort(ordenado, new Comparator<Mascota>() {
            @Override
            public int compare(Mascota o1, Mascota o2) {
                return DateParser.ParseDate(o1.getFecha_denuncia()).compareTo(DateParser.ParseDate(o2.getFecha_denuncia()));
            }
        }.reversed());
        ArrayEnUso = ordenado;
        return ArrayEnUso;
    }

    public static ArrayList<Mascota> getMascotasCercanasAmi() {

        Location MiUbicacion = DevuelveGps.getUbicacion(Datos.getInstance().contexto);
        ArrayList<Mascota> arrayList = new ArrayList<>(TodasLasMascotas);
        if (MiUbicacion != null) {
            final Location finalMiUbicacion = MiUbicacion;
            arrayList.sort(new Comparator<Mascota>() {
                @Override
                public int compare(Mascota o1, Mascota o2) {
                    Address ad1 = null;
                    try {
                        ad1 = DevuelveGps.ConseguirLatyLong(o1.getUltima_posicion_conocida(), Datos.getInstance().contexto);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address ad2 = null;
                    try {
                        ad2 = DevuelveGps.ConseguirLatyLong(o2.getUltima_posicion_conocida(), Datos.getInstance().contexto);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Location loc1 = new Location("");
                    loc1.setLatitude(ad1.getLatitude());
                    loc1.setLongitude(ad1.getLongitude());
                    Location loc2 = new Location("");
                    loc2.setLatitude(ad2.getLatitude());
                    loc2.setLongitude(ad2.getLongitude());
                    Log.d("Milog", String.valueOf(loc1.getLatitude()));
                    return (int) (finalMiUbicacion.distanceTo(loc1) - finalMiUbicacion.distanceTo(loc2));
                }
            });
        }
        ArrayEnUso = arrayList;
        return ArrayEnUso;
    }

    public static ArrayList<Mascota> getTodasLasMascotas() {
        ArrayEnUso = new ArrayList<>(TodasLasMascotas);
        return ArrayEnUso;
    }

    public static void setTodasLasMascotas(ArrayList<Mascota> todasLasMascotas) {
        TodasLasMascotas = todasLasMascotas;
    }

    public static ArrayList<Mascota> Buscar(String Busqueda) {
        Busqueda = Busqueda.toLowerCase();

        ArrayList<Mascota> filtrado = new ArrayList<>();
        for (Mascota m : TodasLasMascotas) {
            if (m.getNombre().toLowerCase().contains(Busqueda) || m.getRaza().toLowerCase().contains(Busqueda)||m.getEspecie().toLowerCase().contains(Busqueda)){
                filtrado.add(m);
            }
        }
        ArrayEnUso = filtrado;
        return filtrado;
    }


    @Override
    public void AlConseguirDato(String output) {

        output = output.trim();
        if (output.length() > 5) {
            Gson gson = new Gson();
            Mascota[] array = gson.fromJson(output, Mascota[].class);
            ArrayList<Mascota> mascotas = new ArrayList<>();
            Collections.addAll(mascotas, array);
            setTodasLasMascotas(mascotas);
            FragmentListadoMascotas Listado = (FragmentListadoMascotas) ((ActividadPrincipal) Datos.getInstance().contexto).getSupportFragmentManager().findFragmentByTag("f0");
            Listado.mascotaAdaptador.setArrayListUsado(mascotas);
            Listado.InicializarAdaptador();
        }
        if ("0".equals(output)) {
            Log.d("Milog", "AlConseguirDato: Se elimino la mascota en el servidor todo bien");
        }
    }
}

package upc.edu.pe.wedraw;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.connectsdk.service.capability.listeners.ResponseListener;
import com.connectsdk.service.command.ServiceCommandError;

import java.util.regex.Pattern;

import upc.edu.pe.wedraw.helpers.ConnectionHelper;
import upc.edu.pe.wedraw.helpers.JsonHelper;
import upc.edu.pe.wedraw.helpers.StatusHelper;

/**
 * Created by aqws3 on 4/12/16.
 */
public class InputNameActivity extends Activity{

    private static final String NAME_REGEX = "[A-Za-z\\s+]{1,20}";
    private static final String NAME_ERROR = "Ingrese un nombre. Maximo 14 caracteres";
    private static final String NAME_SUCCESS = "Conectado";

    EditText mNameEditText;
    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_name);
        ConnectionHelper.sDesaplgListener.setInputNameActivity(this);

        mNameEditText = (EditText) findViewById(R.id.activity_input_name_edit_text);
        mButton = (Button) findViewById(R.id.activity_input_name_button);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Verificar nombre es menor de 20 caracteres
                final String name = mNameEditText.getText().toString().trim();

                if(Pattern.matches(NAME_REGEX, name) && name.length() <= 14){
                    //Enviar el nombre al webapp
                    ConnectionHelper.sWebAppSession.sendMessage(JsonHelper.ConnectPlayer(name), new ResponseListener<Object>() {
                        @Override
                        public void onSuccess(Object object) {
                            StatusHelper.playerName = name;
                            MediaPlayer mp = MediaPlayer.create(InputNameActivity.this, R.raw.conectado);
                            mp.start();
                        }
                        @Override
                        public void onError(ServiceCommandError error) {

                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), NAME_ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void mostrarCreditos(View v){

        ConnectionHelper.sWebAppSession.sendMessage(JsonHelper.showAbout(), new ResponseListener<Object>() {
            @Override
            public void onSuccess(Object object) {
            }
            @Override
            public void onError(ServiceCommandError error) {
            }
        });
    }

    public void conexionExitosa(){

        Toast.makeText(getApplicationContext(), NAME_SUCCESS, Toast.LENGTH_SHORT);
        StatusHelper.conexionExitosa = true;
        Intent i = new Intent(InputNameActivity.this, StartGameActivity.class);
        startActivity(i);
        finish();
    }

    public void limiteJugadores(){

        Toast toast = Toast.makeText(getApplicationContext(), "Usted no se puede conectar porque ha execdido la cantidad de jugadores posibles.", Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    protected void onDestroy() {
        ConnectionHelper.sDesaplgListener.setInputNameActivity(null);
        System.gc();
        StatusHelper.unbindDrawables(findViewById(R.id.layoutInputName));
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {}
}
package com.example.reproductor;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {

    private VideoView videoView;
    private MediaPlayer mediaPlayer;
    private ImageView imageBackground;
    private ImageButton btnReproducir, btnSiguiente, btnAnterior, btnRepetir, btnDetener;

    private ArrayList<MediaItem> mediaList = new ArrayList<>();
    private int currentMediaIndex = 0; // Índice actual del archivo
    private boolean isRepeatEnabled = false; // Estado del modo Repetir

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar controles
        videoView = findViewById(R.id.video_view);
        imageBackground = findViewById(R.id.image_background);
        btnReproducir = findViewById(R.id.btnReproducir);
        btnSiguiente = findViewById(R.id.btnSiguiente);
        btnAnterior = findViewById(R.id.btnAnterior);
        btnRepetir = findViewById(R.id.btnRepetir);
        btnDetener = findViewById(R.id.btnDetener);

        // Configurar lista de medios
        mediaList.add(new MediaItem("video", R.raw.videoplayback)); // Video
        mediaList.add(new MediaItem("audio", R.raw.sound)); // Audio
        mediaList.add(new MediaItem("video", R.raw.vuelve));
        mediaList.add(new MediaItem("video", R.raw.presumida));
        // Reproducir el primer medio
        playMedia(currentMediaIndex);

        // Botón Reproducir/Pausar
        btnReproducir.setOnClickListener(v -> togglePlayPause());

        // Botón Siguiente
        btnSiguiente.setOnClickListener(v -> {
            if (currentMediaIndex < mediaList.size() - 1) {
                currentMediaIndex++;
                playMedia(currentMediaIndex);
            } else {
                Toast.makeText(this, "Este es el último archivo.", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón Anterior
        btnAnterior.setOnClickListener(v -> {
            if (currentMediaIndex > 0) {
                currentMediaIndex--;
                playMedia(currentMediaIndex);
            } else {
                Toast.makeText(this, "Este es el primer archivo.", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón Repetir/No Repetir
        btnRepetir.setOnClickListener(v -> toggleRepeatMode());

        // Botón Detener
        btnDetener.setOnClickListener(v -> stopMedia());
    }

    private void togglePlayPause() {
        MediaItem currentMedia = mediaList.get(currentMediaIndex);
        if (currentMedia.getType().equals("video")) {
            if (videoView.isPlaying()) {
                videoView.pause();
                btnReproducir.setImageResource(R.drawable.reproducir);
            } else {
                videoView.start();
                btnReproducir.setImageResource(R.drawable.pausa);
            }
        } else if (currentMedia.getType().equals("audio")) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                btnReproducir.setImageResource(R.drawable.reproducir);
            } else {
                mediaPlayer.start();
                btnReproducir.setImageResource(R.drawable.pausa);
            }
        }
    }

    private void toggleRepeatMode() {
        isRepeatEnabled = !isRepeatEnabled;
        if (isRepeatEnabled) {
            Toast.makeText(this, "Modo Repetir Activado", Toast.LENGTH_SHORT).show();
            btnRepetir.setImageResource(R.drawable.repetir);
        } else {
            Toast.makeText(this, "Modo Repetir Desactivado", Toast.LENGTH_SHORT).show();
            btnRepetir.setImageResource(R.drawable.no_repetir);
        }
    }

    private void stopMedia() {
        MediaItem currentMedia = mediaList.get(currentMediaIndex);

        if (currentMedia.getType().equals("video")) {
            videoView.stopPlayback();
            videoView.resume(); // Reiniciar posición
            btnReproducir.setImageResource(R.drawable.reproducir);
        } else if (currentMedia.getType().equals("audio")) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset(); // Reiniciar MediaPlayer
                mediaPlayer = MediaPlayer.create(this, currentMedia.getResourceId());
                btnReproducir.setImageResource(R.drawable.reproducir);
            }
        }
        Toast.makeText(this, "Reproducción detenida", Toast.LENGTH_SHORT).show();
    }

    private void playMedia(int index) {
        MediaItem media = mediaList.get(index);

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        videoView.stopPlayback();

        if (media.getType().equals("video")) {
            videoView.setVisibility(VideoView.VISIBLE);
            imageBackground.setVisibility(ImageView.GONE);

            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + media.getResourceId());
            videoView.setVideoURI(videoUri);
            videoView.setOnCompletionListener(mp -> {
                if (isRepeatEnabled) {
                    videoView.start(); // Repetir video
                } else if (currentMediaIndex < mediaList.size() - 1) {
                    currentMediaIndex++;
                    playMedia(currentMediaIndex);
                }
            });
            videoView.start();
            btnReproducir.setImageResource(R.drawable.pausa);
        } else if (media.getType().equals("audio")) {
            videoView.setVisibility(VideoView.GONE);
            imageBackground.setVisibility(ImageView.VISIBLE);
            imageBackground.setImageResource(R.drawable.portada1);

            mediaPlayer = MediaPlayer.create(this, media.getResourceId());
            mediaPlayer.setOnCompletionListener(mp -> {
                if (isRepeatEnabled) {
                    mediaPlayer.start();
                } else if (currentMediaIndex < mediaList.size() - 1) {
                    currentMediaIndex++;
                    playMedia(currentMediaIndex);
                }
            });
            mediaPlayer.start();
            btnReproducir.setImageResource(R.drawable.pausa);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}



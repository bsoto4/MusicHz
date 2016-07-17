package os.musichz;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listPlayer;
    private ArrayList<File> musicFiles;
    private String[] items;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listPlayer = (ListView) findViewById(R.id.list_player);
        musicFiles = findAllMusic(Environment.getExternalStorageDirectory());
        items = new String[musicFiles.size()];
        for (int i = 0; i < musicFiles.size(); i++) {
            items[i] = musicFiles.get(i).getName().toString().replace(".mp3", "").replace(".vav", "").toLowerCase();
        }

        adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.item_music, R.id.tv_music, items);
        listPlayer.setAdapter(adapter);

        listPlayer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getApplicationContext(), Player.class).putExtra("POSITION", position).putExtra("MUSICFILES", musicFiles));
            }
        });
    }

    private ArrayList<File> findAllMusic(File root) {
        File[] allFiles = root.listFiles();
        ArrayList<File> musicFiles = new ArrayList<>();
        for (File file : allFiles) {
            if (file.isDirectory() && !file.isHidden()) {
                musicFiles.addAll(findAllMusic(file));
            } else {
                if (file.getName().endsWith(".mp3") || file.getName().endsWith(".vav")) {
                    musicFiles.add(file);
                }
            }
        }
        return musicFiles;
    }
}

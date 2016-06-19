package wordwallpaper.android.com.wordwallpaper;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Typeface myTypeface;
    Bitmap currentBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Reserve Typeface
        myTypeface = Typeface.createFromAsset(getAssets(), "NotoSansCJKtc-Regular.otf");

        // TODO: Generate bitmap event
        try {
            currentBitmap = BitmapFactory.decodeStream(getAssets().open("greeting.png"));
        } catch (IOException e) {
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setRippleColor(getResources().getColor(R.color.fabRipple));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //((EditText) findViewById(R.id.edit_text)).setText("Why don't you eat shit?");
                if (currentBitmap != null) setAsWallpaper(currentBitmap);
            }
        });
    }

    private void setAsWallpaper(Bitmap b) {
        WallpaperManager wm = WallpaperManager.getInstance(getApplicationContext());
        try {
            wm.setBitmap(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

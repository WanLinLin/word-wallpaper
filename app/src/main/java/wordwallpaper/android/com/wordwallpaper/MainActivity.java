package wordwallpaper.android.com.wordwallpaper;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    Typeface myTypeface;
    Bitmap currentBitmap;
    Snackbar snackbar;
    CoordinatorLayout coordinatorLayout;
    WallpaperManager wm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Define some useful variables
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.my_coordinatorlayout);
        wm = WallpaperManager.getInstance(this);

        // Reserve Typeface
        myTypeface = Typeface.createFromAsset(getAssets(), "NotoSansCJKtc-Regular.otf");

        // Init snackbar
        snackbar = Snackbar.make(coordinatorLayout, getString(R.string.set_wallpaper_ok),
                                 Snackbar.LENGTH_SHORT);

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
                if (currentBitmap != null) {
                    setAsWallpaper(currentBitmap);
                    currentBitmap = null;
                    snackbar.show();
                }
            }
        });
    }

    private void setAsWallpaper(Bitmap b) {
        try {
            Log.d(LOG_TAG, "Try to set Bitmap");
            wm.setBitmap(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package wordwallpaper.android.com.wordwallpaper;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    Typeface myTypeface;
    Bitmap currentBitmap;
    Snackbar snackbar;
    CoordinatorLayout coordinatorLayout;
    WallpaperManager wm;
    int scaledSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Define some useful variables
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.my_coordinatorlayout);
        wm = WallpaperManager.getInstance(this);

        // Get font size
        scaledSize = getResources().getDimensionPixelSize(R.dimen.myFontSize);

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

        textToBitmap("中山大");
    }

    private void setAsWallpaper(Bitmap b) {
        try {
            Log.d(LOG_TAG, "Try to set Bitmap");
            wm.setBitmap(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap textToBitmap(String inputString) {

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
//
//        Log.d("MainActivity", String.valueOf(size.y));
//        Log.d("MainActivity", String.valueOf(size.x));

        Bitmap b = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        Paint p = new Paint();
        c.drawColor(Color.WHITE);
        p.setTypeface(myTypeface);
        p.setTextSize(scaledSize);
        p.setColor(Color.BLACK);// 设置红色
        //p.setTextAlign(Paint.Align.CENTER);


        c.drawText(inputString, 100, 200, p);// 画文本

        ((ImageView)findViewById(R.id.image_view)).setImageBitmap(b);

        return b;
    }
}

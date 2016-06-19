package wordwallpaper.android.com.wordwallpaper;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
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
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    private Typeface myTypeface;
    private Bitmap currentBitmap;
    private CoordinatorLayout coordinatorLayout;
    private WallpaperManager wm;
    private ImageView preview;
    private EditText editText;
    private InputMethodManager inputManager;
    int scaledSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Define some useful variables
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.my_coordinatorlayout);
        wm = WallpaperManager.getInstance(this);
        preview = (ImageView) findViewById(R.id.preview);
        editText = (EditText) findViewById(R.id.edit_text);
        inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // Get font size
        scaledSize = getResources().getDimensionPixelSize(R.dimen.myFontSize);

        // Reserve Typeface
        myTypeface = Typeface.createFromAsset(getAssets(), "NotoSansCJKtc-Regular.otf");

        // Generate bitmap event
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.d(LOG_TAG, "Enter pressed");
                currentBitmap = textToBitmap(editText.getText().toString());
                preview.setImageBitmap(currentBitmap);
                // Hide keyboard
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                return true;
            }
        });

        // Add floating action button and its event
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setRippleColor(getResources().getColor(R.color.fabRipple));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentBitmap != null) {
                    setAsWallpaper(currentBitmap);
                    currentBitmap = null;
                    // Show snackbar
                    Snackbar.make(coordinatorLayout, getString(R.string.set_wallpaper_ok),
                            Snackbar.LENGTH_SHORT).show();
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

    private Bitmap textToBitmap(String inputString) {

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);

        Bitmap b = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        Paint p = new Paint();
        c.drawColor(Color.WHITE);
        p.setTypeface(myTypeface);
        p.setTextSize(scaledSize);
        p.setColor(Color.BLACK);
        p.setTextAlign(Paint.Align.CENTER);

        // Draw text
        c.drawText(inputString, size.x/2, size.y/5, p);

        return b;
    }
}

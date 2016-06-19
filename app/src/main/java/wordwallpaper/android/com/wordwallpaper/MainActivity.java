package wordwallpaper.android.com.wordwallpaper;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    private Typeface myTypeface;
    private Bitmap currentBitmap;
    private Bitmap currentPreview;
    private Bitmap placeholder;
    private CoordinatorLayout coordinatorLayout;
    private WallpaperManager wm;
    private ImageView preview;
    private EditText editText;
    private InputMethodManager inputManager;
    private int scaledSize;
    private boolean confirm = false;
    private Point size;

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
        // Screen size
        Display display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getRealSize(size);
        // Placeholder bitmap
        try {
            placeholder = BitmapFactory.decodeStream(getAssets().open("placeholder.png"));
            placeholder = Bitmap.createScaledBitmap(placeholder, size.x, size.y, false);
        } catch (IOException e) {}

        // Get font size
        scaledSize = getResources().getDimensionPixelSize(R.dimen.myFontSize);

        // Reserve Typeface
        myTypeface = Typeface.createFromAsset(getAssets(), "NotoSansCJKtc-Regular.otf");

        // Generate bitmap event
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                confirm = false;
                // submit();
                // Hide keyboard
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                return true;
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String inputText = editText.getText().toString();
                Bitmap[] tmp = textToBitmap(inputText);
                currentBitmap = tmp[0];
                currentPreview = tmp[1];
                preview.setImageBitmap(currentPreview);
                confirm = false;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Add floating action button and its event
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setRippleColor(getResources().getColor(R.color.fabRipple));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });


    }

    private void submit() {
        if (confirm) {
            if (currentBitmap != null) {
                setAsWallpaper(currentBitmap);
                currentBitmap = null;
                // Show snackbar
                Snackbar.make(coordinatorLayout, getString(R.string.set_wallpaper_ok),
                        Snackbar.LENGTH_SHORT).show();
            }
            confirm = false;
        } else {
            String inputText = editText.getText().toString();
            Bitmap[] tmp = textToBitmap(inputText);
            currentBitmap = tmp[0];
            currentPreview = tmp[1];
            preview.setImageBitmap(currentPreview);
            Snackbar.make(coordinatorLayout, getString(R.string.confirm),
                    Snackbar.LENGTH_SHORT).show();
            confirm = true;
        }
    }

    private void setAsWallpaper(Bitmap b) {
        try {
            Log.d(LOG_TAG, "Try to set Bitmap");
            wm.setBitmap(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap[] textToBitmap(String inputString) {
        Bitmap b = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        Paint p = new Paint();
        c.drawColor(Color.WHITE);
        p.setTypeface(myTypeface);
        p.setTextSize(scaledSize);
        p.setColor(Color.BLACK);
        p.setTextAlign(Paint.Align.CENTER);

        // Draw text
        Paint.FontMetrics fm = p.getFontMetrics();
        float textHeight = 0.7f * (fm.descent - fm.ascent);
        String[] splitString = inputString.split("");
        for (int i = 0; i < splitString.length; i++) {
            String s = splitString[i];
            c.drawText(s, size.x / 2, size.y / 16 + i * textHeight, p);
        }

        Bitmap preview = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);
        Canvas pc = new Canvas(preview);
        pc.drawBitmap(b, 0, 0, null);
        pc.drawBitmap(placeholder, 0, 0, null);

        Bitmap[] rt = {b, preview};

        return rt;
    }
}

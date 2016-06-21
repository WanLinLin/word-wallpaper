package wordwallpaper.android.com.wordwallpaper;

import android.app.WallpaperManager;
import android.content.Context;
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
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    private Typeface myTypeface, tfNoto;
    private Bitmap currentBitmap;
    private Bitmap currentPreview;
    private Bitmap placeholder;
    private CoordinatorLayout coordinatorLayout;
    private WallpaperManager wm;
    private ImageView preview;
    private EditText editText;
    private InputMethodManager inputManager;
    private float scaledSize;
    private boolean confirm = false;
    private Point size;
    private int maxStringLen = 15;
    private float topPaddingRatio = 1.0f;
    private int myBgColor, myFontColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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

        // Init Typeface
        tfNoto = Typeface.createFromAsset(getAssets(), "NotoSansCJKtc-Regular.otf");
        myTypeface = tfNoto;

        // Init colors
        myBgColor = Color.WHITE;
        myFontColor = Color.BLACK;

        // Font spinner
        Spinner fontSpinner = (Spinner) findViewById(R.id.spinner_font);
        String[] fonts = {getString(R.string.notosans)};
        ArrayAdapter<String> fontListAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, fonts);
        fontSpinner.setAdapter(fontListAdapter);
        fontSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        myTypeface = tfNoto;
                        topPaddingRatio = 1.0f;
                        break;
                }
                refreshPreview();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Color spinner
        Spinner colorSpinner = (Spinner) findViewById(R.id.spinner_color);
        String[] colors = {getString(R.string.light), getString(R.string.dark)};
        ArrayAdapter<String> colorListAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, colors);
        colorSpinner.setAdapter(colorListAdapter);
        colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        myBgColor = Color.WHITE;
                        myFontColor = Color.BLACK;
                        break;
                    case 1:
                        myBgColor = Color.parseColor("#212121");
                        myFontColor = Color.WHITE;
                        break;
                }
                refreshPreview();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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
                refreshPreview();
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

    /**
     * Refresh the wallpaper preview image
     */
    private void refreshPreview() {
        String inputText = editText.getText().toString();
        Bitmap[] tmp = textToBitmap(inputText);
        currentBitmap = tmp[0];
        currentPreview = tmp[1];
        preview.setImageBitmap(currentPreview);
        confirm = false;
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
            refreshPreview();
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
        float fontSize;

        if (inputString.length() < maxStringLen / 2)
            fontSize = scaledSize * 11 / inputString.length();
        else
            fontSize = scaledSize * 11 / ((float)inputString.length() / 2);

        Paint p = new Paint();
        c.drawColor(myBgColor);
        p.setTypeface(myTypeface);
        p.setTextSize(fontSize);
        p.setColor(myFontColor);
        p.setTextAlign(Paint.Align.CENTER);

        // Draw text
        Paint.FontMetrics fm = p.getFontMetrics();
        float textHeight = 0.7f * (fm.descent - fm.ascent);
        String[] splitString = inputString.split("");
        float posY = topPaddingRatio *  size.y / 16;

        if (inputString.length() < maxStringLen / 2) {
            for (int i = 0; i < splitString.length; i++) {
                String s = splitString[i];
                c.drawText(s, size.x / 2, posY + i * textHeight, p);
            }
        }
        else {
            int boundary = splitString.length / 2 + 1;

            if (splitString.length % 2 == 0) {
                // draw first line
                for (int i = 0; i < boundary; i++) {
                    String s = splitString[i];
                    c.drawText(s, size.x / 3, posY + i * textHeight, p);
                }
                // draw second line
                for (int i = boundary; i < splitString.length; i++) {
                    String s = splitString[i];
                    c.drawText(s, size.x * 2 / 3, posY + (i - boundary + 1) * textHeight, p);
                }
            }
            else {
                // draw first line
                for (int i = 0; i < boundary; i++) {
                    String s = splitString[i];
                    c.drawText(s, size.x / 3, posY + i * textHeight, p);
                }
                // draw second line
                for (int i = boundary; i < splitString.length; i++) {
                    String s = splitString[i];
                    c.drawText(s, size.x * 2 / 3, posY + (i - boundary + 1) * textHeight, p);
                }
            }
        }

        Bitmap preview = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.ARGB_8888);
        Canvas pc = new Canvas(preview);
        pc.drawBitmap(b, 0, 0, null);
        pc.drawBitmap(placeholder, 0, 0, null);

        Bitmap[] rt = {b, preview};

        return rt;
    }
}

package wordwallpaper.android.com.wordwallpaper;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    Typeface myTypeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myTypeface = Typeface.createFromAsset(getAssets(), "NotoSansCJKtc-Regular.otf");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setRippleColor(getResources().getColor(R.color.fabRipple));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText) findViewById(R.id.edit_text)).setText("Why don't you eat shit?");
            }
        });


    }
}

package com.mountains.link;

import java.util.Random;

import com.mountains.link.utils.DataHandler;
import com.mountains.link.utils.PrefClass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MoreScreen extends Activity implements OnClickListener {

    private Button bhelp, bsound, btags, buttonMoreGame, buttonShare;
    private Context context = this;
    private LinearLayout llmoretitile;
    private Random rand = new Random();

    private int[] draw = new int[]{R.drawable.pressed_roundrect_five, R.drawable.pressed_roundrect_six, R.drawable.pressed_roundrect_seven,
            R.drawable.pressed_roundrect_eight, R.drawable.pressed_roundrect_nine, R.drawable.pressed_roundrect_ten, R.drawable.pressed_roundrect_eleven,
            R.drawable.pressed_roundrect_twelve, R.drawable.pressed_roundrect_thirteen, R.drawable.pressed_roundrect_fourteen};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.morescreen);

        PrefClass p1 = new PrefClass(context);

        bhelp = (Button) findViewById(R.id.bhelp);
        bsound = (Button) findViewById(R.id.bsound);
        btags = (Button) findViewById(R.id.btags);
        buttonMoreGame = (Button) findViewById(R.id.button_more_game);
        buttonShare = (Button) findViewById(R.id.button_share);

        llmoretitile = (LinearLayout) findViewById(R.id.llmoretitle);

        bhelp.setTypeface(DataHandler.getTypeface(context));
        bsound.setTypeface(DataHandler.getTypeface(context));
        btags.setTypeface(DataHandler.getTypeface(context));
        buttonMoreGame.setTypeface(DataHandler.getTypeface(context));
        buttonShare.setTypeface(DataHandler.getTypeface(context));

        bhelp.setOnClickListener(this);
        bsound.setOnClickListener(this);
        btags.setOnClickListener(this);
        buttonMoreGame.setOnClickListener(this);
        buttonShare.setOnClickListener(this);

        setSound();
        ((TextView) findViewById(R.id.tvheading)).setTypeface(DataHandler.getTypeface(context));

        llmoretitile.setBackgroundResource(draw[rand.nextInt(10)]);

    }

    private void setSound() {
        if (PrefClass.getSound())
            bsound.setText(getResources().getString(R.string.sound) + " " + getResources().getString(R.string.on));
        else
            bsound.setText(getResources().getString(R.string.sound) + " " + getResources().getString(R.string.off));
    }

    private void toggleSound() {
        if (PrefClass.getSound())
            PrefClass.setSound(false);
        else
            PrefClass.setSound(true);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.bsound:
                toggleSound();
                setSound();
                break;
            case R.id.bhelp:
                startActivity(new Intent(context, HelpActivity.class));
                break;
            case R.id.btags:
                startActivity(new Intent(context, TagsScreen.class));
                break;
            case R.id.button_more_game:
                moreGame();
                break;
            case R.id.button_share:
                shareGame();
                break;
        }
    }

    private void moreGame() {
		try {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://dev?id=5149690841495855621")));
		} catch (android.content.ActivityNotFoundException anfe) {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=5149690841495855621")));
		}
    }

    private void shareGame() {
        String appName = getApplicationContext().getResources().getString(R.string.app_name);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, appName);
        String shareMessage = appName + "\n";
        shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n";
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        startActivity(Intent.createChooser(shareIntent, "choose one"));
    }
}

package com.levspb666.alphabet.util;

import android.content.Context;
import android.os.AsyncTask;
import android.view.ViewTreeObserver;

import com.levspb666.alphabet.Balloon;

import java.util.Random;

import static com.levspb666.alphabet.Game.canContinue;
import static com.levspb666.alphabet.Game.mBalloons;
import static com.levspb666.alphabet.Game.mContentView;
import static com.levspb666.alphabet.Settings.countBalloons;
import static com.levspb666.alphabet.Settings.speedBalloons;
import static com.levspb666.alphabet.util.GameUtil.mBalloonColors;

public class BalloonUtil {

    private static int mNextColor;
    private static int mScreenHeight;
    private static int mScreenWidth;

    public static void observer() {
        ViewTreeObserver viewTreeObserver = mContentView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mScreenHeight = mContentView.getHeight();
                    mScreenWidth = mContentView.getWidth();
                }
            });
        }
    }

    public static BalloonUtil.BalloonLauncher balloonLauncher;

    public static void startLevel(Context context) {
        balloonLauncher = new BalloonUtil.BalloonLauncher(context);
        balloonLauncher.execute(countBalloons);
    }

    public static class BalloonLauncher extends AsyncTask<Integer, Integer, Void> {

        private Context context;

        BalloonLauncher(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Integer... params) {
            if (params.length != 1) {
                throw new AssertionError(
                        "Expected 1 param for current level");
            }
            int balloonsLaunched = 0;
            while (balloonsLaunched < countBalloons) {
                if (canContinue) {
//              Get a random horizontal position for the next balloon
                    Random random = new Random();
                    int xPosition = random.nextInt(mScreenWidth - 200);
                    publishProgress(xPosition);
                    balloonsLaunched++;

//              Wait a random number of milliseconds before looping
                    int delay = ((random.nextInt(1000/speedBalloons) + 601) - speedBalloons * 60);
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    this.cancel(true);
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int xPosition = values[0];
            launchBalloon(xPosition, context);
        }

    }

    private static void launchBalloon(int x, Context context) {
        Balloon balloon = new Balloon(context, mBalloonColors[mNextColor], 150);
        mBalloons.add(balloon);
        if (mNextColor + 1 == mBalloonColors.length) {
            mNextColor = 0;
        } else {
            mNextColor++;
        }

//      Set balloon vertical position and dimensions, add to container
        balloon.setX(x);
        balloon.setY(mScreenHeight + balloon.getHeight());
        mContentView.addView(balloon);

//      Let 'er fly
        balloon.releaseBalloon(mScreenHeight, 30000 / speedBalloons - 1000);
    }
}

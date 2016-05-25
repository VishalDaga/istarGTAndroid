package visd.istarindia.com.gametry;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import visd.istarindia.com.gametry.pojo.Game;

class UserAsset {
    //ID
    private String id;
    //Name
    private String name;
    //value
    private double value;
    //init value
    private double initValue;

    public double getInitValue() {
        return initValue;
    }

    public void setInitValue(int initValue) {
        this.initValue = initValue;
    }

    //correspoding view
    private View assetView;

    public UserAsset(String id, View assetView, String name, int value, int initValue) {
        this.id = id;
        this.assetView = assetView;
        this.name = name;
        this.value = value;
        this.initValue = initValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public View getAssetView() {
        return assetView;
    }

    public void setAssetView(View assetView) {
        this.assetView = assetView;
    }
}

public class MainActivity extends AppCompatActivity {

    Gson gson;
    Game game;
    int screenWidth;
    int screenHeight;
    RelativeLayout activityLayout;
    List<UserAsset> assets = new ArrayList<UserAsset>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RelativeLayout.LayoutParams layoutParams;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.screenWidth = size.x;
        this.screenHeight = size.y;

        activityLayout = (RelativeLayout) findViewById(R.id.gamecontent);

        InputStream gameJsonString = getResources().openRawResource(R.raw.games);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(gameJsonString, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (Exception e) {

        } finally {
            try {
                gameJsonString.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String jsonString = writer.toString();

        gson = new Gson();
        JSONArray gameJsonObject = new JSONArray();

        try {
            gameJsonObject = new JSONArray(jsonString);

            game = gson.fromJson(gameJsonObject.get(0).toString(), Game.class);
            final List<Game.AssetsBean> assets = game.getAssets();

            LinearLayout assetsBar = (LinearLayout) findViewById(R.id.assetsInfoBar);
            for (Game.AssetsBean asset : assets) {
                TextView assetView = new TextView(MainActivity.this);

                int assetInitValue = Integer.parseInt(asset.getInitValue());

                this.assets.add(new UserAsset(asset.getId(), assetView, asset.getName(), assetInitValue, assetInitValue ));

                assetView.setText(asset.getName() + ": " + assetInitValue);
                assetsBar.addView(assetView);
            }
            //create custom stage views
            //get all stages
            List<Game.NodesBean> nodes = game.getNodes();

            //iterate through the list of stages and create custom views
            final ArrayList<RelativeLayout> stages = new ArrayList<RelativeLayout>(nodes.size());
            for (int i = 0; i < nodes.size(); i++) {
                RelativeLayout newStage = new RelativeLayout(MainActivity.this);
                Game.NodesBean currentStage = nodes.get(i);
                String stageType = currentStage.getStagetype();
                Bitmap stageBackgroudnImgBase64 = base64FromGameResourseData(currentStage.getBackgroundImgRes()) != null
                        ? base64FromGameResourseData(currentStage.getBackgroundImgRes())
                        : null;
                Drawable ob = new BitmapDrawable(getResources(), stageBackgroudnImgBase64);
                ImageView stagebackground = new ImageView(MainActivity.this);
                // stagebackground.setScaleType(ImageView.ScaleType.CENTER);
                stagebackground.setScaleType(ImageView.ScaleType.FIT_XY);
                stagebackground.setImageBitmap(stageBackgroudnImgBase64);
                stagebackground.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                newStage.addView(stagebackground);

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    newStage.setBackground(ob);
//                }else{
//                    newStage.setBackgroundDrawable(ob);
//                }

                switch (stageType) {
                    case "1":
                        //information only stage
                        LinearLayout infoLayout = new LinearLayout(MainActivity.this);
                        TextView title = new TextView(MainActivity.this);
                        setMentionedStyles(title, currentStage.getTitle().getString(), currentStage.getTitle().getStyles());

                        TextView infomation = new TextView(MainActivity.this);
                        setMentionedStyles2(infomation, currentStage.getStageType1data().getInformation().getString(),
                                currentStage.getStageType1data().getInformation().getStyles());

                        infoLayout.addView(title);
                        infoLayout.addView(infomation);
                        newStage.addView(infoLayout);

                        newStage.setTag(currentStage.getTitle().getString());

                        if (currentStage.getEvents().size() > 0) {
                            CompositeOnTouchListener groupListenerOnStage = new CompositeOnTouchListener();

                            newStage.setOnTouchListener(groupListenerOnStage);
                            for (final Game.NodesBean.EventsBean e : currentStage.getEvents()){
                                if (e.getAffectDescription().getNavigateTo() != null){
                                    final String nextSwipeStage = e.getAffectDescription().getNavigateTo();
                                    final RelativeLayout thisview = newStage;
                                    groupListenerOnStage.addOnTouchListener(new View.OnTouchListener() {

                                        int downX, upX;

                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {
                                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                                upX = (int) event.getX();
                                                Log.i("event.getX()", " upX " + downX);
                                                if (upX - downX > 100) {
                                                    //find the layout that needs to be replaced from the stages array
                                                    for (RelativeLayout rl : stages) {
                                                        String layoutTag = (String) rl.getTag();
                                                        if (layoutTag != null && layoutTag.equals(nextSwipeStage)) {
                                                            //remove the current view from main activity and add this view
                                                            MainActivity.this.activityLayout.removeView(thisview);
                                                            MainActivity.this.activityLayout.addView(rl);

                                                        }
                                                    }

                                                } else if (downX - upX > -100) {

                                                }
                                                return true;

                                            }
                                            return false;
                                        }
                                    });
                                }else if (e.getAffectDescription().getAssetId() != null){
                                    groupListenerOnStage.addOnTouchListener(new View.OnTouchListener() {
                                        int downX, upX;
                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {
                                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                                upX = (int) event.getX();
                                                Log.i("event.getX()", " upX " + downX);
                                                if (upX - downX > 100) {
                                                    //swipe right
                                                    //modify asset
                                                    String assetValueCalculation = (String) e.getAffectDescription().getAssetValueCalculation();
                                                    UserAsset effectedUserAsset = getAUserAsset((String)e.getAffectDescription().getAssetId(), null, null, null, null);
                                                    TextView effectedAsset = (TextView) effectedUserAsset.getAssetView();
                                                    double oldValue = effectedUserAsset.getValue();
                                                    double newComputedValue = evaluateFromCalculationExpression(assetValueCalculation, oldValue, effectedUserAsset);
                                                    //int avc = Integer.parseInt((assetValueCalculation.split("#")[2]).substring(1));
                                                    //int finalValue = oldValue + avc;

                                                    double finalValue = newComputedValue;
                                                    effectedUserAsset.setValue(finalValue);
                                                    effectedAsset.setText(((String) effectedAsset.getText()).split(":")[0] + ": " + effectedUserAsset.getValue());


                                                } else if (downX - upX > -100) {

                                                }
                                                return true;
                                            }
                                            return false;
                                        }
                                    });
                                }
                            }

                        }
                        break;
                    case "2":
                        break;
                    case "3":
                        //get all the items
                        newStage.setTag(currentStage.getTitle().getString());

                        LinearLayout infoContainer = new LinearLayout(MainActivity.this);
                        TextView stgTitle = new TextView(MainActivity.this);
                        setMentionedStyles(stgTitle, currentStage.getTitle().getString(), currentStage.getTitle().getStyles());

                        TextView stgInfoText = new TextView(MainActivity.this);
                        setMentionedStyles3(stgInfoText, currentStage.getStageTextInfo().getString(), currentStage.getStageTextInfo().getStyles());

                        newStage.addView(stgTitle);
                        infoContainer.addView(stgInfoText);
                        newStage.addView(infoContainer);
                        List<Game.NodesBean.StageType3dataBean.ItemsBean> items = currentStage.getStageType3data().getItems();
                        for (Game.NodesBean.StageType3dataBean.ItemsBean item : items) {
                            //set all the events on the item
                            ImageView itemView = new ImageView(MainActivity.this);
                                                   byte[] decodedString = Base64.decode(item.getItemimage().getDataURL().substring(22), Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            decodedByte = Bitmap.createScaledBitmap(decodedByte, Integer.parseInt(item.getItemimageW()),
                                    Integer.parseInt(item.getItemimageH()), true);
                            itemView.setImageBitmap(decodedByte);


                            String screenMatrixHb = currentStage.getStageType3data().getHb();
                            String screenMatrixVd = currentStage.getStageType3data().getVb();

                            String itemInfo = item.getItemName();
                            String itemPositionH = item.getItemPositionH();
                            String itemPositionV = item.getItemPositionV();
                            float[] itemPos = new float[2];

                            itemPos = getActualScreenPosition(screenMatrixHb, screenMatrixVd, itemPositionH, itemPositionV);
                            layoutParams = new RelativeLayout.LayoutParams(Integer.parseInt(item.getItemimageW()), Integer.parseInt(item.getItemimageH()));
                            layoutParams.setMargins((int) itemPos[0], (int) itemPos[1], 0, 0);
                            itemView.setLayoutParams(layoutParams);

                            newStage.addView(itemView);
                            if (item.getEvents() != null && item.getEvents().size() > 0) {
                                CompositeOnClickListener groupListenerOnItems = new CompositeOnClickListener();
                                itemView.setOnClickListener(groupListenerOnItems);

                                List<Game.NodesBean.StageType3dataBean.ItemsBean.EventsBean> itemEvents = item.getEvents();
                                for (final Game.NodesBean.StageType3dataBean.ItemsBean.EventsBean event : itemEvents) {

                                    if (event.getAffectDescription().getNavigateTo() == null) {
                                        groupListenerOnItems.addOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                String assetValueCalculation = (String) event.getAffectDescription().getAssetValueCalculation();
                                                UserAsset effectedUserAsset = getAUserAsset(event.getAffectDescription().getAssetId(), null, null, null, null);
                                                TextView effectedAsset = (TextView) effectedUserAsset.getAssetView();
                                                double oldValue = effectedUserAsset.getValue();
                                                double newComputedValue = evaluateFromCalculationExpression(assetValueCalculation, oldValue, effectedUserAsset);
                                                //int avc = Integer.parseInt((assetValueCalculation.split("#")[2]).substring(1));
                                                //int finalValue = oldValue + avc;

                                                double finalValue = newComputedValue;
                                                effectedUserAsset.setValue(finalValue);
                                                effectedAsset.setText(((String) effectedAsset.getText()).split(":")[0] + ": " + effectedUserAsset.getValue());

                                            }
                                        });
                                    } else {
                                        final String nextSwipeStage1 = (String) event.getAffectDescription().getNavigateTo();
                                        final RelativeLayout thisview1 = newStage;
                                        groupListenerOnItems.addOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                for (RelativeLayout rl : stages) {
                                                    String layoutTag = (String) rl.getTag();
                                                    if (layoutTag != null && layoutTag.equals(nextSwipeStage1)) {
                                                        //remove the current view from main activity and add this view
                                                        MainActivity.this.activityLayout.removeView(thisview1);
                                                        try{
                                                            MainActivity.this.activityLayout.removeView(rl);
                                                        }catch(Exception e){

                                                        }

                                                        MainActivity.this.activityLayout.addView(rl);

                                                    }
                                                }

                                            }
                                        });
                                    }

                                }

                            }
                        }
                        if (currentStage.getEvents().size() > 0) {
                            //set the swipe events
                            final String nextSwipeStage = currentStage.getEvents().get(0).getAffectDescription().getNavigateTo();
                            final RelativeLayout thisview = newStage;
                            newStage.setOnTouchListener(new View.OnTouchListener() {

                                int downX, upX;

                                @Override
                                public boolean onTouch(View v, MotionEvent event) {

                                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                        downX = (int) event.getX();
                                        Log.i("event.getX()", " downX " + downX);
                                        return true;
                                    }

                                    if (event.getAction() == MotionEvent.ACTION_UP) {
                                        upX = (int) event.getX();
                                        Log.i("event.getX()", " upX " + downX);
                                        if (upX - downX > 100) {
                                            //find the layout that needs to be replaced from the stages array
                                            for (RelativeLayout rl : stages) {
                                                String layoutTag = (String) rl.getTag();
                                                if (layoutTag != null && layoutTag.equals(nextSwipeStage)) {
                                                    //remove the current view from main activity and add this view
                                                    MainActivity.this.activityLayout.removeView(thisview);
                                                    MainActivity.this.activityLayout.addView(rl);

                                                }
                                            }

                                        } else if (downX - upX > -100) {

                                            // swipe left
                                        }
                                        return true;

                                    }
                                    return false;
                                }
                            });
                        }
                        break;
                }

                stages.add(newStage);
            }

            activityLayout.addView(stages.get(0));

//            String screenMatrixHb = game.getNodes().get(0).getData().getHb();
//            String screenMatrixVd = game.getNodes().get(0).getData().getVb();
//
//            //show the assets
//            List<Game.AssetsBean> assets = game.getAssets();
//            for (int i = 0; i < assets.size(); i++) {
//                Game.AssetsBean a = assets.get(i);
//                layoutParams=new RelativeLayout.LayoutParams(200,100);
//                layoutParams.setMargins(this.screenWidth-((i+1)*150), 10, 0 , 0);
//
//                TextView asset1Tv = new TextView(MainActivity.this);
//                asset1Tv.setText("0");
//                asset1Tv.setTag(a.getId());
//                asset1Tv.setLayoutParams(layoutParams);
//                relativeLayout.addView(asset1Tv);
//            }
//
//            List <Game.NodesBean.DataBean.ItemsBean> items = game.getNodes().get(0).getData().getItems();
//            for (int i = 0; i < items.size(); i++) {
//                Game.NodesBean.DataBean.ItemsBean item = items.get(i);
//                String itemInfo = item.getItemName();
//                String itemPositionH = item.getItemPositionH();
//                String itemPositionV = item.getItemPositionV();
//                float[] itemPos = new float[2];
//                itemPos = getActualScreenPosition(screenMatrixHb, screenMatrixVd, itemPositionH, itemPositionV);
//                TextView textView = new TextView(MainActivity.this);
//                textView.setText(itemInfo);
//                //iwat=nt to make is postion abosoulte and top some left something
//                layoutParams=new RelativeLayout.LayoutParams(800,100);
//                layoutParams.setMargins((int) itemPos[0], (int) itemPos[1], 0 , 0);
//                textView.setLayoutParams(layoutParams);
//
//                relativeLayout.addView(textView);
//
//                for (int j =0; j < item.getEvents().size(); j++){
//                    Game.NodesBean.DataBean.ItemsBean.EventsBean event = item.getEvents().get(j);
//                    final String affectAsset = event.getAffectDescription().getAssetId();
//                    final int valueChange = event.getAffectDescription().getValue();
//                    textView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            TextView tv = (TextView) relativeLayout.findViewWithTag(affectAsset);
//                            int currentValue = Integer.parseInt((String) tv.getText());
//                            int newValue = currentValue + valueChange;
//                            tv.setText(""+newValue);
//
//
//                        }
//                    });
//                }
//
//            }


//            Game.NodesBean.DataBean.ItemsBean items = game.getNodes().get(1).getData().getItems();
//            for (int i = 0; i < items.length; i++){
//                String itemInfo = items[i].getItemName();
//                String itemPositionH = items[i].getItemPositionH();
//                String itemPositionV = items[i].getItemPositionV();
//                float[] itemPos = new float[2];
//                itemPos = getActualScreenPosition(screenMatrixHb, screenMatrixVd, itemPositionH, itemPositionV);
//                TextView textView = new TextView(MainActivity.this);
//                textView.setText(itemInfo);
//                //iwat=nt to make is postion abosoulte and top some left something
//                layoutParams=new RelativeLayout.LayoutParams(800,100);
//                layoutParams.setMargins((int) itemPos[0], (int) itemPos[1], 0 , 0);
//                textView.setLayoutParams(layoutParams);
//
//                relativeLayout.addView(textView);
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private UserAsset getAUserAsset(String id, View assetView, String name, Integer value, Integer initValue) {

        if (id != null){
            for (UserAsset asset: MainActivity.this.assets){
                if (asset.getId().equals(id)){
                   return asset;
                }
            }
        }

        if (assetView != null){

        }

        if (name != null){

        }

        if (value != null){

        }

        if (initValue != null){

        }
        return null;
    }

    private final List<String> keywords = Arrays.asList("currentValue");
    private double evaluateFromCalculationExpression(String assetValueCalculation, double oldValue, UserAsset effectedAsset) {

        //get one after one literal and replace it with it value
        //after the whole expression is traversed evaluate the expresssion and return the result

        int startIndex = 0, endIndex  = 0;
        startIndex = assetValueCalculation.indexOf("`");
        endIndex = assetValueCalculation.indexOf("`", startIndex+1);
        while (startIndex >= 0 && endIndex > startIndex){
            String var = assetValueCalculation.substring(startIndex,endIndex+1);

            double varVal = getValueOfVar(var, effectedAsset);

            assetValueCalculation = assetValueCalculation.replace(var, ""+varVal);

            startIndex = assetValueCalculation.indexOf("`");
            endIndex = assetValueCalculation.indexOf("`", startIndex+1);
        }

        //  HashMap<String, TextView> assets = new HashMap<String, TextView>();

        return eval(assetValueCalculation);


    }

    private double getValueOfVar(String var, UserAsset effectedAsset) {
        if (var.charAt(0) == '`'){
            var = var.substring(1);
        }
        if (var.charAt(var.length()-1) == '`'){
            var = var.substring(0, var.length()-1);
        }
        for (String keyword: keywords){
            if (keyword.equals(var)){
                switch (keyword){
                    case "currentValue":
                        return effectedAsset.getValue();
                }
            }
        }

        for (UserAsset a : MainActivity.this.assets){
            if (a.getName().equals(var)){
                return a.getValue();
            }
        }
        return 0;
    }


    private static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }

    private void setMentionedStyles(TextView title, String text, Game.NodesBean.TitleBean.StylesBean styles) {
        title.setText(text);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        int leftMargin = styles.getFontpositionH() != null ? Integer.parseInt(styles.getFontpositionH()) : 0;
        int topMargin = styles.getFontpositionV() != null ? Integer.parseInt(styles.getFontpositionV()) : 0;
        lp.setMargins(leftMargin, topMargin, 0, 0);

        int fontSize = styles.getFontsize() != null ? Integer.parseInt(styles.getFontsize()) : 20; //default 20
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);

        int color = styles.getFontcolor() != null ? Color.parseColor(styles.getFontcolor()) : 0;
        title.setTextColor(color);
        title.setLayoutParams(lp);
    }

    private void setMentionedStyles2(TextView title, String text, Game.NodesBean.StageType1dataBean.InformationBean.StylesBean styles) {
        title.setText(text);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        int leftMargin = styles.getFontpositionH() != null ? Integer.parseInt(styles.getFontpositionH()) : 0;
        int topMargin = styles.getFontpositionV() != null ? Integer.parseInt(styles.getFontpositionV()) : 0;
        lp.setMargins(leftMargin, topMargin, 0, 0);

        int fontSize = styles.getFontsize() != null ? Integer.parseInt(styles.getFontsize()) : 20; //default 20
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);

        int color = styles.getFontcolor() != null ? Color.parseColor(styles.getFontcolor()) : 0;
        title.setTextColor(color);
        title.setLayoutParams(lp);
    }

    private void setMentionedStyles3(TextView title, String text, Game.NodesBean.StageTextInfoBean.StylesBean styles) {
        title.setText(text);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        int leftMargin = styles.getFontpositionH() != null ? Integer.parseInt(styles.getFontpositionH()) : 0;
        int topMargin = styles.getFontpositionV() != null ? Integer.parseInt(styles.getFontpositionV()) : 0;
        lp.setMargins(leftMargin, topMargin, 0, 0);

        int fontSize = styles.getFontsize() != null ? Integer.parseInt(styles.getFontsize()) : 20; //default 20
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);

        int color = styles.getFontcolor() != null ? Color.parseColor(styles.getFontcolor()) : 0;
        title.setTextColor(color);
        title.setLayoutParams(lp);
    }

    private float[] getActualScreenPosition(String scMatH, String scMatV, String itemPH, String itemPV) {

        float acLeft = (Float.parseFloat(itemPH) / Float.parseFloat(scMatH)) * this.screenWidth;
        float acTop = (Float.parseFloat(itemPV) / Float.parseFloat(scMatV)) * this.screenHeight;

        float[] aa = new float[2];
        aa[0] = acLeft;
        aa[1] = acTop;
        return aa;

    }

//    private void setAsset(int asset1Value, int asset2Value){
//        MainActivity.this.asset1 = asset1Value;
//        MainActivity.this.asset2 = asset2Value;
//        TextView asset1View = (TextView)findViewById(R.id.asset1);
//        asset1View.setText(""+asset1Value);
//        TextView asset2View = (TextView)findViewById(R.id.asset2);
//        asset2View.setText(""+asset2Value);
//
//    }

    private Bitmap base64FromGameResourseData(String id) {
        List<Game.VisdImgResourcesBean> gameVisdImgResources = game.getVisdImgResources();
        if (gameVisdImgResources != null){
            for (Game.VisdImgResourcesBean imgres : gameVisdImgResources) {
                if (imgres.getId().equals(id)) {
                    byte[] decodedString = Base64.decode(imgres.getEnc().substring(22), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    return decodedByte;
                }
            }
        }



        return null;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://visd.istarindia.com.gametry/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://visd.istarindia.com.gametry/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}

class CompositeOnClickListener implements View.OnClickListener{
    List<View.OnClickListener> listeners;

    public CompositeOnClickListener(){
        listeners = new ArrayList<View.OnClickListener>();
    }

    public void addOnClickListener(View.OnClickListener listener){
        listeners.add(listener);
    }

    @Override
    public void onClick(View v){
        for(View.OnClickListener listener : listeners){
            listener.onClick(v);
        }
    }
}

class CompositeOnTouchListener implements View.OnTouchListener{
    List<View.OnTouchListener> listeners;

    public CompositeOnTouchListener(){
        listeners = new ArrayList<View.OnTouchListener>();
    }

    public void addOnTouchListener(View.OnTouchListener listener){
        listeners.add(listener);
    }

    int downX, upX;

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        for(View.OnTouchListener listener : listeners){
            listener.onTouch(v, event);
        }
        return true;
    }
}



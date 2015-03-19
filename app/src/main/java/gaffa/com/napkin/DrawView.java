package gaffa.com.napkin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by clazell on 17/03/2015.
 */
public class DrawView extends View {
    private Paint paintBlack = new Paint();
    private Paint paintRed = new Paint();
    private Paint paintBlue = new Paint();
    private Paint currentPaint = paintBlack;
    private List listOPaths = new ArrayList<Line>();
    ArrayList<Pair<Path, Paint>> paths = new ArrayList<Pair<Path, Paint>>();
    private Bitmap im;
    private Canvas mCanvas;
    int currentPath = 0;
    Bitmap mutableBitmap;

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        im = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.resize);

        mutableBitmap = im.copy(Bitmap.Config.ARGB_8888, true);
        mCanvas = new Canvas(mutableBitmap);
        setDrawingCacheEnabled(true);
        saveLine();
        paintBlack.setAntiAlias(true);
        paintBlack.setStrokeWidth(5f);
        paintBlack.setColor(Color.BLACK);
        paintBlack.setStyle(Paint.Style.STROKE);
        paintBlack.setStrokeJoin(Paint.Join.ROUND);

        paintRed.setAntiAlias(true);
        paintRed.setStrokeWidth(5f);
        paintRed.setColor(Color.parseColor("#CC0000"));
        paintRed.setStyle(Paint.Style.STROKE);
        paintRed.setStrokeJoin(Paint.Join.ROUND);

        paintBlue.setAntiAlias(true);
        paintBlue.setStrokeWidth(5f);
        paintBlue.setColor(Color.parseColor("#0000CC"));
        paintBlue.setStyle(Paint.Style.STROKE);
        paintBlue.setStrokeJoin(Paint.Join.ROUND);
    }

    public void reset(){

        paths.clear();
        currentPath=0;
        listOPaths.clear();
        currentPaint = paintBlack;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (Pair<Path, Paint> p : paths) {
            canvas.drawPath(p.first, p.second);
            mCanvas.drawPath(p.first, p.second);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.i("PAINT",currentPaint.toString());
        saveLine();
        Path path = getCurrentPath(currentPath).getPath();

        float eventX = event.getX();
        float eventY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(eventX, eventY);
                return true;

            case MotionEvent.ACTION_MOVE:
                path.lineTo(eventX, eventY);
                break;

            case MotionEvent.ACTION_UP:
                Paint newPaint = new Paint(currentPaint);
                paths.add(new Pair<Path, Paint>(path, newPaint));
                invalidate();

                break;

            default:
                return false;
        }

        invalidate();
        return true;
    }

    private void saveLine(){
        Path path = new Path();
        Line line = new Line(currentPaint,path);

        listOPaths.add(line);

    }

    private Line getCurrentPath(int pathNo){
        return (Line)listOPaths.get(pathNo);
    }

    public void changeColorToBlack(){
        currentPaint = paintBlack;
        Log.i("LINE PAINT","BLACK");
        saveLine();
        invalidate();
        currentPath++;
    }
    public void changeColorToRed(){
        currentPaint = paintRed;
        Log.i("LINE PAINT","RED");
        saveLine();
        invalidate();
        currentPath++;
    }
    public void changeColorToBlue(){
        currentPaint = paintBlue;
        Log.i("LINE PAINT","BLUE");
        saveLine();
        invalidate();
        currentPath++;
    }

    public File save(){
        this.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache();
        Bitmap bmp = Bitmap.createBitmap(mutableBitmap);
        this.setDrawingCacheEnabled(false);

        Bitmap well = bmp;
        Bitmap save = Bitmap.createBitmap(well.getWidth(), well.getHeight(), Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setColor(Color.TRANSPARENT);
        Canvas now = new Canvas(save);
        now.drawRect(new Rect(0,0,well.getWidth(),well.getHeight()), paint);
        now.drawBitmap(bmp, new Rect(0,0,well.getWidth(),well.getHeight()), new Rect(0,0,well.getWidth(),well.getHeight()), null);

        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        File file = new File(path+"/"+ts+"image.png");
        FileOutputStream ostream;
        try {
            file.createNewFile();
            ostream = new FileOutputStream(file);
            save.compress(Bitmap.CompressFormat.PNG, 100, ostream);
            ostream.flush();
            ostream.close();
            Log.i("PATH ",""+path);
           // Toast.makeText(getContext(), "image saved @ " + path, Toast.LENGTH_LONG).show();
            //reset();
            //invalidate();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            //Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
        }
        return null;
    }



}

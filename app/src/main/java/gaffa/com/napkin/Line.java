package gaffa.com.napkin;

import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by clazell on 18/03/2015.
 */
public class Line {
    private Paint paint;
    private Path path;
    public Line(Paint nPaint, Path nPath){
        this.paint = nPaint;
        this.path = nPath;
    }

    public Path getPath(){
        return path;
    }

    public Paint getPaint(){
        return paint;
    }
}

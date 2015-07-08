package com.example.storm.compass;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by StorM on 29.01.2015.
 */
public class CompassView extends View {
    public CompassView(Context context) {
        super(context);
        initCompassView();
    }

    public CompassView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCompassView();
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCompassView();
    }


    private float bearing;

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
        Activity a = (Activity)this.getContext();
        TextView tv = (TextView)a.findViewById(R.id.bearingIndicator);

        tv.setText(Float.toString(bearing));
        this.invalidate();


    }


    private Paint markerPaint;
    private Paint textPaint;
    private Paint circlePaint;
    private String northString;
    private String eastString;
    private String westString;
    private String southString;
    private int textHeight;



    protected void initCompassView(){
        setFocusable(true);
        Resources r = getResources();
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(r.getColor(R.color.background_color));
        circlePaint.setStrokeWidth(1);
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        northString = r.getString(R.string.cardinal_north);
        eastString = r.getString(R.string.cardinal_east);
        westString = r.getString(R.string.cardinal_west);
        southString = r.getString(R.string.cardinal_south);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(r.getColor(R.color.text_color));

        textHeight = (int)textPaint.measureText("Yy");

        markerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        markerPaint.setColor(r.getColor(R.color.marker_color));

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = measure(widthMeasureSpec);
        int measuredHeight = measure(heightMeasureSpec);

        int d = Math.min(measuredHeight,measuredWidth);
        setMeasuredDimension(d, d);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        int mMeasuredHeight = getMeasuredHeight();
        int mMeasuredWidth =  getMeasuredWidth();

        int py = mMeasuredHeight/2;
        int px = mMeasuredWidth/2;

        int radius = Math.min(px,py);
        canvas.drawCircle(px,py,radius,circlePaint);

        canvas.save();
        canvas.rotate(-bearing,px,py);

        for(int i = 0; i<24; i++){
            canvas.drawLine(px,py-radius,px,py-radius+10,markerPaint);
            canvas.save();
            canvas.translate(0,textHeight);

            if(i % 6 == 0){
                String outDirection = "";
                switch(i){
                    case 0: outDirection = northString;
                        int arrowY = 2*textHeight;
                        canvas.drawLine(px,arrowY,px-5,3*textHeight,markerPaint);
                        canvas.drawLine(px,arrowY,px+5,3*textHeight,markerPaint);

                        break;
                    case 6: outDirection = eastString;
                        break;
                    case 12: outDirection = southString;
                        break;
                    case 18: outDirection = westString;
                }
                canvas.drawText(outDirection,px - textHeight/2,py-radius+textHeight,textPaint);
            }
            else if (i % 3 == 0){
                String angle = String.valueOf(i*15);
                float angleTextWidth = textPaint.measureText(angle);
                float angleTextX = px - angleTextWidth/2;
                float angleTextY = py - radius + textHeight;
                canvas.drawText(angle,angleTextX,angleTextY,textPaint);
            }
            canvas.restore();
            canvas.rotate(15,px,py);


        }
        canvas.restore();




        //super.onDraw(canvas);
    }

    private int measure(int measureSpec){
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if(specMode == MeasureSpec.UNSPECIFIED) {
            result = 200;
        }
        else{
            result = specSize;
        }

        return result;


    }

}

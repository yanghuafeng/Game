package com.example.game;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by YHF at 15:22 on 2019-09-23.
 */

public class GameView extends View {

    private final int CELL_WIDTH = 120;
    private final int MAX_LEVEL = 6;
    private final int MAX_MISSION = 3;
    private final int END = 9;
    private final int CELL_MARGIN = 1;
    private Paint paint = new Paint();
    private Bitmap cell;
    private Bitmap cellFalse;
    private int panelWidth;//view的大小
    private int level = 3;//开始等级
    private int mission = 1;
    private ArrayList<Point> cellsArray = new ArrayList<>();
    private ArrayList<Point> pointsArray = new ArrayList<>();
    private Point pointFalse = new Point();
    private Runnable runnable;
    private Random random = new Random();

    private boolean isGameOver;//游戏是否结束
    private boolean isShow;//true:显示题目  false:显示已选
    private boolean wait = true;//等待
    private OnChangeListener listener = null;


    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        paint.setColor(getResources().getColor(R.color.colorBlack));
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);

        runnable = new Runnable() {
            @Override
            public void run() {
                SoundPoolManager.getInstance().playNext();
                mission++;
                if(level<MAX_LEVEL && mission>MAX_MISSION){
                    mission = 1;
                    level++;
                }
                if(mission==END){
                    showDialog(R.string.end);
                    return;
                }
                wait = false;
                isShow = true;
                clear();
                setCellsArray();
                invalidate();
            }
        };
        int cellWidth = CELL_WIDTH- CELL_MARGIN -1;
        cell = BitmapFactory.decodeResource(getResources(), R.mipmap.icon1);
        cellFalse = BitmapFactory.decodeResource(getResources(), R.mipmap.false_cell);
        cell = Bitmap.createScaledBitmap(cell,cellWidth,cellWidth,false);
        cellFalse = Bitmap.createScaledBitmap(cellFalse,cellWidth,cellWidth,false);
    }

    private void clear(){
        cellsArray.clear();
        pointsArray.clear();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize,heightSize);

        if (widthMode == MeasureSpec.UNSPECIFIED)
            width = heightSize;
        else if (heightMode == MeasureSpec.UNSPECIFIED)
            width = widthSize;

        setMeasuredDimension(width,width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        panelWidth = w;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isGameOver) {
            showDialog(R.string.account);
            return false;
        }
        if(wait){
            return false;
        }
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP){
            int x = (int) event.getX();
            int y = (int) event.getY();
            if(!parsePointAvailable(x,y)){
                return false;
            }
            isShow = false;
            Point p = getPoint(x,y);

            if (cellsArray.contains(p)){
                if(pointsArray.contains(p)){
                    return false;
                }
                SoundPoolManager.getInstance().playRight();
                pointsArray.add(p);
            }else{
                SoundPoolManager.getInstance().playFalse();
                isGameOver = true;
                pointFalse = p;
            }
            if(pointsArray.size()== cellsArray.size()){
                wait = true;
                getHandler().postDelayed(runnable,800);
            }
            invalidate();
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制格子
        drawBoard(canvas);
        if(isGameOver){
            isShow = true;
            drawFalse(canvas);
        }
        if(isShow) {
            drawCells(canvas);
        }else{
            drawPoints(canvas);
        }

        if(listener!=null){
            listener.refresh();
        }
    }

    private boolean parsePointAvailable(int x, int y){
        float mStart = getCellStartPlace();
        if(x<mStart||x>mStart+level*CELL_WIDTH||y<mStart||y>mStart+level*CELL_WIDTH) return false;
        return true;
    }

    private Point getPoint(int x, int y) {
        int mStart = (int)getCellStartPlace();
        int a = (x-mStart)/CELL_WIDTH;
        x = mStart+a*CELL_WIDTH;
        a = (y-mStart)/ CELL_WIDTH;
        y = mStart+a*CELL_WIDTH;
        return new Point(x, y);
    }


    private void showDialog(int stringId){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.game_over));
        builder.setMessage(getResources().getString(stringId,level,mission));
        builder.setCancelable(false);
        builder.setPositiveButton(getResources().getString(R.string.play_again), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startGame(false);
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    public void startGame(boolean jump){
        mission = 1;
        level = 3;
        if(jump)level = 5;
        isGameOver = false;
        isShow = true;
        wait = false;
        clear();
        setCellsArray();
        invalidate();
    }

    private void setCellsArray(){
        int mStart = (int)getCellStartPlace();
        int range = CELL_WIDTH*level;
        int randomPointx ;
        int randomPointy ;
        int num = mission+level;
        for(int i=0;i<num;i++){
            randomPointx = random.nextInt(range)+mStart;
            randomPointy = random.nextInt(range)+mStart;
            if(cellsArray.contains(getPoint(randomPointx,randomPointy))){
                i--;
                continue;
            }
            cellsArray.add(getPoint(randomPointx,randomPointy));
        }
    }

    private void drawPoints(Canvas canvas) {
        for (int i =0; i < pointsArray.size(); i++){
            Point point = pointsArray.get(i);
            canvas.drawBitmap(cell,point.x + CELL_MARGIN,point.y + CELL_MARGIN,null);
        }
    }

    private void drawFalse(Canvas canvas) {
        canvas.drawBitmap(cellFalse,pointFalse.x + CELL_MARGIN,pointFalse.y + CELL_MARGIN,null);
    }

    private void drawCells(Canvas canvas) {
        for (int i = 0; i < cellsArray.size(); i++){
            Point point = cellsArray.get(i);
            canvas.drawBitmap(cell,point.x + CELL_MARGIN,point.y + CELL_MARGIN,null);
        }
    }

    private void drawBoard(Canvas canvas) {
        float mStart = getCellStartPlace();
        float mEnd = getCellEndPlace();
        for (int i = 0; i < level+1; i++){
            float start = mStart+i*CELL_WIDTH;
            canvas.drawLine(start,mStart,start,mEnd,paint);
            canvas.drawLine(mStart,start,mEnd,start,paint);
        }
    }

    private float getCellStartPlace(){
        return (float) panelWidth/2 - ((float)level/2)*CELL_WIDTH;
    }

    private float getCellEndPlace(){
        return (float)panelWidth/2 + ((float)level/2)*CELL_WIDTH;
    }

    public void setOnChangeListener(OnChangeListener onChangeListener){
        listener = onChangeListener;
    }

    public void cheat(){
        isShow = true;
        invalidate();
    }

    public void normal(){
        isShow = false;
        invalidate();
    }

    public int getLevel(){
        return level;
    }
    public int getMission(){
        return mission;
    }

    public int getDoneNum(){
        return pointsArray.size();
    }

    public int getNeedNum(){
        return cellsArray.size();
    }

    public interface OnChangeListener {
        void refresh();
    }
}

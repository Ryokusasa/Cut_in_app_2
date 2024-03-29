package com.ryokusasa.w3033901.cut_in_app_2.AppDataManager;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.ryokusasa.w3033901.cut_in_app_2.Animation.KeyFrame;
import com.ryokusasa.w3033901.cut_in_app_2.Animation.KeyFrameAnimation;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;

/**
 * アニメーションオブジェクト
 * ImageView TextViewいずれかを所持する
 * アニメーション情報も持つ
 */

public class AnimObj implements Cloneable {

    //キーフレームアニメーション
    private KeyFrameAnimation.MoveKeyFrameAnimation moveKeyFrameAnimation;
    private KeyFrameAnimation.RotateKeyFrameAnimation rotateKeyFrameAnimation;
    private KeyFrameAnimation.ScaleKeyFrameAnimation scaleKeyFrameAnimation;

    Context context;

    //オブジェクトタイプ
    public enum Type{
        Image,
        Text
    }
    private Type type;

    //オブジェクト
    private ImageView imageView;
    private TextView textView;
    private float imageRatio;
    private int frameNum;

    //TODO 保存用

    //初期設定
    private int initWidth, initHeight;
    private float initTextSize;


    //画像オブジェクト作成
    public AnimObj(Context context, Drawable drawable, int x, int y, int width, int height){
        this.context = context;

        //ImageView作成
        ImageView imageView  = new ImageView(context);
        imageView.setImageDrawable(drawable);

        //widthまたはheightが-1の場合画像のサイズ
        if (width != -1) {
            imageView.setLayoutParams(new ViewGroup.LayoutParams(width, height));
            initWidth = width;
            initHeight = height;
        }else{
            initWidth = drawable.getIntrinsicWidth();
            initHeight = drawable.getIntrinsicHeight();
        }

        this.imageView = imageView;
        this.type = Type.Image;

        //画像比率保存
        imageRatio = (float)initWidth / initHeight;

        frameNum = 1000;

        //初期化
        moveKeyFrameAnimation = new KeyFrameAnimation.MoveKeyFrameAnimation(frameNum);
        rotateKeyFrameAnimation = new KeyFrameAnimation.RotateKeyFrameAnimation(frameNum);
        scaleKeyFrameAnimation = new KeyFrameAnimation.ScaleKeyFrameAnimation(frameNum);
        initMove(x, y);
        initRotate(0);
        initScale(1, 1);
        makeAnimation();
    }

    public AnimObj(Context context, Drawable drawable, int width, int height){
        this(context, drawable, 0, 0, width, height);
    }

    //テキストオブジェクト作成
    public AnimObj(Context context, String text, float size){
        this(context, text, 0, 0, size);
    }

    public AnimObj(Context context, String text, int x, int y, float size){
        this.context = context;

        //TextView作成
        TextView textView = new TextView(context);
        if(size != -1) textView.setTextSize(size);
        textView.setText(text);

        initTextSize = size;

        this.textView = textView;
        this.type = Type.Text;

        //初期化
        initMove(x, y);
        initRotate(0);
        initScale(1, 1);
        makeAnimation();
    }

    /* アニメーション作成関連 */
    public void initMove(double x, double y){
        moveKeyFrameAnimation.addKeyFrame(new KeyFrame.MoveKeyFrame(0, x, y, new LinearInterpolator()));
    }

    public void initRotate(double radian){
        rotateKeyFrameAnimation.addKeyFrame(new KeyFrame.RotateKeyFrame(0, radian, new LinearInterpolator()));
    }

    public void initScale(double scaleX, double scaleY){
        scaleKeyFrameAnimation.addKeyFrame(new KeyFrame.ScaleKeyFrame(0, scaleX, scaleY, new LinearInterpolator()));
    }

    public Type getType() {
        return type;
    }

    //画像セット
    public void setImage(Bitmap bitmap){
        imageView.setImageBitmap(bitmap);
        //比率測定
        imageRatio = (float)bitmap.getWidth() / bitmap.getHeight();
        initWidth = (int)(imageRatio * initHeight);
    }

    public void setImageRatio(float imageRatio) {
        this.imageRatio = imageRatio;
    }

    public float getImageRatio() {
        return imageRatio;
    }

    //クローン
    public AnimObj clone(){
        try {
            AnimObj animObj = (AnimObj) super.clone();

            if(type == Type.Image) {
                animObj.imageView = new ImageView(context);
                animObj.imageView.setLayoutParams(imageView.getLayoutParams());
                animObj.imageView.setImageDrawable(imageView.getDrawable());
                animObj.imageView.setTranslationX(imageView.getTranslationX());
                animObj.imageView.setTranslationY(imageView.getTranslationY());
                animObj.imageView.setAlpha(imageView.getAlpha());
                animObj.imageView.setScaleX(imageView.getScaleX());
                animObj.imageView.setScaleY(imageView.getScaleY());
                animObj.imageView.setRotation(imageView.getRotation());
            }else if(type == Type.Text) {
                animObj.textView = new TextView(context);
                animObj.textView.setText(textView.getText());
                animObj.textView.setTextSize(textView.getTextSize() / (textView.getText().length()-1));
                animObj.textView.setTranslationX(textView.getTranslationX());
                animObj.textView.setTranslationY(textView.getTranslationY());
                animObj.textView.setAlpha(textView.getAlpha());
                animObj.textView.setScaleX(textView.getScaleX());
                animObj.textView.setScaleY(textView.getScaleY());
                animObj.textView.setRotation(textView.getRotation());
            }

            animObj.moveKeyFrameAnimation = this.moveKeyFrameAnimation.clone();
            animObj.rotateKeyFrameAnimation = this.rotateKeyFrameAnimation.clone();
            animObj.scaleKeyFrameAnimation = this.scaleKeyFrameAnimation.clone();

            return animObj;
        }catch (CloneNotSupportedException e){
            e.printStackTrace();
            return null;
        }
    }

    /* アニメーション編集関連の関数 */
    public void addMove(int frame, double x, double y, TimeInterpolator interpolator){
        moveKeyFrameAnimation.addKeyFrame(new KeyFrame.MoveKeyFrame(frame, x, y, interpolator));
        makeAnimation();
    }

    public void addRotate(int frame, double radian, TimeInterpolator interpolator){
        rotateKeyFrameAnimation.addKeyFrame(new KeyFrame.RotateKeyFrame(frame, radian, interpolator));
        makeAnimation();
    }

    public void addScale(int frame, double scaleX, double scaleY, TimeInterpolator interpolator){
        scaleKeyFrameAnimation.addKeyFrame(new KeyFrame.ScaleKeyFrame(frame, scaleX, scaleY, interpolator));
        makeAnimation();
    }

    //アニメーション作成
    public void makeAnimation(){
        moveKeyFrameAnimation.makeKeyFrameAnimation();
        rotateKeyFrameAnimation.makeKeyFrameAnimation();
        scaleKeyFrameAnimation.makeKeyFrameAnimation();
    }

    //次のフレームへ
    public void nextFrame(){
        //TODO Viewを次のフレームに移動
    }

    //指定フレームへ
    public void setFrame(int frame){
        moveKeyFrameAnimation.setFrame(frame);
        rotateKeyFrameAnimation.setFrame(frame);
        scaleKeyFrameAnimation.setFrame(frame);
    }

    //そのフレームを再生
    public void playFrame(){
        if(type == Type.Image){
            imageView.setTranslationX((float)((KeyFrame.MoveKeyFrame)moveKeyFrameAnimation.nextFrame()).getX());
            imageView.setTranslationY((float)((KeyFrame.MoveKeyFrame)moveKeyFrameAnimation.nextFrame()).getY());
            imageView.setRotation((float) ((KeyFrame.RotateKeyFrame)rotateKeyFrameAnimation.nextFrame()).getRadian());
            imageView.setScaleX((float) ((KeyFrame.ScaleKeyFrame)scaleKeyFrameAnimation.nextFrame()).getScaleX());
            imageView.setScaleY((float) ((KeyFrame.ScaleKeyFrame)scaleKeyFrameAnimation.nextFrame()).getScaleY());
        }else if (type == Type.Text){
            textView.setTranslationX((float)((KeyFrame.MoveKeyFrame)moveKeyFrameAnimation.nextFrame()).getX());
            textView.setTranslationY((float)((KeyFrame.MoveKeyFrame)moveKeyFrameAnimation.nextFrame()).getY());
            textView.setRotation((float) ((KeyFrame.RotateKeyFrame)rotateKeyFrameAnimation.nextFrame()).getRadian());
            textView.setScaleX((float) ((KeyFrame.ScaleKeyFrame)scaleKeyFrameAnimation.nextFrame()).getScaleX());
            textView.setScaleY((float) ((KeyFrame.ScaleKeyFrame)scaleKeyFrameAnimation.nextFrame()).getScaleY());
        }
    }

    public double getInitX(){
        return ((KeyFrame.MoveKeyFrame)moveKeyFrameAnimation.playFrame(0)).getX();
    }

    public double getInitY(){
        return ((KeyFrame.MoveKeyFrame)moveKeyFrameAnimation.playFrame(0)).getY();
    }

    public void setInitHeight(int initHeight) {
        this.initHeight = initHeight;
        imageView.setLayoutParams(new ConstraintLayout.LayoutParams(initWidth, this.initHeight));
    }

    public void setInitWidth(int initWidth) {
        this.initWidth = initWidth;
        imageView.setLayoutParams(new ConstraintLayout.LayoutParams(this.initWidth, initHeight));
    }

    public int getInitHeight() {
        return initHeight;
    }

    public int getInitWidth() {
        return initWidth;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public TextView getTextView() {
        return textView;
    }

    public float getInitTextSize() {
        return initTextSize;
    }

    public View getObjView(){
        if(type == Type.Image){
            return imageView;
        }else{
            return textView;
        }
    }

}
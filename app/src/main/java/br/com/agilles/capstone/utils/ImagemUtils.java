package br.com.agilles.capstone.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.Uri;

import com.squareup.picasso.Transformation;

import java.io.File;
import java.io.IOException;

public class ImagemUtils implements Transformation {


    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap,
                BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);

        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circle";
    }


    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    public Uri carregaFoto(String caminhoFoto) {
        try {
            int codigoOrientacao = pegaCodigoOrientacao(caminhoFoto);
            switch (codigoOrientacao) {
                case ExifInterface.ORIENTATION_NORMAL:
                    //rotaciona 0 graus no sentido horário
                    abreFotoERotaciona(caminhoFoto, 0);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    // rotaciona 90 graus no sentido horário
                    abreFotoERotaciona(caminhoFoto, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    // rotaciona 180 graus no sentido horário
                    abreFotoERotaciona(caminhoFoto, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    // rotaciona 270 graus no sentido horário
                    abreFotoERotaciona(caminhoFoto, 270);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return converteEmUri(caminhoFoto);


    }

    private Uri converteEmUri(String caminhoFoto) {
        File arquivo = new File(caminhoFoto);
        return Uri.fromFile(arquivo);
    }

    private static int pegaCodigoOrientacao(String caminhoFoto) throws IOException {
        ExifInterface exif = new ExifInterface(caminhoFoto.toString());
        String orientacao = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        return Integer.parseInt(orientacao);
    }

    private Bitmap abreFotoERotaciona(String caminhoFoto, int angulo) {
        Bitmap bitmap = BitmapFactory.decodeFile(caminhoFoto.toString());
        Matrix matrix = new Matrix();
        matrix.postRotate(angulo);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


}

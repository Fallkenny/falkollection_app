package falk.falkollection.util;

import android.os.Environment;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public  class CameraFileHandler {
    public static File createFileForCameraPhoto() throws IOException {
        String timeStamp = new
                SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File pasta = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imagem = new File(pasta.getPath() + File.separator
                + "JPG_" + timeStamp + ".jpg");
        return imagem;
    }
}

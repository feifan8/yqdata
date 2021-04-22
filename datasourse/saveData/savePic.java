package getData.saveData;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public abstract class savePic {
    //下载图片
    public void downloadNet(String str,int i) throws MalformedURLException {
        int bytesum = 0;
        int byteread = 0;
        str="http://"+str;
        URL url = new URL(str);
        String name = Integer.toString(i);
        try {
            URLConnection conn = url.openConnection();
            InputStream inStream = conn.getInputStream();
            //使用时填写输出路径
            FileOutputStream fs = new FileOutputStream("输出路径"+name+".png");

            byte[] buffer = new byte[1204];
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread;
                fs.write(buffer, 0, byteread);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

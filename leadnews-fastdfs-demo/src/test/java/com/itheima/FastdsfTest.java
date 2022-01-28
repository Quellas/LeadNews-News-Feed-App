package com.itheima;

import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadCallback;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

import java.io.*;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/11 10:52
 * @description 标题
 * @package com.itheima
 */
@SpringBootTest
public class FastdsfTest {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;


    //要上传文件
    @Test
    public void uploadFile() throws  Exception{
        File file = new File("C:\\Users\\admin\\Pictures\\toutiao\\wKjTiGBD7ziADWIUAADzJTJibuA37.jpeg");
        InputStream inputstream=new FileInputStream(file);
        long fileszie = file.length();
        //得到后缀 而且不带“.”
        String extName = StringUtils.getFilenameExtension(file.getName());


        //参数1 指定文件本身（输入流对象）
        //参数2 指定文件（图片）本身的大小
        //参数3 指定文件的扩展名 比如 图片 后缀：jpg png gif  不要带 "."
        //参数4 元数据（图片本身例如：拍摄日期 作者，地点，图片的大小，像素 ...高度，）
        StorePath storePath = fastFileStorageClient.uploadFile(inputstream, fileszie, extName, null);

        System.out.println(storePath.getGroup());
        System.out.println(storePath.getPath());
        System.out.println(storePath.getFullPath());

        /**
         *
         * group1
         * M00/00/00/wKjTiGDq0F6ADamhAADzJTJibuA14.jpeg
         * group1/M00/00/00/wKjTiGDq0F6ADamhAADzJTJibuA14.jpeg
         */


    }
    //下载
    @Test
    public void downloadFile() throws  Exception{
        String groupName = "group1";
        String path="M00/00/00/wKjTiGDq0F6ADamhAADzJTJibuA14.jpeg";
        //文件本身就是字节数组
       byte[] bytes =  fastFileStorageClient.downloadFile(groupName, path, new DownloadCallback<byte[]>() {

           //参数就是从fastdfs获取到的文件输入流
           @Override
           public byte[] recv(InputStream ins) throws IOException {
               //apache的IOutils
               return IOUtils.toByteArray(ins);
           }
       });

       //获取到字节数组 写入到磁盘

        FileOutputStream outputStream = new FileOutputStream(new File("E:\\111\\1234567.jpg"));

        outputStream.write(bytes);

        //关闭流 finally关闭
        outputStream.close();

    }



    @Test
    public void deleteFile(){
        String groupName = "group1";

        StorePath storePath = StorePath.parseFromUrl("http://192.168.211.136/group1/M00/00/00/akksfjalf.jpg");

        String group = storePath.getGroup();
        String path1 = storePath.getPath();

        String path="M00/00/00/wKjTiGDq0F6ADamhAADzJTJibuA14.jpeg";
        fastFileStorageClient.deleteFile(groupName,path);
    }
}

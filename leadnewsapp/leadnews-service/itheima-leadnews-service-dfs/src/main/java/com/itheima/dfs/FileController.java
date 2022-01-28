package com.itheima.dfs;

import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadCallback;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.itheima.common.pojo.Result;
import com.itheima.common.util.GreenImageScan;
import com.itheima.common.util.GreenTextScan;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ljh
 * @version 1.0
 * @date 2021/7/11 11:50
 * @description 标题
 * @package com.itheima.dfs
 */
@RestController
@RequestMapping("/dfs")
public class FileController {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private FdfsWebServer fdfsWebServer;

    @Autowired
    private GreenImageScan greenImageScan;

    @Autowired
    private GreenTextScan greenTextScan;

    /**
     *
     * @param file  文件名 前端input type="file" name="file"
     * @return 返回图片的路径 map来封装
     */
    @PostMapping("/upload")
    public Result<Map<String,String>> upload(MultipartFile file) throws Exception{
        //1.获取 上传文件的流对象,文件的大小，获取扩展名 不要带点

        InputStream inputStream = file.getInputStream();

        long fileszie = file.getSize();



        String extName = StringUtils.getFilenameExtension(file.getOriginalFilename());

        //2.上传图片到fdfs上
        //参数1 指定文件本身（输入流对象）
        //参数2 指定文件（图片）本身的大小
        //参数3 指定文件的扩展名 比如 图片 后缀：jpg png gif  不要带 "."
        //参数4 元数据（图片本身例如：拍摄日期 作者，地点，图片的大小，像素 ...高度，）
        StorePath storePath = fastFileStorageClient.uploadFile(inputStream, fileszie, extName, null);

        // http://192.168.211.136/group1/M00/00/00/wKjTiGDq0F6ADamhAADzJTJibuA14.jpeg



        //==========================测试图片审核start=========================这个代码将来要删除掉
        List<byte[]> list=  new ArrayList<>();
        list.add(file.getBytes());//图片本身
        Map map1 = greenImageScan.imageScan(list);
        System.out.println("map1的值："+map1);


        List<String> listtext= new ArrayList<>();
        listtext.add("海洛因一块一斤");
        Map map2 = greenTextScan.greeTextScan(listtext);
        System.out.println("map2的值："+map2);
        //==========================测试图片审核end=========================


        //3.获取路径 拼接路径 返回 前端

        // group1/M00/00/00/wKjTiGDq0F6ADamhAADzJTJibuA14.jpeg
        String fullPath = storePath.getFullPath();
        String realPath = fdfsWebServer.getWebServerUrl()+fullPath;
        Map<String,String> map = new HashMap<>();
        map.put("url",realPath);
        return Result.ok(map);
    }

    /**
     * 根据图片的地址列表 查询出图片的字节数组列表
     * @param imagesList ["http://192.168.211.136/group1/M00/00/00/1234.jpg"]
     * @return
     */
    @PostMapping("/downLoad")
    public List<byte[]> downLoadFile(@RequestBody List<String> imagesList){
        List<byte[]> bytesList = new ArrayList<>();

        //  imagePath   === http://192.168.211.136/group1/M00/00/00/1234.jpg
        for (String imagePath : imagesList) {

            //解析url
            StorePath storePath = StorePath.parseFromUrl(imagePath);
            //获取group
            String groupName =storePath.getGroup();
            //获取path
            String path=storePath.getPath();
            //文件本身就是字节数组

            try {
                byte[] bytes = fastFileStorageClient.downloadFile(groupName, path, new DownloadCallback<byte[]>() {

                    //参数就是从fastdfs获取到的文件输入流
                    @Override
                    public byte[] recv(InputStream ins) throws IOException {
                        //apache的IOutils
                        return IOUtils.toByteArray(ins);
                    }
                });
                bytesList.add(bytes);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        return bytesList;


    }

}

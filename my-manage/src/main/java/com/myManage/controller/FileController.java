package com.myManage.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.myManage.common.CheckFileResult;
import com.myManage.common.FileResult;
import com.myManage.common.UpLoadConstant;
import com.utils.RedisUtil;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: menfeng
 * @Date: 2020/11/23 17:01
 * @Version 1.0
 */
@Api(tags = "文件上传")
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private AppendFileStorageClient appendFileStorageClient;

    @Value("${fdfs.tracker-list}")
    private String fdfsPath;
    /**
     * 检查文件是否存在
     * 初始化锁
     *
     * @param fileMd5
     * @param fileName
     * @param fileSize
     * @param userName
     * @return
     */
    @ApiOperation(httpMethod = "POST", value = "检查文件锁是否占用(上传文件前调用)")
    @ApiImplicitParams({
            @ApiImplicitParam(name="fileMd5",value="前端生成的Md5",paramType="query",required = true),
            @ApiImplicitParam(name="fileName",value="文件名称",paramType="query",required = true),
            @ApiImplicitParam(name="fileSize",value="文件总大小",paramType="query",required = true),
            @ApiImplicitParam(name="userName",value="用户名",paramType="query",required = true),
            @ApiImplicitParam(name="token",value="令牌",paramType="query",required = true),

    })
    @ApiResponses({
            @ApiResponse(code = 401, message = "token过期"),
            @ApiResponse(code = 200, message = "文件上传成功"),
    })
    @PostMapping("/checkFile")
    public Map<String, Object> chunkFile(@RequestParam("fileMd5") String fileMd5,//前端生成的Md5
                                         @RequestParam("fileName") String fileName,//文件名称
                                         @RequestParam("fileSize") String fileSize, //文件总大小
                                         @RequestParam("userName") String userName//用户名
    ) {
        Map<String, Object> data = new HashMap<>();
        if (StrUtil.isEmpty(userName)) {
            data.put("code", 500);
            data.put("message", "用户为空");
            return data;
        }
        if (StrUtil.isEmpty(fileMd5)) {
            data.put("code", 501);
            data.put("message", "fileMd5为空");
            return data;
        }
        CheckFileResult checkFileResult = new CheckFileResult();
        //模拟从redis中查询文件表的md5
        List<String> fileList = RedisUtil.getListAll(UpLoadConstant.completedList);
        if (CollUtil.isNotEmpty(fileList)) {
            for (String e : fileList) {
                JSONObject obj = JSONUtil.parseObj(e);
                if (obj.get("md5").equals(fileMd5)) {
                    checkFileResult.setTotalSize(obj.getLong("lenght"));
                    checkFileResult.setViewPath(obj.getStr("url"));
                    data.put("code", 200);
                    data.put("message", "文件上传成功");
                    data.put("data", checkFileResult);
                    return data;
                }
            }
        }
        //查询锁占用
        String lockName = UpLoadConstant.currLocks + fileMd5;
        Long lock = RedisUtil.incrBy(lockName, 1);
        String lockOwner = UpLoadConstant.lockOwner + fileMd5;
        String chunkCurrkey = UpLoadConstant.chunkCurr + fileMd5;
        if (lock > 1) {
            checkFileResult.setLock(1);
            //检查是否为锁的拥有者,如果是放行
            String oWner = RedisUtil.getString(lockOwner);
            if (StrUtil.isEmpty(oWner)) {
                data.put("code", 502);
                data.put("message", "无法获取文件锁拥有者");
                return data;
            } else {
//                if (oWner.equals(request.getSession().getAttribute("name"))){
                if (oWner.equals(userName)) {
                    String chunkCurr = RedisUtil.getString(chunkCurrkey);
                    if (StrUtil.isEmpty(chunkCurr)) {
                        data.put("code", 503);
                        data.put("message", "无法获取当前文件chunkCurr");
                        return data;
                    }
                    checkFileResult.setChunkCurr(Convert.toInt(chunkCurr));
                    data.put("code", 200);
                    data.put("message", "");
                    data.put("data", checkFileResult);
                    return data;
                } else {
                    data.put("code", 504);
                    data.put("message", "当前文件已有人在上传,您暂无法上传该文件");
                    return data;
                }
            }
        } else {
            //初始化锁.分块
            RedisUtil.setString(lockOwner, userName);
            RedisUtil.setString(chunkCurrkey, "1"); //第一块索引是0,与前端保持一致
            checkFileResult.setChunkCurr(1);
            data.put("code", 200);
            data.put("message", "");
            data.put("data", checkFileResult);
            return data;
        }
    }

    /**
     * 上传
     *
     * @param identifier
     * @param chunkNumber
     * @param totalChunks
     * @param chunkSize
     * @param filename
     * @param totalSize
     * @param file
     * @param userName
     * @return
     */
    @ApiOperation(httpMethod = "POST", value = "文件分片上传")
    @ApiImplicitParams({
            @ApiImplicitParam(name="identifier",value="文件md5",paramType="query",required = true),
            @ApiImplicitParam(name="chunkNumber",value="当前第几块",paramType="query",dataType = "Int",required = true),
            @ApiImplicitParam(name="totalChunks",value="总块数",paramType="query",dataType = "Int",required = true),
            @ApiImplicitParam(name="chunkSize",value="当前块大小",paramType="query",required = true),
            @ApiImplicitParam(name="filename",value="文件名称",paramType="query",required = true),
            @ApiImplicitParam(name="totalSize",value="文件总大小",paramType="query",dataType = "Int",required = true),
            //@ApiImplicitParam(name="file",value="文件",paramType="query",required = true),
            @ApiImplicitParam(name="userName",value="用户名",paramType="query",required = true),
            @ApiImplicitParam(name="token",value="令牌",paramType="query",required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 401, message = "token过期"),
            @ApiResponse(code = 200, message = "文件上传成功"),
    })
    @PostMapping("/upload_do")
    public Map<String, Object> upload_do(@RequestParam("identifier") String identifier, //文件md5
                                         @RequestParam("chunkNumber") Integer chunkNumber, //当前第几块
                                         @RequestParam("totalChunks") Integer totalChunks, //总块数
                                         @RequestParam("chunkSize") String chunkSize, //当前块大小
                                         @RequestParam("filename") String filename, //文件名称
                                         @RequestParam("totalSize") Long totalSize, //文件总大小
                                         @RequestParam("file") MultipartFile file, //文件
                                         @RequestParam("userName") String userName //用户名
    ) {
        //String userName = "admin";
        Map<String, Object> data = new HashMap<>();
        String noGroupPath;
        String chunklockName = UpLoadConstant.chunkLock + identifier;
        String temOwner = RandomUtil.randomUUID();
        //chunkNumber = chunkNumber - 1;
        boolean currOwner = false;//真正的拥有者
        try {
            if (StrUtil.isEmpty(userName)) {
                data.put("code", 500);
                data.put("message", "用户名为空");
                return data;
            }
            if (chunkNumber == null) {
                chunkNumber = 1;
            }
            if (totalChunks == null) {
                totalChunks = 1;
            }
            Long lock = RedisUtil.incrBy(chunklockName, 1);
            if (lock > 1) {
                data.put("code", 501);
                data.put("message", "请求块锁失败");
                return data;
            }
            //写入锁的当前拥有者
            currOwner = true;
            BufferedOutputStream stream = null;
            String chunkCurrkey = UpLoadConstant.chunkCurr + identifier; //redis中记录当前应该穿第几块(从0开始)
            String chunkCurr = RedisUtil.getString(chunkCurrkey);
            noGroupPath = "";
            //Integer chunkSize=Convert.toInt("chunkSize");
            if (StrUtil.isEmpty(chunkCurr)) {
                data.put("code", 502);
                data.put("message", "无法获取当前文件chunkCurr");
                return data;
            }
            Integer chunkCurr_int = Convert.toInt(chunkCurr);
            if (chunkNumber < chunkCurr_int) {
                data.put("code", 503);
                data.put("message", "当前文件块已上传");
                return data;
            } else if (chunkNumber > chunkCurr_int) {
                data.put("code", 504);
                data.put("message", "当前文件块需要等待上传,稍后请重试");
                return data;
            }
            StorePath path = null;
            if (!file.isEmpty()) {
                try {
                    Long historyUpload = 0L;
                    String historyUploadStr = RedisUtil.getString(UpLoadConstant.historyUpload + identifier);
                    if (StrUtil.isNotEmpty(historyUploadStr)) {
                        historyUpload = Convert.toLong(historyUploadStr);
                    }
                    log.info("historyUpload大小:" + historyUpload);
                    if (chunkNumber == 1) {
                        RedisUtil.setString(chunkCurrkey, Convert.toStr(chunkCurr_int + 1));
                        log.debug(chunkNumber + ":redis块+1");
                        try {
                            path = appendFileStorageClient.uploadAppenderFile(UpLoadConstant.DEFAULT_GROUP, file.getInputStream(), file.getSize(), FileUtil.extName(filename));
                            log.debug(chunkNumber + ":更新完fastdfs");
                            if (path == null) {
                                RedisUtil.setString(chunkCurrkey, Convert.toStr(chunkCurr_int));
                                data.put("code", 505);
                                data.put("message", "获取远程文件路径出错");
                                return data;
                            }
                        } catch (Exception e) {
                            RedisUtil.setString(chunkCurrkey, Convert.toStr(chunkCurr_int));
                            // e.printStackTrace();
                            //还原历史块
                            log.error("初次上传远程文件出错", e);
                            data.put("code", 506);
                            data.put("message", "上传远程服务器文件出错");
                            return data;
                        }
                        noGroupPath = path.getPath();
                        RedisUtil.setString(UpLoadConstant.fastDfsPath + identifier, path.getPath());
                        log.debug("上传文件 result={}", path);
                    } else {
                        RedisUtil.setString(chunkCurrkey, Convert.toStr(chunkCurr_int + 1));
                        log.debug(chunkNumber + ":redis块+1");
                        noGroupPath = RedisUtil.getString(UpLoadConstant.fastDfsPath + identifier);
                        if (noGroupPath == null) {
                            data.put("code", 507);
                            data.put("message", "无法获取上传远程服务器文件出错");
                            return data;
                        }
                        try {
                            //追加方式实际实用如果中途出错多次,可能会出现重复追加情况,这里改成修改模式,即时多次传来重复文件块,依然可以保证文件拼接正确
                            appendFileStorageClient.modifyFile(UpLoadConstant.DEFAULT_GROUP, noGroupPath, file.getInputStream(), file.getSize(), historyUpload);
                            log.debug(chunkNumber + ":更新完fastdfs");
                        } catch (Exception e) {
                            RedisUtil.setString(chunkCurrkey, Convert.toStr(chunkCurr_int));
                            log.error("更新远程文件出错", e);
                            //   e.printStackTrace();
                            //  throw  new RuntimeException("初次上传远程文件出错");
                            data.put("code", 508);
                            data.put("message", "更新远程文件出错");
                            return data;
                        }
                    }
                    //修改历史上传大小
                    historyUpload = historyUpload + file.getSize();
                    RedisUtil.setString(UpLoadConstant.historyUpload + identifier, Convert.toStr(historyUpload));
                    //最后一块,清空upload,写入数据库
                    if (chunkNumber == totalChunks) {
                        //持久化上传完成文件,也可以存储在mysql中
                        FileResult fileResult = new FileResult();
                        fileResult.setMd5(identifier);
                        fileResult.setName(filename);
                        fileResult.setLenght(totalSize);
                        fileResult.setUrl(UpLoadConstant.DEFAULT_GROUP + "/" + noGroupPath);
                        RedisUtil.rpush(UpLoadConstant.completedList, JSONUtil.toJsonStr(fileResult));
                        RedisUtil.delKeys(new String[]{UpLoadConstant.chunkCurr + identifier, UpLoadConstant.fastDfsPath + identifier, UpLoadConstant.currLocks + identifier, UpLoadConstant.lockOwner + identifier});
                    }
                } catch (Exception e) {
                    log.error("上传文件错误", e);
                    //e.printStackTrace();
                    data.put("code", 509);
                    data.put("message", "上传错误 " + e.getMessage());
                    return data;
                }
            }
        } finally {
            //锁的当前拥有者才能释放块上传锁
            if (currOwner) {
                RedisUtil.setString(chunklockName, "0");
            }
        }
        data.put("code", 200);
        data.put("message", "上传成功");
        data.put("path", UpLoadConstant.DEFAULT_GROUP + "/" + noGroupPath);
        data.put("name", filename);
        return data;
    }

}

package com.dg.split.service;

import com.dg.common.dto.*;
import com.dg.common.enums.FileSuffixType;
import com.dg.split.dao.StoreInfoDao;
import com.dg.split.feign.IStoreService;
import com.dg.split.pojo.StoreInfoEntity;
import com.dg.split.service.SplitService;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import okhttp3.*;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;


@Service
public class SplitServiceImpl implements SplitService {

    /**
     * 设置每个分片的最大行数
     */
    private static final int MAX_ROWS_PER_CHUNK = 50;

    private static final OkHttpClient client = new OkHttpClient();

//    private FastFileStorageClient fastFileStorageClient;

    @Value("${gateway.sign}")
    private String sign;

    @Resource
    private StoreInfoDao storeInfoDao;

    @Autowired
    private IStoreService storeService;

    /**
     * 随机存储文件到可信的服务器中
     *
     * @param file
     * @param fileExtName
     * @return java.lang.String
     * @author Xbin
     * @date 2023-08-02 01:34
     */
    @Override
    public String UploadFastDfs(File file, String fileExtName) {
//        StorePath storePath = null;
//        try {
//            FastFile fastFile;
////            storePath = fastFileStorageClient.uploadFile(new FileInputStream(file),
////                    file.length(), fileExtName, null);
//        } catch (IOException e) {
//           e.printStackTrace();
//        }
//        return storePath.getFullPath();
        return "";
    }

    public static String calculateFileHash(InputStream inputStream, String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] buffer = new byte[8192];
            int read;
            while ((read = inputStream.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            return new String(Hex.encodeHex(digest.digest()));
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }


    // TODO: 2024/4/11 调用其他服务。

    @Override
    public CommonResult<StoreInfoDTO> split(FileSplitRequestDTO request) {

        String dataNo = UUID.randomUUID().toString();

        File originalFile = request.getFile();
        // 获取文件大小（字节）
        long originalFileSize = originalFile.length();

        FileSuffixType fileSuffixTypeByCode = FileSuffixType.getFileTypeByCode(request.getFileType());
        FileSplitInfoDTO fileSplitInfoDTO = new FileSplitInfoDTO();
        fileSplitInfoDTO.setFile(request.getFile());
        fileSplitInfoDTO.setFileSize(originalFileSize);
        fileSplitInfoDTO.setFileSuffix(request.getFileSuffix());
        fileSplitInfoDTO.setMaskColumn(request.getDesenColumn());
//        未启用
        fileSplitInfoDTO.setStoreIndex(1);
        fileSplitInfoDTO.setStoreNo(dataNo);
        fileSplitInfoDTO.setMaskLevel(request.getDesenLevel());
        fileSplitInfoDTO.setFileType(request.getFileType());
        FileSplitResultDTO fileSplitResultDTO = null;
        switch (fileSuffixTypeByCode) {
            case XLSX:
//                表格处理
                fileSplitResultDTO= storeService.processExcelDataBlock(fileSplitInfoDTO);
                break;
            case XLS:
//                表格处理
//                这里返回结果
                fileSplitResultDTO = storeService.processExcelDataBlock(fileSplitInfoDTO);
                break;
            case UNKNOWN:
//                未知文件处理
                fileSplitResultDTO = storeService.processUnKnownData(fileSplitInfoDTO);
                break;
            default:
                fileSplitResultDTO= storeService.processUnKnownData(fileSplitInfoDTO);
                break;
        }

        String chunkInfoNo = UUID.randomUUID().toString();

        StoreInfoEntity storeInfoEntity = new StoreInfoEntity();

        storeInfoEntity.setDataNo(dataNo);

        String originalFilename = originalFile.getName();

        if (StringUtils.isBlank(originalFilename)) {
            return CommonResult.failed("file is empty");
        }
        String[] fileNameArr = originalFilename.split("\\.");
        // 文件后缀
        String suffix = fileNameArr[fileNameArr.length - 1];
        String path = UploadFastDfs(originalFile, suffix);
        storeInfoEntity.setStoreUrl(path); // 文件存储路径
        String hashAlgorithm = "SHA-256";
        String fileHash = null;
        try {
            // 注意：计算哈希值后，需要重置inputStream的读取，因为计算哈希会消耗掉流的数据
            fileHash = calculateFileHash(new FileInputStream(originalFile), hashAlgorithm);
            // 重置流
//            originalFile.getInputStream().reset();

            // 假设有方法来签名文件，这里直接使用哈希值作为示例
//             fileSignature = signFile(fileHash);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 设置当前时间为提交时间
        storeInfoEntity.setSubmitTime(request.getSubmitTime());
        // 设置文件名称
        storeInfoEntity.setStoreName(request.getFileName());
        // 设置文件类型
        storeInfoEntity.setStoreType(request.getFileType());
        // 假设文件大小为 1024k
        storeInfoEntity.setStoreSize(new BigDecimal(originalFileSize));
        // 设置文件内容的哈希
        storeInfoEntity.setStoreHash(fileHash);
        // 设置文件签名
        storeInfoEntity.setStoreSign(sign);
        // 文件标题
        storeInfoEntity.setStoreTitle(request.getFileTitle());
        // 文件摘要
        storeInfoEntity.setStoreAbs(request.getFileAbs());
        // 拆分方法 Column Split
        storeInfoEntity.setSplitMethod("CS");
        // 脱敏意图
        storeInfoEntity.setMaskIntention(String.join(",",request.getDesenIntention()));
        // 脱敏要求
        storeInfoEntity.setMaskRequirements(String.join(",",request.getDesenRequirements()));
        // 脱敏控制集
        storeInfoEntity.setMaskControlSet(request.getDesenControlSet());
        // 脱敏级别
        List<String> stringList = new ArrayList<>();
        if (!Objects.isNull(request.getDesenLevel())){
            for (Integer num : request.getDesenLevel()) {
                stringList.add(String.valueOf(num));
            }
        }

        storeInfoEntity.setMaskLevel(String.join(",",stringList));
        // 存取方名字
        storeInfoEntity.setPerformerName(request.getPerformerName());
        // 存取方编号
        storeInfoEntity.setPerformerCode(null);
        // 假设拆分结果
        if (Objects.isNull(fileSplitResultDTO)){
            storeInfoEntity.setIsSuccess(false);
            // 假设分片数量为5
            storeInfoEntity.setChunkInfoCount(fileSplitResultDTO.getChunkNum());
        }else{

            if (Objects.isNull(fileSplitResultDTO.getStatus())){
                storeInfoEntity.setIsSuccess(false);
            }else{
                storeInfoEntity.setIsSuccess(fileSplitResultDTO.getStatus());
            }

            if (Objects.isNull(fileSplitResultDTO.getChunkNum())){
                storeInfoEntity.setChunkInfoCount(0);
            }else{
                storeInfoEntity.setChunkInfoCount(fileSplitResultDTO.getChunkNum());
            }
        }
        storeInfoEntity.setSubmitTime(request.getSubmitTime());
        storeInfoEntity.setPerformerCode(request.getPerformerCode());
        storeInfoDao.insert(storeInfoEntity);
//        转换
        StoreInfoDTO storeInfoDTO = new StoreInfoDTO();
        storeInfoDTO.setId(storeInfoEntity.getId());
        storeInfoDTO.setDataNo(storeInfoEntity.getDataNo());
        storeInfoDTO.setSubmitTime(storeInfoEntity.getSubmitTime());
        storeInfoDTO.setChunkInfoCount(storeInfoEntity.getChunkInfoCount());
        storeInfoDTO.setStoreName(storeInfoEntity.getStoreName());
        storeInfoDTO.setStoreType(storeInfoEntity.getStoreType());
        storeInfoDTO.setStoreSize(storeInfoEntity.getStoreSize());
        storeInfoDTO.setStoreUrl(storeInfoEntity.getStoreUrl());
        storeInfoDTO.setStoreHash(storeInfoEntity.getStoreHash());
        storeInfoDTO.setStoreTitle(storeInfoEntity.getStoreTitle());
        storeInfoDTO.setStoreAbs(storeInfoEntity.getStoreAbs());
        storeInfoDTO.setStoreSign(storeInfoEntity.getStoreSign());
        storeInfoDTO.setSplitMethod(storeInfoEntity.getSplitMethod());
        storeInfoDTO.setMaskIntention(storeInfoEntity.getMaskIntention());
        storeInfoDTO.setMaskRequirements(storeInfoEntity.getMaskRequirements());
        storeInfoDTO.setMaskControlSet(storeInfoEntity.getMaskControlSet());
        storeInfoDTO.setMaskLevel(storeInfoEntity.getMaskLevel());
        storeInfoDTO.setPerformerName(storeInfoEntity.getPerformerName());
        storeInfoDTO.setPerformerCode(storeInfoEntity.getPerformerCode());
        storeInfoDTO.setIsSuccess(storeInfoEntity.getIsSuccess());
        storeInfoDTO.setCreateTime(storeInfoEntity.getCreateTime());
        storeInfoDTO.setUpdateTime(storeInfoEntity.getUpdateTime());

        return CommonResult.success(storeInfoDTO);
    }

}



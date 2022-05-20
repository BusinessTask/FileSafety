package com.snut.sm2AndSm4.controller;

import com.snut.sm2AndSm4.service.SmService;
import com.snut.sm2AndSm4.utils.customReturn.BaseResult;
import com.snut.sm2AndSm4.utils.customReturn.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/sm")
public class SmController {

    @Autowired
    private SmService smService;

    /**
     * 生成公私钥
     */
    @RequestMapping(value = "/createSecretKey")
    public BaseResult<Map<String, Object>> createSecretKey() {

        try {
            Map<String, Object> secretKey = smService.createSecretKey();
            return ResultUtil.success(secretKey);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("错误");
        }

    }

    /**
     * 利用sm4加密文件
     */
    @RequestMapping(value = "/sm4", method = RequestMethod.POST)
    public BaseResult<Map<String, Object>> sm4(@RequestParam Map<String, Object> map) {

        try {
            return ResultUtil.success(smService.sm4(map));
        } catch (RuntimeException r) {
            r.printStackTrace();
            return ResultUtil.error(r.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("错误");
        }
    }
    /**
     * 利用sm4解密
     */
    @RequestMapping(value = "/sm4decrypt", method = RequestMethod.POST)
    public BaseResult<Map<String, Object>> sm4decrypt(@RequestParam Map<String, Object> map) {

        try {
            return ResultUtil.success(smService.sm4decrypt(map));
        } catch (RuntimeException r) {
            r.printStackTrace();
            return ResultUtil.error(r.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("错误");
        }
    }

}
package com.snut.sm2AndSm4.controller;

import com.snut.sm2AndSm4.service.SmService;
import com.snut.sm2AndSm4.utils.customReturn.BaseResult;
import com.snut.sm2AndSm4.utils.customReturn.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/sm")
public class SmController {

    @Autowired
    private SmService smService;

    /**
     * 利用sm2String对称加密(有密钥的情况下是解密)
     */
    @RequestMapping(value="/sm2StringSymmetry", method= RequestMethod.POST)
    public BaseResult<Map<String, Object>> sm2StringSymmetry(@RequestBody Map<String, Object> map) throws Exception{
    	// 实际工作中， 请求是request类，  有什么字段传什么字段，   返回是DTO类   需要什么就返回什么
        return ResultUtil.success(smService.sm2StringSymmetry(map));
    }

    /**
     * 利用sm2String非对称加密(有密钥的情况下是解密)
     */
    @RequestMapping(value="/sm2StringAsymmetric", method= RequestMethod.POST)
    public BaseResult<Map<String, Object>> sm2StringAsymmetric(@RequestBody Map<String, Object> map) throws Exception{
        return ResultUtil.success(smService.sm2StringAsymmetric(map));
    }

    /**
     * 利用sm4StringECB加密(有密钥的情况下是解密)
     */
    @RequestMapping(value="/sm4StringECB", method= RequestMethod.POST)
    public BaseResult<Map<String, Object>> sm4StringECB(@RequestBody Map<String, Object> map) throws Exception{
        return ResultUtil.success(smService.sm4StringECB(map));
    }

    /**
     * 利用sm4StringCBC加密(有密钥的情况下是解密)
     */
    @RequestMapping(value="/sm4StringCBC", method= RequestMethod.POST)
    public BaseResult<Map<String, Object>> sm4StringCBC(@RequestBody Map<String, Object> map) throws Exception{
        return ResultUtil.success(smService.sm4StringCBC(map));
    }

    /**
     * 利用sm4加密文件(有密钥的情况下是解密)
     */
    @RequestMapping(value="/sm4", method= RequestMethod.POST)
    public BaseResult<Map<String, Object>> sm4(@RequestBody Map<String, Object> map) throws Exception{
        return ResultUtil.success(smService.sm4(map));
    }

}
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@include file="/comm/mytags.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>国密加解密</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <link rel="stylesheet" href="${ctx}/sm2AndSm4/static/layui/css/layui.css" media="all">
    <link rel="stylesheet" href="${ctx}/sm2AndSm4/static/layuiadmin/style/admin.css" media="all">
    <link rel="stylesheet" href="${ctx}/sm2AndSm4/static/layuiadmin/layui/css/layui.css" media="all">
    <link rel="stylesheet" href="${ctx}/sm2AndSm4/static/layuiadmin/style/admin.css" media="all">
    <script src="${ctx}/sm2AndSm4/static/layuiadmin/layui/layui.js"></script>
</head>
<body>

<div class="layui-field-box">
    <div class="layui-tab-content">
        <form class="layui-form" action="">
            <fieldset class="layui-elem-field layui-field-title" style="margin-top: 30px;">
                <legend>第一步：生成密钥</legend>
            </fieldset>

            <a class="layui-btn" id="createSecretKey"><i class="layui-icon larry-icon larry-chaxun7"></i>生 成 密 钥</a>

            <div class="layui-form-item layui-form-text">
                <label class="layui-form-label">SM2公钥：</label>
                <div class="layui-input-block">
                    <textarea name="PublicKey" id="PublicKey" disabled class="layui-textarea"></textarea>
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">SM2私钥：</label>
                <div class="layui-input-block">
                    <input type="text" name="PrivateKey" disabled id="PrivateKey" autocomplete="off" class="layui-input">
                </div>
            </div>

            <div class="layui-form-item">
                <label class="layui-form-label">SM4明文：</label>
                <div class="layui-input-block">
                    <input type="text" name="sm4ClearKey" disabled id="sm4ClearKey" autocomplete="off" class="layui-input">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">SM4秘文：</label>
                <div class="layui-input-block">
                    <textarea name="sm4SecretKey" id="sm4SecretKey" disabled class="layui-textarea"></textarea>
                    <p style="color: #e60000">* SM4秘文是通过SM2公钥保护</p>
                </div>

            </div>

        </form>

        <form class="layui-form" action="">
            <fieldset class="layui-elem-field layui-field-title" style="margin-top: 30px;">
                <legend>第二步：加密/解密文件</legend>
            </fieldset>

            <div class="layui-form-item">
                <label class="layui-form-label">文件地址：</label>
                <div class="layui-input-block">
                    <input type="text" name="oldFile" id="oldFile" required lay-verify="required"
                           placeholder="请输入待加密文件地址" autocomplete="off" class="layui-input">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">输出地址：</label>
                <div class="layui-input-block">
                    <input type="text" name="newFile" id="newFile" disabled
                           placeholder="加密成功之后，回显输出文件地址" autocomplete="off" class="layui-input">
                </div>
            </div>

            <a class="layui-btn layui-btn-normal" id="sm4">文 件 加 密</a>
            <a class="layui-btn layui-btn-danger" id="sm4decrypt">文 件 解 密</a>
        </form>

    </div>
</div>

<script src="${ctx}/sm2AndSm4/static/layuiadmin/layui/layui.js"></script>
<script type="text/javascript" src="${ctx}/sm2AndSm4/static/js/jquery.min.js"></script>

<script>
    layui.config({
        base: '${ctx}/sm2AndSm4/static/layuiadmin/' //静态资源所在路径
    }).extend({
        index: 'lib/index' //主入口模块
    }).use(['index', 'user'], function () {
        var $ = layui.$
            , setter = layui.setter
            , admin = layui.admin
            , form = layui.form
            , router = layui.router()
            , upload = layui.upload
            , search = router.search;


        /**点击验证码重新生成*/
        $('#createSecretKey').on('click', function () {
            alert("生成公私钥");

            //登陆验证
            $.ajax({
                url: '${ctx}/sm2AndSm4/sm/createSecretKey',
                type: 'get',
                async: false,
                success: function (data) {
                    if (data.code == 200) {

                        $('#PrivateKey').val(data.data.privateKey);
                        $('#PublicKey').text(data.data.publicKey);
                        $('#sm4ClearKey').val(data.data.sm4ClearKey);
                        $('#sm4SecretKey').text(data.data.sm4SecretKey);

                    } else {
                        layer.alert("生成失败！");
                        return false;
                    }
                }
            });

        });

        /**点击验证码重新生成*/
        $('#sm4').on('click', function () {

            var oldFile = $('#oldFile').val();

            var PublicKey = $('#PublicKey').val();
            var PrivateKey = $('#PrivateKey').val();

            var sm4ClearKey = $('#sm4ClearKey').val();
            var sm4SecretKey = $('#sm4SecretKey').val();

            if (!oldFile) {
                layer.alert("待加密文件地址不能为空～");
                return false;
            }

            if (!PublicKey || !PrivateKey || !sm4ClearKey || !sm4SecretKey) {
                layer.alert("请先生成公私钥～");
                return false;
            }

            //登陆验证
            $.ajax({
                url: '${ctx}/sm2AndSm4/sm/sm4',
                type: 'post',
                async: false,
                data: {"oldFile": oldFile},
                success: function (data) {
                    if (data.code == 200) {
                        layer.alert("加密成功！");
                        $('#newFile').val(data.data.newFile);
                    } else {
                        layer.alert(data.data.msg);
                        return false;
                    }
                }
            });

        });

        /**点击验证码重新生成*/
        $('#sm4decrypt').on('click', function () {

            var oldFile = $('#oldFile').val();
            var PublicKey = $('#PublicKey').val();
            var PrivateKey = $('#PrivateKey').val();
            var sm4ClearKey = $('#sm4ClearKey').val();
            var sm4SecretKey = $('#sm4SecretKey').val();
            if (!oldFile) {
                layer.alert("解密文件地址不能为空～");
                return false;
            }
            if (!PublicKey || !PrivateKey || !sm4ClearKey || !sm4SecretKey) {
                layer.alert("请先生成公私钥～");
                return false;
            }
            $.ajax({
                url: '${ctx}/sm2AndSm4/sm/sm4decrypt',
                type: 'post',
                async: false,
                data: {"oldFile": oldFile},
                success: function (data) {
                    if (data.code == 200) {
                        layer.alert("解密密成功！");
                        $('#newFile').val(data.data.newFile);
                    } else {
                        layer.alert(data.data.msg);
                        return false;
                    }
                }
            });

        });


    });

</script>
</body>
</html>
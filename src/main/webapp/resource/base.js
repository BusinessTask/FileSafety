function cs() {
    var phone = document.getElementsByName("comment111")[0].value;
    var pcode = document.getElementsByName("comment111")[1].value;

    if (phone == "xujiaqi") {
        if (pcode == "123456") {
            location.href = '/sm2AndSm4/front/index.html#/';
        }
    } else {
        alert('账号或密码错误，请重新输入');
    }


}


function test1(k) {

    alert("test1==" + k);

}

function init() {

//在此添加测试代码
    document.getElementById('testJS').onclick = function () {
        test()
    };
}


//判断是否是IE浏览器，包括Edge浏览器
function IEVersion() {
    //取得浏览器的userAgent字符串
    var userAgent = navigator.userAgent;
    //判断是否IE浏览器
    var isIE = userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1;
    if (isIE) {
        var reIE = new RegExp("MSIE (\\d+\\.\\d+);");
        reIE.test(userAgent);
        var fIEVersion = parseFloat(RegExp["$1"]);
        if (fIEVersion < 10 || !isSupportPlaceholder()) {
            return true;
        }
    } else {
        return false;
    }
}

window.onload = function () {
    if (IEVersion()) {
        window.location.href = '/w3/console/login/h5.html';

    }
};

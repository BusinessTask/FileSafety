
function getStartTime() {
    return getDate() + " 00:00:00"
}

function getEndTime() {
    return getDate() + " 23:59:59"
}

function getDate() {
    let data = new Date();
    let year = data.getFullYear();
    let month = data.getMonth();
    let date = data.getDate();
    month += 1;
    if (month < 10) {
        month = "0" + month;
    }
    return year + "-" + month + "-" + date
}

function formatDateTime(inputTime) {
    var date = new Date(inputTime);
    var y = date.getFullYear();
    var m = date.getMonth() + 1;
    m = m < 10 ? ('0' + m) : m;
    var d = date.getDate();
    d = d < 10 ? ('0' + d) : d;
    var h = date.getHours();
    h = h < 10 ? ('0' + h) : h;
    var minute = date.getMinutes();
    var second = date.getSeconds();
    minute = minute < 10 ? ('0' + minute) : minute;
    second = second < 10 ? ('0' + second) : second;
    return y + '-' + m + '-' + d + ' ' + h + ':' + minute + ':' + second;
};

/**
 * 用于格式化日期
 * 
 * @param strTime
 * @returns
 */
function FormatDate(strTime) {
	var dateStr = strTime.trim().split(" ");
	var strGMT = dateStr[0] + " " + dateStr[1] + " " + dateStr[2] + " " + dateStr[5] + " " + dateStr[3] + " GMT+0800";
	var date = new Date(strGMT);
	var month = date.getMonth() + 1;
	if (month < 10) {
		month = "0" + month;
	}
	var day = date.getDate();
	if (day < 10) {
		day = "0" + day;
	}
	var hour = date.getHours();
	if (hour < 10) {
		hour = "0" + hour;
	}
	var minute = date.getMinutes();
	if (minute < 10) {
		minute = "0" + minute;
	}
	var second = date.getSeconds();
	if (second < 10) {
		second = "0" + second;
	}
	return date.getFullYear() + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
}


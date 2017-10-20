//定义daterangepicker汉化插件

var daterangepicker_locale = {
    "format": 'YYYY-MM-DD HH:mm:ss',
    "separator": " - ",
    "applyLabel": "确定",
    "cancelLabel": "取消",
    "fromLabel": "起始时间",
    "toLabel": "结束时间'",
    "customRangeLabel": "自定义",
    "weekLabel": "周",
    "daysOfWeek": ["日", "一", "二", "三", "四", "五", "六"],
    "monthNames": ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"],
    "firstDay": 1
};
//定义daterangepicker汉化按钮部分
var daterangepicker_range = {
    '今日': [moment(), moment()],
    '昨日': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
    '最近7日': [moment().subtract(6, 'days'), moment()],
    '最近30日': [moment().subtract(29, 'days'), moment()],
    '本月': [moment().startOf('month'), moment().endOf('month')],
    '上月': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
};

var  daterangepicker_singleDate_options = {
    startDate: moment(),
    endDate: moment(),

    singleDatePicker: true, /*是否只显示单个日历 true|false*/
    locale: daterangepicker_locale, /*自定义文字*/

    showDropdowns: true/*月份是否显示成下拉菜单*/
}

var  daterangepicker_custom_options = {

    startDate: moment().subtract(29, 'days'),
    endDate: moment(),

    singleDatePicker: false, /*是否只显示单个日历 true|false*/

    ranges: daterangepicker_range, /*自定义时间范围菜单*/
    showCustomRangeLabel: false, /*是否显示自定义时间范围按钮*/
    alwaysShowCalendars: false, /*是否一直显示时间选择框 true|false*/

    locale: daterangepicker_locale, /*自定义文字*/

    autoApply: true, /*是否选择日期后自动提交,在有时间框的时候无效 true|false*/
    autoUpdateInput: true, /*是否将当前值更新到Input元素中*/
}

var  daterangepicker_full_options = {

    startDate: moment().subtract(29, 'days'),
    endDate: moment(),

    singleDatePicker: false, /*是否只显示单个日历 true|false*/

    ranges: daterangepicker_range, /*自定义时间范围菜单*/
    showCustomRangeLabel: true, /*是否显示自定义时间范围按钮*/
    alwaysShowCalendars: true, /*是否一直显示时间选择框 true|false*/

    locale: daterangepicker_locale, /*自定义文字*/

    opens: "right", /*时间选择框水平偏离方向 left|center|right*/
    drops: "down", /*时间选择框垂直偏离方向 up|down*/


    showDropdowns: false, /*月份是否显示成下拉菜单*/
    linkedCalendars: false, /*2个时间显示器是否显示连续的月份*/

    showWeekNumbers: false, /*是否显示周数 true|false*/
    showISOWeekNumbers: false, /*是否显示自定义周数 true|false*/

    timePicker: true, /*是否显示时间 true|false*/
    timePicker24Hour: true, /*时间是否显示24小时制 true|false*/
    timePickerIncrement: 1, /*时间分钟精度 true|false*/
    timePickerSeconds: true, /*时间是否显示秒 true|false*/


    autoApply: true, /*是否选择日期后自动提交,在有时间框的时候无效 true|false*/
    autoUpdateInput: true, /*是否将当前值更新到Input元素中*/
    /*
     dateLimit: {
     "days": 7
     }时间范围限制*/

}

var dropzone_full_options = {
    url: "#", /*文件提交路径*/
    method: 'post',
    paramName: 'file',/*相当于<input>元素的name属性，默认为file*/
    maxFiles: 10,/*限制最多上传的文件个数,默认null*/
    maxFilesize: 512,/*最大文件大小,单位MB*/
    acceptedFiles: ".js,.obj,.dae",/*指明允许上传的文件类型，格式是逗号分隔的 MIME type 或者扩展名。例如：image/*,application/pdf,.psd,.obj*/
    uploadMultiple:true,/*指明是否允许 Dropzone 一次提交多个文件。默认为false。*/
    dictDefaultMessage:'aa'
}

//
// url：最重要的参数，指明了文件提交到哪个页面。
// method：默认为post，如果需要，可以改为put。
// paramName：相当于<input>元素的name属性，默认为file。
// maxFilesize：最大文件大小，单位是 MB。
// maxFiles：默认为null，可以指定为一个数值，限制最多文件数量。
// addRemoveLinks：默认false。如果设为true，则会给文件添加一个删除链接。
// acceptedFiles：指明允许上传的文件类型，格式是逗号分隔的 MIME type 或者扩展名。例如：image/*,application/pdf,.psd,.obj
//  uploadMultiple：指明是否允许 Dropzone 一次提交多个文件。默认为false。如果设为true，则相当于 HTML 表单添加multiple属性。
//  headers：如果设定，则会作为额外的 header 信息发送到服务器。例如：{"custom-header": "value"}
//  init：一个函数，在 Dropzone 初始化的时候调用，可以用来添加自己的事件监听器。
//  forceFallback：Fallback 是一种机制，当浏览器不支持此插件时，提供一个备选方案。默认为false。如果设为true，则强制 fallback。
//  fallback：一个函数，如果浏览器不支持此插件则调用。

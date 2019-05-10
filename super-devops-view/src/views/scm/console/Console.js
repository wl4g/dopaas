
export default {
  name: 'console',
  data() {
    return {
      formInline: {
        index: 'sink-node',
        loglevle: 3,
        fq: 5,
        content: '',
        enable: false
      },
      fq: [{
        id: 5,
        value: '5 sec'
      }, {
        id: 10,
        value: '10 sec'
      }, {
        id: 15,
        value: '15 sec'
      }, {
        id: 20,
        value: '20 sec'
      }, {
        id: 25,
        value: '25 sec'
      }],
      loglevle: [{
        id: 5,
        value: 'ERROR ↑'
      }, {
        id: 4,
        value: 'WARN ↑'
      }, {
        id: 3,
        value: 'INFO ↑'
      }, {
        id: 2,
        value: 'DEBUG ↑'
      }, {
        id: 1,
        value: 'TRACE ↑'
      }],
      radio: 1,
      radio1: 1,
      //页
      total: 0,
      pageNum: 1,
      pageSize: 10,
      loading: true,
      excute: 'Start',
      excutestatus: false,
      // 弹窗刚开始关闭状态
      dialogVisible: false,
      historylog: false,
      falg: false,
      falg1: false,
      value1: new Date(),
      value2: new Date(),
      value3: new Date(),
      websocket: null,
      _timeOut: '',
      timeout: '',
      textarea: "2018-11-14 13:13:18.496  INFO 4931  \r\n  --- [inner-job-com.sm.sink.service.xschedule.job.DeviceRealtimeEventWatchJob-2] c.s.s.service.rt.RealtimeStateWatcher    : Processing. addr=11111119, order=56/n2018-11-14 13:13:18.496  INFO 4931 --- [inner-job-com.sm.sink.service.xschedule.job.DeviceRealtimeEventWatchJob-2] c.s.s.service.rt.RealtimeStateWatcher    : Processing. addr=11111119, order=58",
      ruleForm: {
        desc: '',
      },
      rules: {
        desc: [
          { required: true, message: '请填写详细描述', trigger: 'blur' }
        ]
      },
      // 默认显示一条空数据
      tableData1: [{
        enable: true,
        value: '',
      }],

      // 最大表格数据
      tableData: [
      ]
    }
  },
  watch: {
    textarea() {
      this.scroll();
    }
  },
  mounted() {
  },

  beforeDestroy() {//路由之后清除定时器
    clearTimeout(this._timeOut);
  },
  // watch:{
  //   $router(){
  //     alert(88888)
  //     clearInterval(this._timeOut);
  //   }
  // },
  // 删除一列
  methods: {
    //滚动
    scroll() {
      this.$nextTick(() => {
        let div = document.querySelector(".el-textarea__inner");
        div.scrollTop = div.scrollHeight;
      })

    },
    // 获取列表数据
    getData() {
      this.$$api_configguration_lists({
        data: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
        },
        fn: data => {
          this.loading = false;
          if (data.code == 200) {
            this.total = data.data.page.total;
            this.tableData = data.data.list;
          } else {
            this.$alert(data.message, '错误', {
              confirmButtonText: '确定'
            });
          }
        },
        errFn: () => {
          this.loading = false;
          this.$alert('访问失败，请稍后重试！', '错误', {
            confirmButtonText: '确定',
          });
        }
      })
    },
    currentChange(i) {
      this.loading = true;
      this.pageNum = i;
      this.getData();
    },
    deleteRow(index, rows) {
      rows.splice(index, 1);
    },
    screen() {
      this.dialogVisible = true;
      if (this.tableData1.length == 1 && this.tableData1[0].value == "") {
        this.tableData1[0].value = this.formInline.content;
      }
    },
    confirm() {
      this.dialogVisible = false;
      this.formInline.enable = true;
    },
    submit() {
      let start = null;
      let end = null;
      let interval = null;
      this.historylog = false;
      if (this.radio == 1) {
        interval = this.radio1;
      } else {
        let ymdDate = this.value1;
        let startDate = this.value2;
        let endDate = this.value3;
        let ymd = '';
        if (ymdDate != '') {
          ymd = this.getDate1(ymdDate);
        }
        if (startDate != '' && ymd != '') {
          start = ymd + " " + this.getDate2(startDate);
        }
        if (endDate != '' && ymd != '') {
          end = ymd + " " + this.getDate2(endDate);
        }
      }
      //clearTimeout(this._timeOut);
      this.excute = 'Start';
      this.execute(start, end, interval);
    },
    getDate1(startDate) {
      let Y = startDate.getFullYear() + '-';
      let M = (startDate.getMonth() + 1 < 10 ? '0' + (startDate.getMonth() + 1) : startDate.getMonth() + 1) + '-';
      let D = startDate.getDate() < 10 ? '0' + (startDate.getDate()) : startDate.getDate();
      return Y + M + D;
    },
    getDate2(startDate) {
      let H = startDate.getHours() < 10 ? '0' + (startDate.getHours()) : startDate.getHours();
      let m = startDate.getMinutes() < 10 ? '0' + (startDate.getMinutes()) : startDate.getMinutes();
      let S = startDate.getSeconds() < 10 ? '0' + (startDate.getSeconds()) : startDate.getSeconds();
      return H + ":" + m + ":" + S;
    },
    schedule(fq) {
      let _this = this;
      _this.execute();
      // _this.execute(null,null,null,data=>{
      //   let list = data.value;
      //   let size = list.length;
      //   let fq = 5*1000/size;
      //   let index=0;
      //   clearInterval(this.timeout);
      //   this.timeout = setInterval(()=>{
      //     this.textarea = this.textarea+"\r\n"+list[index];
      //     index++;
      //     if(index>list.length-2){
      //       clearInterval(this.timeout);
      //     }
      //   },fq);
      // });
      this._timeOut = setTimeout(() => {
        _this.schedule(fq);
      }, fq);
    },
    // schedule(fq) {
    //   this._timeOut = setInterval(() => { 
    //       this.execute();
    //   }, fq)
    // },
    initWebSocket() { //初始化weosocket
      this.websocket = new WebSocket('ws://localhost:8080/monitor/logsocket/');
      //指定事件回调
      this.websocket.onmessage = this.websocketOnMessage;
      this.websocket.onopen = this.websocketOnOpen;
      this.websocket.onerror = this.websocketOnError;
      this.websocket.onclose = this.websocketClose;
    },

    websocketOnOpen() { //连接建立之后执行send方法发送数据
      this.excutestatus = !this.excutestatus;
      if (this.excutestatus) {
        let actions = { 'index': 'sink-node', 'level': 3 }
        this.websocketSend(JSON.stringify(actions));
        //连接后,定时发送,否则不段时间不通信会自动断连(时间长短一般是服务端指定的)
        var that = this;
        setInterval(function () {
          that.websocketSend(JSON.stringify({ 'index': 'sink-node', 'level': 3 }));
        }, 15000);
        this.excute = 'Stop';
        //this.schedule(this.formInline.fq*1000);
      } else {
        this.excute = 'Start';
        // clearTimeout(this._timeOut);
        this.websocket.close();
      }
    },

    websocketOnError() {//连接建立失败重连
      this.initWebSocket();
    },

    websocketOnMessage(e) { //数据接收
      this.textarea = this.textarea + e.data;
    },

    websocketSend(Data) {//数据发送
      this.websocket.send(Data);
    },

    // eslint-disable-next-line
    websocketClose(e) {  //关闭
      // eslint-disable-next-line
      console.log('断开连接', e);
    },
    excutemethod() {
      this.excutestatus = !this.excutestatus;
      if (this.excutestatus) {
        this.excute = 'Stop';
        this.execute(null, null, null, true);
        //this.schedule(this.formInline.fq*1000);
      } else {
        this.excute = 'Start';
        //clearTimeout(this._timeOut);
      }

    },
    // excutemethod(){
    //   this.excutestatus = !this.excutestatus;
    //   if( this.excutestatus){
    //     let websocket = new WebSocket('ws://localhost:8080/monitor/logsocket/sink-node/3');
    //     this.excute = 'Stop';
    //     websocket.onmessage = data => {
    //       // 接收服务端的实时日志并添加到HTML页面中
    //       this.textarea = this.textarea + data.data ;
    //     };
    //     //this.schedule(this.formInline.fq*1000);
    //   }else{
    //     let websocket = new WebSocket('ws://localhost:8080/monitor/logsocket/');
    //     this.excute = 'Start';
    //    // clearTimeout(this._timeOut);
    //     websocket.close();
    //   }

    // },
    execute(start, end, interval, flag) {
      let queryList;
      if (this.formInline.enable) {
        queryList = this.tableData1;
      } else {
        queryList = [{
          enable: true,
          value: this.formInline.content,
        }]
      }
      this.$$api_configguration_getlog({
        data: {
          queryList: queryList,
          level: this.formInline.loglevle,
          index: this.formInline.index,
          startDate: start,
          endDate: end,
          flag: flag,
          interval: interval
        },
        fn: data => {
          if (data.code == 200) {
            if (this.excute == 'Stop') {
              // if(this.falg&&this.falg1){
              //   this.textarea = data.data+"进行中.";
              //   this.falg =false;
              // }else if(!this.falg&&this.falg1){
              //   this.falg1 = false;
              //   this.textarea = data.data+"进行中. . ";
              // }else if(!this.falg&&!this.falg1){
              //   this.textarea = data.data+"进行中. . .";
              //   this.falg = true;
              // }else{
              //   this.falg1 = true;
              //   this.textarea = data.data+"进行中. . . .";
              // }
              // callback(data.data)
              let _self = this;
              let list = data.data.value;
              let st = data.data.key;
              let size = list.length;
              let sec = _self.formInline.fq -1;
              let fq = sec * 1000 / size;
              let index = 0;

              if(data.data.value.length == 0){
                // setTimeout(()=>{

                // },1000)
                _self.execute(null, end, interval, true);
                return;
              }
              
              clearInterval(_self.timeout);
              _self.timeout = setInterval(() => {
                _self.textarea = _self.textarea + "\r\n" + list[index];
                index++;
                if (index > list.length - 1) {
                  clearInterval(_self.timeout);
                  _self.execute(st, end, interval, true)
                }
              }, fq);
            } else {
              if (!flag) {
                this.textarea = data.data;
              }
            }
          } else {
            this.$alert(data.message, '错误', {
              confirmButtonText: '确定'
            });
          }
        },
        errFn: () => {
          this.$alert('访问失败，请稍后重试！', '错误', {
            confirmButtonText: '确定',
          });
        }
      })
    },
    sleep(numberMillis) {
      var now = new Date();
      var exitTime = now.getTime() + numberMillis;
      while (true) {
        now = new Date();
        if (now.getTime() > exitTime)
          return;
      }
    },
    // 增加一列表格
    flshfq() {
      if (this.excute == 'Stop') {
        clearTimeout(this._timeOut);
        this.schedule(this.formInline.fq * 1000);
      }
    },
    // 加载最新
    onflush() {
      this.excute = 'Start'
      this.execute();
    },
    // 增加一列表格
    addRow() {

      this.tableData1.push({ enable: true, value: '' })
    }
  },

}

package com.wl4g.dopaas.lcdp.tools.hbase.bulk.mapred;
///*
// * Copyright 2017 ~ 2025 the original author or authors. <wanglsir@gmail.com, 983708408@qq.com>
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.wl4g.dopaas.lcdp.tools.hbase.bulk.mapred;
//
//import java.io.IOException;
//import java.util.Iterator;
//
//import org.apache.hadoop.io.Text;
//import org.apache.hadoop.mapreduce.Reducer;
//
///**
// * {@link HileToCsvReducer}
// * 
// * @author Wangl.sir &lt;wanglsir@gmail.com, 983708408@qq.com&gt;
// * @version 2022-02-18 v1.0.0
// * @since v1.0.0
// */
//public class HileToCsvReducer extends Reducer<Text, Text, Text, Text> {
//
//    @Override
//    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
//        Iterator<Text> it = values.iterator();
//        while (it.hasNext()) {
//            // 这样可以只保留下Key字段，也就只有一行数据了
//            Text value = it.next();
//            Text mergeKey = new Text();
//            mergeKey.set(key.toString() + "," + value.toString());
//            Text v = new Text();
//            v.set("");
//            context.write(mergeKey, v);
//        }
//    }
//
//}

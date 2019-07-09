package com.wl4g.devops.umc.notification.wechat;

import com.wl4g.devops.umc.notification.AbstractAlarmNotifier;

import java.util.List;

/**
 * @author vjay
 * @date 2019-06-10 15:10:00
 */
public class WeChatNotifier extends AbstractAlarmNotifier {

    @Override
    public void simpleNotify(List<String> targets, String message) {
        //send msg

    }

}

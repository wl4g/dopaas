package com.wl4g.devops.share.service.impl;

import com.wl4g.devops.share.service.DictService;
import com.wl4g.devops.common.bean.share.Dict;
import com.wl4g.devops.dao.share.DictDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wl4g.devops.common.bean.BaseBean.DEL_FLAG_DELETE;

/**
 * @author vjay
 * @date 2019-08-13 09:51:00
 */
@Service
public class DictServiceImpl  implements DictService {

    @Autowired
    private DictDao dictDao;


    @Override
    public void save(Dict dict) {
        Assert.notNull(dict,"dict is null");
        if(dict.getId()!=null){
            dict.preUpdate();
            dictDao.updateByPrimaryKeySelective(dict);
        }else{
            dict.preInsert();
            dictDao.insertSelective(dict);
        }
    }

    @Override
    public void del(Integer id) {
        Assert.notNull(id,"id is null");
        Dict dict = new Dict();
        dict.setId(id);
        dict.preUpdate();
        dict.setDelFlag(DEL_FLAG_DELETE);
        dictDao.updateByPrimaryKeySelective(dict);
    }

    @Override
    public List<Dict> getBytype(String type) {
        Assert.hasText(type,"type is blank");
        return dictDao.selectByType(type);
    }

    @Override
    public Dict getByKey(String key) {
        Assert.hasText(key,"key is blank");
        return dictDao.getByKey(key);
    }

    @Override
    public List<String> allType() {
        return dictDao.allType();
    }

    @Override
    public Map<String, Object> cache() {
        Map<String, Object> result = new HashMap<>();
        List<Dict> dicts = dictDao.list(null, null, null, null);
        Map<String, List<Dict>> dictList = new HashMap<>();
        Map<String, Map<String,Dict>> dictMap = new HashMap<>();
        for(Dict dict : dicts){
            String type = dict.getType();
            List<Dict> list = dictList.get(type);
            Map<String,Dict> map = dictMap.get(type);
            if(null==list){
                list = new ArrayList<>();
            }
            if(null==map){
                map = new HashMap<>();
            }
            list.add(dict);
            map.put(dict.getValue(),dict);
            dictList.put(type,list);
            dictMap.put(type,map);
        }
        result.put("dictList",dictList);
        result.put("dictMap",dictMap);
        return result;
    }


}

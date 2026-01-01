package org.dows.setting.dao;

import org.dows.rade.crud.CrudDaoImpl;
import org.springframework.stereotype.Component;
import org.dows.setting.entity.SettingStoreEntity;
import org.dows.setting.mapper.SettingStoreMapper;

@Component
public class SettingStoreDao extends CrudDaoImpl<SettingStoreMapper,SettingStoreEntity>{

}
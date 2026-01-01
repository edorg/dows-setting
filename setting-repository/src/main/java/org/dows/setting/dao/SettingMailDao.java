package org.dows.setting.dao;

import org.dows.rade.crud.CrudDaoImpl;
import org.springframework.stereotype.Component;
import org.dows.setting.entity.SettingMailEntity;
import org.dows.setting.mapper.SettingMailMapper;

@Component
public class SettingMailDao extends CrudDaoImpl<SettingMailMapper,SettingMailEntity>{

}
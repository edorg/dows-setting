package org.dows.setting.dao;

import org.dows.rade.crud.CrudDaoImpl;
import org.springframework.stereotype.Component;
import org.dows.setting.entity.SettingSmsEntity;
import org.dows.setting.mapper.SettingSmsMapper;

@Component
public class SettingSmsDao extends CrudDaoImpl<SettingSmsMapper,SettingSmsEntity>{

}
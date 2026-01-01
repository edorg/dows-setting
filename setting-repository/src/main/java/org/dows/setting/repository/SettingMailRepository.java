package org.dows.setting.repository;

import org.dows.rade.crud.CrudRepository;
import org.springframework.stereotype.Component;

import org.dows.setting.entity.SettingMailEntity;
import org.dows.setting.dao.SettingMailDao;

@Component
public class SettingMailRepository  extends CrudRepository<SettingMailDao,SettingMailEntity> {

}
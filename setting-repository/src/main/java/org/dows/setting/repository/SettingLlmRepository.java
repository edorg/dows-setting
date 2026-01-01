package org.dows.setting.repository;

import org.dows.rade.crud.CrudRepository;
import org.springframework.stereotype.Component;

import org.dows.setting.entity.SettingLlmEntity;
import org.dows.setting.dao.SettingLlmDao;

@Component
public class SettingLlmRepository  extends CrudRepository<SettingLlmDao,SettingLlmEntity> {

}
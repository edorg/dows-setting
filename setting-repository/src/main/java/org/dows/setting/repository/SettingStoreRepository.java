package org.dows.setting.repository;

import org.dows.rade.crud.CrudRepository;
import org.springframework.stereotype.Component;

import org.dows.setting.entity.SettingStoreEntity;
import org.dows.setting.dao.SettingStoreDao;

@Component
public class SettingStoreRepository  extends CrudRepository<SettingStoreDao,SettingStoreEntity> {

}
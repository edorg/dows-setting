package org.dows.setting.repository;

import org.dows.rade.crud.CrudRepository;
import org.springframework.stereotype.Component;

import org.dows.setting.entity.SettingSmsEntity;
import org.dows.setting.dao.SettingSmsDao;

@Component
public class SettingSmsRepository  extends CrudRepository<SettingSmsDao,SettingSmsEntity> {

}
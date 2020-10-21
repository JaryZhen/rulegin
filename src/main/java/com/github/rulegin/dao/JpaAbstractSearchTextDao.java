package com.github.rulegin.dao;


import com.github.rulegin.dao.model.BaseEntity;
import com.github.rulegin.dao.model.SearchTextEntity;

/**
 * Created by Valerii Sosliuk on 5/6/2017.
 */
public abstract class JpaAbstractSearchTextDao <E extends BaseEntity<D>, D> extends JpaAbstractDao<E, D> {

    @Override
    protected void setSearchText(E entity) {
        ((SearchTextEntity) entity).setSearchText(((SearchTextEntity) entity).getSearchTextSource().toLowerCase());
    }
}


package com.shinnoderibarii.service;

import com.shinnoderibarii.entity.Store;
import com.shinnoderibarii.mapper.StoreMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreService {

    private final StoreMapper storeMapper;

    public StoreService(StoreMapper storeMapper) {
        this.storeMapper = storeMapper;
    }

    public List<Store> findAll() {
        // List<Store> stores = storeMapper.findAll();

        // if (!stores.isEmpty()) {
        // System.out.println("Store name: " + stores.get(0).getName());
        // System.out.println("Store address: " + stores.get(0).getAddress());
        // }

        // // System.out.println(storeMapper.getCharsetInfo());

        // System.out.println(storeMapper.inspectStoreNames());

        return storeMapper.findAll();
    }
}


package com.shinnoderibarii.controller;

import com.shinnoderibarii.entity.Store;
import com.shinnoderibarii.service.StoreService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping
    public List<Store> findAll() {
        return storeService.findAll();
    }
}
